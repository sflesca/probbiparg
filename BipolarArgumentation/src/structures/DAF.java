package structures;

import java.util.ArrayList;
import java.util.HashMap;

public class DAF {
    double weight = 1;
        
    public ArgSet args = new ArgSet();
    public HashMap<String,ArgSet> defeats = new HashMap<String,ArgSet>();
    public HashMap<String,ArgSet> defeatedBy = new HashMap<String,ArgSet>();
    public static ArgSet emptySet = new ArgSet ();

    ArrayList<String> argsList = new ArrayList<String> ();

    @Override
    public boolean equals (Object o) {
        DAF daf = (DAF) o;
        return args.equals (daf.args) && defeats.equals (daf.defeats);
    }

    @Override
    public int hashCode () {
        return (args.hashCode() << 32) + defeats.hashCode ();
    }

    public void addArg (String name) {
        args.add (name);
        argsList.add (name);
    }

    public void removeArg (String name) {
        args.remove (name);
        argsList.remove (name);
        for (String b: getDefeatedBy (name)) {
            defeats.get(b).remove (name);
        }
        for (String b: getDefeats (name)) {
            defeatedBy.get(b).remove (name);
        }
        defeats.remove (name);
        defeatedBy.remove (name);
    }

    public void addWeight (double x) {
        weight *= x;
    }

    public double getWeight () {
        return weight;
    }

    public void addArgs (String ... names) {
        for (String n: names) {
            addArg (n);
        }
    }

    public void addDefeat (String a, String b) {
        ArgSet out = defeats.get(a);
        if (out == null) {
            out = new ArgSet();
            defeats.put (a, out);
        }
        out.add (b);

        ArgSet in = defeatedBy.get(b);
        if (in == null) {
            in = new ArgSet();
            defeatedBy.put (b, in);
        }
        in.add (a);
    }

    public void addDefeats (String ... defs) {
        for (int i=0; i < defs.length; i += 2) {
            addDefeat (defs[i], defs[i+1]);
        }
    }

    public ArgSet getDefeats (String a) {
        ArgSet defs = defeats.get (a);
        if (defs == null) {
            return emptySet;
        }
        return defs;
    }

    public ArgSet getDefeatedBy (String a) {
        ArgSet defby = defeatedBy.get (a);
        if (defby == null) {
            return emptySet;
        }
        return defby;
    }

    public boolean acceptable (ArgSet set, String a) {
        for (String b: getDefeatedBy(a)) {
            boolean defended = false;
            for (String c: getDefeatedBy(b)) {
                if (set.contains (c)) {
                    defended = true;
                    break;
                }
            }
            if (!defended) {
                return false;
            }
        }
        return true;
    }

    public boolean hasArgSet (ArgSet set) {
        return args.containsAll (set);
    }

    // SEMANTICS

    public boolean conflictFree (ArgSet set) {
        if (!hasArgSet (set)) {
            // set not in any extension
            return false;
        }
        for (String a: set) {
            for (String b: getDefeats(a)) {
                if (set.contains (b)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean admissibleGivenConflictFree (ArgSet set) {
        for (String a: set) {
            if (!acceptable (set, a)) {
                return false;
            }
        }
        return true;
    }

    public boolean admissible (ArgSet set) {
        return conflictFree (set) && admissibleGivenConflictFree (set);
    }

    public boolean stableGivenConflictFree (ArgSet set) {
        for (String a: args) {
            if (!set.contains (a)) {
                boolean defeated = false;
                for (String b: set) {
                    if (getDefeats(b).contains (a)) {
                        defeated = true;
                        break;
                    }
                }
                if (!defeated) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean completeGivenConflictFree (ArgSet set) {
        for (String a: args) {
            if (!set.contains (a) && acceptable (set, a)) {
                return false;
            } else if (set.contains (a) && !acceptable (set, a)) {
                return false;
            }
        }
        return true;
    }

    public boolean completeGivenAdmissible (ArgSet set) {
        for (String a: args) {
            if (!set.contains (a) && acceptable (set, a)) {
                return false;
            }
        }
        return true;
    }

    public boolean complete (ArgSet set) {
        return conflictFree (set) && completeGivenConflictFree (set);
    }

    public boolean stable (ArgSet set) {
        return conflictFree (set) && stableGivenConflictFree (set);
    }

    private boolean preferredHelper (ArgSet set, String curArg) {
        if (curArg != null && admissible (set)) {
            // we added at least one argument and the set is admissible
            return false;
        }
        int idx = curArg == null ? -1 : argsList.indexOf (curArg);
        for (int i=idx+1; i < argsList.size(); i++) {
            String newArg = argsList.get (i);
            if (set.contains (newArg)) {
                continue;
            }
            set.add (newArg);
            if (!preferredHelper (set, newArg)) {
                return false;
            }
            set.remove (newArg);
        }
        return true;
    }

    public boolean preferred (ArgSet set) {
        return admissible(set) && preferredHelper (new ArgSet (set), null);
    }

    public boolean preferredGivenConflictFree (ArgSet set) {
        return admissibleGivenConflictFree(set) && preferredHelper (new ArgSet (set), null);
    }

    public boolean preferredGivenAdmissible (ArgSet set) {
        return preferredHelper (new ArgSet (set), null);
    }

    public ArgSet initial () {
        ArgSet set = new ArgSet ();
        for (String a: argsList) {
            if (getDefeatedBy(a).isEmpty ()) {
                set.add (a);
            }
        }
        return set;
    }

    public DAF copy () {
        DAF daf = new DAF ();
        for (String a: argsList) {
            daf.addArg (a);
        }
        for (String a: argsList) {
            for (String b: getDefeats (a)) {
                daf.addDefeat (a, b);
            }
        }
        return daf;
    }

    public ArgSet groundedSet () {
        DAF copy = copy ();
        ArgSet set = new ArgSet ();
        while (true) {
            ArgSet newset = copy.initial ();
            if (set.equals (newset)) {
                break;
            }
            set = newset;
            ArgSet suppress = new ArgSet ();
            for (String a: set) {
                for (String b: copy.getDefeats (a)) {
                    suppress.add (b);
                }
            }
            for (String a: suppress) {
                copy.removeArg (a);
            }
        }
        return set;
    }

    public boolean grounded (ArgSet set) {
        return groundedSet().equals (set);
    }

    public boolean groundedGivenConflictFree (ArgSet set) {
        return grounded (set);
    }

    public boolean groundedGivenAdmissible (ArgSet set) {
        return grounded (set);
    }

    public String toString () {
        StringBuilder b = new StringBuilder();
        for (String a: args) {
            b.append (a);
            b.append (" ");
        }
        b.append ("\n");
        for (String a: defeats.keySet()) {
            for (String c: defeats.get(a)) {
                b.append ("{"+a+","+c+"}");
                b.append (" ");
            }
        }
        return b.toString ();
    }
}