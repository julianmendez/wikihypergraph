package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.wikidata.wdtk.datamodel.interfaces.Claim;
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
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An object of this class extracts a 'module'.
 * 
 * @author Julian Mendez
 *
 */
public class ModuleExtractionMwRevisionProcessor implements MwRevisionProcessor {

	public static final String EXPECTED_FORMAT = "application/json";

	private final Set<String> module = new TreeSet<String>();
	private final Set<String> visited = new TreeSet<String>();
	private final Set<String> toVisit = new TreeSet<String>();
	private final List<Pair> followers = new ArrayList<Pair>();
	private final Set<String> items = new TreeSet<String>();
	private BufferedWriter output;

	class Pair {

		private String a;
		private String b;

		public Pair(String a, String b) {
			if (a == null || b == null) {
				throw new IllegalArgumentException("Parameters cannot be null.");
			}
			this.a = a;
			this.b = b;
		}

		public String getA() {
			return this.a;
		}

		public String getB() {
			return this.b;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Pair) {
				Pair other = (Pair) obj;
				return this.a.equals(other.a) && this.b.equals(other.b);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return a == null ? 0 : a.hashCode() + (0x1F * (b == null ? 0 : b.hashCode()));
		}

		public String toString() {
			return a + " " + b;
		}

	}

	class EntitySnakVisitor implements SnakVisitor<Set<String>> {

		@Override
		public Set<String> visit(ValueSnak snak) {
			ValueSnakVisitor valueVisitor = new ValueSnakVisitor();
			Set<String> ret = new TreeSet<String>();
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

	class ValueSnakVisitor implements ValueVisitor<Set<String>> {

		@Override
		public Set<String> visit(DatatypeIdValue value) {
			return Collections.emptySet();
		}

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

	public ModuleExtractionMwRevisionProcessor(Collection<String> items, Writer writer) {
		this.output = new BufferedWriter(writer);
		this.items.addAll(items);
	}

	public void configure(Set<String> initialSet) {
		this.module.clear();
		this.visited.clear();
		this.toVisit.clear();
		this.followers.clear();
		this.toVisit.addAll(initialSet);
	}

	Set<String> collectEntities(MwRevision mwRevision) throws JsonProcessingException, IOException {
		Set<String> ret = new TreeSet<String>();
		List<Statement> statements = getStatementGroup(mwRevision);
		for (Statement statement : statements) {
			ret.addAll(collectEntities(statement));
		}
		return ret;
	}

	Set<String> collectEntities(Statement statement) {
		Set<String> ret = new TreeSet<String>();
		Claim claim = statement.getClaim();

		// this is not necessary
		if (claim.getSubject() != null) {
			ret.add(claim.getSubject().getId());
		}

		Snak snak = claim.getMainSnak();

		// properties are also included
		ret.add(snak.getPropertyId().getId());

		EntitySnakVisitor entityVisitor = new EntitySnakVisitor();
		ret.addAll(snak.accept(entityVisitor));

		return ret;
	}

	public void visit(MwRevision mwRevision) throws JsonProcessingException, IOException {
		String origPage = mwRevision.getTitle();
		this.visited.add(origPage);

		Set<String> pages = collectEntities(mwRevision);
		for (String page : pages) {
			if (!this.visited.contains(page)) {
				this.toVisit.add(page);
				this.followers.add(new Pair(origPage, page));
			}
		}
	}

	public Set<String> getVisited() {
		return this.visited;
	}

	public Set<String> getToVisit() {
		return this.toVisit;
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl, Map<Integer, String> namespaces) {
		try {
			this.output.write("" + (new Date()).toString() + ": start revision processing with site name=" + siteName
					+ ", baseUrl=" + baseUrl + " namespaces=" + namespaces);
			this.output.newLine();
			this.output.flush();
			configure(this.items);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		try {
			String title = mwRevision.getTitle();
			if (this.toVisit.contains(title)) {
				this.output.write(title);
				this.output.newLine();
				visit(mwRevision);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void finishRevisionProcessing() {
		try {
			this.output.write("" + (new Date()).toString() + ": finish revision processing.");
			this.output.newLine();
			this.output.newLine();
			this.output.write(this.followers.toString());
			this.output.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	List<Statement> getStatementGroup(MwRevision mwRevision) throws JsonProcessingException, IOException {
		List<Statement> statements = new ArrayList<Statement>();
		String model = mwRevision.getModel();
		String format = mwRevision.getFormat();

		if (format.equals(EXPECTED_FORMAT)
				&& (model.equals(JacksonDatatypeId.JSON_DT_ITEM) || model.equals(JacksonDatatypeId.JSON_DT_PROPERTY))) {

			String text = mwRevision.getText();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode mainNode = mapper.readTree(text);
			JacksonTermedStatementDocument document = mapper.readValue(mainNode.toString(),
					JacksonTermedStatementDocument.class);

			List<StatementGroup> list = document.getStatementGroups();
			for (StatementGroup group : list) {
				statements.addAll(group.getStatements());
			}
		}
		return statements;
	}

}
