package de.tudresden.inf.lat.wikihypergraph.selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonDatatypeId;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;
import org.wikidata.wdtk.dumpfiles.MwRevision;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AxiomSelectorSnakAndValueVisitor {

	public static final String EXPECTED_FORMAT = "application/json";
	public static final String STATEMENT_PREFIX = "S";
	public static final String PARSING_PROBLEM_MESSAGE = "ERROR";

	class EntitySnakVisitor implements SnakVisitor<List<SelectorTuple>> {

		private long statement;
		private String subject;

		public EntitySnakVisitor(long statement, String subject) {
			if (subject == null) {
				throw new IllegalArgumentException("Null argument.");
			}

			this.statement = statement;
			this.subject = subject;
		}

		@Override
		public List<SelectorTuple> visit(ValueSnak snak) {
			ValueSnakVisitor valueVisitor = new ValueSnakVisitor();
			List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
			String relation = snak.getPropertyId().getId();
			SelectorTuple tuple = new SelectorTuple(STATEMENT_PREFIX + this.statement, this.subject, relation,
					snak.getValue().accept(valueVisitor));
			this.statement += 1;
			ret.add(tuple);
			return ret;
		}

		@Override
		public List<SelectorTuple> visit(SomeValueSnak snak) {
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(NoValueSnak snak) {
			return Collections.emptyList();
		}

	}

	class ValueSnakVisitor implements ValueVisitor<String> {

		@Override
		public String visit(DatatypeIdValue value) {
			return value.toString();
		}

		@Override
		public String visit(EntityIdValue value) {
			return value.getId();
		}

		@Override
		public String visit(GlobeCoordinatesValue value) {
			return value.toString();
		}

		@Override
		public String visit(MonolingualTextValue value) {
			return value.toString();
		}

		@Override
		public String visit(QuantityValue value) {
			return value.toString();
		}

		@Override
		public String visit(StringValue value) {
			return value.toString();
		}

		@Override
		public String visit(TimeValue value) {
			return value.toString();
		}

	}

	private long statementId = 0;

	public AxiomSelectorSnakAndValueVisitor() {
	}

	public List<SelectorTuple> process(MwRevision mwRevision) throws JsonProcessingException, IOException {
		if (mwRevision == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
		List<StatementGroup> statementGroups = getStatementGroups(mwRevision);
		for (StatementGroup statementGroup : statementGroups) {
			String subject = mwRevision.getTitle();
			if (subject == null) {
				subject = PARSING_PROBLEM_MESSAGE;
			}
			ret.addAll(process(statementGroup, subject));
		}
		return ret;
	}

	public List<SelectorTuple> process(StatementGroup statementGroup, String subject) {
		if (statementGroup == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (subject == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
		List<Statement> statements = statementGroup.getStatements();
		for (Statement statement : statements) {
			ret.addAll(process(statement, subject));
		}
		return ret;
	}

	public List<SelectorTuple> process(Statement statement, String subject) {
		if (statement == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (subject == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
		Snak snak = statement.getClaim().getMainSnak();
		EntitySnakVisitor entityVisitor = new EntitySnakVisitor(this.statementId, subject);
		ret.addAll(snak.accept(entityVisitor));
		this.statementId++;
		return ret;
	}

	List<StatementGroup> getStatementGroups(MwRevision mwRevision) throws JsonProcessingException, IOException {
		List<StatementGroup> ret = new ArrayList<StatementGroup>();
		String format = mwRevision.getFormat();
		String model = mwRevision.getModel();

		if (format.equals(EXPECTED_FORMAT)
				&& (model.equals(JacksonDatatypeId.JSON_DT_ITEM) || model.equals(JacksonDatatypeId.JSON_DT_PROPERTY))) {

			ObjectMapper mapper = new ObjectMapper();
			String text = mwRevision.getText();
			JacksonTermedStatementDocument document = mapper.readValue(text, JacksonTermedStatementDocument.class);
			document.setSiteIri(Datamodel.SITE_WIKIDATA);
			ret.addAll(document.getStatementGroups());

		}
		return ret;
	}

}
