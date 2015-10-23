package de.tudresden.inf.lat.wikihypergraph.selector;

import java.io.Writer;
import java.util.Set;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;

/**
 * An object of this class selects pages from a set of properties and items and
 * outputs tuples.
 * 
 * @author Julian Mendez
 *
 */
public class AxiomSelectorMain {

	public static final String WIKIDATAWIKI = "wikidatawiki";

	/**
	 * Constructs a new axiom selector.
	 */
	public AxiomSelectorMain() {
	}

	/**
	 * Selects pages from a set of properties and items and outputs tuples.
	 * 
	 * @param entities
	 *            properties and items
	 * @param output
	 *            output
	 */
	public void selectAxioms(Set<String> entities, Writer output) {
		DumpProcessingController controller = new DumpProcessingController(WIKIDATAWIKI);
		AxiomSelectorMwRevisionProcessor mwRevisionProcessor = new AxiomSelectorMwRevisionProcessor(entities, output);
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);
		controller.processMostRecentMainDump();
	}

	/**
	 * Selects pages from a set of properties and items and outputs tuples.
	 * 
	 * @param entities
	 *            properties and items
	 * @param output
	 *            output
	 */
	public void selectAxioms(Writer output) {
		DumpProcessingController controller = new DumpProcessingController(WIKIDATAWIKI);
		AxiomSelectorMwRevisionProcessor mwRevisionProcessor = new AxiomSelectorMwRevisionProcessor(output);
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);
		controller.processMostRecentMainDump();
	}

}
