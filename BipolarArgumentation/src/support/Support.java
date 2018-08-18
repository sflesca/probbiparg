package support;


import java.util.LinkedList;
import java.util.List;
import structures.ArgSet;


public class Support {
	
	
	public static List<ArgSet> getAllPermutations(ArgSet args) {
		List<ArgSet> result = new LinkedList<ArgSet>();
		result.add(new ArgSet());
		//TODO
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
