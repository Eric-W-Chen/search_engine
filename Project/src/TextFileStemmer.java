import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into sets of
 * stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 *
 * @see TextParser
 */
public class TextFileStemmer {

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from the provided line
	 *
	 * @param line	the line of words to clean, split, and stem
	 * @param stemmer	the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> individualStem(String line, Stemmer stemmer){
		ArrayList<String> newArrayList = new ArrayList<String>();
		String[] parsed = TextParser.parse(line); //putting parsed words into new string array
		for(String words : parsed) { //getting the words in the parsed line
			newArrayList.add(stemmer.stem(words).toString()); //adds stemmed word into tree set
		}
		return newArrayList;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted ArrayList of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #individualStem(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> individualStem(Path inputFile) throws IOException {
		ArrayList<String> newArrayList = new ArrayList<String>();

		try(BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);){
			String line = reader.readLine();
			while (line != null){
				newArrayList.addAll(individualStem(line)); //adds all the clean tree sets into an empty tree set
				line = reader.readLine();
			}
		}
		catch(Exception e) {
			System.err.printf("File: %s has an error", inputFile.toString());
		}
		return newArrayList;
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see #individualStem(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> individualStem(String line) {
		// THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
		return individualStem(line, new SnowballStemmer(DEFAULT));
	}
}