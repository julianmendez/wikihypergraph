package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;

/**
 * This is the main class to extract a module.
 * 
 * @author Julian Mendez
 */
public class ModuleExtractionMain {

	public static final String WIKIDATAWIKI = "wikidatawiki";

	public static final String TEMPORARY_FILE = "dependencies.properties";

	public static final String HELP = "Parameters: <input file> <output file>" //
			+ "\n" //
			+ "\nwhere" //
			+ "\n <input file>  : file name of list of items" //
			+ "\n <output file> : file name of output file" //
			+ "\n" //
			+ "\n";

	// this is the default list of items to process
	private Reader input = null;

	// this is the default output file name
	private Writer output = null;

	ModuleExtractionMain() {
	}

	/**
	 * Creates a new module extractor.
	 * 
	 * @param input
	 *            input
	 * @param output
	 *            output
	 */
	public ModuleExtractionMain(Reader input, Writer output) {
		this.input = input;
		this.output = output;
	}

	public Reader getInput() {
		return input;
	}

	public void setInput(Reader input) {
		this.input = input;
	}

	public Writer getOutput() {
		return output;
	}

	public void setOutput(Writer output) {
		this.output = output;
	}

	void outputList(Set<Integer> set, Writer writer, IntegerManager manager) throws IOException {
		for (Integer key : set) {
			writer.write(manager.asString(key));
			writer.write(" ");
		}
		writer.flush();
	}

	void outputJustification(Map<Integer, Integer> map, Writer writer, IntegerManager manager) throws IOException {
		Set<Integer> keySet = map.keySet();
		for (Integer key : keySet) {
			String value = manager.asString(map.get(key));
			writer.write(manager.asString(key));
			writer.write("\t");
			writer.write(value);
			writer.write("\n");
		}
		writer.flush();
	}

	/**
	 * Downloads all dependencies.
	 * 
	 * @param output
	 *            writer that gets the result of the processing
	 */
	public void downloadDependencies(Writer output) {
		DumpProcessingController controller = new DumpProcessingController(WIKIDATAWIKI);
		DependencyPropertiesMwRevisionProcessor mwRevisionProcessor = new DependencyPropertiesMwRevisionProcessor(
				output);
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);
		controller.processMostRecentMainDump();
	}

	public void writeModule(Set<Integer> setOfItems, AdjacencyMap dependencyMap, Writer output, IntegerManager manager)
			throws IOException {
		ReachabilityFinder finder = new ReachabilityFinder(dependencyMap);
		Set<Integer> module = new TreeSet<Integer>();
		Map<Integer, Integer> reachableVertices = new TreeMap<Integer, Integer>();
		for (Integer itemIdentifier : setOfItems) {
			reachableVertices.putAll(finder.getReachabilityMap(itemIdentifier));
			module.add(itemIdentifier);
		}
		module.addAll(reachableVertices.keySet());

		output.write("Module:\n");
		outputList(module, output, manager);
		output.write("\n\n\n");
		output.write("Justification:\n");
		outputJustification(reachableVertices, output, manager);
		output.write("\n\n\n");
		output.flush();
	}

	List<String> readListOfItems(Reader reader0) throws IOException {
		BufferedReader reader = new BufferedReader(reader0);
		List<String> ret = new ArrayList<String>();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			String item = (new StringTokenizer(line)).nextToken();
			ret.add(item);
		}
		return ret;
	}

	/**
	 * Runs this dump processor.
	 *
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public void run() throws IOException {

		File propertiesFile = new File(TEMPORARY_FILE);
		if (!propertiesFile.exists()) {
			downloadDependencies(new FileWriter(propertiesFile));
		}

		IntegerManager manager = new IntegerManager();
		List<String> setOfItems = readListOfItems(this.input);
		Set<Integer> setOfItemIdentifiers = new TreeSet<Integer>();
		setOfItemIdentifiers.addAll(manager.asNumber(setOfItems));
		writeModule(setOfItemIdentifiers, new MapOnProperties(TEMPORARY_FILE), this.output, manager);
	}

	/**
	 * Runs this dump processor using the given arguments.
	 * 
	 * @param args
	 *            arguments (1) input file and (2) output file
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */

	public void run(String args[]) throws IOException {
		if (args.length == 2) {
			this.input = new FileReader(args[0]);
			this.output = new FileWriter(args[1]);
			run();
			this.input.close();
			this.output.flush();
			this.output.close();
		} else {
			System.out.println(HELP);
		}
	}

	/**
	 * Runs this dump processor.
	 *
	 * @param args
	 *            arguments
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public static void main(String[] args) throws IOException {
		ModuleExtractionMain instance = new ModuleExtractionMain();
		instance.run(args);
	}

}
