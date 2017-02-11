package net.aquadc.blitz;

/**
 * Created by mike on 24.01.17
 */

public interface LongSet extends PrimitiveSet<Long> {

    /**
     * Searches for specified element and returns {@code true} if it was found and false otherwise.
     * @param element    element to search for
     * @return {@code true} if {@code element} was found in this collection, false otherwise
     */
    boolean contains(long element);

    /**
     * Checks whether this collection contains all of the specified elements or not
     * @return true if this collection contains all of given {@code elements}
     */
    boolean containsAll(long[] elements);

    /**
     * {@link this#containsAll(long[])}
     */
    boolean containsAll(LongSet elements);

    /**
     * Checks whether this collection contains at least one of given elements of not
     * @param elements    elements to search
     * @return {@code true} if at least one element is in this collection, {@code false} otherwise
     */
    boolean containsAny(long[] elements);

    /**
     * {@link this#containsAny(long[])}
     */
    boolean containsAny(LongSet elements);


    /**
     * Returns mutable copy of this collection, which conforms the rule: {@code copy.equals(this)}
     * @return mutable copy of this collection
     */
    MutableLongSet copyToMutable();

    /**
     * Returns immutable copy of this collection, which conforms the rule: {@code copy.equals(this)}
     * @return immutable copy of this collection, or {@code this}, if this collection is immutable
     */
    ImmutableLongSet asImmutable();

    /**
     * Returns elements of this collection as an array.
     * @return (your own) array of this collection's elements
     */
    long[] copyToArray();

    /**
     * Returns a new iterator over this collection.
     * @return iterator over this collection
     */
    LongIterator iterator();

}
