package fj.data;

import fj.P2;
import org.junit.Test;

/**
 * Created by maperr on 7/06/2016.
 */
public class SeqTest {

    @Test
    public void test() {
        P2<Seq<Integer>, Seq<Integer>> p2 = Seq.single(1).split(5);
        System.out.println(p2);
    }

}
