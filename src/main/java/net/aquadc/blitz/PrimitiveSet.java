package net.aquadc.blitz;

/**
 * A set of primitives.
 * @param <E> wrapper-type of element-type
 */
public interface PrimitiveSet<E> {

    /**
     * Returns size of this collection.
     * @return size of this collection.
     */
    int size();

    /**
     * Returns {@code true} if this collection is empty, {@code false} otherwise.
     * @return {@code true} if this collection contains no elements
     */
    boolean isEmpty();

    /**
     * Returns string representation of this collection: [comma-and-space-separated values]
     * @return opening square bracket, values separated by comma and space, closing square bracket
     */
    String toString();

    /**
     * Checks for equality
     * @param other    any object
     * @return true, if given object is a Set of the same element type ({@code E}) and with same contents // mutableCollection.equals(immutableCollection) = ??
     */
    boolean equals(Object other);

    /**
     * Calculates this collection's hashCode, i. e. sum of all elements' hashCodes
     * @return hashCode of this collection
     */
    int hashCode();

}
