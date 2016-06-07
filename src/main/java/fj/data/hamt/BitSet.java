package fj.data.hamt;

import fj.F2;
import fj.F2Functions;
import fj.Show;
import fj.data.List;
import fj.data.Stream;

/**
 * Created by maperr on 31/05/2016.
 *
 * BitSet("1011") represents the number 11 and has indices [3, 0] inclusive where the lowest index
 * is the rightmost bit
 */
public class BitSet {

    public static final char TRUE_CHAR = '1';
    public static final char FALSE_CHAR = '0';

    public static final int TRUE_BIT = 1;
    public static final int FALSE_BIT = 0;

    public static final int BITS = Long.SIZE;

    private long value;

    private BitSet(long l) {
        value = l;
    }

    public static BitSet empty() {
        return new BitSet(FALSE_BIT);
    }

    public static BitSet fromLong(long l) {
        return new BitSet(l);
    }

    // most significant bit first [1, 1, 0] = 6
    public static BitSet fromList(List<Boolean> list) {
        int n = Long.SIZE;
        if (list.length() > n) {
            throw new IllegalArgumentException("Does not support lists greater than " + n + " bits");
        }
        long result = 0;
        for (Boolean b: list) {
            result = (result << 1) | toInt(b);
        }
        return fromLong(result);
    }

    public static BitSet fromStream(Stream<Boolean> s) {
        return fromList(s.toList());
    }

    public static BitSet fromString(String s) {
        return fromStream(Stream.fromString(s).map(c -> toBoolean(c)));
    }

    public boolean isSet(int index) {
        return (value & (1L << index)) != 0;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public BitSet set(int index) {
        return fromLong(value | (1L << index));
    }

    public BitSet set(int index, boolean b) {
        return b ? set(index) : clear(index);
    }

    public BitSet clear(int index) {
        // TODO, this looks wrong, need xor
        return and(fromLong(1L << index).not());
    }

    public long longValue() {
        return value;
    }

    public BitSet and(BitSet bs) {
        return fromLong(value & bs.longValue());
    }

    public BitSet or(BitSet bs) {
        return fromLong(value | bs.longValue());
    }

    public BitSet shiftRight(int n) {
        return fromLong(value >> n);
    }

    public BitSet shiftLeft(int n) {
        return fromLong(value << n);
    }

    public int bitsUsed() {
        return toStream().length();
    }

    public Stream<Boolean> toStream() {
        return Stream.fromString(Long.toBinaryString(value)).map(c -> toBoolean(c)).dropWhile(b -> !b);
    }

    public String asString() {
        return Long.toBinaryString(value);
    }

    public String toString() {
        return "BitSet(" + asString() + ")";
    }

    public int bitsToRight(int index) {
        //  fromString("10101111").bitsRoRight(2)= 2
        int pos = index - 1;
        long mask = 1 << (pos);
        int result = 0;
        while (pos >= 0) {
            if ((mask & value) != 0) {
                result++;
            }
            mask = mask >> 1;
            pos--;
        }
        return result;
    }

    public Stream<Boolean> toReverseStream() {
        return toStream().reverse();
    }

    public List<Boolean> toReverseList() {
        return toReverseStream().toList();
    }

    public List<Boolean> toList() {
        return toStream().toList();
    }

    public <A> A foldRight(F2<Boolean, A, A> f, A acc) {
        return toStream().foldRight(b -> p -> f.f(b, p._1()), acc);
    }

    public <A> A foldLeft(A acc, F2<A, Boolean, A> f) {
        return toStream().foldLeft((a, b) -> f.f(a, b), acc);
    }

    public BitSet xor(BitSet bs) {
        return fromLong(value ^ bs.longValue());
    }

    public BitSet not() {
        return fromLong(~value);
    }

    public BitSet takeLower(int n) {
        return fromStream(toReverseStream().take(n).reverse());
    }

    public BitSet takeUpper(int n) {
        return fromStream(toStream().take(n));
    }

    // min index starts from the least significant bit (on the right), e.g. "101101".range(1, 4) == "110"
    public BitSet range(int highIndex, int lowIndex) {
        int max = Math.max(lowIndex, highIndex);
        int min = Math.min(lowIndex, highIndex);
        return fromStream(toReverseStream().drop(min).take(max - min).reverse());
    }

    public static boolean toBoolean(char c) {
        return Character.toString(c).equals(Integer.toString(TRUE_BIT));
    }

    public static boolean toBoolean(int i) {
        return i != FALSE_BIT;
    }

    public static int toInt(boolean b) {
        return b ? TRUE_BIT : FALSE_BIT;
    }

    public static char toChar(boolean b) {
        return b ? TRUE_CHAR : FALSE_CHAR;
    }

    public static final Show<BitSet> bitSetShow = Show.show(bs -> Stream.fromString(bs.toString()));

}
