package net.aquadc.blitz.impl;

import net.aquadc.blitz.ImmutableLongSet;
import net.aquadc.blitz.MutableLongIterator;
import net.aquadc.blitz.MutableLongSet;
import net.aquadc.blitz.LongSet;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.RandomAccess;

import static net.aquadc.blitz.impl.Longs.*;

public final class MutableLongTreeSet implements MutableLongSet, RandomAccess {

    private long[] longs;
    private int size;
    private byte version; // mod counter for fail-fast iterator

    /**
     * Allocate an empty set
     */
    public MutableLongTreeSet() {
        longs = EMPTY;
    }

    /**
     * Allocate a set.
     * @param initialSize    allocate space for {@code initialSize} elements.
     */
    public MutableLongTreeSet(int initialSize) {
        if (initialSize == 0) {
            longs = EMPTY;
            // size = 0
        } else {
            // allocate exactly what client says: it might be e. g. 1 so don't over-allocate
            if (initialSize == 4 || initialSize == 8) { // todo: pooled sizes here
                longs = allocate(initialSize); // pick up from pool
                // size = 0
            } else {
                longs = new long[initialSize];
                // size = 0
            }
        }
    }

    /**
     * Allocate a set
     * @param initialContents    initial contents of a set
     */
    public MutableLongTreeSet(long[] initialContents) {
        longs = EMPTY;
        // size = 0

        addAll(initialContents);
    }

    /**
     * Allocate a copy of {@code original}
     * @param original    set to copy elements from
     */
    public MutableLongTreeSet(LongSet original) {
        if (original instanceof MutableLongTreeSet) {
            this.longs = ((MutableLongTreeSet) original).longs.clone();
            this.size = ((MutableLongTreeSet) original).size;
        } else if (original instanceof ImmutableLongTreeSet) {
            this.longs = original.copyToArray();
            this.size = longs.length;
        } else {
            this.longs = EMPTY;
            // this.size = 0
            addAll(original);
        }
    }


    // from PrimitiveSet

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override // this method is not for tight loops
    public String toString() {
        return Longs.toString(longs, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LongSet)) {
            return false;
        }

        return equal(longs, size, (LongSet) obj);
    }

    @Override
    public int hashCode() {
        return hashCodeOfSet(longs, size);
    }


    // from LongSet

    @Override
    public long get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + " is not in [0; " + (size-1) + ']');
        }
        return longs[index];
    }

    @Override
    public int indexOf(long element) {
        int search = binarySearch0(longs, 0, size, element);
        if (search < -1) {
            search = -1;
        }
        return search;
    }

    @Override
    public boolean contains(long o) {
        int search = binarySearch0(longs, 0, size, o);
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
        return ImmutableLongTreeSet.from(this);
    }

    @Override
    public long[] copyToArray() {
        int size = this.size;
        if (size == 0) {
            return EMPTY;
        }

        return Arrays.copyOf(longs, size);
    }


    // from MutableLongSet

    @Override
    public boolean add(long l) {
        version++; // detect not only true insertions, but also insertion attempts

        long[] longs = this.longs;
        int size = this.size;

        int index = binarySearch0(longs, 0, size, l);
        if (index >= 0) {
            return false; // already exists
        }

        int insertionIndex = -(index + 1);

        int newSize = size + 1;
        if (longs.length == size) { // no more room
            this.longs = reallocAndInsert(longs, size, newSize, insertionIndex, l);
            this.size = newSize;
            return true;
        }

        // we have sufficient room
        insert(longs, insertionIndex, size, l);

        this.size = newSize;
//        System.out.println(l + " inserted at " + insertionIndex + " without array expansion: " + Arrays.toString(longs) + "; size: " + size);

        return true;
    }

    @Override
    public boolean addAll(long[] elements) {
        version++;

        int newElements = 0;
        for (int i = 0, size = elements.length; i < size; i++) {
            long element = elements[i];
            if (!contains(element) && Longs.indexOf(elements, element) == i) {
                newElements++; //     ^ guarantees that it is a first occurrence of an element
            }
        }

        if (newElements == 0) {
            return false; // all elements are already in this collection
        }

        long[] longs = this.longs;
        int size = this.size;
        int newSize = size + newElements;
        if (longs.length < newSize) {
            long[] newLongs = allocate(newSize);
            System.arraycopy(longs, 0, newLongs, 0, size);
            longs = newLongs;
        }

        for (long element : elements) {
            int index = binarySearch0(longs, 0, size, element);
            if (index >= 0) {
                continue;
            }

            insert(longs, -(index + 1), size, element);
            size++;
        }

        if (size != newSize) { // fixme rm
            throw new AssertionError(size + " != " + newSize);
        }

        this.longs = longs;
        this.size = size; // <!--
        return true;
    }

    @Override
    public boolean addAll(LongSet elements) {
        // copy of {@link this#addAll(long[])}, keep in sync
        version++;

        int newElements = 0;
        int elementsSize = elements.size();
        for (int i = 0; i < elementsSize; i++) {
            if (!contains(elements.get(i))) {
                newElements++;
            }
        }

        if (newElements == 0) {
            return false; // all elements are already in this collection
        }

        long[] longs = this.longs;
        int size = this.size;
        int newSize = size + newElements;
        if (longs.length < newSize) {
            long[] newLongs = allocate(newSize);
            System.arraycopy(longs, 0, newLongs, 0, size);
            longs = newLongs;
        }

        for (int i = 0; i < elementsSize; i++) {
            long element = elements.get(i);
            int index = binarySearch0(longs, 0, size, element);
            if (index >= 0) {
                continue;
            }

            insert(longs, -(index + 1), size, element);
            size++;
        }

        if (size != newSize) { // fixme rm
            throw new AssertionError(size + " != " + newSize);
        }

        this.longs = longs;
        this.size = size;
        return true;
    }

    @Override
    public boolean remove(long l) {
        long[] longs = this.longs;
        int size = this.size;

        int index = binarySearch0(longs, 0, size, l);
        return removeAt(index);
    }

    /*pkg*/ boolean removeAt(int index) {
        version++;

        if (index < 0) {
            return false;
        }

        int newSize = size - 1;
        long[] longs = this.longs;
        if (index < newSize) { // removing not last item: [a, b, c, DELETE ME, e, f] -> [a, b, c, e, f]
            System.arraycopy(longs, index + 1, longs, index, newSize - index);
        }

        this.size = newSize;
        return false;
    }

    @Override
    public boolean removeAll(long[] elements) {
        return batchRemove(elements, true);
    }

    @Override
    public boolean removeAll(LongSet elements) {
        return batchRemove(elements, true);
    }

    @Override
    public boolean retainAll(long[] elements) {
        return batchRemove(elements, false);
    }

    @Override
    public boolean retainAll(LongSet elements) {
        return batchRemove(elements, false);
    }

    private boolean batchRemove(long[] elements, boolean remove) {
        version++;

        boolean changed = false;
        long[] longs = this.longs;
        int size = this.size;
        for (int i = 0; i < size; i++) {
            int index = Longs.indexOf(elements, longs[i]);
            // found && remove: remove
            // not found && remove: skip
            // found && retain: skip
            // not found && retain: remove
            if (index >= 0 == remove) {
                size--;
                if (i < size) { // not a last item
                    System.arraycopy(longs, i + 1, longs, i, size - i);
                }
                i--; // iterate this cell one more time
                changed = true;
            }
        }
        this.size = size;
        return changed;
    }

    private boolean batchRemove(LongSet elements, boolean remove) {
        // copy of batchRemove(long[], boolean), keep in sync
        version++;

        boolean changed = false;
        long[] longs = this.longs;
        int size = this.size;
        for (int i = 0; i < size; i++) {
            int index = elements.indexOf(longs[i]);
            // found && remove: remove
            // not found && remove: skip
            // found && retain: skip
            // not found && retain: remove
            if (index >= 0 == remove) {
                size--;
                if (i < size) { // not a last item
                    System.arraycopy(longs, i + 1, longs, i, size - i);
                }
                i--; // iterate this cell one more time
                changed = true;
            }
        }
        this.size = size;
        return changed;
    }

    @Override
    public MutableLongIterator iterator() {
        return new Iterator();
    }

    private class Iterator implements MutableLongIterator {
        private int next = 0;
        private int version = MutableLongTreeSet.this.version;
        @Override public boolean hasNext() {
            checkForComodification();
            return next < size;
        }
        @Override public long next() {
            checkForComodification();
            return longs[next++];
        }
        @Override public void remove() {
            checkForComodification();
            removeAt(next - 1);
            version++;
        }
        private void checkForComodification() {
            if (version != MutableLongTreeSet.this.version) {
                throw new ConcurrentModificationException("This collection was modified while iterating.");
            }
        }
    }

    // todo: cleanup method that shrinks array
}
