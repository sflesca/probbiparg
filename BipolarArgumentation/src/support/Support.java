package support;


import java.util.LinkedList;
import java.util.List;
import structures.ArgSet;


public class Support {
	
	
	public static List<ArgSet> getAllPermutations(List<String> args) {
		List<ArgSet> result = new LinkedList<ArgSet>();
		int size = args.size();
	    for ( int i = 0; i < (1 << size); i ++ ) {
	    		ArgSet current = new ArgSet();
	    		for ( int j = 0; j < size; j ++ ) {
	    			if ( (i & (1 << j)) > 0 ) {
	    				current.add(args.get(j));
	            }
	    		}
	    		result.add(current);
		}
		return result;
	}
	
	public static boolean contains(ArgSet arguments, Pair pair) {
		for ( String arg : arguments ) {
			if ( arg.equals(pair.getA()) || arg.equals(pair.getB()) ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean contains(ArgSet arguments, String value) {
		for ( String arg : arguments ) {
			if ( arg.equals(value) ) {
				return true;
			}
		}
		return false;
	}
	
	
}
