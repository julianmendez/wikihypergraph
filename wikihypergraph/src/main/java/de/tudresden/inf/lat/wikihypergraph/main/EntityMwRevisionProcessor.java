package de.tudresden.inf.lat.wikihypergraph.main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
public class EntityMwRevisionProcessor implements MwRevisionProcessor {

	private BufferedWriter output;
	private final Set<String> items = new TreeSet<String>();
	private final Set<String> processedItems = new TreeSet<String>();

	/**
	 * Constructs a new MediaWiki revision processor.
	 *
	 * @param writer
	 *            writer where the result of the processing is written
	 */
	public EntityMwRevisionProcessor(Collection<String> items, Writer writer) {
		this.output = new BufferedWriter(writer);
		this.items.addAll(items);
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces) {
		try {
			this.output.write("" + (new Date()).toString()
					+ ": start revision processing with site name=" + siteName
					+ ", baseUrl=" + baseUrl + " namespaces=" + namespaces);
			this.output.newLine();
			this.output.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method processes an already verified revision, i.e. a revision that
	 * is contained in the list of items to be processed.
	 * 
	 * @param mwRevision
	 *            revision
	 * @throws IOException
	 *             if something went wrong with the input/output
	 */
	void processVerifiedRevision(MwRevision mwRevision) throws IOException {
		this.output.write("" + (new Date()).toString() + ": pageId="
				+ mwRevision.getPageId() + " : " + mwRevision.getTitle());
		this.output.newLine();
		this.output.flush();
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		try {
			String title = mwRevision.getTitle();
			if (this.items.contains(title)) {
				this.processedItems.add(title);
				processVerifiedRevision(mwRevision);
				if (this.processedItems.equals(this.items)) {
					throw new AllItemsProcessedException(
							"All items have been processed.");
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void finishRevisionProcessing() {
		try {
			this.output.write("" + (new Date()).toString()
					+ ": finish revision processing.");
			this.output.newLine();
			this.output.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
