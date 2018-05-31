package structures;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class PrBAF extends BAF {

	private Map<String, Double> argProb = new HashMap<>();
	private Map<String, ArgProb> defProb = new HashMap<>();
	private Map<String, ArgProb> supProb = new HashMap<>();

	private class ArgProb extends HashMap<String, Double> {
	};

	public void addArg(String s, double d) {
		if (d < 0 || d > 1)
			throw new RuntimeException("Argument " + s + " must have a probability p  0<p<1");
		super.addArg(s);
		argProb.put(s, d);
	}

	@Override
	public void addArg(String s) {
		super.addArg(s);
		addArg(s, 1);
	}

	@Override
	public void removeArg(String s) {
		super.removeArg(s);
		defProb.remove(s);
		supProb.remove(s);
		argProb.remove(s);
	}

	public void addSupport(String a, String b, double d) {
		super.addSupport(a, b);
		ArgProb sup = supProb.get(a);
		if (sup == null)
			sup = new ArgProb();
		sup.put(b, d);
		supProb.put(a, sup);

	}

	@Override
	public void addSupport(String a, String b) {
		addSupport(a, b, 1d);
	}

	public void addDefeat(String a, String b, double d) {
		super.addDefeat(a, b);
		ArgProb def = defProb.get(a);
		if (def == null)
			def = new ArgProb();
		def.put(b, d);
		defProb.put(a, def);
	}

	public void addDefeat(String a, String b) {
		addDefeat(a, b, 1d);
	}

	public void removeSupport(String a, String b) {
		super.removeSupport(a, b);
		supProb.get(a).remove(b);
	}

	public void removeDefeat(String a, String b) {
		super.removeDefeat(a, b);
		defProb.get(a).remove(b);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Arguments :");
		for (String a : args) {
			b.append("{" + a + ":" + argProb.get(a) + "} ");
		}
		b.append("\nDefeats :");
		for (String a : defeats.keySet()) {
			for (String c : defeats.get(a)) {
				b.append("{" + a + "," + c + ":" + defProb.get(a).get(c) + "}");
				b.append(" ");
			}
		}
		b.append("\nSupp :");
		for (String a : supports.keySet()) {
			for (String c : supports.get(a)) {
				b.append("{" + a + "," + c + ":" + supProb.get(a).get(c) + "}");
				b.append(" ");
			}
		}
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((argProb == null) ? 0 : argProb.hashCode());
		result = prime * result + ((defProb == null) ? 0 : defProb.hashCode());
		result = prime * result + ((supProb == null) ? 0 : supProb.hashCode());
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
		PrBAF other = (PrBAF) obj;
		if (argProb == null) {
			if (other.argProb != null)
				return false;
		} else if (!argProb.equals(other.argProb))
			return false;
		if (defProb == null) {
			if (other.defProb != null)
				return false;
		} else if (!defProb.equals(other.defProb))
			return false;
		if (supProb == null) {
			if (other.supProb != null)
				return false;
		} else if (!supProb.equals(other.supProb))
			return false;
		return true;
	}
	
	public Iterator<BAF> possibleWorlds(){
		return pw().iterator();
	}

	
	public Iterator<BAF> worlds(){
		return new Iterator<BAF>() {
			Iterator<Set<String>> args=new PowerSet<String>(getArgs()).iterator();
			Iterator<Set<Pair<String,String>>> defs;
			Iterator<Set<Pair<String, String>>> sups;
			BAF currArgs;
			BAF currSup;
			BAF currDef;
			boolean avanzaArgs=true;
			boolean avanzaSup;
			boolean avanzaDef;
			@Override
			public boolean hasNext() {
				return avanzaArgs||avanzaDef||avanzaSup;
			}
			
			@Override
			public BAF next() {
				
				if(avanzaDef) {
					currDef=currSup.copy();
					for(Pair<String,String> d:defs.next())
						currDef.addDefeat(d.getKey(), d.getValue());
					if(!defs.hasNext())
						avanzaDef=false;
						return currDef;
				}
				if(avanzaSup) {
					currSup=currArgs.copy();
					for(Pair<String,String> d:sups.next())
						currSup.addSupport(d.getKey(), d.getValue());
						defs=new PowerSet<Pair<String,String>>(generateDefeats(currSup.getArgs())).iterator();
						avanzaDef=true;
					if(!sups.hasNext())
						avanzaSup=false;
					currDef=currSup.copy();
					for(Pair<String,String> d:defs.next())
						currDef.addDefeat(d.getKey(), d.getValue());
					if(!defs.hasNext())
						avanzaDef=false;
						return currDef;
				}
				if(avanzaArgs) {
					currArgs=new BAF();
					for(String a:args.next())
						currArgs.addArg(a);
						sups=new PowerSet<Pair<String,String>>(generateSupports(currArgs.getArgs())).iterator();
						avanzaSup=true;
						currSup=currArgs.copy();
						for(Pair<String,String> d:sups.next())
							currSup.addSupport(d.getKey(), d.getValue());
							defs=new PowerSet<Pair<String,String>>(generateDefeats(currSup.getArgs())).iterator();
							avanzaDef=true;
						if(!sups.hasNext())
							avanzaSup=false;
						currDef=currSup.copy();
						for(Pair<String,String> d:defs.next())
							currDef.addDefeat(d.getKey(), d.getValue());
						if(!defs.hasNext())
							avanzaDef=false;
						
					if(!args.hasNext())
						avanzaArgs=false;
						return currDef;
				}
				return null;
			}

		
			private Set<Pair<String, String>> generateDefeats(ArgSet args) {
				Set<Pair<String,String>> set = new HashSet<>();
				for(String a:args)
					for(String b: getDefeats(a))
						if(args.contains(b))
							set.add(new Pair<String,String>(a,b));
				return set;
			}

			private Set<Pair<String, String>> generateSupports(ArgSet args) {
				Set<Pair<String,String>> set = new HashSet<>();
				for(String a:args)
					for(String b: getSupports(a))
						if(args.contains(b))
							set.add(new Pair<String,String>(a,b));
				return set;
			}
			
		};
	}
	public Iterator<BAF> notZeroProbPossibleWorlds(){
		return new Iterator<BAF>() {
			Iterator<BAF> it = worlds();
			BAF curr;
			@Override
			public boolean hasNext() {
				if(!it.hasNext())
					return false;
				curr=it.next();
				while(it.hasNext()&&Double.compare(computeProb(curr),0d)==0) {
					curr=it.next();
				}
				if(Double.compare(computeProb(curr),0d)==0)
					return false;
				return true;
			}

			@Override
			public BAF next() {
				return curr;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	public Set<BAF> pw() {
		Set<BAF> res = new HashSet<>();
		PowerSet<String> argPower=new PowerSet<>(getArgs());
		for(Set<String> s: argPower)
		{
			 BAF  baf= new BAF();
			 for(String a:s)
				 baf.addArg(a);
			 res.add(baf); 
			 
			 Set<Pair<String,String>> sup=new HashSet<>();
			 for(String a: getSupports().keySet())
					for(String b: getSupports(a))
						if(s.contains(a)&&s.contains(b)) 
							sup.add(new Pair<String,String>(a,b));
						
			 PowerSet<Pair<String,String>> supPower=new PowerSet<>(sup);
			 			for(Set<Pair<String ,String>> supP:supPower)
			 			{
				 			BAF baf2=baf.copy();
				 				for(Pair<String,String> pair:supP)
				 					baf2.addSuport(pair.getKey(),pair.getValue());
				 				res.add(baf2);
				 				
				 				
				 				
				 				Set<Pair<String,String>> def=new HashSet<>();
				 				 for(String a: getDefeats().keySet())
				 						for(String b: getDefeats(a))
				 							if(s.contains(a)&&s.contains(b)) 
				 								def.add(new Pair<String,String>(a,b));
				 							
				 				 PowerSet<Pair<String,String>> defPower=new PowerSet<>(def);
				 				 			for(Set<Pair<String ,String>> defP:defPower)
				 				 			{
				 					 			BAF baf3=baf2.copy();
				 					 				for(Pair<String,String> pair:defP)
				 					 					baf3.addDefeat(pair.getKey(),pair.getValue());
				 					 				res.add(baf3);	
				 				 			}		
			 			}
			 			Set<Pair<String,String>> def=new HashSet<>();
		 				 for(String a: getDefeats().keySet())
		 						for(String b: getDefeats(a))
		 							if(s.contains(a)&&s.contains(b)) 
		 								def.add(new Pair<String,String>(a,b));
		 							
		 				 PowerSet<Pair<String,String>> defPower=new PowerSet<>(def);
		 				 			for(Set<Pair<String ,String>> defP:defPower)
		 				 			{
		 					 			BAF baf3=baf.copy();
		 					 				for(Pair<String,String> pair:defP)
		 					 					baf3.addDefeat(pair.getKey(),pair.getValue());
		 					 				res.add(baf3);	
		 				 			}
						}
		return res;
	}

	


	public class PowerSet<E> implements Iterator<Set<E>>,Iterable<Set<E>>{
	    private E[] arr = null;
	    private BitSet bset = null;

	    public PowerSet(Set<E> set)
	    {
	        arr = (E[])set.toArray();
	        bset = new BitSet(arr.length + 1);
	    }

	    @Override
	    public boolean hasNext() {
	        return !bset.get(arr.length);
	    }

	    @Override
	    public Set<E> next() {
	        Set<E> returnSet = new HashSet<E>();
	        for(int i = 0; i < arr.length; i++){
	            if(bset.get(i))
	                returnSet.add(arr[i]);
	        }
	        for(int i = 0; i < bset.size(); i++){
	            if(!bset.get(i)){
	                bset.set(i);
	                break;
	            }else
	                bset.clear(i);
	        }
	        return returnSet;
	    }

	    @Override
	    public void remove() {
	        throw new UnsupportedOperationException("Not Supported!");
	    }

	    @Override
	    public Iterator<Set<E>> iterator() {
	        return this;
	    }

	}
	public double computeProb(BAF baf) {
		PrBAF prBaf = this;
		double prob = 1;
		ArgSet difference = new ArgSet(prBaf.getArgs());
		difference.removeAll(baf.getArgs());

		for (String a : baf.args) 
				prob *= argProb.get(a);
		

		for (String a : difference)
			prob *= (1 - argProb.get(a));

		for (String a : baf.getArgs())
			for (String b : baf.getDefeats(a))
				prob *= defProb.get(a).get(b);

		for (String a : baf.getArgs())
			for (String b : baf.getSupports(a))
				prob *= supProb.get(a).get(b);
		Map<String, ArgSet> defeatsNotOccurredInBaf = new HashMap<>(getDefeats().keySet().stream().collect(Collectors.toMap(a->a, a->new ArgSet(getDefeats(a)))));

		for(String a:getDefeats().keySet())
			for(String b: getDefeats(a))
				if(!baf.getArgs().contains(a)||!baf.getArgs().contains(b))
					defeatsNotOccurredInBaf.get(a).remove(b);
		
				
		for(String a: baf.getDefeats().keySet())
			for(String b: baf.getDefeats(a))
				defeatsNotOccurredInBaf.get(a).remove(b);
		
		
		Map<String, ArgSet> supportsNotOccurredInBaf = new HashMap<>(getSupports().keySet().stream().collect(Collectors.toMap(a->a, a->new ArgSet(getSupports(a)))));
		for(String a:getSupports().keySet())
			for(String b: getSupports(a))
				if(!baf.getArgs().contains(a)||!baf.getArgs().contains(b))
					supportsNotOccurredInBaf.get(a).remove(b);
		
				
		for(String a: baf.getSupports().keySet())
			for(String b: baf.getSupports(a))
				supportsNotOccurredInBaf.get(a).remove(b);
	
	

		for (String a : defeatsNotOccurredInBaf.keySet())
			for (String b : defeatsNotOccurredInBaf.get(a))
				prob *= (1 - defProb.get(a).get(b));

		for (String a : supportsNotOccurredInBaf.keySet())
			for (String b : supportsNotOccurredInBaf.get(a))
				prob *= (1 - supProb.get(a).get(b));

		return prob;
	}

	public double probDadmissible(ArgSet set) {
		double prob = 0;
		Iterator<BAF> it = notZeroProbPossibleWorlds();
		while (it.hasNext()) {
			BAF baf = it.next();
			if (baf.Dadmissible(set))
				prob += computeProb(baf);
		}
		return prob;
	}

	public double probCadmissible(ArgSet set) {
		double prob = 0;
		Iterator<BAF> it = notZeroProbPossibleWorlds();
		while (it.hasNext()) {
			BAF baf = it.next();
			if (baf.Cadmissible(set))
				prob += computeProb(baf);
		}
		return prob;
	}

	public double probSadmissible(ArgSet set) {
		double prob = 0;
		Iterator<BAF> it = notZeroProbPossibleWorlds();
		while (it.hasNext()) {
			BAF baf = it.next();
			if (baf.Sadmissible(set))
				prob += computeProb(baf);
		}
		return prob;
	}

	public double probComplete(ArgSet set) {
		double prob = 0;
		Iterator<BAF> it = notZeroProbPossibleWorlds();
		while (it.hasNext()) {
			BAF baf = it.next();
			if (baf.complete(set))
				prob += computeProb(baf);
		}
		return prob;
	}

	public double probGrounded(ArgSet set) {
		double prob = 0;
		Iterator<BAF> it = notZeroProbPossibleWorlds();
		while (it.hasNext()) {
			BAF baf = it.next();
			if (baf.grounded(set))
				prob += computeProb(baf);
		}
		return prob;
	}

	public double probPreferred(ArgSet set) {
		double prob = 0;
		Iterator<BAF> it = notZeroProbPossibleWorlds();
		while (it.hasNext()) {
			BAF baf = it.next();
			if (baf.preferred(set))
				prob += computeProb(baf);
		}
		return prob;
	}

	public double probIdeal(ArgSet set) {
		double prob = 0;
		Iterator<BAF> it = notZeroProbPossibleWorlds();
		while (it.hasNext()) {
			BAF baf = it.next();
			if (baf.ideal(set))
				prob += computeProb(baf);
		}
		return prob;
	}

}