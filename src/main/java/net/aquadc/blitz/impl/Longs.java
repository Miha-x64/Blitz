package net.aquadc.blitz.impl;

import net.aquadc.blitz.OrderedLongSet;

/**
 * Created by mike on 25.01.17
 */

final class Longs {

    private Longs() {}

    static final long[] EMPTY = new long[0];

    // from java.util.Arrays
    // Like public version, but without range checks.
    static int binarySearch0(long[] a, int fromIndex, int toIndex, long key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    static long[] reallocAndInsert(long[] array, int oldSize, int newSize, int insertionIndex, long value) {
        // create gap at pos, expand array

        // new array
        long[] newLongs = allocate(newSize);

        if (insertionIndex > 0) {
            // copy data before gap [0; insertionIndex)
            System.arraycopy(array, 0, newLongs, 0, insertionIndex);
        }
        if (insertionIndex < oldSize) {
            // copy data after gap (insertionIndex; size)
            System.arraycopy(array, insertionIndex, newLongs, insertionIndex + 1, oldSize - insertionIndex);
        }

        newLongs[insertionIndex] = value;
//            System.out.println(l + " inserted at " + pos + " with array expansion, " + Arrays.toString(longs) + " -> " + Arrays.toString(newLongs));

        free(array);

        return newLongs;
    }

    static void insert(long[] array, int insertionIndex, int size, long value) {
        // just move elements after insertionIndex if necessary
        if (insertionIndex < size) {
            // copy data after insertionIndex (insertionIndex; size)
            System.arraycopy(array, insertionIndex, array, insertionIndex + 1, size - insertionIndex);
        }

        array[insertionIndex] = value;
    }

    static int indexOf(long[] array, long value) {
        for (int i = 0, size = array.length; i < size; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    static String toString(long[] longs, int size) {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder(5 * size); // 3-digit numbers expected
        sb.append('[');
        int last = size - 1;
        for (int i = 0; i <= last; i++) {
            sb.append(longs[i]);
            if (i != last) {
                sb.append(", ");
            }
        }

        return sb.append(']').toString();
    }

    static boolean orderedSetsEqual(long[] array, int size, OrderedLongSet b) {
        if (size != b.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (array[i] != b.get(i)) {
                return false;
            }
        }
        return true;
    }

    static int hashCodeOfSet(long[] array, int size) {
        int hashCode = 0;
        for (int i = 0; i < size; i++) {
            long value = array[i];
            hashCode += (int) (value ^ (value >>> 32));
        }
        return hashCode;
    }

    static long[] allocate(int size) {
        int i = 4;
        while (i < size) {
            i <<= 1;
        }
//        Log.e("MutableLongTreeSet", "allocated long[" + i + ']'); // fixme
        return new long[i];
    }

    static boolean containingAll(long[] where, long[] from) {
        for (long val : from) {
            if (indexOf(where, val) == -1) {
                return false;
            }
        }
        return true;
    }

    static void free(long[] array) {
        // no-op, hello, it's JVM ;)
        // todo: pooling
//        Log.e("MutableLongTreeSet", "long[" + array.length + "] is breaking free!"); // fixme
    }

}
