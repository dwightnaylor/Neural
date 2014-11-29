package helpers.dataStructures;

/**
 * A type designed for pairing two objects that don't then have any ordering
 * after being paired.
 * 
 * @author Dwight
 */
public class OrderedPair<TYPE extends Comparable<TYPE>> {
	private final TYPE a;
	private final TYPE b;

	public OrderedPair(TYPE a, TYPE b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OrderedPair)) {
			return false;
		}
		OrderedPair<?> pair = (OrderedPair<?>) obj;
		return a.equals(pair.a) && b.equals(pair.b);
	}

	@Override
	public int hashCode() {
		return a.hashCode() * 100000 + b.hashCode();
	}

	public TYPE first() {
		return a;
	}

	public TYPE second() {
		return b;
	}

	public UnorderedPair<TYPE> getUnordered() {
		return new UnorderedPair<TYPE>(a, b);
	}
}
