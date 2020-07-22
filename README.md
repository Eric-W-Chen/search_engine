# Search Engine 

Created a fully functional search engine. From the front end, utilized a multithreaded web crawler using a work queue to build the index from a seed URL, and a search engine web interface using embedded Jetty and servlets to search that index. For the functionality, I created a Java program that processes all text files in a directory and its subdirectories, cleans and parses the text into word stems, and builds an in-memory inverted index to store the mapping from word stems to the documents and position within those documents where those word stems were found. This supports exact search, partial search, and multithreading. 

