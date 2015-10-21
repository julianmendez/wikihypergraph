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

	class EntitySnakVisitor implements SnakVisitor<List<SelectorTuple>> {

		@Override
		public List<SelectorTuple> visit(ValueSnak snak) {
			ValueSnakVisitor valueVisitor = new ValueSnakVisitor();
			List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
			ret.addAll(snak.getValue().accept(valueVisitor));
			// FIXME
			return ret;
		}

		@Override
		public List<SelectorTuple> visit(SomeValueSnak snak) {
			// FIXME
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(NoValueSnak snak) {
			// FIXME
			return Collections.emptyList();
		}

	}

	class ValueSnakVisitor implements ValueVisitor<List<SelectorTuple>> {

		@Override
		public List<SelectorTuple> visit(DatatypeIdValue value) {
			// FIXME
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(EntityIdValue value) {
			// FIXME
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(GlobeCoordinatesValue value) {
			// FIXME
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(MonolingualTextValue value) {
			// FIXME
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(QuantityValue value) {
			// FIXME
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(StringValue value) {
			// FIXME
			return Collections.emptyList();
		}

		@Override
		public List<SelectorTuple> visit(TimeValue value) {
			// FIXME
			return Collections.emptyList();
		}

	}

	public AxiomSelectorSnakAndValueVisitor() {
	}

	public List<SelectorTuple> process(MwRevision mwRevision) throws JsonProcessingException, IOException {
		List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
		List<StatementGroup> statementGroups = getStatementGroups(mwRevision);
		for (StatementGroup statementGroup : statementGroups) {
			ret.addAll(process(statementGroup));
		}
		return ret;
	}

	public List<SelectorTuple> process(StatementGroup statementGroup) {
		List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
		List<Statement> statements = statementGroup.getStatements();
		for (Statement statement : statements) {
			ret.addAll(process(statement));
		}
		return ret;
	}

	public List<SelectorTuple> process(Statement statement) {
		List<SelectorTuple> ret = new ArrayList<SelectorTuple>();
		Snak snak = statement.getClaim().getMainSnak();
		// FIXME
		EntitySnakVisitor entityVisitor = new EntitySnakVisitor();
		ret.addAll(snak.accept(entityVisitor));
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
