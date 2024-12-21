package de.tudresden.inf.lat.wikihypergraph.selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;
//import org.wikidata.wdtk.datamodel.json.jackson.JacksonDatatypeId;
//import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;
import org.wikidata.wdtk.dumpfiles.MwRevision;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An object of this class is a visitor for snaks and values.
 * 
 * @author Julian Mendez
 *
 */
public class AxiomSelectorSnakAndValueVisitor {

	public static final String EXPECTED_FORMAT = "application/json";
	public static final String REFERENCE_PREFIX = "R";
	public static final String STATEMENT_PREFIX = "S";
	public static final String PAIR_VALUE_PREFIX = "PV";
	public static final String PARSING_PROBLEM_MESSAGE = "ERROR";

	static final char QUOTES_CHAR = '"';
	static final char UNDERSCORE_CHAR = '_';

	/**
	 * An object of this class is a visitor for snaks.
	 * 
	 * @author Julian Mendez
	 *
	 */
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
			List<SelectorTuple> ret = new ArrayList<>();
			String relation = snak.getPropertyId().getId();
			SelectorTuple tuple = new SelectorTuple(STATEMENT_PREFIX + this.statement, this.subject, relation,
					snak.getValue().accept(valueVisitor));
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

	/**
	 * An object of this class is a visitor for values.
	 * 
	 * @author Julian Mendez
	 *
	 */
	class ValueSnakVisitor implements ValueVisitor<String> {

		@Override
		public String visit(EntityIdValue value) {
			return value.getId();
		}

		@Override
		public String visit(GlobeCoordinatesValue value) {
			return QUOTES_CHAR + value.toString() + QUOTES_CHAR;
		}

		@Override
		public String visit(MonolingualTextValue value) {
			return QUOTES_CHAR + value.toString().replace(QUOTES_CHAR, UNDERSCORE_CHAR) + QUOTES_CHAR;
		}

		@Override
		public String visit(QuantityValue value) {
			return QUOTES_CHAR + value.toString() + QUOTES_CHAR;
		}

		@Override
		public String visit(StringValue value) {
			return value.toString();
		}

		@Override
		public String visit(TimeValue value) {
			return QUOTES_CHAR + value.toString() + QUOTES_CHAR;
		}

	}

	private long statementId = 0;
	private long referenceId = 0;
	private long pairValueId = 0;

	/**
	 * Constructs a new axiom selector, visitor of snaks and values.
	 */
	public AxiomSelectorSnakAndValueVisitor() {
	}

	/**
	 * Processes a revision and returns a list of selector tuples.
	 * 
	 * @param mwRevision
	 *            revision
	 * @return a list of selector tuples
	 * @throws JsonProcessingException
	 *             if something goes wrong with parsing
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public List<SelectorTuple> process(MwRevision mwRevision) throws JsonProcessingException, IOException {
		if (mwRevision == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		List<SelectorTuple> ret = new ArrayList<>();
		List<StatementGroup> statementGroups = getStatementGroups(mwRevision);
		statementGroups.forEach(statementGroup -> {
			String subject = mwRevision.getTitle();
			if (subject == null) {
				subject = PARSING_PROBLEM_MESSAGE;
			}
			ret.addAll(process(statementGroup, subject));
		});
		return ret;
	}

	public List<SelectorTuple> process(StatementGroup statementGroup, String subject) {
		if (statementGroup == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (subject == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		List<SelectorTuple> ret = new ArrayList<>();
		List<Statement> statements = statementGroup.getStatements();
		statements.forEach(statement -> {
			ret.addAll(process(statement, subject));
		});
		return ret;
	}

	public List<SelectorTuple> process(Statement statement, String subject) {
		if (statement == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (subject == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		List<SelectorTuple> ret = new ArrayList<>();
		addTuple(ret, asStatement(this.statementId + 1), TypeAndRelationName.RELATION_HAS_TYPE,
				TypeAndRelationName.TYPE_STATEMENT);

		long mainStatementId = this.statementId;
		{
			EntitySnakVisitor entityVisitor = new EntitySnakVisitor(this.statementId, subject);
			Snak snak = statement.getClaim().getMainSnak();
			ret.addAll(snak.accept(entityVisitor));
		}
		this.statementId++;

		statement.getReferences().forEach(reference -> {
			addReference(ret, reference, mainStatementId);
		});
		return ret;
	}

	/**
	 * Adds a selector tuple to the given list.
	 * 
	 * @param list
	 *            list of selector tuples
	 * @param subject
	 *            subject
	 * @param relation
	 *            relation
	 * @param object
	 *            object
	 */
	void addTuple(List<SelectorTuple> list, String subject, String relation, String object) {
		SelectorTuple tuple = new SelectorTuple(asStatement(this.statementId), subject, relation, object);
		list.add(tuple);
		this.statementId++;
	}

	/**
	 * Adds a pair value to a list of selector tuples.
	 * 
	 * @param list
	 *            list of selector tuple
	 * @param pairValue
	 *            pair value as a selector tuple where only the relation and
	 *            object are used
	 */
	void addPairValue(List<SelectorTuple> list, SelectorTuple pairValue) {
		addTuple(list, asPairValue(this.pairValueId), TypeAndRelationName.RELATION_HAS_TYPE,
				TypeAndRelationName.TYPE_PAIR_VALUE);

		addTuple(list, asReference(this.referenceId), TypeAndRelationName.RELATION_HAS_PAIR_VALUE,
				asPairValue(this.pairValueId));

		addTuple(list, asPairValue(this.pairValueId), TypeAndRelationName.RELATION_HAS_PROPERTY,
				pairValue.getRelation());

		addTuple(list, asPairValue(this.pairValueId), TypeAndRelationName.RELATION_HAS_VALUE, pairValue.getObject());
	}

	/**
	 * Adds the pair values in a snak to a list of selector tuples.
	 * 
	 * @param list
	 *            list of selector tuple
	 * @param snak
	 *            snak
	 */
	void addPairValuesInSnak(List<SelectorTuple> list, Snak snak) {
		EntitySnakVisitor entityVisitor = new EntitySnakVisitor(this.statementId, asPairValue(this.pairValueId));
		List<SelectorTuple> pairValues = new ArrayList<>();
		pairValues.addAll(snak.accept(entityVisitor));

		pairValues.forEach(currentPairValue -> {
			addPairValue(list, currentPairValue);
		});
		this.pairValueId++;

	}

	void addReference(List<SelectorTuple> ret, Reference reference, long mainStatementId) {
		addTuple(ret, asReference(this.referenceId), TypeAndRelationName.RELATION_HAS_TYPE,
				TypeAndRelationName.TYPE_REFERENCE);

		addTuple(ret, asStatement(mainStatementId), TypeAndRelationName.RELATION_HAS_REFERENCE,
				asReference(this.referenceId));

		reference.getSnakGroups().forEach(snakGroup -> {
			snakGroup.getSnaks().forEach(snak -> {
				addPairValuesInSnak(ret, snak);
			});
		});
		this.referenceId++;

	}

	List<StatementGroup> getStatementGroups(MwRevision mwRevision) throws JsonProcessingException, IOException {
		List<StatementGroup> ret = new ArrayList<>();
		String format = mwRevision.getFormat();
		String model = mwRevision.getModel();

		if (format.equals(EXPECTED_FORMAT)
				&& (model.equals(DatatypeIdValue.DT_ITEM) || model.equals(DatatypeIdValue.DT_PROPERTY))) {

			String text = mwRevision.getText();
			try {
				ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);
				ObjectReader documentReader = mapper.readerFor(StatementGroup.class);
				MappingIterator<StatementGroup> documentIterator =
						documentReader.readValue(text);
				StatementGroup document = documentIterator.nextValue();
				ret.add(document);
			} catch (JsonMappingException e) {
				// if the page cannot be parsed, it is ignored
			}

		}
		return ret;
	}

	String asReference(long referenceId) {
		return REFERENCE_PREFIX + referenceId;
	}

	String asStatement(long statementId) {
		return STATEMENT_PREFIX + statementId;
	}

	String asPairValue(long pairValueId) {
		return PAIR_VALUE_PREFIX + pairValueId;
	}

}
