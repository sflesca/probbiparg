package structures;

import java.util.HashSet;
import java.util.List;

public class ArgSet extends HashSet<String> {
	private static final long serialVersionUID = 1L;

	
	public ArgSet (ArgSet set) {
        super (set);
    }
	
	public ArgSet (List<String> set) {
        super (set);
    }

    public ArgSet (String ... args) {
        for (String n: args) {
            this.add (n);
        }
    }

    public String toString () {
        StringBuilder b = new StringBuilder();
        b.append("[");
        for (String a: this) {
            b.append (a);
            b.append (",");
        }
        
        if(!b.toString().endsWith("[")) 
        	b.setLength(b.toString().length()-1);
        b.append("]");
        return b.toString();
    }
}
