import java.io.IOException;
import java.nio.file.Path;

/**
 * Class to create multi-thread safe inverted index
 * @author EricChen
 *
 */
public class MultiIndexBuilder extends IndexBuilder {

	/** Creates a work queue that we can reuse between building and searching */
	private final WorkQueue workQueue;

	/** Thread safe Inverted Index	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Constructor for the multithread safe inverted index
	 * @param index	thread-safe inverted index
	 * @param workQueue work queue to start multithreading
	 */
	public MultiIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue workQueue) {
		super(index);
		this.index = index;
		this.workQueue = workQueue;
	}

	/** Recursively traverses through directories to access individual text files. If it's a text file, stems the file and updates indexCount.
	 *  Then, builds inverted index by adding in the stemmed word, file path, and updated index count.
	 * @param 	path	path to the file with the word
	 * @throws 	IOException if unable to read or parse file
	 */
	@Override
	public void build(Path path) throws IOException {
		super.build(path);
		try {
			workQueue.finish();
		}
		catch(Exception e) {
			System.err.printf("Error: ", e.toString());
		}
	}

	@Override
	public void addFile(Path inputFile) throws IOException {
		workQueue.execute(new Task(inputFile));
	}

	/**
	 * Task class to allow execution by threads
	 * @author EricChen
	 */
	private class Task implements Runnable {

		/** File to be stemmed */
		private Path file;

		/**
		 * Constructor for task class that takes in file to be stemmed
		 * @param file	file to be stemmed
		 */
		public Task(Path file) {
			this.file = file;
		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				addFile(file, local);
				index.addAll(local);
			}
			catch (IOException e) {
				System.err.printf("File: %s has an error", file.toString());
			}
		}
	}
}