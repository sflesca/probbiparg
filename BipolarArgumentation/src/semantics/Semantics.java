package semantics;

import java.util.*;

import structures.*;

public abstract class Semantics {
        public abstract boolean evaluate(DAF daf, ArgSet set);

        public DAF montecarloSample(PAF paf, ArgSet set) {
                DAF daf;
                do {
                        daf = new DAF();
                        for (String a : paf.argsList) {
                                if (paf.args.get(a) >= Math.random()) {
                                        daf.addArg(a);
                                }
                        }
                        for (String a : daf.getArgs()) {
                                for (String b : paf.getDefeats(a).keySet()) {
                                        if (daf.getArgs().contains(b)
                                                        && paf.defeats.get(a).get(b) >= Math.random()) {
                                                daf.addDefeat(a, b);
                                        }
                                }
                        }
                } while (filterSample(daf, set));
                return daf;
        }

        public boolean filterSample(DAF daf, ArgSet set) {
                return false; // don't filter by default
        }

        public double conditional(PAF paf, ArgSet set) {
                return 1;
        }


    public String toString () {
        return "Naive";
    }
}