package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

/**
 * An object of this class stores the dependencies between pages in a
 * {@link Properties} file.
 * 
 * @author Julian Mendez
 *
 */
public class DependencyPropertiesMwRevisionProcessor implements MwRevisionProcessor {

	private final BufferedWriter output;

	/**
	 * Constructs a new processor.
	 */
	public DependencyPropertiesMwRevisionProcessor(Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		this.output = new BufferedWriter(writer);
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl, Map<Integer, String> namespaces) {
		try {
			if (this.output != null) {
				this.output.write("# " + (new Date()).toString() + ": start revision processing with site name="
						+ siteName + ", baseUrl=" + baseUrl + " namespaces=" + namespaces);
				this.output.newLine();
				this.output.write("#");
				this.output.newLine();
				this.output.flush();
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	String asString(Set<String> set) {
		StringBuffer sbuf = new StringBuffer();
		boolean firstTime = true;
		for (String str : set) {
			if (!firstTime) {
				sbuf.append(" ");
			}
			firstTime = false;
			sbuf.append(str);
		}
		return sbuf.toString();
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getTitle();
		if ((new IntegerManager()).isValid(title)) {
			Set<String> entities = new TreeSet<String>();

			try {
				entities.addAll((new EntityCollector()).collectEntities(mwRevision));

			} catch (IOException e) {
				entities.add("ERROR");
			}

			try {
				this.output.write(title);
				this.output.write(" = ");
				this.output.write(asString(entities));
				this.output.newLine();
				this.output.flush();

			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void finishRevisionProcessing() {
		try {
			if (this.output != null) {
				this.output.write("#");
				this.output.newLine();
				this.output.write("# " + (new Date()).toString() + ": finish revision processing.");
				this.output.newLine();
				this.output.newLine();
				this.output.flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
