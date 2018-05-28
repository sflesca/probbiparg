package test;

import java.io.FileNotFoundException;
import java.util.Iterator;

import structures.ArgSet;
import structures.BAF;
import structures.PrBAF;

public class TestBaf {

	public static void main(String[] args) throws FileNotFoundException {

		BAF baf = new BAF();
	
		// Esempio contenuto nell'articolo
		baf.addArgs("a", "b", "c", "d", "e", "f");

		baf.addDefeats("e", "b", "b", "c", "f", "d");
		baf.addSuport("a", "b", "c", "d");
		System.out.println(baf);
		System.out.println();
		BAF extended = BAF.generateExtendedBaf(baf);
		System.out.println(extended);
		ArgSet ae = new ArgSet("a", "e");
		ArgSet fc = new ArgSet("f", "c");
		ArgSet abf = new ArgSet("a", "b", "f");
		ArgSet adb = new ArgSet("a", "d", "b");
		ArgSet aef = new ArgSet("a", "e", "f");
		ArgSet ef = new ArgSet("e", "f");
		ArgSet bd = new ArgSet("b", "d");
		ArgSet e = new ArgSet("e");
		Test.runAlltest(baf, ae);
		Test.runAlltest(baf, fc);
		Test.runAlltest(baf, abf);
		Test.runAlltest(baf, adb);
		Test.runAlltest(baf, aef);
		Test.runAlltest(baf, ef);
		Test.runAlltest(baf, bd);
		Test.runAlltest(baf, e);
	}

}

class Test {
	public static enum whichTest {
		CONFLICT_FREE, SAFE, CLOSED, D_ADMISSIBLE, S_ADMISSIBLE, C_ADMISSIBLE, STABLE, COMPLETE, GROUNDED, PREFERRED
	}

	ArgSet set;
	whichTest test;
	BAF baf;

	public Test(BAF baf, ArgSet set, whichTest test) {
		this.set = set;
		this.test = test;
		this.baf = baf;
	};

	public void runTest() {
		switch (test) {
		case CONFLICT_FREE: {
			System.out.println(test + " wrt " + set + "\nFound: " + baf.conflictFree(set) + "\n");
			break;
		}
		case SAFE: {
			System.out.println(test + " wrt " + set + "\nFound: " + baf.safe(set) + "\n");
			break;
		}
		case STABLE:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.stable(set) + "\n");
			break;
		case D_ADMISSIBLE:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.Dadmissible(set) + "\n");
			break;
		case S_ADMISSIBLE:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.Sadmissible(set) + "\n");
			break;
		case C_ADMISSIBLE:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.Cadmissible(set) + "\n");
			break;
		case COMPLETE:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.complete(set) + "\n");
			break;
		case GROUNDED:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.grounded(set) + "\n");
			break;
		case PREFERRED:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.preferred(set) + "\n");
			break;
		case CLOSED:
			System.out.println(test + " wrt " + set + "\nFound: " + baf.closed(set) + "\n");
			break;
		default:
			break;

		}
	}

	public static void runAlltest(BAF baf, ArgSet set) {
		System.out.println("========================================");
		System.out.println("Test on: " + baf + "\nS: " + set);

		for (whichTest t : whichTest.values()) {
			Test test = new Test(baf, set, t);
			test.runTest();
		}
	}

}
