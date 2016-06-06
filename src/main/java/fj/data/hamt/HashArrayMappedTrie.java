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

//    Seq<Option<SeqNode<K, V>>> seq;
    Seq<SeqNode<K, V>> seq2;
    BitSet bitSet = BitSet.fromLong(0);

    Hash<K> hash;
    Equal<K> equal;

    public static final int BITS_IN_INDEX = 5;
    public static final int SIZE = (int) Math.pow(2, BITS_IN_INDEX);
    public static final int MIN_INDEX = 0;
    public static final int MAX_INDEX = SIZE - 1;

    private HashArrayMappedTrie(BitSet bs, Seq<SeqNode<K, V>> s, Equal<K> e, Hash<K> h) {
//    private HashArrayMappedTrie(Seq<Option<SeqNode<K, V>>> s, Equal<K> e, Hash<K> h) {
        bitSet = bs;
        seq2 = s;
        hash = h;
        equal = e;
    }

    public static <K, V> HashArrayMappedTrie<K, V> empty(Equal<K> e, Hash<K> h) {
//        Array<Option<SeqNode<K, V>>> a = Array.range(MIN_INDEX, MAX_INDEX + 1).map(i -> Option.none());
//        Seq<Option<SeqNode<K, V>>> s = Seq.seq(a.toJavaArray());
        return new HashArrayMappedTrie<>(BitSet.empty(), Seq.empty(), e, h);
    }

    public static <K, V> HashArrayMappedTrie<K, V> hamt(BitSet bs, Seq<SeqNode<K, V>> s, Equal<K> e, Hash<K> h) {
        return new HashArrayMappedTrie<>(bs, s, e, h);
    }

    public Option<V> find(K k) {
        return find(k, 0, BITS_IN_INDEX);
    }

    public Option<V> find(K k, int lowIndex, int highIndex) {

        int bits = bitsBetween(hash.hash(k), lowIndex, highIndex);

        // look up a 32 bit bitmap
        int seqIndex = bitSet.bitsToRight(bits);
        if (seqIndex >= seq2.length()) {
            return Option.none();
        }
        SeqNode<K, V> sn2 = seq2.index(seqIndex);
        return sn2.find(n -> {
            boolean b = equal.eq(n.getKey(), k);
            return b ? Option.some(n.getValue()) : Option.none();
        }, hamt -> {
            return hamt.find(k, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX);
        });


    }

    public HashArrayMappedTrie<K, V> set(K k, V v) {
        return set(k, v, 0, BITS_IN_INDEX);
    }

    public HashArrayMappedTrie<K, V> set(List<P2<K, V>> list) {
        return list.foldLeft(h -> p -> h.set(p._1(), p._2()), this);
    }

    public HashArrayMappedTrie<K, V> set(K k, V v, int lowIndex, int highIndex) {

        int bsIndex = bitsBetween(hash.hash(k), lowIndex, highIndex);

        if (!bitSet.isSet(bsIndex)) {
            // append new node
            SeqNode<K, V> sn1 = SeqNode.seqNode(SimpleNode.simpleNode(k, v));
            return HashArrayMappedTrie.hamt(bitSet.set(bsIndex), SeqUtil.insert(seq2, bsIndex, sn1), equal, hash);
        } else {
            int index = bitSet.bitsToRight(bsIndex);
            SeqNode<K, V> sn2 = seq2.index(index);
            SeqNode<K, V> match2 = sn2.match(n -> {
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
            // return
            // TODO
            return hamt(bitSet, seq2.update(index, match2), equal, hash);

        }


    }

    // bits between low (inclusive) and high (exclusive)
    public static int bitsBetween(int n, int low, int high) {
        return (int) BitSet.fromLong(n).range(high, low).longValue();
    }

    public Stream<P2<K, V>> toStream() {
        return seq2.toStream().bind(sn -> sn.toStream());
    }

    public String toString() {
        return "HashArrayMappedTrie(" + bitSet.toString() + ", " + seq2.toString() + ")";
    }

    public int length() {
        return seq2.foldLeft((acc, sn) -> sn.match(sn2 -> acc + 1, hamt -> acc + hamt.length()), 0);
    }

}
