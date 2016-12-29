package de.tudresden.inf.lat.wikihypergraph.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import de.tudresden.inf.lat.wikihypergraph.module.ModuleExtractionMain;
import de.tudresden.inf.lat.wikihypergraph.selector.AxiomSelectorMain;

/**
 * This is the main class. An object of this class reads a list of properties
 * and items, extracts a module, and outputs tuples with the information in the
 * pages in the module.
 * 
 * @author Julian Mendez
 */
public class Main {

	public static final String HELP = "Parameters: <input file> <output file>" //
			+ "\n" //
			+ "\nwhere" //
			+ "\n <input file>  : file name of list of properties and items" //
			+ "\n <output file> : file name of output file" //
			+ "\n" //
			+ "\n" //
			+ "\nThis program reads an input file containing a set of properties and items." //
			+ "\nThis input file needs to have one property or item per line." //
			+ "\nIf the read set is non-empty, the processor extracts a module." //
			+ "\nIf the read set is empty, the processor assumes that the module comprises the whole domain." //
			+ "\nThe processor outputs the tuples based on the elements of the module." //
			+ "\n" //
			+ "\n" //
			+ "\n";

	public static final String TEMPORARY_MODULE_FILE_NAME = "extracted-module.txt";
	public static final String TEMPORARY_DEPENDENCY_FILE_NAME = "dependencies.properties";

	// this is the default list of items to process
	private Reader input = null;

	// this is the default output file name
	private Writer output = null;

	Main() {
	}

	/**
	 * Constructs a new processor.
	 * 
	 * @param input
	 *            input
	 * @param output
	 *            output
	 */
	public Main(Reader input, Writer output) {
		this.input = input;
		this.output = output;
	}

	/**
	 * Returns the input.
	 * 
	 * @return the input
	 */
	public Reader getInput() {
		return input;
	}

	/**
	 * Sets the input.
	 * 
	 * @param input
	 *            input
	 */
	public void setInput(Reader input) {
		this.input = input;
	}

	/**
	 * Returns the output.
	 * 
	 * @return the output
	 */
	public Writer getOutput() {
		return output;
	}

	/**
	 * Sets the output.
	 * 
	 * @param output
	 *            output
	 */
	public void setOutput(Writer output) {
		this.output = output;
	}

	/**
	 * Reads a list of items.
	 * 
	 * @param reader
	 *            reader
	 * @return a list of items
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	Set<String> readEntities(Reader reader) throws IOException {
		BufferedReader input = new BufferedReader(reader);
		Set<String> ret = new TreeSet<>();
		input.lines() //
				.filter(line -> !line.trim().isEmpty()) //
				.forEach(line -> {
					String item = (new StringTokenizer(line)).nextToken();
					ret.add(item);
				});
		return ret;
	}

	/**
	 * Writes entities.
	 * 
	 * @param list
	 *            list
	 * @param writer
	 *            writer
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	void writeEntities(Collection<String> list, Writer writer) throws IOException {
		for (String entity : list) {
			writer.write(entity);
			writer.write("\n");
		}
		writer.flush();
		writer.close();

	}

	/**
	 * Extracts a module and runs the selector.
	 * 
	 * @param setOfEntities
	 *            set of entities
	 * @param writer
	 *            writer
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public void run(Set<String> setOfEntities, Writer writer) throws IOException {
		AxiomSelectorMain axiomSelector = new AxiomSelectorMain();
		if (setOfEntities.isEmpty()) {
			axiomSelector.selectAxioms(writer);
		} else {
			ModuleExtractionMain moduleExtractor = new ModuleExtractionMain(TEMPORARY_DEPENDENCY_FILE_NAME);
			Collection<String> module = moduleExtractor.extractModule(setOfEntities);
			writeEntities(module, new FileWriter(TEMPORARY_MODULE_FILE_NAME));
			Set<String> moduleAsSet = new HashSet<>();
			moduleAsSet.addAll(module);
			axiomSelector.selectAxioms(moduleAsSet, writer);
		}
		writer.flush();
	}

	/**
	 * Extracts a module and runs the selector.
	 *
	 * @param reader
	 *            reader
	 * @param writer
	 *            writer
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public void run(Reader reader, Writer writer) throws IOException {
		Set<String> listOfItems = readEntities(reader);
		run(listOfItems, writer);
	}

	/**
	 * Extracts a module and runs the selector on the given arguments.
	 * 
	 * @param args
	 *            arguments (1) input file and (2) output file
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public void run(String args[]) throws IOException {
		if (args.length == 2) {
			this.input = new FileReader(args[0]);
			this.output = new FileWriter(args[1]);
			run(this.input, this.output);
			this.input.close();
			this.output.flush();
			this.output.close();
		} else {
			System.out.println(HELP);
		}
	}

	/**
	 * Starts the processor.
	 *
	 * @param args
	 *            arguments
	 * @throws IOException
	 *             if something goes wrong with I/O
	 */
	public static void main(String[] args) throws IOException {
		Main instance = new Main();
		instance.run(args);
	}

}
