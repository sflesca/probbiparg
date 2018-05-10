package structures;

import java.util.HashSet;

public class DefeatSet extends HashSet<Defeat> {
    public String toString () {
        StringBuilder b = new StringBuilder ();
        for (Defeat d: this) {
            b.append (d);
            b.append (" ");
        }
        return b.toString();
    }
}

