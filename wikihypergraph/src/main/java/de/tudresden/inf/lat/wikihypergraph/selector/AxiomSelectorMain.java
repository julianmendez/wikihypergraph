package de.tudresden.inf.lat.wikihypergraph.selector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;

public class AxiomSelectorMain {

	public static final String WIKIDATAWIKI = "wikidatawiki";

	public static final String HELP = "Parameters: <input file> <output file>" //
			+ "\n" //
			+ "\nwhere" //
			+ "\n <input file>  : file name of list of entities" //
			+ "\n <output file> : file name of output file" //
			+ "\n" //
			+ "\n";

	private Reader input = null;
	private Writer output = null;

	AxiomSelectorMain() {
	}

	public AxiomSelectorMain(Reader input, Writer output) {
		if (input == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (output == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.input = input;
		this.output = output;
	}

	public Reader getInput() {
		return input;
	}

	public void setInput(Reader input) {
		if (input == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.input = input;
	}

	public Writer getOutput() {
		return output;
	}

	public void setOutput(Writer output) {
		if (output == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.output = output;
	}

	public void processTuples(Set<String> entities, Writer output) {
		DumpProcessingController controller = new DumpProcessingController(WIKIDATAWIKI);
		AxiomSelectorMwRevisionProcessor mwRevisionProcessor = new AxiomSelectorMwRevisionProcessor(entities, output);
		controller.registerMwRevisionProcessor(mwRevisionProcessor, null, true);
		controller.processMostRecentMainDump();
	}

	List<String> readListOfEntities(Reader reader0) throws IOException {
		BufferedReader reader = new BufferedReader(reader0);
		List<String> ret = new ArrayList<String>();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			StringTokenizer stok = new StringTokenizer(line);
			while (stok.hasMoreTokens()) {
				ret.add(stok.nextToken());
			}
		}
		return ret;
	}

	public void run() throws IOException {
		Set<String> setOfEntities = new TreeSet<String>();
		setOfEntities.addAll(readListOfEntities(this.input));
		processTuples(setOfEntities, this.output);
	}

	public void run(String args[]) throws IOException {
		if (args.length == 2) {
			this.input = new FileReader(args[0]);
			this.output = new FileWriter(args[1]);
			run();
			this.input.close();
			this.output.flush();
			this.output.close();
		} else {
			System.out.println(HELP);
		}
	}

	public static void main(String[] args) throws IOException {
		AxiomSelectorMain instance = new AxiomSelectorMain();
		instance.run(args);
	}

}
