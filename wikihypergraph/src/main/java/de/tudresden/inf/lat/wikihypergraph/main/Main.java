package de.tudresden.inf.lat.wikihypergraph.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.WdhgDumpProcessingController;

/**
 * This is the main class to process the dump files.
 * 
 * @author Julian Mendez
 */
public class Main {

	public static final String WIKIDATAWIKI = "wikidatawiki";

	// the bit vector size of pages is only needed when using Wikidata Toolkit
	// v0.4.0 (or previous versions)
	public static final long MAX_PAGES = 100000000;

	// the bit vector size of revisions is only needed when using Wikidata
	// Toolkit v0.4.0 (or previous versions)
	public static final long MAX_REVISIONS = 1000000000;

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

		try {

			// this processes the most recent dump file in JSON format
			controller.processMostRecentJsonDump();

		} catch (NullPointerException e) {

			// this catch block is for debugging purposes, if a
			// NullPointerException is thrown, the exception is shown but the
			// process is not interrupted
			e.printStackTrace();
		}
	}

	/**
	 * Runs this dump processor.
	 *
	 * @throws IOException
	 */
	public void run() throws IOException {
		DumpProcessingController controller = new WdhgDumpProcessingController(
				WIKIDATAWIKI, MAX_PAGES, MAX_REVISIONS);
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
