package test;

import structures.ArgSet;
import structures.DAF;

public class TestDaf {

	public static void main(String[] args) {
		DAF daf = new DAF();

		String[] argNames = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };

		daf.addArgs(argNames);

		daf.addDefeat("a", "b");
		daf.addDefeat("a", "f");
		daf.addDefeat("c", "e");
		daf.addDefeat("d", "e");
		daf.addDefeat("e", "b");
		daf.addDefeat("g", "f");
		daf.addDefeat("g", "d");
		daf.addDefeat("h", "d");

		ArgSet s1 = new ArgSet(new String[] { "a", "e" });
		ArgSet s2 = new ArgSet(new String[] { "a", "g", "c" });
		ArgSet s3 = new ArgSet(new String[] { "a", "e", "f" });
		ArgSet s4 = new ArgSet(new String[] { "c", "d" });
		ArgSet s5 = new ArgSet(new String[] { "a", "f", "g" });

		System.out.println(daf);
		System.out.println();

		verCF(daf, s1, true);
		verCF(daf, s2, true);
		verCF(daf, s3, false);

		verACC(daf, s1, "f", false);
		verACC(daf, s1, "g", true);
		verACC(daf, s4, "b", false);
		verACC(daf, s5, "h", true);

		verADM(daf, s1, false);
		verADM(daf, s2, true);
		verADM(daf, s4, false);
		verADM(daf, s5, false);

	}

	private static void verCF(DAF daf, ArgSet set, boolean atteso) {
		System.out.println("Conflict free rispetto " + set + ". \nATTESO: " + atteso);
		if (daf.conflictFree(set) == atteso)
			System.out.println("SUPERATO");
		else
			System.out.println("NON SUPERATO");
		System.out.println();

	}

	private static void verACC(DAF daf, ArgSet set, String argument, boolean atteso) {
		System.out.println("Acceptable rispetto " + set + "," + argument + ".\nATTESO: " + atteso);
		if (daf.acceptable(set, argument) == atteso)
			System.out.println("SUPERATO");
		else
			System.out.println("NON SUPERATO");
		System.out.println();

	}

	private static void verADM(DAF daf, ArgSet set, boolean atteso) {
		System.out.println("Acceptable rispetto " + set + ".  \nATTESO: " + atteso);
		if (daf.admissible(set) == atteso)
			System.out.println("SUPERATO");
		else
			System.out.println("NON SUPERATO");
		System.out.println();

	}

}