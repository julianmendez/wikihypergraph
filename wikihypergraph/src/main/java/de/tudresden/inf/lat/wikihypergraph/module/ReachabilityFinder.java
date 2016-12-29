package de.tudresden.inf.lat.wikihypergraph.module;

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

	/**
	 * Constructs a new module extractor.
	 * 
	 * @param adjacencyMap
	 *            map of dependencies
	 */
	public ReachabilityFinder(AdjacencyMap adjacencyMap) {
		this.adjacencyMap = adjacencyMap;
	}

	/**
	 * Returns the map of dependencies.
	 * 
	 * @return the map of dependencies
	 */
	public AdjacencyMap getAdjacencyMap() {
		return this.adjacencyMap;
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
		Map<Integer, Integer> ret = new TreeMap<>();
		Set<Integer> visited = new TreeSet<>();
		Set<Integer> toVisit = new TreeSet<>();
		toVisit.add(origin);
		while (!toVisit.isEmpty()) {
			Integer current = toVisit.iterator().next();
			visited.add(current);
			toVisit.remove(current);

			Set<Integer> adjacentSet = this.adjacencyMap.get(current);
			if (adjacentSet != null) {
				adjacentSet.forEach(adjacent -> {
					if (!visited.contains(adjacent)) {
						toVisit.add(adjacent);
						ret.put(adjacent, current);
					}
				});
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
