package de.tudresden.inf.lat.wikihypergraph.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
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
 * from a {@link Properties} file. This does not support values with multiple
 * lines.
 * 
 * @author Julian Mendez
 *
 */
public class MapOnFile implements AdjacencyMap {

	public static final String EQUALS_SIGN = "=";
	public static final String COMMENT_CHAR = "#";
	public static final String PARSING_PROBLEM_MESSAGE = DependencyPropertiesMwRevisionProcessor.PARSING_PROBLEM_MESSAGE;

	private Map<String, String> map = new HashMap<String, String>();

	/**
	 * Constructs a new map.
	 * 
	 * @param reader
	 *            file name of Properties file
	 */
	public MapOnFile(Reader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		try {
			loadMap(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	boolean isValidValue(String value) {
		return value != null && !value.isEmpty() && !value.equals(PARSING_PROBLEM_MESSAGE);
	}

	void loadMap(Reader reader) throws IOException {
		BufferedReader input = new BufferedReader(reader);
		for (String line = input.readLine(); line != null; line = input.readLine()) {
			if (!line.trim().isEmpty() && !line.startsWith(COMMENT_CHAR)) {
				String key = getKey(line);
				String value = getValue(line);
				if (isValidValue(value)) {
					this.map.put(key, value);
				}
			}
		}
		input.close();
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
			ret = this.map.get(cleanKey);
		}
		return ret;
	}

	@Override
	public Set<Integer> get(Integer key) {
		if (key == null) {
			return null;
		} else {
			Set<Integer> ret = new TreeSet<Integer>();
			IntegerManager manager = new IntegerManager();
			String keyStr = manager.asString(key);
			String valueStr = get(keyStr);
			if (valueStr != null) {
				List<String> valueListStr = asList(valueStr);
				valueListStr.forEach(current -> {
					if (manager.isValid(current)) {
						ret.add(manager.asNumber(current));
					}
				});
			}
			return ret;
		}
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

	public Map<String, String> getMap() {
		return this.map;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof MapOnFile) {
			MapOnFile other = (MapOnFile) obj;
			return this.map.equals(other.map);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.map.hashCode();
	}

	@Override
	public String toString() {
		return this.map.toString();
	}

}
