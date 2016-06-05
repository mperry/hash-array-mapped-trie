package fj.data.hamt;

import fj.Equal;
import fj.data.List;
import fj.function.Booleans;
import fj.test.Arbitrary;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.data.hamt.BitSet.fromLong;
import static fj.test.Arbitrary.arbBoolean;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbIntegerBoundaries;
import static fj.test.Arbitrary.arbList;
import static fj.test.Arbitrary.arbLong;
import static fj.test.Arbitrary.arbitrary;
import static fj.test.Property.implies;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static java.lang.System.out;

/**
 * Created by maperr on 31/05/2016.
 */

@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class BitSetProperties {


    Property longRoundTrip() {
        return property(arbBoundedLong, l -> prop(fromLong(l).longValue() == l));
    }

    Property emptyIsEmpty() {
        return property(arbBoolean, i -> prop(BitSet.empty().isEmpty()));
    }

    Property generalEmptiness() {
        return property(arbList(arbBoolean), list ->
                prop(list.dropWhile(Booleans.not).isEmpty() == BitSet.fromList(list).isEmpty())
        );
    }

    Property toList() {
        Arbitrary<List<Boolean>> alb = arbitrary(Gen.choose(0, BitSet.BITS).bind(i -> Gen.sequenceN(i, arbBoolean.gen)));
        return property(alb, list -> {
//            int n = list.length();
            List<Boolean> list1 = list.dropWhile(Booleans.not);
            List<Boolean> list2 = BitSet.fromList(list).toList();
            boolean b = Equal.listEqual(Equal.booleanEqual).eq(list1, list2);
            return prop(b);
        });

//        Gen.choose(0, 100);
//        return property(arbList(arbBoolean), list -> {
//            return implies(list.length() < Long.SIZE, () -> {
//                List<Boolean> list1 = list.dropWhile(b -> !b);
//                List<Boolean> list2 = BitSet.fromList(list).toList();
//                boolean b = Equal.listEqual(Equal.booleanEqual).eq(list1, list2);
//                if (!b) {
//                    out.println(list.length());
//                    int z = 0;
//                }
//
//                return prop(b);
//            });
//        });
    }

    Property isSet() {
        return property(arbBoundedLong, arbitrary(Gen.choose(0, BitSet.BITS)),
                (Long l, Integer i) -> prop(fromLong(l).isSet(i) == ((l & (1 << i)) != 0))
        );
    }

    Property strings() {
        return property(arbBoundedLong, l ->
                prop(BitSet.fromString(BitSet.fromLong(l).asString()).longValue() == l)
        );
    }

    static final Arbitrary<Long> arbNonNegativeLong = arbitrary(arbLong.gen.map(l -> l < 0 ? -l : l));

    static final Arbitrary<Long> arbBoundedLong = arbitrary(Gen.choose(0, 1000).map(i -> i.longValue()));

}
