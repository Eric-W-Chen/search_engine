import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Creates the inverted index, locations (file path mapped to word count), and query
 * along with necessary getters and setters
 * @author EricChen
 *
 */
public class InvertedIndex {

	/** Nested data structure that sets inverted index with word, file path, and index count **/
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/** TreeMap that maps file path to word count **/
	private final TreeMap<String, Integer> locations;	//want to map the location to the word count and print in json format that count.json looks like

	/** Creates inverted index, locations (file path mapped to word count), and query **/
	public InvertedIndex() {
		index = new TreeMap<>();
		locations = new TreeMap<>();
	}

	/**
	 * Adds word and file path to the inverted index and updates indexCount
	 *
	 * @param word the word that we are adding
	 * @param file the location of the file
	 * @param indexCount counter for what index the word is in
	 */
	public void add(String word, String file, int indexCount) {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(file, new TreeSet<Integer>());
		boolean updatingCount = index.get(word).get(file).add(indexCount);
		locations.putIfAbsent(file, 0);

		if(updatingCount) {
			locations.put(file, Math.max(locations.get(file), indexCount));
		}
	}

	/**
	 * Adds word, location, and index count into the inverted index all at once rather than one at a time
	 * @param otherIndex	passed in inverted index
	 */
	public void addAll(InvertedIndex otherIndex) {
		for (String word:otherIndex.index.keySet()) {
			if (this.index.containsKey(word) == false) {
				this.index.put(word, otherIndex.index.get(word));
			}
			else {
				for (String location:otherIndex.index.get(word).keySet()) {
					if (this.index.get(word).containsKey(location) == false) {
						this.index.get(word).put(location, otherIndex.index.get(word).get(location));
					}
					else {
						this.index.get(word).get(location).addAll(otherIndex.index.get(word).get(location));
					}
				}
			}
		}
		for (String path:otherIndex.locations.keySet()) {
			if (this.locations.containsKey(path) == false) {
				this.locations.put(path, otherIndex.locations.get(path));
			}
			else {
				this.locations.put(path, Math.max(this.locations.get(path), otherIndex.locations.get(path)));
			}
		}
	}

	/**
	 * Chooses whether to perform an exact or partial search
	 * @param queries	collection of stemmed words to search through
	 * @param exact		boolean to decide whether to perform exact or partial search
	 * @return	list of search result objects
	 */
	public List<SearchResult> genericSearch(Collection<String> queries, boolean exact) {
		if (exact) {
			return exactSearch(queries);
		}
		else {
			return partialSearch(queries);
		}
	}

	/**
	 * Searches for words in map of stemmed words that matches exactly with the word we're looking for
	 * and adding it to list of search result objects
	 *
	 * @param queries collection of strings that holds stemmed words to search through
	 * @return	list of search result objects to get desired output
	 */
	public List<SearchResult> exactSearch(Collection<String> queries){
		Map<String, SearchResult> lookup = new HashMap<>();
		List<SearchResult> results = new ArrayList<>();

		for (String words : queries) {
			if(index.containsKey(words)) {
				searchHelper(lookup, results, words);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Searches for words in map of stemmed words that starts with the word we're looking for
	 * and adding it to list of search result objects
	 *
	 * @param queries collection of strings that holds stemmed words to search through
	 * @return	list of search result objects to get desired output
	 */
	public List<SearchResult> partialSearch(Collection<String> queries){
		Map<String, SearchResult> lookup = new HashMap<>();
		List<SearchResult> results = new ArrayList<>();

		for (String words : queries) {
			for(String key : index.tailMap(words).keySet()) {
				if(key.startsWith(words)) {
					searchHelper(lookup, results, key);
				}
				else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Search helper method to avoid repetitive code. Finds the path of the word we're looking for, if it's there update count.
	 * If not, create new search result object for it
	 *
	 * @param lookup	map of stemmed words
	 * @param results	list of search result object
	 * @param key		word that we're looking for
	 */
	private void searchHelper(Map<String, SearchResult> lookup, List<SearchResult> results, String key) {
		for (String location : index.get(key).keySet()) {
			if (!lookup.containsKey((location))){
				SearchResult result = new SearchResult(location);
				lookup.put(location, result);
				results.add(result);
			}
			lookup.get(location).updateCount(key);
		}
	}

	/**
	 * Obtains a set of the words (keys)
	 *
	 * @return unmodifiable set of inverted index
	 */
	public Set<String> getWord() {
		return Collections.unmodifiableSet(this.index.keySet());
	}

	/**
	 * Returns unmodifiable view of TreeMap containing file path and word count
	 *
	 * @return TreeMap containing file path and word count
	 */
	public Map<String, Integer> getCounts() {
		return Collections.unmodifiableMap(locations);
	}

	/**
	 * Gets the path to the word if the word exists
	 *
	 * @param word	word that we're looking for
	 * @return set of strings that can either be the path or a new set to hold the path
	 */
	public Set<String> getCounts(String word) {
		if(hasWord(word)) {
			return Collections.unmodifiableSet(this.index.get(word).keySet());
		}
		else {
			return Collections.emptySet();
		}
	}

	/**
	 * Gets the index count of the inverted index
	 *
	 * @param word	word that we're looking for
	 * @param path	path to the word
	 * @return	set of integer that can either be the index counts or a new set to hold the count
	 */
	public Set<Integer> getPositions(String word, String path){ //
		if(hasPath(word, path)) {
			return Collections.unmodifiableSet(this.index.get(word).get(path));
		}
		return Collections.emptySet();
	}

	/**
	 * Checks to see if the index contains the word
	 *
	 * @param 	word word that we're looking for
	 * @return	boolean to see if we've found the word or not
	 */
	public boolean hasWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * Checks to see if the path exists
	 *
	 * @param word	word that we're looking for
	 * @param location	file path to the word we're looking for
	 * @return	if the path exists for the particular word
	 */
	public boolean hasPath(String word, String location) {
		if(this.index.get(word) == null) {
			return false;
		}
		return this.index.get(word).containsKey(location);
	}

	/**
	 * Checks to see if the position exists
	 *
	 * @param word	word that we're looking for
	 * @param location	file path to the word we're looking for
	 * @param indexCount	counter for what index the word is in
	 * @return if the index position exists for the particular word
	 */
	public boolean hasPosition(String word, String location, int indexCount) {
		if(this.index.get(word) == null || this.index.get(word).get(location) == null) {
			return false;
		}
		return this.index.get(word).get(location).contains(indexCount);
	}

	/**
	 * Writes to the JSON writer the inverted index
	 *
	 * @param path the location of the file
	 * @throws IOException if unable to read or parse file
	 */
	public void writeIndex(Path path) throws IOException {
		SimpleJsonWriter.asInvertedObject(this.index, path);
	}

	@Override
	public String toString() {
		return this.index.toString();
	}

	/**
	 * Creates SearchResult object to put into query to store score, count, and location
	 * @author EricChen
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/** Path to the file **/
		private final String path;

		/** Number of times word is found / total words **/
		private double score;

		/** Number of times word is found **/
		private int count;

		/**
		 * Search Result constructor
		 * @param path location to file
		 */
		public SearchResult(String path) {
			this.path = path;
			this.score = 0;
			this.count = 0;
		}

		@Override
		public int compareTo(SearchResult other) {	//if score of one object is less than other, organize it
			if((this.score == other.score) && (this.count == other.count)) {
				return (this.path.compareToIgnoreCase(other.path));
			}
			else if(this.score == other.score) {
				return (Integer.compare(other.count,  this.count));
			}
			else {
				return (Double.compare(this.score, other.score) * -1);
			}
		}

		/**
		 * Returns the file location of the word
		 * @return file location
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Returns the number of times we find the word in the file
		 * @return	amount of times we find the word
		 */
		public int getCount() {
			return this.count;
		}

		/**
		 * Gets the score (total matches/total words)
		 * @return score (total matches/total words)
		 */
		public double getScore() {
			return this.score;
		}

		/**
		 * Updates the count
		 * @param key word that we're looking for
		 */
		private void updateCount(String key) {
			count = count + index.get(key).get(path).size();
			score = this.count * 1.0 / locations.get(this.path);
		}
	}
}