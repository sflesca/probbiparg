package structures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BAF extends DAF {

	private Map<String, ArgSet> supports = new HashMap<>();
	private Map<String, ArgSet> supportedBy = new HashMap<>();

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
	}

	public void addDefeats(String... sup) {
		if (sup.length % 2 != 0)
			throw new RuntimeException("Length must be an even number." + Arrays.toString(sup));
		for (int i = 0; i < sup.length; i += 2) {
			addDefeat(sup[i], sup[i + 1]);
		}
	}

	public void removeSupport(String a, String b) {

		ArgSet supportSet = supports.get(a) == null ? DAF.emptySet : supports.get(a);
		if (supportSet != null)
			supportSet.remove(b);

		ArgSet supportedBySet = supportedBy.get(b) == null ? DAF.emptySet : supportedBy.get(b);
		if (supportedBySet != null)
			supportedBySet.remove(b);
	}

	public ArgSet getSupports(String a) {
		ArgSet sup = supports.get(a);
		if (sup == null) {
			return emptySet;
		}
		return sup;
	}

	public ArgSet getSupportedBy(String a) {
		ArgSet supBy = supportedBy.get(a);
		if (supBy == null) {
			return emptySet;
		}
		return supBy;
	}

	@Override
	public BAF copy() {
		BAF baf = new BAF();
		for (String s : argsList)
			baf.addArg(s);
		for (String a : argsList)
			for (String b : getDefeats(a))
				baf.addDefeat(a, b);
		for (String a : argsList)
			for (String b : getSupports(a))
				baf.addSupport(a, b);
		return baf;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (String a : args) {
			b.append(a);
			b.append(" ");
		}
		b.append("\n");
		for (String a : defeats.keySet()) {
			for (String c : defeats.get(a)) {
				b.append("{" + a + "," + c + "}");
				b.append(" ");
			}
		}
		b.append("\n");
		for (String a : supports.keySet()) {
			for (String c : supports.get(a)) {
				b.append("{" + a + "," + c + "}");
				b.append(" ");
			}
		}
		return b.toString();
	}

	// Anche hashcode è stato generato
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((supportedBy == null) ? 0 : supportedBy.hashCode());
		result = prime * result + ((supports == null) ? 0 : supports.hashCode());
		return result;
	}

	// Ho utilizzato l'equals generato da eclipse perchè i controlli sono più
	// esaustivi di quelli che avrei fatto io
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

	// SEMANTICS
	public boolean safe(ArgSet set) {
		return false;
	}

	public boolean closed(ArgSet set) {
		return false;
	}

}
