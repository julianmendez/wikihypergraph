package de.tudresden.inf.lat.wikihypergraph.selector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

import de.tudresden.inf.lat.wikihypergraph.module.IntegerManager;

public class AxiomSelectorMwRevisionProcessor implements MwRevisionProcessor {

	public static final String PARSING_PROBLEM_MESSAGE = "ERROR";

	private final BufferedWriter output;
	private AxiomSelectorSnakAndValueVisitor visitor = new AxiomSelectorSnakAndValueVisitor();

	public AxiomSelectorMwRevisionProcessor(Set<String> entities, Writer writer) {
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

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getTitle();
		if ((new IntegerManager()).isValid(title)) {
			try {
				List<SelectorTuple> tuples = new ArrayList<SelectorTuple>();
				tuples.addAll(this.visitor.process(mwRevision));
				for (SelectorTuple tuple : tuples) {
					this.output.write(tuple.toString());
					this.output.newLine();
				}
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
