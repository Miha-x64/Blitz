# Blitz
Blitz! — lightning fast primitive collections for Java

I work with sets of longs every day. SQLite primary keys and many other things are longs. So I've written these collections: MutableLongTreeSet and ImmutableLongTreeSet.

This project is currently work in progress. I'll be glad to see your suggestions, so feel free to open feature requests; API is quite experimental and is a subject to change.

## API

### MutableLongSet
(implementation: MutableLongTreeSet)
```java
interface MutableLongTreeSet extends LongSet {
    boolean add(long element);
    boolean addAll(long[] elements);
    boolean addAll(LongSet elements);
    boolean remove(long element);
    boolean removeAll(long[] elements);
    boolean removeAll(LongSet elements);
    boolean retainAll(long[] elements);
    boolean retainAll(LongSet elements);
    MutableLongIterator iterator();
    int addOrRemove(long element);
}
```

### ImmutableLongSet
(implementation: ImmutableLongTreeSet)
```java
interface ImmutableLongSet extends LongSet {
    ImmutableLongSet with(long element);
    ImmutableLongSet withAll(long[] elements);
    ImmutableLongSet withAll(LongSet elements);
    ImmutableLongSet without(long element);
    ImmutableLongSet withoutAll(long[] elements);
    ImmutableLongSet withoutAll(LongSet elements);
    ImmutableLongSet intersectionWith(long[] elements);
    ImmutableLongSet intersectionWith(LongSet elements);
}
```

### Both interfaces derive these ones
```java
interface LongSet extends PrimitiveSet<Long> {
    long get(int index);
    int indexOf(long element);
    boolean contains(long element);
    boolean containsAll(long[] elements);
    boolean containsAll(LongSet elements);
    boolean containsAny(long[] elements);
    boolean containsAny(LongSet elements);
    MutableLongSet copyToMutable();
    ImmutableLongSet asImmutable();
    long[] copyToArray();
    LongIterator iterator();
}
```

```java
public interface PrimitiveSet<E> {
    int size();
    boolean isEmpty();
    String toString();
    boolean equals(Object other);
    int hashCode();
}
```

## Performance

I believe `MutableLongTreeSet` is faster than `TreeSet<Long>`
and much more memory-efficient. I know that I should measure it. ;)

## Threading

`MutableLongTreeSet` is absolutely **not** thread-safe.
In multithreaded environment, use immutable collections,
or drop me a line and I will implement `ConcurrentLongTreeSet`.