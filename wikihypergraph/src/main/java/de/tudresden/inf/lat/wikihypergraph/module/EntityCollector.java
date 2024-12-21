package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
//import org.wikidata.wdtk.datamodel.json.jackson.JacksonDatatypeId;
//import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An object of this class collects entities. This class contains different
 * methods to collect entities from different structures.
 * 
 * @author Julian Mendez
 *
 */
public class EntityCollector {

	public static final String EXPECTED_FORMAT = "application/json";

	/**
	 * An object of this class collects all entities referred in a snak (
	 * {@link Snak}).
	 * 
	 * @author Julian Mendez
	 *
	 */
	class EntitySnakVisitor implements SnakVisitor<Set<String>> {

		@Override
		public Set<String> visit(ValueSnak snak) {
			ValueSnakVisitor valueVisitor = new ValueSnakVisitor();
			Set<String> ret = new TreeSet<>();
			ret.addAll(snak.getValue().accept(valueVisitor));
			ret.add(snak.getPropertyId().getId());
			return ret;
		}

		@Override
		public Set<String> visit(SomeValueSnak snak) {
			return Collections.singleton(snak.getPropertyId().getId());
		}

		@Override
		public Set<String> visit(NoValueSnak snak) {
			return Collections.singleton(snak.getPropertyId().getId());
		}

	}

	/**
	 * An object of this class collects all entities referred in a value of a
	 * snak ({@link Value}).
	 * 
	 * @author Julian Mendez
	 *
	 */
	class ValueSnakVisitor implements ValueVisitor<Set<String>> {

		@Override
		public Set<String> visit(EntityIdValue value) {
			return Collections.singleton(value.getId());
		}

		@Override
		public Set<String> visit(GlobeCoordinatesValue value) {
			return Collections.emptySet();
		}

		@Override
		public Set<String> visit(MonolingualTextValue value) {
			return Collections.emptySet();
		}

		@Override
		public Set<String> visit(QuantityValue value) {
			return Collections.emptySet();
		}

		@Override
		public Set<String> visit(StringValue value) {
			return Collections.emptySet();
		}

		@Override
		public Set<String> visit(TimeValue value) {
			return Collections.emptySet();
		}

	}

	/**
	 * Constructs a new instance of an entity collector.
	 */
	public EntityCollector() {
	}

	/**
	 * Returns the set containing all entities in a MediaWiki revision (
	 * {@link MwRevision}).
	 * 
	 * @param mwRevision
	 *            MediaWiki revision
	 * @return set of all entities contained in the given revision
	 * @throws JsonProcessingException
	 *             if something goes wrong with parsing
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public Set<String> collectEntities(MwRevision mwRevision) throws JsonProcessingException, IOException {
		Set<String> ret = new TreeSet<>();
		List<StatementGroup> statementGroups = getStatementGroups(mwRevision);
		statementGroups.forEach(statementGroup -> {
			ret.addAll(collectEntities(statementGroup));
		});
		return ret;
	}

	/**
	 * Returns the set of all entities contained in the given statement group.
	 * 
	 * @param statementGroup
	 *            statement group
	 * @return set of all entities contained in the given statement group
	 */
	public Set<String> collectEntities(StatementGroup statementGroup) {
		Set<String> ret = new TreeSet<>();
		List<Statement> statements = statementGroup.getStatements();
		statements.forEach(statement -> {
			ret.addAll(collectEntities(statement));
		});
		return ret;
	}

	/**
	 * Returns the set of all entities contained in the given statement.
	 * 
	 * @param statement
	 *            statement
	 * @return set of all entities contained in the given statement
	 */
	public Set<String> collectEntities(Statement statement) {
		Set<String> ret = new TreeSet<>();
		Snak snak = statement.getClaim().getMainSnak();
		ret.add(snak.getPropertyId().getId());
		EntitySnakVisitor entityVisitor = new EntitySnakVisitor();
		ret.addAll(snak.accept(entityVisitor));
		return ret;
	}

	/**
	 * Returns a list containing all statement groups in the given MediaWiki
	 * revision. This method uses a Jackon parser to read those statements.
	 * 
	 * @return a list containing all statement groups in the given MediaWiki
	 *         revision
	 * @throws JsonProcessingException
	 *             if something goes wrong with parsing
	 * @throws IOException
	 *             if something goes wrong with I/O
	 * 
	 */
	List<StatementGroup> getStatementGroups(MwRevision mwRevision) throws JsonProcessingException, IOException {
		List<StatementGroup> ret = new ArrayList<>();
		String format = mwRevision.getFormat();
		String model = mwRevision.getModel();

		if (format.equals(EXPECTED_FORMAT)
				&& (model.equals(DatatypeIdValue.DT_ITEM) || model.equals(DatatypeIdValue.DT_PROPERTY))) {

			String text = mwRevision.getText();
			ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);
            ObjectReader documentReader = mapper.readerFor(StatementGroup.class);
            MappingIterator<StatementGroup> documentIterator =
					documentReader.readValue(text);
			StatementGroup document = documentIterator.nextValue();
			ret.add(document);

		}
		return ret;
	}

}
