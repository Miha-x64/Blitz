package net.aquadc.blitz.impl;

import net.aquadc.blitz.*;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import static net.aquadc.blitz.impl.Longs.*;

public final class MutableLongTreeSet implements MutableLongSet, OrderedLongSet, RandomAccess {

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
        if (initialSize < 0) {
            throw new IllegalArgumentException("initialSize must be non-negative.");
        }
        if (initialSize == 0) {
            longs = EMPTY;
            // size = 0
        } else {
            // allocate exactly what client says: it might be e. g. 1 so don't over-allocate
            longs = new long[initialSize];
        }
    }

    /**
     * Allocate a set
     * @param initialContents    initial contents of a set
     */
    public MutableLongTreeSet(long[] initialContents) {
        longs = EMPTY;
        // size = 0

        addAll(initialContents); // we can't
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

        if (obj instanceof OrderedLongSet) {
            return orderedSetsEqual(longs, size, (OrderedLongSet) obj);
        }

        LongSet ls = (LongSet) obj;
        return ls.size() == size && containsAll(ls);
    }

    @Override
    public int hashCode() {
        return hashCodeOfSet(longs, size);
    }


    // from LongSet

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
        version++;
        return addInternal(l);
    }

    private boolean addInternal(long l) {
        int index = binarySearch0(longs, 0, size, l);
        if (index >= 0) {
            return false; // already exists
        }

        insertAt(-(index + 1), l);
        return true;
    }

    private void insertAt(int index, long value) {
        long[] longs = this.longs;
        int size = this.size;

        int newSize = size + 1;
        if (longs.length == size) { // no more room
            this.longs = reallocAndInsert(longs, size, newSize, index, value);
        } else { // we have sufficient room
            insert(longs, index, size, value);
        }
        this.size = newSize;
    }

    @Override
    public boolean addAll(long[] elements) {
        version++;

        boolean changed = false;
        for (long l : elements) {
            changed |= addInternal(l);
        }
        return changed;
    }

    @Override
    public boolean addAll(LongSet elements) {
        // copy of {@link this#addAll(long[])}, keep in sync
        version++;

        boolean changed = false;
        LongIterator itr = elements.iterator();
        while (itr.hasNext()) {
            changed |= addInternal(itr.next());
        }
        return changed;
    }

    @Override
    public boolean remove(long l) {
        int index = binarySearch0(longs, 0, size, l);
        if (index < 0) {
            version++; // detect unsuccessful mod attempts
            return false;
        }

        removeAt(index);
        return true;
    }

    /*pkg*/ void removeAt(int index) {
        version++;

        long[] longs = this.longs;
        int newSize = size - 1;
        if (index < newSize) { // remove not last item: [a, b, c, DELETE ME, e, f] -> [a, b, c, e, f]
            System.arraycopy(longs, index + 1, longs, index, newSize - index);
        }
        this.size = newSize;
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
            // found && remove: remove
            // not found && remove: skip
            // found && retain: skip
            // not found && retain: remove
            if (elements.contains(longs[i]) == remove) {
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
        @Override public void reset() {
            next = 0;
            version = MutableLongTreeSet.this.version;
        }
        private void checkForComodification() {
            if (version != MutableLongTreeSet.this.version) {
                throw new ConcurrentModificationException("This collection was modified while iterating.");
            }
        }
    }

    @Override
    public int addOrRemove(long element) {
        version++;

        int index = binarySearch0(longs, 0, size, element);
        if (index >= 0) {
            // already contains, remove
            removeAt(index);
            return -1;
        }

        // does not contain, add
        insertAt(-(index + 1), element);
        return +1;
    }

    // from OrderedSet

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
    public long smallest() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return longs[0];
    }

    @Override
    public long biggest() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return longs[size-1];
    }

    // todo: cleanup method that shrinks array
}
