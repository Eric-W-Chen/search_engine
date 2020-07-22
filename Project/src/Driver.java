import java.io.IOException;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {

	/**
	 * Default path for index
	 */
	private static final Path DEFAULT_INDEX_PATH = Path.of("index.json");

	/**
	 * Default path for count
	 */
	private static final Path DEFAULT_COUNT_PATH = Path.of("counts.json");

	/**
	 * Default path for result
	 */
	private static final Path DEFAULT_RESULT_PATH = Path.of("results.json");

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		InvertedIndex index;
		IndexBuilder builder;
		QueryBuilderInterface build;
		WorkQueue workQueue = null;
		ArgumentParser parser = new ArgumentParser(args);
		int threads = 5;

		if(parser.hasFlag("-threads")) {
			if(parser.hasValue("-threads")) {
				threads = parser.getInt("-threads", threads);
			}
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;
			workQueue = new WorkQueue(threads);
			builder = new MultiIndexBuilder(threadSafeIndex, workQueue);
			build = new MultiQueryBuilder(threadSafeIndex, parser.hasFlag("-exact"), threads);
		}
		else {
			index = new InvertedIndex();
			builder = new IndexBuilder(index);
			build = new QueryBuilder(index, parser.hasFlag("-exact"));
		}

		if(parser.hasFlag("-path")) {
			if(parser.hasValue("-path")) {
				Path path = parser.getPath("-path");
				try {
					builder.build(path);
				}
				catch (IOException e) {
					System.err.printf("Unable to build the index from %s due to error: %s", path.toString(), e.getCause().toString());
				}
			}
		}
		else {
			System.err.printf("Provided -path flag without the required value.");
		}

		if(parser.hasFlag("-index")) {	//if it has a path
			Path path = parser.getPath("-index", DEFAULT_INDEX_PATH);
			indexJson(path, index);
		}

		if(parser.hasFlag("-counts")) {
			Path path = parser.getPath("-counts", DEFAULT_COUNT_PATH);
			countJson(path, index);
		}

		if (parser.hasFlag("-query")) {
			if (parser.hasValue("-query")) {
				Path path = parser.getPath("-query");
				try {
					if (path != null) {
						build.build(path, parser.hasFlag("-exact"));
					}
				}
				catch (IOException e) {
					System.err.printf("File %s has an error caused by %s", path.toString(), e.getCause().toString());
				}
			}
		}

		if (parser.hasFlag("-results")) {
			Path path = parser.getPath("-results", DEFAULT_RESULT_PATH);
			try {
				build.queryJson(path);
			}
			catch (IOException e) {
				System.err.printf("File %s has an error caused by %s", path.toString(), e.getCause().toString());
			}
		}

		if(workQueue != null) {
			workQueue.shutdown();
		}
	}

	/**
	 * Prints out the file path and word count in JSON format
	 * @param path	file path
	 * @param index	Inverted Index
	 */
	public static void countJson(Path path, InvertedIndex index) {
		try {
			SimpleJsonWriter.asObject(index.getCounts(), path);
		}
		catch (IOException e) {
			System.err.printf("File %s has an error caused by %s", path.toString(), e.getCause().toString());
		}
	}

	/**
	 * Prints out the whole inverted index
	 * @param path file path
	 * @param index	Inverted Index
	 */
	public static void indexJson(Path path, InvertedIndex index) {
		try {
			index.writeIndex(path);
		}
		catch (IOException e) {
			System.err.printf("File %s has an error caused by %s", path.toString(), e.getCause().toString());
		}
	}
}