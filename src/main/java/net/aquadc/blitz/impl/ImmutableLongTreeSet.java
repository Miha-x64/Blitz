package net.aquadc.blitz.impl;

import net.aquadc.blitz.ImmutableLongSet;
import net.aquadc.blitz.LongSet;
import net.aquadc.blitz.MutableLongSet;

import java.util.Arrays;

import static net.aquadc.blitz.impl.Longs.*;

/**
 * Created by mike on 25.01.17
 */

public final class ImmutableLongTreeSet implements ImmutableLongSet {

    private static final ImmutableLongTreeSet EMPTY = new ImmutableLongTreeSet();

    private final long[] array;

    private ImmutableLongTreeSet() {
        this.array = Longs.EMPTY;
    }
    private ImmutableLongTreeSet(long[] array) {
        this.array = array;
    }


    // from PrimitiveSet


    @Override
    public int size() {
        return array.length;
    }

    @Override
    public boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LongSet)) {
            return false;
        }

        return equal(array, array.length, (LongSet) obj);
    }

    @Override
    public int hashCode() {
        return hashCodeOfSet(array, array.length);
    }


    // from LongSet
    // copied from MutableLongSet, keep in sync
    @Override
    public long get(int index) {
        long[] array = this.array;
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException("index " + index + " is not in [0; " + (array.length-1) + ']');
        }
        return array[index];
    }

    @Override
    public int indexOf(long element) {
        long[] array = this.array;
        int search = binarySearch0(array, 0, array.length, element);
        if (search < -1) {
            search = -1;
        }
        return search;
    }

    @Override
    public boolean contains(long o) {
        long[] longs = this.array;
        int search = binarySearch0(longs, 0, longs.length, o);
//        System.out.println("array " + Arrays.toString(longs) + " in [0; " + size + ") contains " + o + " at " + search);
        return search >= 0;
    }

    @Override
    public boolean containsAll(long[] elements) {
        for (long element : elements) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(LongSet elements) {
        for (int i = 0, size = elements.size(); i < size; i++) {
            if (!contains(elements.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAtLeastOne(long[] elements) {
        for (long el : elements) {
            if (contains(el)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAtLeastOne(LongSet elements) {
        for (int i = 0, size = elements.size(); i < size; i++) {
            if (contains(elements.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MutableLongSet copyToMutable() {
        return new MutableLongTreeSet(this);
    }

    @Override
    public ImmutableLongSet asImmutable() {
        return this;
    }

    @Override
    public long[] copyToArray() {
        long[] array = this.array;
        if (array.length == 0) {
            return Longs.EMPTY;
        }

        return array.clone();
    }


    // from ImmutableLongSet


    @Override
    public ImmutableLongSet with(long element) {
        long[] array = this.array;
        int length = array.length;
        int index = binarySearch0(array, 0, length, element);
        if (index >= 0) {
            return this;
        }

        int insertionIndex = -(index + 1);
        int newLength = length + 1;
        long[] newArray = new long[newLength];
        if (insertionIndex != 0) {
            System.arraycopy(array, 0, newArray, 0, insertionIndex);
        }
        if (insertionIndex != length) {
            System.arraycopy(array, insertionIndex, newArray, insertionIndex + 1, length - insertionIndex - 1);
        }
        newArray[insertionIndex] = element;
        return new ImmutableLongTreeSet(newArray);
    }

    @Override
    public ImmutableLongSet withAll(long[] elements) {
        if (containsAll(elements)) {
            return this;
        }
        MutableLongTreeSet set = new MutableLongTreeSet(this);
        set.addAll(elements);
        return from(set);
    }

    @Override
    public ImmutableLongSet withAll(LongSet elements) {
        if (containsAll(elements)) {
            return this;
        }
        MutableLongTreeSet set = new MutableLongTreeSet(this);
        set.addAll(elements);
        return from(set);
    }

    @Override
    public ImmutableLongSet without(long element) {
        long[] array = this.array;
        int length = array.length;
        int index = binarySearch0(array, 0, length, element);
        if (index < 0) {
            return this;
        }

        int newLength = length - 1;
        long[] newArray = new long[newLength];
        if (index != 0) {
            System.arraycopy(array, 0, newArray, 0, index);
        }
        if (index != newLength) {
            System.arraycopy(array, index, newArray, index - 1, newLength - index);
        }
        return new ImmutableLongTreeSet(newArray);
    }

    @Override
    public ImmutableLongSet withoutAll(long[] elements) {
        if (!containsAtLeastOne(elements)) {
            return this;
        }

        MutableLongTreeSet set = new MutableLongTreeSet(this);
        set.removeAll(elements);
        return from(set);
    }

    @Override
    public ImmutableLongSet withoutAll(LongSet elements) {
        if (!containsAtLeastOne(elements)) {
            return this;
        }

        MutableLongTreeSet set = new MutableLongTreeSet(this);
        set.removeAll(elements);
        return from(set);
    }

    @Override
    public ImmutableLongSet intersectionWith(long[] elements) {
        if (containsAll(elements) && containingAll(elements, array)) { // looks quite heavyweight
            return this;
        }

        MutableLongTreeSet set = new MutableLongTreeSet(this);
        set.retainAll(elements);
        return from(set);
    }

    @Override
    public ImmutableLongSet intersectionWith(LongSet elements) {
        if (containsAll(elements) && elements.containsAll(this)) { // looks quite heavyweight
            return this;
        }

        MutableLongTreeSet set = new MutableLongTreeSet(this);
        set.retainAll(elements);
        return from(set);
    }

    // static factory

    public static ImmutableLongTreeSet empty() {
        return EMPTY;
    }

    public static ImmutableLongTreeSet from(long[] array) {
        if (array.length == 0) {
            return EMPTY;
        }

        return new ImmutableLongTreeSet(new MutableLongTreeSet(array).copyToArray()); // ugh...
    }

    public static ImmutableLongTreeSet from(LongSet original) {
        if (original instanceof ImmutableLongTreeSet) {
            return (ImmutableLongTreeSet) original;
        }
        if (original.isEmpty()) {
            return EMPTY;
        }

        long[] array = original.copyToArray();
        if (!(original instanceof MutableLongTreeSet)) { // if set not guarantees ordering...
            Arrays.sort(array); // just sort elements, hope array contains only unique ones
        }
        return new ImmutableLongTreeSet(array);
    }
}
