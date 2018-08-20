package test;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import generator.PrBAFGenerator;
import structures.ArgSet;
import structures.PrBAF;
import support.Constants.SemanticsType;
import support.Pair;
import support.Support;


public class TestImprovedPrBAF {
	
	
	public static void main(String[] args) {
		System.out.println("Generating sample...");
//		PrBAF baf = new PrBAF();
//		baf.addArg("a", 1);
//		baf.addArg("b", 1);
//		baf.addArg("c", 1);
//		baf.addArg("d", 1);
//		baf.addArg("e", 0.5);
//		baf.addArg("f", 1);
//		baf.addDefeat("e", "b", 0.5);
//		baf.addDefeat("b", "c", 1);
//		baf.addDefeat("f", "d", 1);
//		baf.addSupport("a", "b", 1);
//		baf.addSupport("c", "d", 1);
		try {
			FileInputStream stream = new FileInputStream("prbafs/14prbaf-2.txt");
			PrBAF baf = PrBAFGenerator.readPrBA(stream);
			ArgSet S = new ArgSet("a0", "a1");
			SemanticsType sem = SemanticsType.c_ad;
			// elaborating
			long startTime = System.currentTimeMillis();
			System.out.println("Calculating...");
			double result = elaborate(baf, S, sem);
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("Completed!");
			System.out.println("Result: " + result);
			System.out.println("Computation Time: " + elapsedTime + " ms");		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static double elaborate(PrBAF baf, ArgSet S, SemanticsType sem) {
		System.out.println("Inside elaborate!");
		double pr = 0.0f;
		List<String> Ae = baf.computeAe();
		List<Pair> Re = baf.computeRe(Ae);
		for ( List<String> currentArgumentSubset : Support.getAllArgumentsSubsets(Ae) ) {
			List<Pair> rightPairs = Support.filterPairs(Re, currentArgumentSubset);
			for ( List<Pair> currentPairSubset : Support.getAllPairsSubsets(rightPairs) ) {
				double prPrime = 0.0f;
				PrBAF fStar = baf.contract(currentArgumentSubset, currentPairSubset, Support.generateComplemetaryArgumentSubset(Ae, currentArgumentSubset), Support.generateComplemetaryPairsSubset(Re, currentPairSubset));
				double prStar = fStar.calculatePr(currentArgumentSubset, currentPairSubset); 
				PrBAF fPrime = fStar.complete(currentArgumentSubset, currentPairSubset);
				if ( sem == SemanticsType.s_ad ) { 
					if ( !fPrime.safe(S) ) {
						prPrime = 0.0f;
					}
				}
				else if ( sem == SemanticsType.c_ad ) { 
					if ( !fPrime.closed(S) ) {
						prPrime = 0.0f;
					}
				}
				else {
					prPrime = 1.0f;
				}
				PrBAF fSigned = fStar;
				SemanticsType aafsem;
				if ( sem == SemanticsType.st ) { 
					aafsem = SemanticsType.st; 
				}
				else {
					aafsem = SemanticsType.ad; 
				}
				double prSigned = fSigned.computePrAAF(S, aafsem);
				pr += prStar * prPrime * prSigned;
			}
		}
		return pr;
	}

	
}
