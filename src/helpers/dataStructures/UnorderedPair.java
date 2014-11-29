package helpers.dataStructures;

/**
 * A type designed for pairing two objects that don't then have any ordering
 * after being paired.
 * 
 * @author Dwight
 */
public class UnorderedPair<TYPE extends Comparable<TYPE>> {
	private final TYPE a;
	private final TYPE b;

	public UnorderedPair(TYPE a, TYPE b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UnorderedPair)) {
			return false;
		}
		UnorderedPair<?> pair = (UnorderedPair<?>) obj;
		return a.equals(pair.a) && b.equals(pair.b) || a.equals(pair.b) && b.equals(pair.a);
	}

	@Override
	public int hashCode() {
		return a.hashCode() * b.hashCode();
	}

	public TYPE first() {
		return a.compareTo(b) > 0 ? a : b;
	}

	public TYPE second() {
		return a.compareTo(b) > 0 ? b : a;
	}

	public OrderedPair<TYPE> getOrdered() {
		return new OrderedPair<TYPE>(a, b);
	}
}
