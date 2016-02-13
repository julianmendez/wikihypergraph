package de.tudresden.inf.lat.wikihypergraph.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * This is a MediaWiki revision processor that processes all entities. This
 * class is mostly an example of how the Wikidata revisions are processed. The
 * output is a log file of all the visited entities.
 * 
 * @author Julian Mendez
 *
 */
public class ExampleEntityMwRevisionProcessor implements MwRevisionProcessor {

	private BufferedWriter output;

	/**
	 * Constructs a new MediaWiki revision processor.
	 *
	 * @param writer
	 *            writer where the result of the processing is written
	 */
	public ExampleEntityMwRevisionProcessor(Writer writer) {
		this.output = new BufferedWriter(writer);
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl, Map<Integer, String> namespaces) {
		try {
			this.output.write("" + (new Date()).toString() + ": start revision processing with site name=" + siteName
					+ ", baseUrl=" + baseUrl + " namespaces=" + namespaces);
			this.output.newLine();
			this.output.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		try {
			this.output.write("" + (new Date()).toString() + ": pageId=" + mwRevision.getPageId() + " : "
					+ mwRevision.getTitle());
			this.output.newLine();
			this.output.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void finishRevisionProcessing() {
		try {
			this.output.write("" + (new Date()).toString() + ": finish revision processing.");
			this.output.newLine();
			this.output.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
