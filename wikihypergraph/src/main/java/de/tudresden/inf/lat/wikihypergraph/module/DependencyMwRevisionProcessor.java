package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * An object of this class stores the dependencies between pages.
 * 
 * @author Julian Mendez
 *
 */
public class DependencyMwRevisionProcessor implements MwRevisionProcessor {

	private final Map<Integer, Set<Integer>> dependencyMap = new TreeMap<Integer, Set<Integer>>();
	private final BufferedWriter output;
	private final IntegerManager manager;

	/**
	 * Constructs a new processor.
	 */
	public DependencyMwRevisionProcessor(IntegerManager manager) {
		this.output = null;
		this.manager = manager;
	}

	/**
	 * Constructs a new processor.
	 * 
	 * @param writer
	 *            writer
	 */
	public DependencyMwRevisionProcessor(Writer writer, IntegerManager manager) {
		this.output = new BufferedWriter(writer);
		this.manager = manager;
	}

	/**
	 * Returns the dependency map.
	 * 
	 * @return the dependency map
	 */
	public Map<Integer, Set<Integer>> getDependencyMap() {
		return this.dependencyMap;
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl, Map<Integer, String> namespaces) {
		try {
			if (this.output != null) {
				this.output.write("" + (new Date()).toString() + ": start revision processing with site name="
						+ siteName + ", baseUrl=" + baseUrl + " namespaces=" + namespaces);
				this.output.newLine();
				this.output.flush();
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getTitle();
		if (this.manager.isValid(title)) {
			Integer pageIdentifier = this.manager.asNumber(title);
			try {
				Set<String> entities = (new EntityCollector()).collectEntities(mwRevision);
				Set<Integer> entityIdentifiers = new TreeSet<Integer>();
				entityIdentifiers.addAll(this.manager.asNumber(entities));
				this.dependencyMap.put(pageIdentifier, entityIdentifiers);

				if (this.output != null) {
					this.output.write(title);
					this.output.write("\t");
					this.output.write(entities.toString());
					this.output.newLine();
					this.output.flush();
				}
			} catch (IOException e) {
				if (this.output != null) {
					try {
						this.output.write("Could not process '" + title + "'.");
						this.output.newLine();
						this.output.flush();
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		}
	}

	@Override
	public void finishRevisionProcessing() {
		try {
			if (this.output != null) {
				this.output.write("" + (new Date()).toString() + ": finish revision processing.");
				this.output.newLine();
				this.output.newLine();
				this.output.flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
