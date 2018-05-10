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
                        for (String a : daf.args) {
                                for (String b : paf.getDefeats(a).keySet()) {
                                        if (daf.args.contains(b)
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

        public static Semantics fromName(String name) {
                if (name.equals("null")) {
                        return null;
                }
                String[] split = name.split(" ");
                String className = split[0];
                int cacheSize = 0;
                if (split.length > 1) {
                        cacheSize = Integer.parseInt(split[1]);
                }
                try {
                        Semantics sem = (Semantics) Class.forName(className).newInstance();
                        if (cacheSize > 0) {
                                sem = new CachedSemantics(sem, cacheSize);
                        }
                        return sem;
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

    public String toString () {
        return "Naive";
    }
}

class ConflictFreeSemantics extends Semantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.conflictFree(set);
        }
}

class AdmissibleSemantics extends Semantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.admissible(set);
        }
}

class StableSemantics extends Semantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.stable(set);
        }
}

class CompleteSemantics extends Semantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.complete(set);
        }
}

class PreferredSemantics extends Semantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.preferred(set);
        }
}

class GroundedSemantics extends Semantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.grounded(set);
        }
}

abstract class GivenConflictFreeSemantics extends Semantics {
        @Override
        public DAF montecarloSample(PAF paf, ArgSet set) {
                DAF daf;
                do {
                        daf = new DAF();
                        for (String a : set) {
                                // args must be in daf for set to be conflict-free
                                daf.addArg(a);
                        }
                        for (String a : paf.argsList) {
                                double r = Math.random();
                                if (!set.contains(a) && paf.args.get(a) >= r) {
                                        daf.addArg(a);
                                }
                        }
                        for (String a : daf.args) {
                                for (String b : paf.getDefeats(a).keySet()) {
                                        if (set.contains(a) && set.contains(b)) {
                                                if (paf.defeats.get(a).get(b) == 1.0) {
                                                        // can't sample
                                                        return null;
                                                }
                                                // conflict
                                                continue;
                                        }
                                        if (daf.args.contains(b)
                                                        && paf.defeats.get(a).get(b) >= Math.random()) {
                                                daf.addDefeat(a, b);
                                        }
                                }
                        }
                } while (filterSample(daf, set));
                return daf;
        }

        @Override
        public double conditional(PAF paf, ArgSet set) {
                return paf.conflictFree(set);
        }

    @Override
    public String toString () {
        return "CF-based";
    }
}

abstract class GivenAdmissibleSemantics extends Semantics {
    @Override
    public DAF montecarloSample(PAF paf, ArgSet set) {
        DAF daf;
        do {
            daf = new DAF();
            Set<String> attackedByS = new HashSet<String>();
            HashMap<String, Double> defeatsToS = new HashMap<String, Double>(); // per argument
            HashMap<String, Double> defeatsFromS = new HashMap<String, Double>(); // per argument
            for (String a : paf.argsList) {
                if (set.contains(a)) {
                    // the set must be in daf for the set itself to be admissible
                    daf.addArg (a);
                    continue;
                }
                // We have to pick "a" given that we are generating an admissible AAF.
                // The probability for which "a" occurs then is not P(a) but P(a|locadm).
                // P(a|locadm) = P(locadm|a)*P(a)/P(locadm)
                // P(locadm|a) = P(a not attacks S) + P(a attacks S)*P(S attacks a)
                // P(locadm) = P(locadm|a)*P(a) + P(locadm|not a)*P(not a)
                // P(locadm|not a) = 1

                double probAttackS = 1.0d;
                double probAttackedByS = 1.0d;
                defeatsToS.clear ();
                defeatsFromS.clear ();
                // prob. that "a" attacks S
                for (String b : paf.getDefeats(a).keySet()) {
                    if (set.contains(b)) {
                        probAttackS *= 1 - paf.defeats.get(a).get(b);
                        defeatsToS.put(b, paf.defeats.get(a).get(b));
                    }
                }
                probAttackS = 1 - probAttackS;
                // prob. that S attacks "a"
                for (String b : paf.getDefeatedBy(a).keySet()) {
                    if (set.contains(b)) {
                        probAttackedByS *= 1 - paf.defeats.get(b).get(a);
                        defeatsFromS.put(b, paf.defeats.get(b).get(a));
                    }
                }
                probAttackedByS = 1 - probAttackedByS;

                double probAttackAndDefend = probAttackedByS * probAttackS;
                double probAdmGivenOccurs = (1 - probAttackS) + probAttackAndDefend;
                double probA = paf.args.get (a);
                double probAdm = probAdmGivenOccurs*probA + 1*(1-probA);
                double probOccursGivenAdm = probAdmGivenOccurs*probA/probAdm;
                double probOccursAndNoAttackSGivenAdm = (1-probAttackS)*probA/probAdm;

                double rnda = Math.random();
                // "a" can be picked either because it doesn't attack S,
                // or because it attacks S and S attacks "a" for defence
                if (rnda <= probOccursGivenAdm) {
                    // We chose to pick "a"
                    daf.addArg(a);
                    if (rnda > probOccursAndNoAttackSGivenAdm) {
                        // We chose to pick "a" w.r.t the attack+defend case

                        // Generate at least one defeat from "a" to S
                        {
                            double rnd = Math.random() * probAttackS;
                            double offset = 0.0;
                            for (String b : defeatsToS.keySet()) {
                                if (offset + defeatsToS.get(b) >= rnd) {
                                    daf.addDefeat(a, b);
                                } else {
                                    offset += (1 - offset) * defeatsToS.get(b);
                                }
                            }
                        }

                        // Generate at least one defeat from S to "a"
                        {
                            double rnd = Math.random() * probAttackedByS;
                            double offset = 0.0;
                            for (String b : defeatsFromS.keySet()) {
                                if (offset + defeatsFromS.get(b) >= rnd) {
                                    daf.addDefeat(b, a);
                                } else {
                                    offset += (1 - offset) * defeatsFromS.get(b);
                                }
                            }
                        }

                        // Remember that we generated defeats from S to "a"
                        attackedByS.add (a);
                    }
                }
            }
            // generate defeats that do not attack S and that are not already in the DAF
            for (String a : daf.args) {
                for (String b : paf.getDefeats(a).keySet()) {
                    if (set.contains(b)) {
                        // already considered
                    } else if (set.contains(a) && attackedByS.contains(b)) {
                        // already considered
                    } else if (daf.args.contains(b)
                               && !daf.getDefeats(a).contains(b)
                               && Math.random() <= paf.defeats.get(a).get(b)) {
                        daf.addDefeat(a, b);
                    }
                }
            }
        } while (filterSample(daf, set));
        return daf;
    }

    @Override
    public double conditional(PAF paf, ArgSet set) {
        return paf.admissible(set);
    }

    @Override
    public String toString () {
        return "Adm-based";
    }
}

class CompleteGivenConflictFreeSemantics extends GivenConflictFreeSemantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.completeGivenConflictFree(set);
        }
}

class CompleteGivenAdmissibleSemantics extends GivenAdmissibleSemantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.completeGivenAdmissible(set);
        }
}

class GroundedGivenAdmissibleSemantics extends GivenAdmissibleSemantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.groundedGivenAdmissible(set);
        }
}

class PreferredGivenAdmissibleSemantics extends GivenAdmissibleSemantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.preferredGivenAdmissible(set);
        }
}

class PreferredGivenConflictFreeSemantics extends GivenConflictFreeSemantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.preferredGivenConflictFree(set);
        }
}

class GroundedGivenConflictFreeSemantics extends GivenConflictFreeSemantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.groundedGivenConflictFree(set);
        }
}

class StableGivenConflictFreeSemantics extends GivenConflictFreeSemantics {
        @Override
        public boolean evaluate(DAF daf, ArgSet set) {
                return daf.stableGivenConflictFree(set);
        }
}

class CachedSemantics extends Semantics {
        private int cacheSize;
        private ArgSet cacheSet; // cache only for this set of arguments
        private Semantics realSemantics;
        private LinkedHashMap<DAF, Boolean> cache = new LinkedHashMap<DAF, Boolean>() {
                protected boolean removeEldestEntry(Map.Entry eldest) {
                        return size() > cacheSize;
                }
        };

        public CachedSemantics(Semantics realSemantics, int cacheSize) {
                this.realSemantics = realSemantics;
                this.cacheSet = cacheSet;
                this.cacheSize = cacheSize;
        }

        public boolean evaluate(DAF daf, ArgSet set) {
                if (!set.equals(cacheSet)) {
                        cache.clear();
                        cacheSet = set;
                }
                Boolean b = cache.get(daf);
                if (b != null) {
                        return b;
                }
                b = realSemantics.evaluate(daf, set);
                cache.put(daf, b);
                return b;
        }

        public DAF montecarloSample(PAF paf, ArgSet set) {
                return realSemantics.montecarloSample(paf, set);
        }

        public boolean filterSample(DAF daf, ArgSet set) {
                return realSemantics.filterSample(daf, set);
        }

        public double conditional(PAF paf, ArgSet set) {
                return realSemantics.conditional(paf, set);
        }
}
