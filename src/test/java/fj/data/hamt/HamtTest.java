package fj.data.hamt;

import fj.Equal;
import fj.Hash;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Stream;
import org.junit.Test;

import static fj.P.p;
import static java.lang.System.out;

/**
 * Created by maperr on 3/06/2016.
 */
public class HamtTest {

    HashArrayMappedTrie<Integer, Integer> empty() {
        HashArrayMappedTrie<Integer, Integer> empty = HashArrayMappedTrie.empty(Equal.intEqual, Hash.intHash);
        return empty;
    }

    @Test
    public void testEmpty() {
        out.println(empty().toString());
    }

    @Test
    public void one() {
        out.println(empty().set(3, 6));
    }

    @Test
    public void update() {
        HashArrayMappedTrie<Integer, Integer> h1 = empty();
        HashArrayMappedTrie<Integer, Integer> h2 = h1.set(3, 3);
        HashArrayMappedTrie<Integer, Integer> h3 = h2.set(3, 5);
        out.println(h1);
        out.println(h2);
        out.println(h3);
    }

    @Test
    public void subtrie() {
        HashArrayMappedTrie<Integer, Integer> h = empty();
        HashArrayMappedTrie<Integer, Integer> h2 = h.set(List.list(p(0, 1), p(31, 1), p(32, 1), p(33, 1)));
        out.println(h);
        out.println(h2);

        Stream<P2<Integer, Integer>> s = h2.toStream();
        out.println(s.toList());
    }

}
