package net.aquadc.blitz.impl;

import net.aquadc.blitz.*;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import static net.aquadc.blitz.impl.Longs.*;

/**
 * Created by mike on 25.01.17
 * @deprecated immutable data structures are cool
 *             when they're persistent and cheap to copy, this one doesn't
 */
public final class ImmutableLongTreeSet implements ImmutableLongSet, OrderedLongSet, RandomAccess {

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

    private String toString;
    @Override
    public String toString() {
        int size = array.length;
        if (size == 0) {
            return "[]";
        }

        if (toString != null) {
            return toString;
        }

        StringBuilder sb = new StringBuilder(size * 5); // for 3-digit numbers ;)
        sb.append('[');
        boolean first = true;
        for (long item : array) {
            if (first) first = false;
            else sb.append(", ");
            sb.append(item);
        }
        return toString = sb.append(']').toString(); // assignment-as-expression, he-he
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LongSet)) {
            return false;
        }

        if (obj instanceof OrderedLongSet) {
            return orderedSetsEqual(array, array.length, (OrderedLongSet) obj);
        }

        LongSet ls = (LongSet) obj;
        return ls.size() == array.length && containsAll(ls);
    }

    @Override
    public int hashCode() {
        return hashCodeOfSet(array, array.length);
    }


    // from LongSet

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
        LongIterator itr = elements.iterator();
        while (itr.hasNext()) {
            if (!contains(itr.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAny(long[] elements) {
        for (long el : elements) {
            if (contains(el)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(LongSet elements) {
        LongIterator itr = elements.iterator();
        while (itr.hasNext()) {
            if (contains(itr.next())) {
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
        int removeAt = binarySearch0(array, 0, length, element);
        if (removeAt < 0) {
            return this;
        }

        if (length == 1) return EMPTY; // new length will be 0, preserve identity

        int newLength = length - 1;
        long[] newArray = new long[newLength];
        if (removeAt != 0) { // copy head
            System.arraycopy(array, 0, newArray, 0, removeAt);
        }
        if (removeAt != newLength) { // copy tail
            System.arraycopy(array, removeAt + 1, newArray, removeAt, newLength - removeAt);
        }
        return new ImmutableLongTreeSet(newArray);
    }

    @Override
    public ImmutableLongSet withoutAll(long[] elements) {
        if (!containsAny(elements)) {
            return this;
        }

        MutableLongTreeSet set = new MutableLongTreeSet(this);
        set.removeAll(elements);
        return from(set);
    }

    @Override
    public ImmutableLongSet withoutAll(LongSet elements) {
        if (!containsAny(elements)) {
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

    @Override
    public LongIterator iterator() {
        return new Iterator();
    }

    private class Iterator implements LongIterator {
        private int next = 0;
        @Override public boolean hasNext() {
            return next < array.length;
        }
        @Override public long next() {
            if (next == array.length) {
                throw new NoSuchElementException("No more elements, end reached. (collection size: " + array.length + ')');
            }
            return array[next++];
        }
        @Override public void reset() {
            next = 0;
        }
    }

    // from OrderedLongSet

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
    public long smallest() {
        if (this == EMPTY) {
            throw new NoSuchElementException();
        }
        return array[0];
    }

    @Override
    public long biggest() {
        if (this == EMPTY) {
            throw new NoSuchElementException();
        }
        return array[array.length - 1];
    }

    // static factory

    public static ImmutableLongTreeSet empty() {
        return EMPTY;
    }

    public static ImmutableLongTreeSet singleton(long value) {
        return new ImmutableLongTreeSet(new long[] { value });
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
