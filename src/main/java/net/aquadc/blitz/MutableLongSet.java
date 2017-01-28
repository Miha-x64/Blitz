package net.aquadc.blitz;

/**
 * Created by mike on 25.01.17
 */

public interface MutableLongSet extends LongSet {

    /**
     * Adds the specified {@code element} to this collection if it is not already there.
     * @param element    element to add
     * @return {@code true} if {@code element} was added, {@code false} if it is already in this collection
     */
    boolean add(long element);

    /**
     * Adds each of the specified {@code elements} to this collection if not already there
     * @param elements    elements to add
     * @return {@code true} if collection modified, {@code false} otherwise
     */
    boolean addAll(long[] elements);

    /**
     * {@link this#addAll(long[])
     */
    boolean addAll(LongSet elements);

    /**
     * Removes the specified {@code element} from this collection if it was found there
     * @param element    element to remove
     * @return {@code true} if {@code element} was removed, {@code false} if there's no such element
     */
    boolean remove(long element);

    /**
     * removes all of the specified {@code elements} from this collection
     * @param elements    elements to remove
     * @return {@code true} if collection was changed, {@code false} otherwise
     */
    boolean removeAll(long[] elements);

    /**
     * {@link this#removeAll(long[])}
     */
    boolean removeAll(LongSet elements);

    /**
     * Leaves intersection of {@code this} and {@code elements}, removes any other elements
     * @param elements    elements to retain
     * @return {@code true} if collection was changed, {@code false} otherwise
     */
    boolean retainAll(long[] elements);

    /**
     * {@link this#retainAll(long[])}
     */
    boolean retainAll(LongSet elements);

    /**
     * Returns a mutable iterator over this collection.
     * @return a mutable iterator over this collection
     */
    @Override
    MutableLongIterator iterator();
}
