package test;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import generator.PrBAFGenerator;
import structures.ArgSet;
import structures.BAF;
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
			FileInputStream stream = new FileInputStream("/Users/francesco/Software/Java/BipolarArgumentation/BipolarArgumentation/prbafs/6prbaf-0.txt");
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
/*			
			System.out.println("TUTTI I POSSIBILI MONDI");
			long t = System.currentTimeMillis();
			double prob = 0;
			int k = 0;
			for (BAF baffo : baf.pw()) {
//				System.out.println(baffo);
				double p = baf.computeProb(baffo);
				prob += p;
			}
			long t3 = System.currentTimeMillis();
			System.out.println("Tempo impiegato al calcolo " + (t3 - t) + " ms");
			System.out.println("Prob totale :" + prob);
			System.out.println("=================================");
			*/
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
			System.out.println("caaaaaxXXXXX");
			List<Pair> rightPairs = Support.filterPairs(Re, currentArgumentSubset);
			for ( List<Pair> currentPairSubset : Support.getAllPairsSubsets(rightPairs) ) {
				System.out.println("INNNNNNNNN");

System.out.println("AE"+ Ae.toString());
System.out.println("cas"+ currentArgumentSubset.toString());
System.out.println("-COMPL"+ Support.generateComplemetaryArgumentSubset(Ae, currentArgumentSubset).toString());


				double prPrime = 1.0f;
				PrBAF fStar = baf.contract(currentArgumentSubset, currentPairSubset, Support.generateComplemetaryArgumentSubset(Ae, currentArgumentSubset), Support.generateComplemetaryPairsSubset(Re, currentPairSubset));
System.out.println("F*.A"+ fStar.args.toString());
System.out.println("F.PRob"+ baf.argProb.toString());
System.out.println("F*.PRob"+ fStar.argProb.toString());
				double prStar = fStar.calculatePr(currentArgumentSubset, currentPairSubset); 
				PrBAF fPrime = fStar.complete(currentArgumentSubset, currentPairSubset);
				System.out.println(sem);
				if ( sem == SemanticsType.s_ad ) { 
					if ( !fPrime.safe(S) ) {
						System.out.println("prPrime!safe");
						prPrime = 0.0f;
					}
				}
				else if ( sem == SemanticsType.c_ad ) { 
					if ( !fPrime.closed(S) ) {
						System.out.println("prPrime!closed");
						prPrime = 0.0f;
					}
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
				System.out.println("1: " + prStar);
				System.out.println("2: " + prPrime);
				System.out.println("3: " + prSigned);

				pr += prStar * prPrime * prSigned;
			}
		}
		return pr;
	}

	
}
