package fj.data.hamt;

import fj.P;
import fj.P2;

/**
 * Created by maperr on 31/05/2016.
 */
public class SimpleNode<K, V> {
    P2<K, V> pair;

    private SimpleNode(K k, V v) {
        pair = P.p(k, v);
    }

    public static <K, V> SimpleNode<K, V> simpleNode(K k, V v) {
        return new SimpleNode<K, V>(k, v);
    }

    public K getKey() {
        return pair._1();
    }

    public V getValue() {
        return pair._2();
    }


    public String toString() {
        return "SimpleNode" + pair.toString() + "";
    }
}
