import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Builds the inverted index by searching and stemming text files to search for the word while updating file path and index count.
 * @author EricChen
 */
public class IndexBuilder {

	/** Inverted index to hold stemmed word, file path, and index count. */
	private final InvertedIndex index;

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Constructor to create inverted index
	 *
	 * @param index	inverted index
	 */
	public IndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * 	Recursively traverses through directories to access individual text files. If it's a text file, stems the file and updates indexCount.
	 *  Then, builds inverted index by adding in the stemmed word, file path, and updated index count.
	 *
	 * @param 	path	path to the file with the word
	 * @throws 	IOException if unable to read or parse file
	 */
	public void build(Path path) throws IOException {
		if(Files.isDirectory(path)) {	//if the path is a directory
			for(Path file : TextFileFinder.list(path)) {	//looping through directory stream for files
				addFile(file);
			}
		}
		else if(isTextFile(path)) {	//if the path leads to a file
			addFile(path);
		}
	}

	/** Stem the words and add it directly into the inverted index while updating the index count
	 * @param inputFile 	path to the file with the word
	 * @throws IOException 	if unable to read or parse file
	 */
	public void addFile(Path inputFile) throws IOException {
		addFile(inputFile, this.index);
	}

	/** Static method to stem the words and add it directly into the inverted index while updating the index count
	 * @param inputFile 	path to the file with the word
	 * @param index         the inverted index
	 * @throws IOException 	if unable to read or parse file
	 */
	public static void addFile(Path inputFile, InvertedIndex index) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		int count = 0;

		try(BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);){
			String line = reader.readLine();
			String result = inputFile.toString();
			while(line != null){
				String[] parsed = TextParser.parse(line); //putting parsed words into new string array
				for(String words : parsed) { //getting the words in the parsed line
					count++;
					index.add(stemmer.stem(words).toString(), result, count); //adds stemmed word into tree set
				}
				line = reader.readLine();
			}
		}
	}

	/** Returns true if path is a text file, otherwise returns false
	 * @param path to file
	 * @return true if is text file, false if not
	 */
	public static boolean isTextFile(Path path) {
		if((path.toString().toLowerCase().endsWith(".txt")) ||path.toString().toLowerCase().endsWith(".text")) {
			return true;
		}
		return false;
	}
}