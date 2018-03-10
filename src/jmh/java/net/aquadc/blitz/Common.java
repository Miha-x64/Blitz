package net.aquadc.blitz;

import net.aquadc.blitz.impl.MutableLongHashSet;
import net.aquadc.blitz.impl.MutableLongTreeSet;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Common {

    static Object createAndFill(String setClass, int setSize) {
        Object set;
        switch (setClass) {
            case "MutableLongTreeSet":
                set = new MutableLongTreeSet(setSize);
                break;

            case "MutableLongHashSet":
                set = new MutableLongHashSet(setSize);
                break;

            case "TreeSet":
                set = new TreeSet<>();
                break;

            case "HashSet":
                set = new HashSet<>(setSize);
                break;

            default:
                throw new AssertionError();
        }

        Random random = new Random("before".hashCode());
        if (set instanceof MutableLongSet) {
            MutableLongSet pSet = (MutableLongSet) set;
            for (int i = 0; i < setSize; i++) {
                pSet.add(random.nextLong());
            }
        } else if (set instanceof Set<?>) {
            Set<Long> jSet = (Set<Long>) set;
            for (int i = 0; i < setSize; i++) {
                jSet.add(random.nextLong());
            }
        } else {
            throw new AssertionError();
        }

        return set;
    }

}
