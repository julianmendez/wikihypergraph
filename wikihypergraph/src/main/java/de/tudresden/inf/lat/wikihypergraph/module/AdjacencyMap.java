package de.tudresden.inf.lat.wikihypergraph.module;

import java.util.Set;

/**
 * This models a map of adjacency.
 * 
 * @author Julian Mendez
 *
 */
public interface AdjacencyMap {

	/**
	 * Returns the set of vertices adjacent to the given vertex.
	 * 
	 * @param vertex
	 *            vertex
	 * @return the set of vertices adjacent to the given vertex
	 */
	Set<Integer> get(Integer vertex);

}
