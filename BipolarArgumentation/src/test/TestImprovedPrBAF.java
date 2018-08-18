package test;


import java.util.List;
import structures.ArgSet;
import structures.PrBAF;
import support.Constants.SemanticsType;
import support.Pair;
import support.Support;


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
		ArgSet S = new ArgSet("a", "b", "c");
		SemanticsType sem = SemanticsType.c_ad;
		// elaborating
		long startTime = System.currentTimeMillis();
		System.out.println("Calculating...");
		float result = elaborate(baf, S, sem);
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Completed!");
		System.out.println("Result: " + result);
		System.out.println("Computation Time: " + elapsedTime);
	}

	private static float elaborate(PrBAF baf, ArgSet S, SemanticsType sem) {
		float Pr = 0.0f;
		ArgSet A_e = baf.computeAe();
		List<Pair> R_e = baf.computeRe(A_e);
		for ( ArgSet A_pe : Support.getAllPermutations(A_e) ) {
			for ( Pair currentR_e : R_e ) {
				float Pr_s = 0.0f;
				if ( Support.contains(A_pe, currentR_e) ) {
					PrBAF F_p = baf.contract(A_pe, currentR_e);
					float Pr_p = F_p.calculatePr(A_e, A_pe, R_e); 
					PrBAF F_s = F_p.complete(A_pe, currentR_e);
					if ( sem == SemanticsType.s_ad ) { 
						if ( !F_s.safe(S) ) {
							Pr_s = 0.0f;
						}
					}
					else if ( sem == SemanticsType.c_ad ) { 
						if ( !F_s.closed(S) ) {
							Pr_s = 0.0f;
						}
					}
					else {
						Pr_s = 1.0f;
					}
					PrBAF F_c = F_s.toPrAAF();
					SemanticsType aafsem;
					if ( sem == SemanticsType.st ) { 
						aafsem = SemanticsType.st; 
					}
					else {
						aafsem = SemanticsType.ad; 
					}
					float Pr_pp = F_c.computePrAAF(aafsem);
					Pr += Pr_p * Pr_s * Pr_pp;
				}
			}
		}
		return Pr;
	}

	
}
