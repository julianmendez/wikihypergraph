package de.tudresden.inf.lat.wikihypergraph.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * This is the main class to process the dump files.
 * 
 * @author Julian Mendez
 */
public class Main {

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

	Main() {
	}

	public Main(Reader input, Writer output) {
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

	/**
	 * Processes the most recent dump.
	 * 
	 * @param controller
	 *            dump processing controller
	 * @param output
	 *            writer that gets the result of the processing
	 */
	public void processDump(DumpProcessingController controller,
			List<String> listOfItems, Writer output) {
		MwRevisionProcessor mwRevisionProcessor = new EntityMwRevisionProcessor(
				listOfItems, output);

		// this registers the processor
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);

		try {

			// this processes the most recent dump file
			controller.processMostRecentMainDump();

		} catch (AllItemsProcessedException e) {

			// if all items have been already processed, ...
			try {
				output.write(e.getMessage());
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

			// ... finish the revision processing
			mwRevisionProcessor.finishRevisionProcessing();
		}

	}

	List<String> readListOfItems(Reader reader0) throws IOException {
		BufferedReader reader = new BufferedReader(reader0);
		List<String> ret = new ArrayList<String>();
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
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
		DumpProcessingController controller = new DumpProcessingController(
				WIKIDATAWIKI);

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
		Main instance = new Main();
		instance.run(args);
	}

}
