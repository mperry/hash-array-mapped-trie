package fj.data.hamt;

import fj.Equal;
import fj.F;
import fj.Hash;
import fj.P;
import fj.P2;
import fj.data.Either;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Stream;

/**
 * Created by maperr on 31/05/2016.
 */
public class SeqNode<K, V> {


    Either<SimpleNode<K, V>, HashArrayMappedTrie<K, V>> either;

    public SeqNode(Either<SimpleNode<K, V>, HashArrayMappedTrie<K, V>> e) {
        either = e;
    }

    public SeqNode(SimpleNode<K, V> simpleNode) {

        this(Either.left(simpleNode));
    }

    public SeqNode(HashArrayMappedTrie<K, V> hamt) {
        this(Either.right(hamt));
    }


    public static <K, V> SeqNode<K, V> seqNode(SimpleNode<K, V> sn) {
        return new SeqNode<>(sn);
    }

    public static <K, V> SeqNode<K, V> seqNode(HashArrayMappedTrie<K, V> hamt) {
        return new SeqNode<>(hamt);
    }

    public Option<V> find(F<SimpleNode<K, V>, Option<V>> f, F<HashArrayMappedTrie<K, V>, Option<V>> g) {
        Either<Option<V>, Option<V>> eo = either.bimap(f, g);
        return eo.isLeft() ? eo.left().value() : eo.right().value();

    }

    public <B> B match(F<SimpleNode<K, V>, B> sn, F<HashArrayMappedTrie<K, V>, B> hamt) {
        Either<B, B> e = either.bimap(sn, hamt);
        B b = e.isLeft() ? e.left().value() : e.right().value();
        return b;
    }

    public Stream<P2<K, V>> toStream() {
        return match(sn -> Stream.single(P.p(sn.getKey(), sn.getValue())), h -> h.toStream());
    }

    public String toString() {
        return "SeqNode(" + either.toString() + ")";
    }

}
