package structures;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BAF extends DAF {

	protected Map<String, ArgSet> supports = new HashMap<>();
	protected Map<String, ArgSet> supportedBy = new HashMap<>();
	private BAF extended;
	protected boolean stillValid;

	public void addSupport(String a, String b) {
		ArgSet out = supports.get(a);
		if (out == null) {
			out = new ArgSet();
			supports.put(a, out);
		}
		out.add(b);

		ArgSet in = supportedBy.get(b);
		if (in == null) {
			in = new ArgSet();
			supportedBy.put(b, in);
		}
		in.add(a);
		stillValid = false;

	}

	@Override
	public void addDefeat(String a, String b) {
		stillValid = false;
		super.addDefeat(a, b);
	}

	@Override
	public void addDefeats(String... def) {
		stillValid = false;
		super.addDefeats(def);
	}

	@Override
	public void removeArg(String a) {
		stillValid = false;
		ArgSet sup = getSupports(a);
		for (String b : sup)
			removeSupport(a, b);
		super.removeArg(a);
	}

	@Override
	public void addArg(String a) {
		stillValid = false;
		super.addArg(a);
	}

	@Override
	public void addArgs(String... a) {
		stillValid = false;
		super.addArgs(a);
	}

	public void addSuport(String... sup) {
		if (sup.length % 2 != 0)
			throw new RuntimeException("Length must be an even number." + Arrays.toString(sup));
		for (int i = 0; i < sup.length; i += 2) {
			addSupport(sup[i], sup[i + 1]);
		}
		stillValid = false;
	}
	public void removeDefeat(String a , String b) {
		super.removeDefeat(a, b);
		stillValid=false;
	}
	public void removeSupport(String a, String b) {

		ArgSet supportSet = supports.get(a);
		supportSet.remove(b);

		ArgSet supportedBySet = supportedBy.get(b);
		supportedBySet.remove(b);

		stillValid = false;
	}

	public ArgSet getSupports(String a) {
		ArgSet sup = supports.get(a);
		if (sup == null) {
			return emptySet;
		}
		return sup;
	}
	public Map<String,ArgSet> getSupports() {
		return Collections.unmodifiableMap(supports);
	}	
	public Map<String,ArgSet> getSupportedBy() {
		return Collections.unmodifiableMap(supportedBy);
	}
	public ArgSet getSupportedBy(String a) {
		ArgSet supBy = supportedBy.get(a);
		if (supBy == null) {
			return emptySet;
		}
		return supBy;
	}

	public boolean containsSupport(String a, String b) {
		ArgSet sup = getSupports(a);
		return sup.contains(b);
	}

	@Override
	public BAF copy() {
		BAF baf = new BAF();
		for (String s : getArgs())
			baf.addArg(s);
		for (String a : getArgs())
			for (String b : getDefeats(a))
				baf.addDefeat(a, b);
		for (String a : getArgs())
			for (String b : getSupports(a))
				baf.addSupport(a, b);
		
		return baf;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Arg: ");
		for (String a : args) {
			b.append(a);
			b.append(" ");
		}
		b.append("\nDef: ");
		for (String a : defeats.keySet()) {
			for (String c : defeats.get(a)) {
				b.append("{" + a + "," + c + "}");
				b.append(" ");
			}
		}
		b.append("\nSup: ");
		for (String a : supports.keySet()) {
			for (String c : supports.get(a)) {
				b.append("{" + a + "," + c + "}");
				b.append(" ");
			}
		}
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((supportedBy == null) ? 0 : supportedBy.hashCode());
		result = prime * result + ((supports == null) ? 0 : supports.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BAF other = (BAF) obj;
		if (supportedBy == null) {
			if (other.supportedBy != null)
				return false;
		} else if (!supportedBy.equals(other.supportedBy))
			return false;
		if (supports == null) {
			if (other.supports != null)
				return false;
		} else if (!supports.equals(other.supports))
			return false;
		return super.equals(obj);
	}

	public static BAF generateExtendedBaf(BAF bf) {
		if (bf.extended == null || !bf.stillValid) {
			bf.stillValid = true;
			bf.extended = extendBaf(bf);
		}
		return bf.extended;
	}

	private static BAF extendBaf(BAF bf) {
		BAF baf = bf.copy();

		Map<String, List<List<String>>> dSupport = computeDSupports(baf);
		for (String b : dSupport.keySet())
			for (List<String> ls : dSupport.get(b))
				for (String a : ls)
					baf.addSupport(a, b);

		Map<String, ArgSet> dAttacks = computeDAttacks(baf);
		boolean more = false;

		do {
			for (String a : dAttacks.keySet())
				for (String b : dAttacks.get(a))
					baf.addDefeat(a, b);

			Map<String, ArgSet> tmp = computeDAttacks(baf);

			more = !tmp.equals(dAttacks);
			dAttacks = tmp;
		} while (more);

		return baf;
	}

	private static Map<String, List<List<String>>> computeDSupports(BAF baf) {

		Map<String, List<List<String>>> dsupports = new HashMap<>();
		for (String s : baf.getArgs())
			dsupports.put(s, new LinkedList<List<String>>());

		Set<String> toVisit = new HashSet<String>(baf.argsList);

		for (String s : baf.getArgs()) {
			toVisit.remove(s);
			computeSupportsOf(baf, s, s, toVisit, new LinkedList<String>(), dsupports);
			toVisit.add(s);
		}

		return dsupports;
	}

	/**
	 * 
	 * @param baf
	 *            the baf to be extended
	 * @param against
	 *            the argument at the end of the chain of supports
	 * @param a
	 *            the current first argument of the chain
	 * @param toVisit
	 *            the set of arguments that still have to be visited
	 * @param partialSupport
	 *            the current calculated chain of support
	 * @param dsupports
	 *            map which holds for each end argument of the chain, the list of
	 *            d-supports associated with that end argument.Each d-support is
	 *            ordered : first argument<=>first position in the chain last
	 *            argument<=> last position in the chain
	 */
	private static void computeSupportsOf(BAF baf, String against, String a, Set<String> toVisit,
			List<String> partialSupport, Map<String, List<List<String>>> dsupports) {

		for (String s : baf.getSupportedBy(a)) {
			if (toVisit.contains(s)) {
				toVisit.remove(s);
				List<String> ls = new LinkedList<>(partialSupport);
				ls.add(0, s);
				computeSupportsOf(baf, against, s, toVisit, ls, dsupports);
				dsupports.get(against).add(ls);
				toVisit.add(s);
			}

		}
	}

	private static Map<String, ArgSet> computeDAttacks(BAF baf) {
		Map<String, ArgSet> result = new HashMap<>();

		for (String a : baf.getArgs())
			for (String b : baf.getArgs()) {
				boolean add = false;
				if (baf.containsDefeat(a, b))
					add = true;
				else
					for (String c : baf.getArgs())
						if (baf.containsSupport(a, c) && baf.containsDefeat(c, b))
							add = true;
						else if (baf.containsSupport(b, c) && baf.containsDefeat(a, c))
							add = true;
				if (add) {
					if (!result.keySet().contains(a))
						result.put(a, new ArgSet(b));
					else
						result.get(a).add(b);
				}
			}
		return result;
	}

	// SEMANTICS

	@Override
	public boolean conflictFree(ArgSet s) {
		if(!hasArgSet(s))return false;
		BAF extended = BAF.generateExtendedBaf(this);
		for (String a : s)
			for (String b : s)
				if (extended.setAttack(new ArgSet(a), b))
					return false;
		return true;
	}

	public boolean safe(ArgSet set) {
		if(!hasArgSet(set))return false;
		BAF extended = BAF.generateExtendedBaf(this);
		for (String b : getArgs())
			if (extended.setAttack(set, b) && (extended.setSupport(set, b) || set.contains(b)))
				return false;
		return true;
	}

	public boolean closed(ArgSet set) {
		if(!hasArgSet(set))return false;

		BAF extended = BAF.generateExtendedBaf(this);
		for (String a : set)
			for (String b : extended.getArgs())
				if (extended.containsSupport(a, b) && !set.contains(b))
					return false;
		return true;
	}

	private boolean setSupport(ArgSet s, String a) {
		BAF extended = BAF.generateExtendedBaf(this);
		for (String b : s)
			if (extended.containsSupport(b, a))
				return true;
		return false;
	}

	private boolean setAttack(ArgSet s, String a) {
		BAF extended = BAF.generateExtendedBaf(this);
		for (String b : s)
			if (extended.containsDefeat(b, a))
				return true;
		return false;
	}

	private boolean setDefend(ArgSet s, String a) {
		BAF extended = BAF.generateExtendedBaf(this);
		boolean all = true;
		for (String b : getArgs()) {
			if (extended.setAttack(new ArgSet(b), a)) {
				boolean cFound = false;
				for (String c : s) {
					if (extended.setAttack(new ArgSet(c), b)) {
						cFound = true;
						break;
					}

				}
				if (!cFound) {
					all = false;
					break;
				}
			}
		}
		if (!all)
			return false;
		return true;
	}

	public boolean Dadmissible(ArgSet s) {
		BAF extended = BAF.generateExtendedBaf(this);
		for (String a : s)
			if (!extended.setDefend(s, a)) {
				return false;
			}
		return extended.conflictFree(s);
	}

	public boolean Sadmissible(ArgSet s) {
		BAF extended = BAF.generateExtendedBaf(this);
		for (String a : s)
			if (!extended.setDefend(s, a)) {
				return false;
			}
		return extended.safe(s);
	}

	public boolean Cadmissible(ArgSet s) {
		BAF extended = BAF.generateExtendedBaf(this);
		for (String a : s)
			if (!extended.setDefend(s, a)) {
				return false;
			}
		return extended.conflictFree(s) && extended.closed(s);
	}

	@Override
	public boolean stable(ArgSet s) {
		BAF extended = BAF.generateExtendedBaf(this);

		Set<String> difference = new HashSet<>(getArgs());
		difference.removeAll(s);
		for (String a : difference)
			if (!extended.setAttack(s, a)) {
				return false;
			}
		return extended.conflictFree(s);

	}

	@Override
	public boolean complete(ArgSet s) {
		BAF extended = BAF.generateExtendedBaf(this);
		ArgSet setDefendedByS = new ArgSet();
		for (String a : getArgs())
			if (extended.setDefend(s, a))
				setDefendedByS.add(a);

		return setDefendedByS.equals(s) && admissible(s);
	}

	@Override
	public boolean grounded(ArgSet s) {
		if (!complete(s))
			return false;
		ArgSet possibleMinimal = new ArgSet(s);
		for (String a : s) {
			possibleMinimal.remove(a);
			if (complete(possibleMinimal))
				return false;
			possibleMinimal.add(a);
		}
		return true;
	}

	@Override
	public boolean preferred(ArgSet s) {
		if (!complete(s))
			return false;
		ArgSet argumentNotInS = new ArgSet(getArgs());
		argumentNotInS.removeAll(s);
		ArgSet possibleMaximal = new ArgSet(s);
		for (String a : argumentNotInS) {
			possibleMaximal.add(a);
			if (complete(possibleMaximal))
				return false;
			possibleMaximal.remove(a);
		}
		return true;
	}

	// In ogni preferred extension!
	//
	public boolean ideal(ArgSet s) {
		// if(!admissible(s))return false;
		//
		// ArgSet argumentNotInS=new ArgSet(getArgs());
		// argumentNotInS.removeAll(s);
		// ArgSet possibleMaximal=new ArgSet(s);
		// for(String a: argumentNotInS) {
		// possibleMaximal.add(a);
		// if(admissible(possibleMaximal))return false;
		// possibleMaximal.remove(a);
		// }

		return false;
	}
}
