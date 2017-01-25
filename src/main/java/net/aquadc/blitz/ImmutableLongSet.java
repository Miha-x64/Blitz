package net.aquadc.blitz;

/**
 * Created by mike on 25.01.17
 */

public interface ImmutableLongSet extends LongSet {

    /**
     * Returns collection with elements from {@code this} and {@code element}
     * @param element    element to add
     * @return new set if there was no {@code element} in this collection, {@code this} otherwise
     */
    ImmutableLongSet with(long element);

    /**
     * Returns collection with elements from {@code this} and {@code elements}
     * @param elements    elements to add
     * @return new set if this set does not contain at least one of {@code elements}, this set otherwise
     */
    ImmutableLongSet withAll(long[] elements);

    /**
     * {@link this#withAll(long[])
     */
    ImmutableLongSet withAll(LongSet elements);

    /**
     * Returns the collection consisting of elements from {@code this}, but without the specified {@code element}
     * @param element    element to remove
     * @return new collection without {@code element}, if {@code this} contains {@code element}, {@code this} otherwise
     */
    ImmutableLongSet without(long element);

    /**
     * Returns the collection consisting of elements from {@code this} excluding {@code elements}
     * @param elements    elements to remove
     * @return new collection without {@code elements}, if {@code this} contains any of them, {@code this} otherwise
     */
    ImmutableLongSet withoutAll(long[] elements);

    /**
     * {@link this#withoutAll(long[])}
     */
    ImmutableLongSet withoutAll(LongSet elements);

    /**
     * Returns intersection of {@code this} and {@code elements}
     * @param elements    elements to retain
     * @return intersection of {@code this} and {@code elements}
     */
    ImmutableLongSet intersectionWith(long[] elements);

    /**
     * {@link this#intersectionWith(long[])} (long[])}
     */
    ImmutableLongSet intersectionWith(LongSet elements);

}
