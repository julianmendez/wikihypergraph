package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * An object of this class stores the dependencies between pages.
 * 
 * @author Julian Mendez
 *
 */
public class DependencyMwRevisionProcessor implements MwRevisionProcessor {

	private final Map<String, Set<String>> dependencyMap = new TreeMap<String, Set<String>>();
	private BufferedWriter output;

	/**
	 * Constructs a new processor.
	 */
	public DependencyMwRevisionProcessor(Writer writer) {
		this.output = new BufferedWriter(writer);
	}

	/**
	 * Returns the dependency map.
	 * 
	 * @return the dependency map
	 */
	public Map<String, Set<String>> getDependencyMap() {
		return this.dependencyMap;
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl, Map<Integer, String> namespaces) {
		try {
			this.output.write("" + (new Date()).toString() + ": start revision processing with site name=" + siteName
					+ ", baseUrl=" + baseUrl + " namespaces=" + namespaces);
			this.output.newLine();
			this.output.flush();

			this.dependencyMap.clear();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getTitle();
		try {
			Set<String> entities = (new EntityCollector()).collectEntities(mwRevision);
			this.dependencyMap.put(title, entities);

			this.output.write(title);
			this.output.write("\t");
			this.output.write(entities.toString());
			this.output.newLine();
			this.output.flush();
		} catch (IOException e) {
			System.out.println("Cannot process '" + title + "'.");
		}
	}

	@Override
	public void finishRevisionProcessing() {
		try {
			this.output.write("" + (new Date()).toString() + ": finish revision processing.");
			this.output.newLine();
			this.output.newLine();
			this.output.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
