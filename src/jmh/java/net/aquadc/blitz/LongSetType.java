package net.aquadc.blitz;


import com.carrotsearch.hppc.LongHashSet;
import com.koloboke.collect.set.hash.HashLongSet;
import com.koloboke.collect.set.hash.HashLongSets;
import gnu.trove.set.hash.TLongHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.aquadc.blitz.impl.MutableLongHashSet;

import java.util.HashSet;
import java.util.Random;
import java.util.TreeSet;

public enum LongSetType {
    MutableLongHashSet {
        @Override Object create(int size, Random source) {
            MutableLongHashSet set = new MutableLongHashSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    TreeSet {
        @Override Object create(int size, Random source) {
            java.util.TreeSet<Long> set = new TreeSet<>();
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    HashSet {
        @Override Object create(int size, Random source) {
            java.util.HashSet<Long> set = new HashSet<>(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    TLongHashSet {
        @Override Object create(int size, Random source) {
            TLongHashSet set = new TLongHashSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    HPPC_LongHashSet {
        @Override Object create(int size, Random source) {
            LongHashSet set = new LongHashSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    HPPC_RT_LongHashSet {
        @Override Object create(int size, Random source) {
            com.carrotsearch.hppcrt.sets.LongHashSet set = new com.carrotsearch.hppcrt.sets.LongHashSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    FastUtil_LongOpenHashSet {
        @Override Object create(int size, Random source) {
            LongOpenHashSet set = new LongOpenHashSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    Koloboke_HashLongSets {
        @Override Object create(int size, Random source) {
            HashLongSet set = HashLongSets.newMutableSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    Eclipse_LongHashSet {
        @Override Object create(int size, Random source) {
            org.eclipse.collections.impl.set.mutable.primitive.LongHashSet set = new org.eclipse.collections.impl.set.mutable.primitive.LongHashSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    },
    Agrona_LongHashSet {
        @Override Object create(int size, Random source) {
            org.agrona.collections.LongHashSet set = new org.agrona.collections.LongHashSet(size);
            for (int i = 0; i < size; i++)
                set.add(source.nextLong());
            return set;
        }
    }
    ;

    abstract Object create(int size, Random source);

}
