package fj.data.hamt;

import fj.F;
import fj.P;
import fj.P2;
import fj.data.Either;
import fj.data.Option;
import fj.data.Stream;

/**
 * Created by maperr on 31/05/2016.
 */
public final class Node<K, V> {

    private final Either<SimpleNode<K, V>, HashArrayMappedTrie<K, V>> either;

    public Node(final Either<SimpleNode<K, V>, HashArrayMappedTrie<K, V>> e) {
        either = e;
    }

    public Node(final SimpleNode<K, V> simpleNode) {
        this(Either.left(simpleNode));
    }

    public Node(final HashArrayMappedTrie<K, V> hamt) {
        this(Either.right(hamt));
    }

    public static <K, V> Node<K, V> simpleNodeNode(final SimpleNode<K, V> sn) {
        return new Node<>(sn);
    }

    public static <K, V> Node<K, V> hamtNode(final HashArrayMappedTrie<K, V> hamt) {
        return new Node<>(hamt);
    }

    public Option<V> find(final F<SimpleNode<K, V>, Option<V>> f, final F<HashArrayMappedTrie<K, V>, Option<V>> g) {
        return match(sn -> f.f(sn), hamt -> g.f(hamt));
    }

    public <B> B match(F<SimpleNode<K, V>, B> f, F<HashArrayMappedTrie<K, V>, B> g) {
        return either.either(sn -> f.f(sn), hamt -> g.f(hamt));
    }

    public Stream<P2<K, V>> toStream() {
        return match(sn -> Stream.single(P.p(sn.getKey(), sn.getValue())), h -> h.toStream());
    }

    public String toString() {
        return "Node(" + either.toString() + ")";
    }

}
