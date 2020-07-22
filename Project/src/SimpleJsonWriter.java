import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Integer> iterator = elements.iterator();
		writer.write("[\n");
		if(iterator.hasNext()) {
			indent(iterator.next().toString(), writer, level + 1);
		}
		while(iterator.hasNext()) {
			writer.write(",\n");
			indent(iterator.next().toString(), writer, level + 1);
		}
		writer.write("\n");
		indent("]", writer, level);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{\n");
		if(iterator.hasNext()) {
			String key = iterator.next().toString();
			quote(key, writer, level + 1);
			writer.write(": "+ elements.get(key).toString());
		}
		while(iterator.hasNext()) {
			writer.write(",\n");
			String key= iterator.next().toString();
			quote(key, writer, level + 1);
			writer.write(": "+ elements.get(key).toString());
		}
		writer.write("\n");
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException	if unable to read or parse file
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{\n");
		if(iterator.hasNext()) {
			String key = iterator.next().toString();
			quote(key, writer, level + 1);
			writer.write(": ");
			asArray(elements.get(key), writer, level + 1);
			while(iterator.hasNext()) {
				writer.write(",\n");
				key = iterator.next().toString();
				quote(key, writer, level + 1);
				writer.write(": ");
				asArray(elements.get(key), writer, level + 1);
			}
		}
		writer.write("\n");
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static String asNestedObject(Map<String, ? extends Collection<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a inverted pretty JSON object.
	 *
	 * @param elements	the elements to write
	 * @param writer	the path to the file
	 * @param level		the initial indent level
	 * @throws IOException	if unable to read or parse the file
	 */
	public static void asInvertedObject(Map<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{\n");
		if(iterator.hasNext()) {
			String key = iterator.next().toString();
			quote(key, writer, level + 1);
			writer.write(": ");
			asNestedObject(elements.get(key), writer, level + 1);
			while(iterator.hasNext()) {
				writer.write(",\n");
				key = iterator.next().toString();
				quote(key, writer, level + 1);
				writer.write(": ");
				asNestedObject(elements.get(key), writer, level + 1);
			}
		}
		writer.write("\n");
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a inverted pretty JSON object to file.
	 *
	 * @param elements	the elements to write
	 * @param path the path to the file
	 * @throws IOException if unable to read or parse the file
	 */
	public static void asInvertedObject(Map<String, TreeMap<String, TreeSet<Integer>>> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asInvertedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a query pretty JSON object.
	 *
	 * @param elements	the elements to write
	 * @param writer	the path to the file
	 * @param level		the initial indent level
	 * @throws IOException	if unable to read or parse the file
	 */
	public static void asQueryObject(Map<String, List<InvertedIndex.SearchResult>> elements, Writer writer, int level)throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");
		if(iterator.hasNext()) {
			writer.write("\n");
			String key = iterator.next().toString();
			quote(key, writer, level + 1);
			writer.write(": [");
			Collections.sort(elements.get(key));
			writeArrayList(elements.get(key), writer, level);
			writer.write("\t]");
		}
		while(iterator.hasNext()) {
			writer.write(",\n");
			String key = iterator.next().toString();
			quote(key, writer, level + 1);
			writer.write(": [");
			Collections.sort(elements.get(key));
			writeArrayList(elements.get(key), writer, level);
			writer.write("\t]");
		}
		if(!iterator.hasNext()) {
			writer.write("\n");
		}
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a inverted pretty JSON object to file.
	 *
	 * @param elements	the elements to write
	 * @param path the path to the file
	 * @throws IOException if unable to read or parse the file
	 */
	public static void asQueryObject(Map<String, List<InvertedIndex.SearchResult>> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asQueryObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the array list of search result objects
	 *
	 * @param elements	the elements to write
	 * @param writer	the path to the file
	 * @param level		the initial indent level
	 * @throws IOException	if unable to read or parse the file
	 */
	public static void writeArrayList(List<InvertedIndex.SearchResult> elements, Writer writer, int level) throws IOException {
		if(!elements.isEmpty()) {
			for(int i = 0 ; i < elements.size() - 1; i++) {
				Integer count = elements.get(i).getCount();
				Double score = elements.get(i).getScore();

				writer.write("\n\t\t{\n");
				writer.write("\t\t\t\"where\": \"" + elements.get(i).getPath() + "\",\n");
				writer.write("\t\t\t\"count\": " + count.toString() + ",\n");
				writer.write("\t\t\t\"score\": " + 	String.format("%.8f", score));
				writer.write("\n\t\t},");
			}
			Integer count = elements.get(elements.size() - 1).getCount();
			Double score = elements.get(elements.size() - 1).getScore();

			writer.write("\n\t\t{\n");
			writer.write("\t\t\t\"where\": \"" + elements.get(elements.size() - 1).getPath() + "\",\n");
			writer.write("\t\t\t\"count\": " + count.toString() + ",\n");
			writer.write("\t\t\t\"score\": " + 	String.format("%.8f", score));
			writer.write("\n\t\t}");
		}
		writer.write("\n");

	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}
}