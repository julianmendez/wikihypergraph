package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;

/**
 * An object of this class extracts a so-called module, which is set of
 * properties and items that are ultimately referred starting from a given set.
 * 
 * @author Julian Mendez
 * 
 */
public class ModuleExtractionMain {

	public static final String WIKIDATAWIKI = "wikidatawiki";

	final static char NEW_LINE_CHAR = '\n';
	final static char TAB_CHAR = '\t';

	/**
	 * Constructs a new module extractor.
	 */
	public ModuleExtractionMain() {
	}

	/**
	 * Outputs the module.
	 * 
	 * @param set
	 *            module
	 * @param writer
	 *            writer
	 * @param manager
	 *            integer manager
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	void outputList(Set<Integer> set, Writer writer, IntegerManager manager) throws IOException {
		for (Integer key : set) {
			writer.write(manager.asString(key));
			writer.write(NEW_LINE_CHAR);
		}
		writer.flush();
	}

	/**
	 * Downloads all dependencies.
	 * 
	 * @param output
	 *            writer that gets the result of the processing
	 */
	void downloadDependencies(Writer output) {
		DumpProcessingController controller = new DumpProcessingController(WIKIDATAWIKI);
		DependencyPropertiesMwRevisionProcessor mwRevisionProcessor = new DependencyPropertiesMwRevisionProcessor(
				output);
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);
		controller.processMostRecentMainDump();
	}

	/**
	 * Writes the module.
	 * 
	 * @param setOfItems
	 *            set of items
	 * @param dependencyMap
	 *            dependency map
	 * @param output
	 *            output
	 * @param manager
	 *            integer manager
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	void writeModule(Set<Integer> setOfItems, AdjacencyMap dependencyMap, Writer output, IntegerManager manager)
			throws IOException {
		ReachabilityFinder finder = new ReachabilityFinder(dependencyMap);
		Set<Integer> module = new TreeSet<Integer>();
		Map<Integer, Integer> reachableVertices = new TreeMap<Integer, Integer>();
		for (Integer itemIdentifier : setOfItems) {
			reachableVertices.putAll(finder.getReachabilityMap(itemIdentifier));
			module.add(itemIdentifier);
		}
		module.addAll(reachableVertices.keySet());

		outputList(module, output, manager);
		output.write(NEW_LINE_CHAR);
		output.write(NEW_LINE_CHAR);
		output.write(NEW_LINE_CHAR);
		output.flush();
	}

	/**
	 * Runs this dump processor.
	 * 
	 * @param setOfItems
	 *            set of items
	 * @param output
	 *            output
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public void extractModule(Set<String> setOfItems, String temporaryFileName, Writer output) throws IOException {

		File propertiesFile = new File(temporaryFileName);
		if (!propertiesFile.exists()) {
			downloadDependencies(new FileWriter(propertiesFile));
		}

		IntegerManager manager = new IntegerManager();
		Set<Integer> setOfItemIdentifiers = new TreeSet<Integer>();
		setOfItemIdentifiers.addAll(manager.asNumber(setOfItems));
		MapOnFile dependencyMap = new MapOnFile(new FileReader(propertiesFile));
		writeModule(setOfItemIdentifiers, dependencyMap, output, manager);
	}

}
