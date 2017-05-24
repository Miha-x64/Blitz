@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LongSets")

package net.aquadc.blitz

import net.aquadc.blitz.impl.ImmutableLongTreeSet
import net.aquadc.blitz.impl.MutableLongHashSet

/**
 * Created by miha on 28.03.17
 */

// factory

inline fun mutableLongSetOf(): MutableLongSet = MutableLongHashSet()
inline fun mutableLongSetOf(vararg elements: Long): MutableLongSet = MutableLongHashSet(elements)

inline fun immutableLongSetOf(): ImmutableLongSet = ImmutableLongTreeSet.empty()
inline fun immutableLongSetOf(element: Long): ImmutableLongSet = ImmutableLongTreeSet.singleton(element)
inline fun immutableLongSetOf(vararg elements: Long): ImmutableLongSet = ImmutableLongTreeSet.from(elements)

inline fun Long.toImmutableLongSet(): ImmutableLongSet = ImmutableLongTreeSet.singleton(this)
inline fun LongArray.toImmutableLongSet(): ImmutableLongSet = ImmutableLongTreeSet.from(this)
inline fun LongArray.toMutableLongSet(): MutableLongSet = MutableLongHashSet(this)

// common

// already picked up by Kotlin
//inline operator fun LongSet.contains(l: Long) = contains(l)
//inline operator fun LongSet.iterator()

inline operator fun LongSet.contains(l: LongArray) = containsAll(l)
inline operator fun LongSet.contains(l: LongSet) = containsAll(l)

inline fun LongSet.single(): Long = when (size()) {
    0 -> throw NoSuchElementException("Collection is empty.")
    1 -> if (this is OrderedLongSet) get(0) else iterator().next()
    else -> throw IllegalArgumentException("Collection has more than one element.")
}

inline fun LongSet.singleOrDefault(default: Long) = when (size()) {
    1 -> if (this is OrderedLongSet) get(0) else iterator().next()
    else -> default
}

inline fun LongSet.filtered(predicate: (Long) -> Boolean): LongSet = filterTo(MutableLongHashSet(), predicate)
inline fun LongSet.filteredNot(predicate: (Long) -> Boolean): LongSet = filterNotTo(MutableLongHashSet(), predicate)

inline fun LongSet.filterTo(destination: MutableLongSet, predicate: (Long) -> Boolean): MutableLongSet {
    for (element in this) if (predicate(element)) destination.add(element)
    return destination
}
inline fun LongSet.filterNotTo(destination: MutableLongSet, predicate: (Long) -> Boolean): MutableLongSet {
    for (element in this) if (!predicate(element)) destination.add(element)
    return destination
}

inline fun LongSet.asSortedArray(): LongArray = copyToArray().also { java.util.Arrays.sort(it) }

inline fun LongSet.forEach(code: (Long)->Unit) {
    val itr = iterator()
    while (itr.hasNext()) code(itr.next())
}

inline fun <T : LongSet> T.onEach(code: (Long)->Unit): T = apply {
    val itr = iterator()
    while (itr.hasNext()) code(itr.next())
}

inline fun <T> Collection<T>.mapToLongs(mapToLong: (T) -> Long) =
        MutableLongHashSet(size).also { set ->
            forEach { t -> set.add(mapToLong(t)) }
        }

inline fun <T> Array<T>.mapToLongs(mapToLong: (T) -> Long) =
        MutableLongHashSet(size).also { set ->
            forEach { t -> set.add(mapToLong(t)) }
        }

inline fun LongSet.joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "..."
): String {
    return joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated).toString()
}

fun <A : Appendable> LongSet.joinTo(
        buffer: A,
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "..."
): A {
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) buffer.append(element.toString()) else break
    }
    if (limit >= 0 && count > limit) buffer.append(truncated)
    buffer.append(postfix)
    return buffer
}

inline fun LongSet.average(): Double {
    var sum: Double = 0.0
    var count: Int = 0
    for (element in this) {
        sum += element
        count += 1
    }
    if (count == 0) throw NoSuchElementException()
    return sum / count
}

inline fun LongSet.sum(): Long {
    var sum: Long = 0L
    for (element in this) sum += element
    return sum
}

// mutable

inline operator fun MutableLongSet.plusAssign(l: Long) { add(l) }
inline operator fun MutableLongSet.plusAssign(l: LongArray) { addAll(l) }
inline operator fun MutableLongSet.plusAssign(l: LongSet) { addAll(l) }

inline operator fun MutableLongSet.minusAssign(l: Long) { remove(l) }
inline operator fun MutableLongSet.minusAssign(l: LongArray) { removeAll(l) }
inline operator fun MutableLongSet.minusAssign(l: LongSet) { removeAll(l) }

// immutable

inline operator fun ImmutableLongSet.plus(l: Long): ImmutableLongSet = with(l)
inline operator fun ImmutableLongSet.plus(l: LongArray): ImmutableLongSet = withAll(l)
inline operator fun ImmutableLongSet.plus(l: LongSet): ImmutableLongSet = withAll(l)

inline operator fun ImmutableLongSet.minus(l: Long): ImmutableLongSet = without(l)
inline operator fun ImmutableLongSet.minus(l: LongArray): ImmutableLongSet = withoutAll(l)
inline operator fun ImmutableLongSet.minus(l: LongSet): ImmutableLongSet = withoutAll(l)