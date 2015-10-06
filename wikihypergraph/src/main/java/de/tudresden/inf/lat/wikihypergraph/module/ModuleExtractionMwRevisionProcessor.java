package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * An object of this class extracts a 'module'.
 * 
 * @author Julian Mendez
 *
 */
public class ModuleExtractionMwRevisionProcessor implements MwRevisionProcessor {

	private final Set<String> module = new TreeSet<String>();
	private final Set<String> visited = new TreeSet<String>();
	private final Set<String> toVisit = new TreeSet<String>();
	private final List<Pair> followers = new ArrayList<Pair>();
	private final Set<String> items = new TreeSet<String>();
	private BufferedWriter output;

	class Pair {

		private String a;
		private String b;

		public Pair(String a, String b) {
			if (a == null || b == null) {
				throw new IllegalArgumentException("Parameters cannot be null.");
			}
			this.a = a;
			this.b = b;
		}

		public String getA() {
			return this.a;
		}

		public String getB() {
			return this.b;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Pair) {
				Pair other = (Pair) obj;
				return this.a.equals(other.a) && this.b.equals(other.b);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return a == null ? 0 : a.hashCode() + (0x1F * (b == null ? 0 : b.hashCode()));
		}

		public String toString() {
			return a + " " + b;
		}

	}

	public ModuleExtractionMwRevisionProcessor(Collection<String> items, Writer writer) {
		this.output = new BufferedWriter(writer);
		this.items.addAll(items);
	}

	public void configure(Set<String> initialSet) {
		this.module.clear();
		this.visited.clear();
		this.toVisit.clear();
		this.followers.clear();
		this.toVisit.addAll(initialSet);
	}

	public void visit(MwRevision mwRevision) throws JsonProcessingException, IOException {
		String origPage = mwRevision.getTitle();
		this.visited.add(origPage);

		Set<String> pages = (new EntityCollector()).collectEntities(mwRevision);
		for (String page : pages) {
			if (!this.visited.contains(page)) {
				this.toVisit.add(page);
				this.followers.add(new Pair(origPage, page));
			}
		}
	}

	public Set<String> getVisited() {
		return this.visited;
	}

	public Set<String> getToVisit() {
		return this.toVisit;
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl, Map<Integer, String> namespaces) {
		try {
			this.output.write("" + (new Date()).toString() + ": start revision processing with site name=" + siteName
					+ ", baseUrl=" + baseUrl + " namespaces=" + namespaces);
			this.output.newLine();
			this.output.flush();
			configure(this.items);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getTitle();
		try {
			if (this.toVisit.contains(title)) {
				this.output.write(title);
				Set<String> entities = (new EntityCollector()).collectEntities(mwRevision);
				this.output.write("\t" + entities.toString());
				this.output.newLine();
				this.output.flush();
				visit(mwRevision);
			}
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
			this.output.write(this.followers.toString());
			this.output.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
