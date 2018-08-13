package test;


import java.util.List;
import semantics.Semantics;
import structures.ArgSet;
import structures.PrBAF;


public class TestImprovedPrBAF {

	
	public static void main(String[] args) {
		System.out.println("Generating sample...");
		PrBAF baf = new PrBAF();
		baf.addArg("a", 1);
		baf.addArg("b", 1);
		baf.addArg("c", 1);
		baf.addArg("d", 1);
		baf.addArg("e", 0.5);
		baf.addArg("f", 1);
		baf.addDefeat("e", "b", 0.5);
		baf.addDefeat("b", "c", 1);
		baf.addDefeat("f", "d", 1);
		baf.addSupport("a", "b", 1);
		baf.addSupport("c", "d", 1);
		ArgSet S = null;
		Semantics sem = null;
		elaborate(baf, S, sem);
	}

	private static void elaborate(PrBAF baf, ArgSet S, Semantics sem) {
		System.out.println("Calculating...");
		long startTime = System.currentTimeMillis();
		float Pr = 0.0f;
		List<Object> A_e = computeAe(baf);  
		List<Object> R_e = computeRe(baf, A_e);
		for ( Object currentA_e : A_e ) {
			for ( Object currentR_e : R_e ) {
				float Pr_s = 0.0f;
				if ( true ) { //TODO
					Object F_p = contract(baf, currentA_e, currentR_e);
					float Pr_p = 0; //TODO
					Object F_s = complete(F_p, currentA_e, currentR_e);
					if ( true ) { //TODO
						if ( !isSafe(F_s, S, currentA_e, currentR_e) ) {
							Pr_s = 0.0f;
						}
					}
					else if ( true ) { //TODO
						if ( !baf.closed(S) ) {
							Pr_s = 0.0f;
						}
					}
					else {
						Pr_s = 1.0f;
					}
					Object F_c = toPrAAF(F_s);
					Object aafsem;
					if ( true ) { //TODO
						aafsem = null; //TODO
					}
					else {
						aafsem = null; //TODO
					}
					float Pr_pp = computePrAAF(F_c, aafsem);
					Pr = Pr + Pr_p * Pr_s * Pr_pp;
				}
			}
		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Completed!");
		System.out.println("Result: " + Pr);
		System.out.println("Computation Time: " + elapsedTime);
	}

	private static float computePrAAF(Object f_c, Object aafsem) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static Object toPrAAF(Object f_s) {
		// TODO Auto-generated method stub
		return null;
	}

	private static boolean superClosed(Object f_s, Object s) {
		// TODO Auto-generated method stub
		return false;
	}

	private static boolean isSafe(Object f_s, Object s, Object currentA_e, Object currentR_e) {
		// TODO Auto-generated method stub
		return false;
	}

	private static Object complete(Object f_p, Object currentA_e, Object currentR_e) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Object contract(PrBAF baf, Object currentAe, Object currentRe) {
		// TODO Auto-generated method stub
		return null;
	}

	private static List<Object> computeAe(PrBAF baf) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static List<Object> computeRe(PrBAF baf, Object A_e) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
