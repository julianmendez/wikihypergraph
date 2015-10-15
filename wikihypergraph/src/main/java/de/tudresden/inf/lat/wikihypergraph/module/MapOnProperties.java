package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * An object of this class models an unmodifiable map that reads its entries
 * from a {@link Properties} file.
 * 
 * @author Julian Mendez
 *
 */
public class MapOnProperties implements AdjacencyMap {

	public static final String EQUALS_SIGN = "=";

	private final String fileName;

	private Map<String, String> cache = new HashMap<String, String>();

	/**
	 * Constructs a new map.
	 * 
	 * @param fileName
	 *            file name of Properties file
	 */
	public MapOnProperties(String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.fileName = fileName;
	}

	/**
	 * Returns the file name of the dependency file.
	 * 
	 * @return the file name of the dependency file
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Returns the value for the given key. This method does not use the cache.
	 * 
	 * @param key
	 *            key
	 * @return the value for the given key
	 */
	public String getX(String key) {
		String result = null;
		boolean found = false;
		try {
			BufferedReader input = new BufferedReader(new FileReader(this.fileName));
			for (String line = input.readLine(); line != null && !found; line = input.readLine()) {
				if (getKey(line).equals(key)) {
					found = true;
					result = getValue(line);
				}
			}
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Returns the value for the given key. This method uses the cache.
	 * 
	 * @param key
	 *            key
	 * @return the value for the given key
	 */
	public String get(String key) {
		String ret = this.cache.get(key);
		if (ret == null) {
			ret = getX(key);
			if (ret != null) {
				this.cache.put(key, ret);
			}
		}
		return ret;
	}

	@Override
	public Set<Integer> get(Integer key) {
		IntegerManager manager = new IntegerManager();
		Set<Integer> ret = new TreeSet<Integer>();
		String keyStr = manager.asString(key);
		String valueStr = get(keyStr);
		List<String> valueListStr = asList(valueStr);
		ret.addAll(manager.asNumber(valueListStr));
		return ret;
	}

	String getKey(String line) {
		String ret = null;
		int index = line.indexOf(EQUALS_SIGN);
		if (index != -1) {
			ret = line.substring(0, index);
		}
		return ret;
	}

	String getValue(String line) {
		String ret = null;
		int index = line.indexOf(EQUALS_SIGN);
		if (index != -1) {
			ret = line.substring(index + EQUALS_SIGN.length());
		}
		return ret;
	}

	List<String> asList(String line) {
		List<String> ret = new ArrayList<String>();
		StringTokenizer stok = new StringTokenizer(line);
		while (stok.hasMoreTokens()) {
			ret.add(stok.nextToken());
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof MapOnProperties) {
			MapOnProperties other = (MapOnProperties) obj;
			return this.fileName.equals(other.fileName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.fileName.hashCode();
	}

	@Override
	public String toString() {
		return this.fileName.toString();
	}

}
