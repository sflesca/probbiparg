package support;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Support {
	
	
	public static List<List<String>> getAllArgumentsSubsets(List<String> args) {
		List<List<String>> result = new LinkedList<>();
		int size = args.size();
	    for ( int i = 0; i < (1 << size); i ++ ) {
	    		List<String> current = new ArrayList<>();
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
	
	public static boolean contains(List<String> arguments, Pair pair) {
		for ( String arg : arguments ) {
			if ( arg.equals(pair.getA()) || arg.equals(pair.getB()) ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean contains(List<String> arguments, String value) {
		for ( String arg : arguments ) {
			if ( arg.equals(value) ) {
				return true;
			}
		}
		return false;
	}

	public static List<Pair> filterPairs(List<Pair> pairs, List<String> arguments) {
		List<Pair> result = new ArrayList<>();
		for ( Pair current : pairs ) {
			if ( arguments.contains(current.getA()) && arguments.contains(current.getB()) ) {
				result.add(current);
			}
		}
		return result;
	}
	
	public static List<String> generateComplemetaryArgumentSubset(List<String> allArgs, List<String> complementary) {
		List<String> result = new ArrayList<>();
		for ( String currentArg : allArgs ) {
			if ( !complementary.contains(currentArg) ) {
				result.add(currentArg);
			}
		}
		return result;
	}
	
	public static List<Pair> generateComplemetaryPairsSubset(List<Pair> allPairs, List<Pair> complementary) {
		List<Pair> result = new ArrayList<>();
		for ( Pair currentPair : allPairs ) {
			if ( !complementary.contains(currentPair) ) {
				result.add(currentPair);
			}
		}
		return result;
	}
	
	
}
