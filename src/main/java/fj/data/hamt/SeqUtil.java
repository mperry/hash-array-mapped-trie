package fj.data.hamt;

import fj.F;
import fj.data.Seq;



/**
 * Created by maperr on 6/06/2016.
 */
public class SeqUtil {

    public static <A> Seq<A> filter(Seq<A> s, fj.F<A, Boolean> f) {

        return s.foldLeft((Seq<A> acc, A a) -> f.f(a) ? acc.snoc(a) : acc, Seq.<A>empty());
    }
}
