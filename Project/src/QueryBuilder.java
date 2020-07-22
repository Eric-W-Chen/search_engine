import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Builds the query index by searching and stemming text files to search for the word
 * @author EricChen
 */
public class QueryBuilder implements QueryBuilderInterface{

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/** Word mapped to list of search result objects */
	private final TreeMap<String, List<InvertedIndex.SearchResult>> results;

	/**	Inverted index holding word, location, and index count */
	private final InvertedIndex index;

	/**
	 * 	Constructor for results (map of words and list of search result objects) and passed in inverted index
	 * @param index the inverted index
	 * @param exact determines whether to perform exact or partial search
	 */
	public QueryBuilder(InvertedIndex index, boolean exact) {
		results = new TreeMap<>();
		this.index = index;
	}

	/**
	 * Stems the words and then adds it into the results
	 * @param line	line to stem
	 * @param exact determines whether to perform exact or partial search
	 */
	@Override
	public void queryStem(String line, boolean exact) {
		TreeSet<String> stemmedWords = new TreeSet<String>();
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		String[] parsed = TextParser.parse(line);

		if (parsed.length < 1) {
			return;
		}

		for(String words : parsed) { //getting the words in the parsed line
			stemmedWords.add(stemmer.stem(words).toString()); //adds stemmed word into tree set
		}

		String joined = String.join(" ", stemmedWords);

		if (results.containsKey(joined)) {
			return;
		}

		List<InvertedIndex.SearchResult> local = index.genericSearch(stemmedWords, exact);
		results.put(joined, local);
	}

	/**
	 * Prints out the query
	 * @param path	file path
	 * @throws IOException if file not found
	 */
	@Override
	public void queryJson(Path path) throws IOException{
		SimpleJsonWriter.asQueryObject(results, path);
	}
}