package fj.data.hamt;

import fj.F2;
import fj.F2Functions;
import fj.Show;
import fj.data.List;
import fj.data.Stream;

/**
 * Created by maperr on 31/05/2016.
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
        return (value & (1 << index)) != 0;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public BitSet set(int index) {
        return fromLong(value | (1 << index));
    }

    public BitSet set(int index, boolean b) {
        return b ? set(index) : clear(index);
    }

    public BitSet clear(int index) {
        return and(fromLong(1 << index).not());
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
        return toReverseStream().length();
    }

    public Stream<Boolean> toStream() {

//        Stream.nil()
        return toReverseStream().reverse();
    }

    public String asString() {
        return toStream().foldLeft((acc, b) -> acc + toChar(b), "");
    }

    public String toString() {
        return "BitSet(" + asString() + ")";
    }

    public Stream<Boolean> toReverseStream() {
        return value == 0 ? Stream.nil() : Stream.cons(isSet(0), () -> shiftRight(1).toReverseStream());
    }

    public List<Boolean> toReverseList() {
        return toReverseStream().toList();
//        return value == 0 ? List.nil() : List.cons(isSet(0), shiftRight(1).toReverseList());
    }

    public List<Boolean> toList() {
        return toReverseStream().toList().reverse();
    }

    public <A> A foldRight(F2<Boolean, A, A> f, A acc) {
        return toReverseStream().foldLeft(F2Functions.flip(f), acc);
    }

    public <A> A foldLeft(A acc, F2<A, Boolean, A> f) {
        return toReverseStream().foldRight((b, la) -> f.f(la._1(), b), acc);
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
