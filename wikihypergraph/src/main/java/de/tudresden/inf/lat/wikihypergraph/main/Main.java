package de.tudresden.inf.lat.wikihypergraph.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * This is the main class to process the dump files.
 * 
 * @author Julian Mendez
 */
public class Main {

	public static final String WIKIDATAWIKI = "wikidatawiki";

	// this is the default output file name
	private String outputFileName = "output.txt";

	public Main() {
	}

	/**
	 * Processes the most recent dump.
	 * 
	 * @param controller
	 *            dump processing controller
	 * @param output
	 *            writer that gets the result of the processing
	 */
	public void processDump(DumpProcessingController controller, Writer output) {
		MwRevisionProcessor mwRevisionProcessor = new EntityMwRevisionProcessor(
				output);

		// this registers the processor
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);

		// this processes the most recent dump file
		controller.processMostRecentMainDump();

	}

	/**
	 * Runs this dump processor.
	 *
	 * @throws IOException
	 *             if something went wrong with the input/output streams.
	 */
	public void run() throws IOException {
		DumpProcessingController controller = new DumpProcessingController(
				WIKIDATAWIKI);
		FileWriter writer = new FileWriter(this.outputFileName);

		processDump(controller, writer);

		writer.flush();
		writer.close();
	}

	/**
	 * Runs this dump processor. Parameters are ignored.
	 *
	 * @param args
	 *            arguments
	 * @throws IOException
	 *             if something went wrong with the input/output streams.
	 */
	public static void main(String[] args) throws IOException {
		Main instance = new Main();
		instance.run();
	}

}
