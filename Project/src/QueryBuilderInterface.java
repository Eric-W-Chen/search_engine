import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Query builder interface to share abstract methods without needing to implement through a common superclass.
 * @author EricChen
 */
public interface QueryBuilderInterface {

	/**
	 * Default method to go through files to stem them
	 * @param path	file path
	 * @param exact determines whether to perform exact or partial search
	 * @throws IOException	if file not found
	 */
	public default void build(Path path, boolean exact) throws IOException {
		if(Files.isDirectory(path)) {	//if the path is a directory
			for(Path file : TextFileFinder.list(path)) {	//looping through directory stream for files
				queryStem(file, exact);		//recursive call to access everything inside the stream
			}
		}
		else if(IndexBuilder.isTextFile(path)) {
			queryStem(path, exact);
		}
	}

	/**
	 * Default method to read the file and stem each line
	 * @param inputFile	file to read
	 * @param exact determines whether to perform exact or partial search
	 * @throws IOException if file not found
	 */
	public default void queryStem(Path inputFile, boolean exact) throws IOException {
		try(BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);){
			String line;
			while ((line = reader.readLine()) != null){
				queryStem(line, exact);
			}
		}
	}

	/**
	 * Abstract declaration of stemming the words and adding it into the results
	 * @param line	line to stem
	 * @param exact determines whether to perform exact or partial search
	 */
	public abstract void queryStem(String line, boolean exact);

	/**
	 * Abstract declaration of writing method
	 * @param path	path to file
	 * @throws IOException	if file not found
	 */
	public abstract void queryJson(Path path) throws IOException;
}
