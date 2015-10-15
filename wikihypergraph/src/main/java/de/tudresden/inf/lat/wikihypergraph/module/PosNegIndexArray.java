package de.tudresden.inf.lat.wikihypergraph.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * An object of this class models an array that stores sets of integers at
 * possibly negative positions.
 * 
 * @author Julian Mendez
 *
 */
public class PosNegIndexArray implements AdjacencyMap {

	private List<Set<Integer>> negative = new ArrayList<Set<Integer>>();
	private List<Set<Integer>> positive = new ArrayList<Set<Integer>>();

	/**
	 * Creates a new array.
	 */
	public PosNegIndexArray() {
	}

	void set(List<Set<Integer>> list, int index, Set<Integer> element) {
		while (list.size() < index) {
			list.add(new TreeSet<Integer>());
		}
		if (index == list.size()) {
			list.add(element);
		} else {
			list.set(index, element);
		}
	}

	public int size() {
		return this.negative.size() + this.positive.size();
	}

	public boolean isEmpty() {
		return this.negative.isEmpty() && this.positive.isEmpty();
	}

	@Override
	public Set<Integer> get(Integer index) {
		if (index < 0) {
			return this.negative.get((-1) * index);
		} else {
			return this.positive.get(index);
		}
	}

	public Set<Integer> set(Integer index, Set<Integer> value) {
		if (index < 0) {
			set(this.negative, (-1) * index, value);
		} else {
			set(this.positive, index, value);
		}
		return value;
	}

	public void clear() {
		this.negative.clear();
		this.positive.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof PosNegIndexArray) {
			PosNegIndexArray other = (PosNegIndexArray) obj;
			return this.negative.equals(other.negative) && this.positive.equals(other.positive);
		}
		return false;
	}

	public int hashCode() {
		return this.negative.hashCode() + 0x1F * this.positive.hashCode();
	}

	public String toString() {
		return this.negative.toString() + "\n" + this.positive.toString();
	}

}
