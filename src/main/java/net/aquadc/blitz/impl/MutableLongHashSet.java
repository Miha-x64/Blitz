package net.aquadc.blitz.impl;

import net.aquadc.blitz.*;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Created by mike on 07.02.17
 */
public final class MutableLongHashSet implements MutableLongSet {

    private static final float LOAD_FACTOR = .75F;

    public MutableLongHashSet() {
        buckets = new Node[4];
    }

    public MutableLongHashSet(int size) {
        int actualSize = 4;
        while (actualSize < size) {
            actualSize <<= 1;
        }
        buckets = new Node[actualSize];
    }

    public MutableLongHashSet(long[] elements) {
        buckets = new Node[4];
        addAll(elements);
    }

    public MutableLongHashSet(LongSet original) {
        this(original.size());
        addAll(original);
    }

    /*pkg*/ Node[] buckets;
    /*pkg*/ int size;
    /*pkg*/ int version;

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
        for (Node n : buckets) {
            Node o = n;
            while (o != null) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(o.value);
                o = o.next;
            }
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

    @Override // LOL, differently sized sets with the same contents are going to have different hash codes
    public int hashCode() {
        int hash = 0;
        for (Node n : buckets) {
            Node o = n;
            while (o != null) {
                long value = o.value;
                hash += (int) (value ^ (value >>> 32));
                o = o.next;
            }
        }
        return hash;
    }

    // LongSet

    public boolean contains(long element) {
        Node bucket = buckets[bucketOf(element)];
        while (bucket != null) {
            if (bucket.value == element) {
                return true;
            }
            bucket = bucket.next;
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
        long[] array = new long[size];
        int index = 0;
        for (Node n : buckets) {
            Node o = n;
            while (o != null) {
                array[index++] = o.value;
                o = o.next;
            }
        }
        if (array.length != index) {
            throw new AssertionError(); // todo rm
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
        int bucketIndex = bucketOf(element);
        Node node = buckets[bucketIndex];
        while (node != null) {
            if (node.value == element) {
                return false;
            }
            node = node.next;
        }

        int newSize = size + 1;
        int bucketsRequired = (int) (newSize / LOAD_FACTOR);
        if (bucketsRequired > buckets.length) {
            resize(bucketsRequired);
            bucketIndex = bucketOf(element); // update index after resize
        }

        buckets[bucketIndex] = new Node(element, buckets[bucketIndex]);
        size = newSize;
        return true;
    }

    @Override
    public boolean remove(long element) {
        version++;
        return removeInternal(element);
    }

    @Override public boolean removeAll(long[] elements) {
        return batchRemove(elements, true);
    }

    @Override public boolean removeAll(LongSet elements) {
        return batchRemove(elements, true);
    }

    @Override public boolean retainAll(long[] elements) {
        return batchRemove(elements, false);
    }

    @Override public boolean retainAll(LongSet elements) {
        return batchRemove(elements, false);
    }

    private boolean batchRemove(long[] elements, boolean remove) {
        version++;

        boolean changed = false;
        Node[] buckets = this.buckets;
        for (Node bucket : buckets) {
            Node n = bucket;
            while (n != null) {
                long value = n.value;
                int index = Longs.indexOf(elements, value);
                if (index >= 0 == remove) {
                    removeInternal(value);
                    changed = true;
                }
                n = n.next;
            }
        }
        return changed;
    }

    private boolean batchRemove(LongSet elements, boolean remove) {
        // copy of ^^
        version++;

        boolean changed = false;
        Node[] buckets = this.buckets;
        for (Node bucket : buckets) {
            Node n = bucket;
            while (n != null) {
                long value = n.value;
                if (elements.contains(value) == remove) {
                    removeInternal(value);
                    changed = true;
                }
                n = n.next;
            }
        }
        return changed;
    }

    private boolean removeInternal(long element) {
        int index = bucketOf(element);
        Node node = buckets[index];
        if (node == null) {
            return false;
        }

        if (node.value == element) {    // bucket: node0 -> node1 -> node2 ...
            buckets[index] = node.next; // rm node0. bucket: node1 -> node2 ...
            size--;
            return true;
        }

        Node previous = node;
        while ((node = previous.next) != null) {
            if (node.value == element) { // bucket: node0 -> node1 -> node2 ...
                previous.next = node.next; // rm node1: node0 -> node2 ...
                size--;
                return true;
            }

            previous = node;
        }
        return false; // end of the list
    }

    @Override
    public MutableLongIterator iterator() {
        return new MutableLongIterator() {
            int currentBucket = -1;
            Node previousNodeInBucket;
            Node currentNode;

            int nextElementIndex;
            int version = MutableLongHashSet.this.version;
            @Override public boolean hasNext() {
                return nextElementIndex < size;
            }
            @Override public long next() {
                checkComod();
                if (nextElementIndex++ == size) {
                    throw new NoSuchElementException();
                }

                Node currentNode = this.currentNode;
                Node previous = previousNodeInBucket;
                if (currentNode != null) { // step into linked list if we can
                    previous = currentNode;
                    currentNode = currentNode.next;
                }
                while (currentNode == null) { // we're in the middle of nowhere, move to next buckets
                    previous = null;
                    currentNode = buckets[++currentBucket];
                }
                this.currentNode = currentNode;
                this.previousNodeInBucket = previous;

                return currentNode.value;
            }
            @Override public void remove() {
                checkComod();
                if (currentNode == null) {
                    throw new IllegalStateException("Iterator is not initialized.");
                }

                version++;
                size--;
                nextElementIndex--;
                if (previousNodeInBucket == null) {
                    buckets[currentBucket] = currentNode.next; // unlink head
                } else {
                    previousNodeInBucket = currentNode.next; // unlink ordinary bucket
                }
                MutableLongHashSet.this.version++; // lol, iterator removing element from set without set's help
            }
            @Override public void reset() {
                currentBucket = -1;
                previousNodeInBucket = null;
                currentNode = null;
                nextElementIndex = 0;
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
            removeInternal(element);
            return -1;
        } else {
            addInternal(element);
            return +1;
        }
    }

    // internal

    private void resize(int requiredSize) {
        int newSize = 4;
        while (newSize < requiredSize) {
            newSize <<= 1;
        }

//        System.out.println("resizing from " + buckets.length + " to " + newSize);

        Node[] newBuckets = new Node[newSize];
        Node[] oldBuckets = buckets;
        for (Node oldBucket : oldBuckets) {
            while (oldBucket != null) {
                Node newBucket = oldBucket;
                oldBucket = oldBucket.next;

                int index = (int) (newSize-1 & newBucket.value); // keep in sync with `bucketOf(long)`
                newBucket.next = newBuckets[index];
                newBuckets[index] = newBucket;
            }

        }
        buckets = newBuckets;
    }

    private int bucketOf(long element) {
        return (int) (buckets.length-1 & element);
    }

    private static final class Node {
        long value;
        Node next;
        private Node(long val, Node next) {
            this.value = val;
            this.next = next;
        }
        @Override public String toString() {
            return "Node@" + Integer.toHexString(hashCode()) + "(" + value + ", " + next + ")";
        }
    }

}
