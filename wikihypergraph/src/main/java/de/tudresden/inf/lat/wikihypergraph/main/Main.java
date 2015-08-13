package de.tudresden.inf.lat.wikihypergraph.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;

/**
 * @author Julian Mendez
 */
public class Main {

	public static final String WIKIDATAWIKI = "wikidatawiki";

	private String outputFileName = "output.txt";

	public Main() {
	}

	/**
	 * Processes the most recent dump.
	 * 
	 * @param controller
	 *            dump processing controller
	 * @param output
	 */
	public void processDump(DumpProcessingController controller, Writer output) {
		EntityMwRevisionProcessor mwRevisionProcessor = new EntityMwRevisionProcessor(
				output);

		// This registers the processor
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);

		// This processes the most recent dump
		controller.processAllRecentRevisionDumps();
	}

	public void run() throws IOException {
		DumpProcessingController controller = new DumpProcessingController(
				WIKIDATAWIKI);
		FileWriter writer = new FileWriter(this.outputFileName);

		processDump(controller, writer);

		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		Main instance = new Main();
		instance.run();
	}

}
