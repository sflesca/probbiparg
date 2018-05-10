package structures;

import java.util.HashSet;

public class ArgSet extends HashSet<String> {
    public ArgSet (ArgSet set) {
        super (set);
    }

    public ArgSet (String ... args) {
        for (String n: args) {
            this.add (n);
        }
    }

    public String toString () {
        StringBuilder b = new StringBuilder();
        for (String a: this) {
            b.append (a);
            b.append (" ");
        }
        return b.toString();
    }
}
