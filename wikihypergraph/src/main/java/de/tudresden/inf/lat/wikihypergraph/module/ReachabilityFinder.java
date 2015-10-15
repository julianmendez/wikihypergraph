package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * An object of this class represents a directed graph that can compute the set
 * of vertices reachable from a given vertex.
 * 
 * @author Julian Mendez
 *
 */
public class ReachabilityFinder {

	private final AdjacencyMap adjacencyMap;
	private final Writer output;

	/**
	 * Constructs a new module extractor.
	 * 
	 * @param adjacencyMap
	 *            map of dependencies
	 */
	public ReachabilityFinder(AdjacencyMap adjacencyMap) {
		this.adjacencyMap = adjacencyMap;
		this.output = null;
	}

	/**
	 * Constructs a new module extractor.
	 * 
	 * @param adjacencyMap
	 *            map of dependencies
	 */
	public ReachabilityFinder(AdjacencyMap adjacencyMap, Writer output) {
		this.adjacencyMap = adjacencyMap;
		this.output = output;
	}

	/**
	 * Returns the map of dependencies.
	 * 
	 * @return the map of dependencies
	 */
	public AdjacencyMap getAdjacencyMap() {
		return this.adjacencyMap;
	}

	void log(Integer current) {
		if (this.output != null) {
			try {
				IntegerManager manager = new IntegerManager();
				this.output.write(" " + manager.asString(current));
				this.output.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Returns a map of vertices reachable from the given vertex. Each key is a
	 * reachable vertex and its value is the vertex that included the key into
	 * the map. The origin vertex is not included in the keys.
	 * 
	 * @param origin
	 *            starting vertex
	 * @return a map of vertices reachable from the given vertex
	 */
	public Map<Integer, Integer> getReachabilityMap(Integer origin) {
		Map<Integer, Integer> ret = new TreeMap<Integer, Integer>();
		Set<Integer> visited = new TreeSet<Integer>();
		Set<Integer> toVisit = new TreeSet<Integer>();
		toVisit.add(origin);
		while (!toVisit.isEmpty()) {
			Integer current = toVisit.iterator().next();
			visited.add(current);
			toVisit.remove(current);
			log(current);

			Set<Integer> adjacentSet = this.adjacencyMap.get(current);
			if (adjacentSet != null) {
				for (Integer adjacent : adjacentSet) {
					if (!visited.contains(adjacent)) {
						toVisit.add(adjacent);
						ret.put(adjacent, current);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Returns a set of vertices reachable from the given vertex. The origin
	 * vertex is not included in the set.
	 * 
	 * @param origin
	 *            starting vertex
	 * @return a set of vertices reachable from the given vertex
	 */
	public Set<Integer> getReachabilitySet(Integer origin) {
		return getReachabilityMap(origin).keySet();
	}

}
