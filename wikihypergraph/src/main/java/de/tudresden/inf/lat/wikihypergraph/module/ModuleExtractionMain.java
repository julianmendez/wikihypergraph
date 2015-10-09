package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedReader;
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

import de.tudresden.inf.lat.wikihypergraph.main.AllItemsProcessedException;

/**
 * This is the main class to extract a module.
 * 
 * @author Julian Mendez
 */
public class ModuleExtractionMain {

	public static final String WIKIDATAWIKI = "wikidatawiki";

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

	void outputList(Set<String> set, Writer writer) throws IOException {
		for (String key : set) {
			writer.write(key);
			writer.write(" ");
		}
		writer.flush();
	}

	void outputJustification(Map<String, String> map, Writer writer) throws IOException {
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			String value = map.get(key);
			writer.write(key);
			writer.write("\t");
			writer.write(value);
			writer.write("\n");
		}
		writer.flush();
	}

	/**
	 * Processes the most recent dump.
	 * 
	 * @param controller
	 *            dump processing controller
	 * @param listOfItems
	 *            list of items
	 * @param output
	 *            writer that gets the result of the processing
	 */
	public void processDump(DumpProcessingController controller, List<String> listOfItems, Writer output) {

		DependencyMwRevisionProcessor mwRevisionProcessor = new DependencyMwRevisionProcessor();

		// this registers the processor
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);

		try {

			try {

				// this processes the most recent dump file
				controller.processMostRecentMainDump();

			} catch (AllItemsProcessedException e) {
			}

			Map<String, Set<String>> dependencyMap = mwRevisionProcessor.getDependencyMap();

			ReachabilityFinder finder = new ReachabilityFinder(dependencyMap);

			Map<String, String> reachableVertices = new TreeMap<String, String>();
			for (String item : listOfItems) {
				reachableVertices.putAll(finder.getReachabilityMap(item));
			}

			Set<String> module = new TreeSet<String>();
			module.addAll(listOfItems);
			module.addAll(reachableVertices.keySet());

			output.write("Module:\n");
			outputList(module, output);
			output.write("\n\n\n");
			output.write("Justification:\n");
			outputJustification(reachableVertices, output);
			output.write("\n\n\n");
			output.flush();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

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
	 *             if something went wrong with the input/output
	 */
	public void run() throws IOException {
		DumpProcessingController controller = new DumpProcessingController(WIKIDATAWIKI);

		List<String> listOfItems = readListOfItems(this.input);

		processDump(controller, listOfItems, this.output);

		this.output.flush();
	}

	/**
	 * Runs this dump processor using the given arguments.
	 * 
	 * @param args
	 *            arguments (1) input file and (2) output file
	 * @throws IOException
	 *             if something went wrong with the input/output
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
	 *             if something went wrong with the input/output
	 */
	public static void main(String[] args) throws IOException {
		ModuleExtractionMain instance = new ModuleExtractionMain();
		instance.run(args);
	}

}
