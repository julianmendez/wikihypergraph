package org.wikidata.wdtk.dumpfiles;

/**
 * This class is a wrapper of {@link DumpProcessingController}, to use Wikidata
 * Toolkit v0.4.0 (or previous versions).
 * 
 * 
 * @author Julian Mendez
 * 
 */
public class WdhgDumpProcessingController extends DumpProcessingController {

	long maxPages;
	long maxRevisions;

	/**
	 * Constructs a new dump processing controller.
	 * 
	 * @param projectName
	 *            project name
	 * @param maxPages
	 *            maximum size of bit vector of pages
	 * @param maxRevisions
	 *            maximum size of bit vector of revisions
	 */
	public WdhgDumpProcessingController(String projectName, long maxPages,
			long maxRevisions) {
		super(projectName);
		this.maxPages = maxPages;
		this.maxRevisions = maxRevisions;
	}

	MwDumpFileProcessor getRevisionDumpFileProcessor() {
		return new MwRevisionDumpFileProcessor(
				new WdhgMwRevisionProcessorBroker(this.maxPages,
						this.maxRevisions));
	}

}
