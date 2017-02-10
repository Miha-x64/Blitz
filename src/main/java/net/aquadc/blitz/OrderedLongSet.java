package net.aquadc.blitz;

/**
 * Created by miha on 10.02.17
 */
public interface OrderedLongSet extends LongSet {

    /**
     * Returns element at {@code index}.
     * It is OK to completely change order of elements when collection is mutated.
     * @param index    index
     * @return element at {@code index}
     */
    long get(int index);

    /**
     * Returns the smallest element in the set.
     * @return the smallest element in the set
     */
    long smallest();

    /**
     * Returns the biggest element in the set.
     * @return the biggest element in the set
     */
    long biggest();
}
