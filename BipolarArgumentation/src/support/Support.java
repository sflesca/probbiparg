package support;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import structures.ArgSet;


public class Support {
	
	
	public static List<ArgSet> getAllArgumentsSubsets(List<String> args) {
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
	
	public static List<List<Pair>> getAllPairsSubsets(List<Pair> pairs) {
		List<List<Pair>> result = new LinkedList<>();
		int size = pairs.size();
	    for ( int i = 0; i < (1 << size); i ++ ) {
	    		List<Pair> current = new ArrayList<>();
	    		for ( int j = 0; j < size; j ++ ) {
	    			if ( (i & (1 << j)) > 0 ) {
	    				current.add(pairs.get(j));
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

	public static List<Pair> filterPairs(List<Pair> pairs, ArgSet arguments) {
		List<Pair> result = new ArrayList<>();
		for ( Pair current : pairs ) {
			if ( arguments.contains(current.getA()) && arguments.contains(current.getB()) ) {
				result.add(current);
			}
		}
		return result;
	}
	
	
}
