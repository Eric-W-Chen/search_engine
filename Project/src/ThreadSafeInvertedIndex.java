import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to create thread safe inverted index
 * @author EricChen
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** Lock to be used for synchronization */
	private final SimpleReadWriteLock lock;

	/** Constructor for the thread safe inverted index */
	public ThreadSafeInvertedIndex() {
		lock = new SimpleReadWriteLock();
	}

	@Override
	public void add(String word, String file, int indexCount) {
		lock.writeLock().lock();
		try {
			super.add(word, file, indexCount);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex otherIndex) {
		lock.writeLock().lock();
		try {
			super.addAll(otherIndex);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public List<SearchResult> exactSearch(Collection<String> queries){
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> partialSearch(Collection<String> queries){
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getWord() {
		lock.readLock().lock();
		try {
			return super.getWord();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> getCounts() {
		lock.readLock().lock();
		try {
			return super.getCounts();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getCounts(String word) {
		lock.readLock().lock();
		try {
			return super.getCounts(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getPositions(String word, String path){
		lock.readLock().lock();
		try {
			return super.getPositions(word, path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPath(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasPath(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPosition(String word, String location, int indexCount) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, location, indexCount);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void writeIndex(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.writeIndex(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}
}