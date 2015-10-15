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
	public static final String COMMENT_CHAR = "#";

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
		if (key != null) {
			String cleanKey = key.trim();
			boolean found = false;
			try {
				BufferedReader input = new BufferedReader(new FileReader(this.fileName));
				for (String line = input.readLine(); line != null && !found; line = input.readLine()) {
					if (!line.trim().isEmpty() && !line.startsWith(COMMENT_CHAR)) {
						if (getKey(line).equals(cleanKey)) {
							found = true;
							result = getValue(line);
						}
					}
				}
				input.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
		String ret = null;
		if (key != null) {
			String cleanKey = key.trim();
			ret = this.cache.get(cleanKey);
			if (ret == null) {
				ret = getX(cleanKey);
				if (ret != null) {
					this.cache.put(cleanKey, ret);
				}
			}
		}
		return ret;
	}

	@Override
	public Set<Integer> get(Integer key) {
		Set<Integer> ret = null;
		if (key != null) {
			ret = new TreeSet<Integer>();
			IntegerManager manager = new IntegerManager();
			String keyStr = manager.asString(key);
			String valueStr = get(keyStr);
			if (valueStr != null) {
				List<String> valueListStr = asList(valueStr);
				for (String current : valueListStr) {
					if (manager.isValid(current)) {
						ret.add(manager.asNumber(current));
					}
				}
			}
		}
		return ret;
	}

	String getKey(String line) {
		String ret = null;
		int index = line.indexOf(EQUALS_SIGN);
		if (index != -1) {
			ret = line.substring(0, index).trim();
		}
		return ret;
	}

	String getValue(String line) {
		String ret = null;
		int index = line.indexOf(EQUALS_SIGN);
		if (index != -1) {
			ret = line.substring(index + EQUALS_SIGN.length()).trim();
		}
		return ret;
	}

	List<String> asList(String line) {
		List<String> ret = null;
		if (line != null) {
			ret = new ArrayList<String>();
			StringTokenizer stok = new StringTokenizer(line);
			while (stok.hasMoreTokens()) {
				ret.add(stok.nextToken());
			}
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
