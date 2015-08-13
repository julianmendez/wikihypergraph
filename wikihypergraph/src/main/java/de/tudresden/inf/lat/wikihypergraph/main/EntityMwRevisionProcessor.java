package de.tudresden.inf.lat.wikihypergraph.main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * This is MediaWiki revision processor that processes all entities.
 * 
 * @author Julian Mendez
 *
 */
public class EntityMwRevisionProcessor implements MwRevisionProcessor {

	private BufferedWriter output;

	public EntityMwRevisionProcessor(Writer writer) {
		this.output = new BufferedWriter(writer);
	}

	@Override
	public void finishRevisionProcessing() {
		// TODO Auto-generated method stub
		// FIXME
		try {
			this.output.write("finishRevisionProcessing()");
			this.output.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void processRevision(MwRevision arg0) {
		// TODO Auto-generated method stub
		// FIXME
		try {
			this.output.write("processRevision(MwRevision)");
			this.output.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void startRevisionProcessing(String arg0, String arg1,
			Map<Integer, String> arg2) {
		// TODO Auto-generated method stub
		// FIXME
		try {
			this.output
					.write("startRevisionProcessing(String, String, Map<Integer, String>)");
			this.output.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
