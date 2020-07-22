import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class to create multi-thread safe query
 * @author EricChen
 */
public class MultiQueryBuilder implements QueryBuilderInterface{

	/** Inverted index containing word, file path, and index count */
	private InvertedIndex index;

	/** Work queue to keep track of pending work */
	private WorkQueue workQueue;

	/** Map of word mapped to search result objects */
	private final TreeMap<String, List<InvertedIndex.SearchResult>> results;

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Constructor for the multi-thread safe query
	 * @param index	inverted index
	 * @param exact	boolean to determine exact or partial search
	 * @param threads	amount of threads to use
	 */
	public MultiQueryBuilder(InvertedIndex index, boolean exact, int threads) {
		this.workQueue = new WorkQueue(threads);
		results = new TreeMap<>();
		this.index = index;
	}

	/** Recursively traverses through directories to access individual text files. If it's a text file, stems the file and updates indexCount.
	 *  Then, builds inverted index by adding in the stemmed word, file path, and updated index count.
	 * @param 	inputFile	path to the file with the word
	 * @param	exact	boolean to determine whether to do partial or exact search
	 * @throws 	IOException	if unable to read or parse file
	 */
	@Override
	public void queryStem(Path inputFile, boolean exact) throws IOException {
		QueryBuilderInterface.super.queryStem(inputFile, exact);
		try {
			workQueue.finish();
		}
		catch(Exception e) {
			System.err.printf("Error: ", e.toString());
		}
	}

	@Override
	public void queryStem(String line, boolean exact) {
		workQueue.execute(new Task(line, exact));
	}

	@Override
	public void queryJson(Path path) throws IOException {
		synchronized (results) {
			SimpleJsonWriter.asQueryObject(results, path);
		}
	}

	/**
	 * Task class to allow execution by threads
	 * @author EricChen
	 */
	private class Task implements Runnable {

		/** Line to be stemmed */
		private String line;

		/** Boolean to determine exact or partial search */
		private boolean exact;

		/**
		 * Constructor for task class that takes in line to be stemmed and determines which search to conduct
		 * @param line	line to be stemmed
		 * @param exact	boolean to determine partial or exact search
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			try {
				TreeSet<String> stemmedWords = new TreeSet<String>();
				SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
				String[] parsed = TextParser.parse(line);

				if (parsed.length < 1) {
					return;
				}

				for (String words : parsed) { // getting the words in the parsed line
					stemmedWords.add(stemmer.stem(words).toString()); // adds stemmed word into tree set
				}

				String joined = String.join(" ", stemmedWords);

				synchronized (results) {
					if (results.containsKey(joined)) {
						return;
					}
				}

				List<InvertedIndex.SearchResult> local = index.genericSearch(stemmedWords, exact);
				synchronized (results) {
					results.put(joined, local);
				}
			}
			catch (Exception e) {
				System.err.printf("Error: ", e.toString());
			}
		}
	}
}
