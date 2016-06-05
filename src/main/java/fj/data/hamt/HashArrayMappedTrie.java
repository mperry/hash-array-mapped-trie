package fj.data.hamt;

import fj.Equal;
import fj.Hash;
import fj.P2;
import fj.data.Array;
import fj.data.List;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Stream;

/**
 * Created by maperr on 31/05/2016.
 */
public class HashArrayMappedTrie<K, V> {

    Seq<Option<SeqNode<K, V>>> seq;
    Hash<K> hash;
    Equal<K> equal;

    public static final int BITS_IN_INDEX = 5;
    public static final int SIZE = (int) Math.pow(2, BITS_IN_INDEX);
    public static final int MIN_INDEX = 0;
    public static final int MAX_INDEX = SIZE - 1;

    private HashArrayMappedTrie(Seq<Option<SeqNode<K, V>>> s, Equal<K> e, Hash<K> h) {
        seq = s;
        hash = h;
        equal = e;
    }

    public static <K, V> HashArrayMappedTrie<K, V> empty(Equal<K> e, Hash<K> h) {
        Array<Option<SeqNode<K, V>>> a = Array.range(MIN_INDEX, MAX_INDEX + 1).map(i -> Option.none());
        Seq<Option<SeqNode<K, V>>> s = Seq.seq(a.toJavaArray());
        return new HashArrayMappedTrie<>(s, e, h);
    }

    public static <K, V> HashArrayMappedTrie<K, V> hamt(Seq<Option<SeqNode<K, V>>> s, Equal<K> e, Hash<K> h) {
        return new HashArrayMappedTrie<>(s, e, h);
    }

    public Option<V> find(K k) {
        return find(k, 0, BITS_IN_INDEX);
    }

    public Option<V> find(K k, int lowIndex, int highIndex) {

        int bits = bitsBetween(hash.hash(k), lowIndex, highIndex);
        Option<SeqNode<K, V>> o = seq.index(bits);
        if (o.isNone()) {
            return Option.none();
        } else {
            SeqNode<K, V> sn = o.some();
            return sn.find(n -> {
                boolean b = equal.eq(n.getKey(), k);
                return b ? Option.some(n.getValue()) : Option.none();
            }, hamt -> {
                return hamt.find(k, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX);
            });
        }
    }

    public HashArrayMappedTrie<K, V> set(K k, V v) {
        return set(k, v, 0, BITS_IN_INDEX);
    }

    public HashArrayMappedTrie<K, V> set(List<P2<K, V>> list) {
        return list.foldLeft(h -> p -> h.set(p._1(), p._2()), this);
    }

    public HashArrayMappedTrie<K, V> set(K k, V v, int lowIndex, int highIndex) {

        int bits = bitsBetween(hash.hash(k), lowIndex, highIndex);
        Option<SeqNode<K, V>> o = seq.index(bits);
        if (o.isNone()) {
            SimpleNode<K, V> sn = SimpleNode.simpleNode(k, v);
            return hamt(seq.update(bits, Option.some(SeqNode.seqNode(sn))), equal, hash);
        } else {
            SeqNode<K, V> sn = o.some();
            SeqNode<K, V> match = sn.match(n -> {
                boolean b = equal.eq(n.getKey(), k);
                if (b) {
                    return SeqNode.seqNode(SimpleNode.simpleNode(k, v));
                } else {
                    HashArrayMappedTrie<K, V> hamt = HashArrayMappedTrie.<K, V>empty(equal, hash)
                            .set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX)
                            .set(n.getKey(), n.getValue(), lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX);
                    return SeqNode.seqNode(hamt);
                }
            }, hamt -> {
                SeqNode<K, V> kvSeqNode = SeqNode.seqNode(hamt.set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX));
                return kvSeqNode;
//                return null;
            });
            return hamt(seq.update(bits, Option.some(match)), equal, hash);

        }

    }

    // bits between low (inclusive) and high (exclusive)
    public static int bitsBetween(int n, int low, int high) {
        return (int) BitSet.fromLong(n).range(high, low).longValue();
    }

    public Stream<P2<K, V>> toStream() {
        return SeqUtil.filter(seq, o -> o.isSome()).toStream().bind(o -> o.some().toStream());
    }

    public String toString() {
        return "HashArrayMappedTrie(" + seq.toString() + ")";
    }

}
