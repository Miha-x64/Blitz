package net.aquadc.blitz;

/**
 * Created by miha on 28.01.17
 */
public interface LongIterator {
    boolean hasNext();
    long next();

    /**
     * Brings iterator to its initial state including version / modCount, position, etc.
     */
    void reset();
}
