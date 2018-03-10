package net.aquadc.blitz.impl;

import net.aquadc.blitz.*;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Created by mike on 07.02.17
 */
public final class MutableLongHashSet implements MutableLongSet {

    private static final int DEFAULT_SIZE = 4;
    private static final float DEFAULT_LOAD_FACTOR = .95f;

    public MutableLongHashSet() {
        this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MutableLongHashSet(float loadFactor) {
        this(DEFAULT_SIZE, loadFactor);
    }

    public MutableLongHashSet(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    public MutableLongHashSet(int size, float loadFactor) {
        int actualSize = DEFAULT_SIZE;
        while (actualSize < size) {
            actualSize <<= 1;
        }
        this.buckets = new long[actualSize];
        this.middle = actualSize / 2;
        this.loadFactor = checkLoaf(loadFactor);
    }

    public MutableLongHashSet(long[] elements) {
        this(elements, DEFAULT_LOAD_FACTOR);
    }

    public MutableLongHashSet(long[] elements, float loadFactor) {
        this.buckets = new long[DEFAULT_SIZE];
        this.middle = 8;
        this.loadFactor = checkLoaf(loadFactor);
        addAll(elements);
    }

    public MutableLongHashSet(LongSet original) {
        this(original.size(), DEFAULT_LOAD_FACTOR);
        addAll(original);
    }

    public MutableLongHashSet(LongSet original, float loadFactor) {
        this(original.size(), loadFactor);
        addAll(original);
    }

    private float checkLoaf(float loaf) {
        if (loaf > 0 && loaf < 1)
            return loaf;

        throw new IllegalArgumentException("loadFactor must be > 0 and < 1, " + loaf + " given");
    }

    /*pkg*/ boolean containsZero;
    /*pkg*/ long[] buckets;
    /*pkg*/ int size;
    private int middle;
    /*pkg*/ int version;
    private final float loadFactor;

    // PrimitiveSet

    @Override public int size() {
        return size;
    }

    @Override public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder(size * 5); // for 3-digit numbers ;)
        sb.append('[');
        boolean first = true;

        if (containsZero) {
            sb.append('0');
            first = false;
        }

        for (long l : buckets) {
            if (l == 0) continue;

            if (first) first = false;
            else sb.append(", ");

            sb.append(l);
        }

        return sb.append(']').toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LongSet)) {
            return false;
        }

        LongSet ls = (LongSet) obj;
        return size == ls.size() && containsAll(ls);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (long value : buckets) {
            hash += (int) (value ^ (value >>> 32));
        }
        return hash;
    }

    // LongSet

    public boolean contains(long element) {
        if (element == 0)
            return containsZero;

        int idx = bucketOf(element);
        long value = buckets[idx];
        if (value == element)
            return true;

        if (idx > middle) {
            final int limit = idx - middle/4;
            while (idx > limit) {
                idx--;
                if (buckets[idx] == element)
                    return true;

                if (buckets[idx] == 0)
                    return false;
            }
        } else {
            final int limit = idx + middle/4;
            while (idx < limit) {
                idx++;
                if (buckets[idx] == element)
                    return true;

                if (buckets[idx] == 0) {
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(long[] elements) {
        for (long l : elements) {
            if (!contains(l)) {
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
        for (long l : elements) {
            if (contains(l)) {
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
        return new MutableLongHashSet(this);
    }

    @Override
    public ImmutableLongSet asImmutable() {
        // fixme: there's no corresponding immutable set
        return ImmutableLongTreeSet.from(this);
    }

    @Override
    public long[] copyToArray() {
        if (size == 0)
            return Longs.EMPTY;

        long[] array = new long[size];

        int index = 0;
        for (long l : buckets) {
            if (l != 0) {
                array[index++] = l;
            }
        }

        if (containsZero) {
            array[index++] = 0;
        }

        if (index != size) {
            throw new AssertionError();
        }

        return array;
    }

    // MutableLongSet

    @Override
    public boolean add(long element) {
        version++;
        return addInternal(element);
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
        version++;

        boolean changed = false;
        LongIterator itr = elements.iterator();
        while (itr.hasNext()) {
            changed |= addInternal(itr.next());
        }
        return changed;
    }

    private boolean addInternal(long element) {
        if (element == 0) {
            if (containsZero) {
                return false;
            } else {
                size++;
                return containsZero = true;
            }
        }

        switch (putIntoBucket(element)) {
            case 0:
                return false;

            case 1:
                size++;
                return true;

            case 2:
                break;

            default:
                throw new AssertionError();
        }

        // okaaaay, must resize

        long[] oldBuckets = buckets;
        final int oldSize = oldBuckets.length;
        buckets = new long[2 * oldSize];
        middle = oldSize;
        for (long l : oldBuckets) {
            putIntoBucket(l);
        }

        return addInternal(element); // try again
    }

    private int putIntoBucket(long element) {
        if (size > loadFactor * buckets.length) return 2;
        int idx = bucketOf(element);
        long value = buckets[idx];

        if (value == element)
            return 0;

        if (idx > middle) {
            final int limit = idx - middle/4;
            while (idx > limit) {
                if (buckets[idx] == element)
                    return 0;

                if (buckets[idx] == 0) {
                    buckets[idx] = element;
                    return 1;
                }

                idx--;
            }
        } else {
            final int limit = idx + middle/4;
            while (idx < limit) {
                if (buckets[idx] == element)
                    return 0;

                if (buckets[idx] == 0) {
                    buckets[idx] = element;
                    return 1;
                }

                idx++;
            }
        }

        return 2; // no space, must resize
    }

    @Override
    public boolean remove(long element) {
        version++;
        return removeInternal(element);
    }

    @Override
    public boolean removeAll(long[] elements) {
        version++;
        boolean changed = false;
        for (long l : elements) {
            changed |= removeInternal(l);
        }
        return changed;
    }

    @Override
    public boolean removeAll(LongSet elements) {
        version++;
        boolean changed = false;
        LongIterator itr = elements.iterator();
        while (itr.hasNext()) {
            changed |= removeInternal(itr.next());
        }
        return changed;
    }

    @Override
    public boolean retainAll(long[] elements) {
        version++;
        boolean changed = false;
        if (containsZero && Longs.indexOf(elements, 0) < 0) {
            // this.contains(0) && !that.contains(0)
            containsZero = false;
            changed = true;
        }
        for (long l : buckets) {
            if (l == 0) continue;

            if (Longs.indexOf(elements, l) < 0) {
                removeInternal(l);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(LongSet elements) {
        version++;
        boolean changed = false;
        if (containsZero && !elements.contains(0)) {
            containsZero = false;
            changed = true;
        }
        for (long l : buckets) {
            if (l == 0) continue;

            if (!elements.contains(l)) {
                removeInternal(l);
                changed = true;
            }
        }
        return changed;
    }

    private boolean removeInternal(long element) {
        if (element == 0) {
            if (!containsZero) {
                return false;
            } else {
                containsZero = false;
                size--;
                return true;
            }
        }

        int idx = bucketOf(element);
        long target = buckets[idx];
        if (target == 0)
            return false;

        boolean changed = false;
        if (idx > middle) {
            final int limit = idx - middle/4;
            while (idx > limit) {
                long value = buckets[idx];
                if (value == element) {
                    buckets[idx] = 0;
                    changed = true;
                    size--;
                    continue;
                }

                if (value == 0)
                    return changed;

                if (changed) { // values shifted, re-insert it
                    buckets[idx] = 0;
                    addInternal(value);
                }

                idx--;
            }
        } else {
            final int limit = idx + middle/4;
            while (idx < limit) {
                long value = buckets[idx];
                if (value == element) {
                    buckets[idx] = 0;
                    changed = true;
                    size--;
                    continue;
                }

                if (value == 0)
                    return changed;

                if (changed) {
                    buckets[idx] = 0;
                    addInternal(value);
                }

                idx++;
            }
        }

        return changed;
    }

    @Override
    public MutableLongIterator iterator() {
        return new MutableLongIterator() {
            int currentBucket = -1;
            int visited = 0;
            int version = MutableLongHashSet.this.version;
            @Override public boolean hasNext() {
                return visited < size;
            }
            @Override public long next() {
                checkComod();

                if (visited++ == size)
                    throw new NoSuchElementException();

                if (containsZero && visited == size)
                    return 0;

                while (true)
                    if (buckets[++currentBucket] != 0)
                        return buckets[currentBucket];
            }
            @Override public void remove() {
                checkComod();
                if (currentBucket < 0) {
                    throw new IllegalStateException("Iterator is not initialized.");
                }

                if (!MutableLongHashSet.this.removeInternal(buckets[currentBucket]))
                    throw new AssertionError();

                version++;
                MutableLongHashSet.this.version++;
            }
            @Override public void reset() {
                currentBucket = -1;
                visited = 0;
                version = MutableLongHashSet.this.version;
            }
            private void checkComod() {
                if (version != MutableLongHashSet.this.version) {
                    throw new ConcurrentModificationException("This collection was modified while iterating.");
                }
            }
        };
    }

    @Override
    public int addOrRemove(long element) {
        // todo: may be faster
        version++;
        if (contains(element)) {
            if (!removeInternal(element))
                throw new AssertionError("failed to remove " + element + ", buckets: " + Arrays.toString(buckets));
            return -1;
        } else {
            if (!addInternal(element))
                throw new AssertionError();
            return +1;
        }
    }

    // internal

    private int bucketOf(long element) {
        int h = (int) ((element >>> 32) ^ element);
        h ^= (h >>> 20) ^ (h >>> 12);
        h ^= (h >>> 7) ^ (h >>> 4);
        return h & buckets.length-1;
    }

}
