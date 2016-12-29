package de.tudresden.inf.lat.wikihypergraph.selector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

import de.tudresden.inf.lat.wikihypergraph.module.IntegerManager;

/**
 * An object of this class processes revisions and outputs tuples representing
 * their content.
 * 
 * @author Julian Mendez
 *
 */
public class AxiomSelectorMwRevisionProcessor implements MwRevisionProcessor {

	public static final String PARSING_PROBLEM_MESSAGE = "ERROR";

	private final BufferedWriter output;
	private final Set<String> entities;
	private final boolean traverseAllEntities;
	private AxiomSelectorSnakAndValueVisitor visitor = new AxiomSelectorSnakAndValueVisitor();

	/**
	 * Creates a new revision processor.
	 * 
	 * @param writer
	 *            writer
	 */
	public AxiomSelectorMwRevisionProcessor(Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.output = new BufferedWriter(writer);
		this.entities = null;
		this.traverseAllEntities = true;
	}

	/**
	 * Creates a new revision processor.
	 * 
	 * @param entities
	 *            set of entities
	 * @param writer
	 *            writer
	 */
	public AxiomSelectorMwRevisionProcessor(Set<String> entities, Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (entities == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.output = new BufferedWriter(writer);
		this.entities = entities;
		this.traverseAllEntities = false;
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl, Map<Integer, String> namespaces) {

	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getTitle();
		if ((new IntegerManager()).isValid(title)) {
			if (this.traverseAllEntities || this.entities.contains(title)) {
				try {
					List<SelectorTuple> tuples = new ArrayList<>();
					tuples.addAll(this.visitor.process(mwRevision));
					tuples.forEach(tuple -> {
						try {
							this.output.write(tuple.toString());
							this.output.newLine();
							this.output.flush();
						} catch (IOException ex) {
							throw new UncheckedIOException(ex);
						}
					});
				} catch (IOException ex) {
					throw new UncheckedIOException(ex);
				}
			}
		}
	}

	@Override
	public void finishRevisionProcessing() {
		try {
			if (this.output != null) {
				this.output.newLine();
				this.output.newLine();
				this.output.flush();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
