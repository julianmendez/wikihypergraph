package org.wikidata.wdtk.dumpfiles;

import org.wikidata.wdtk.storage.datastructures.BitVectorImpl;

/**
 * This class is a wrapper of {@link MwRevisionProcessorBroker}, to use Wikidata
 * Toolkit v0.4.0 (or previous versions).
 * 
 * @author Julian Mendez
 * 
 */
public class WdhgMwRevisionProcessorBroker extends MwRevisionProcessorBroker {

	/**
	 * Constructs a new revision processor broker.
	 * 
	 * @param maxPages
	 *            maximum size of bit vector of pages
	 * @param maxRevisions
	 *            maximum size of bit vector of revisions
	 */

	public WdhgMwRevisionProcessorBroker(long maxPages, long maxRevisions) {
		super();
		encounteredPages = new BitVectorImpl(maxPages);
		encounteredRevisions = new BitVectorImpl(maxRevisions);
	}

}
