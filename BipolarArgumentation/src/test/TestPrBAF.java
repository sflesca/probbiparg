package test;

import java.util.HashSet;
import java.util.Set;

import structures.ArgSet;
import structures.BAF;
import structures.PrBAF;

public class TestPrBAF {
	public static void main(String[] args) {

		PrBAF baf = new PrBAF();

		baf.addArg("a",1);
		baf.addArg("b",1);
		baf.addArg("c",1);
		baf.addArg("d",1);
		baf.addArg("e",0.5);
		baf.addArg("f",1);

		baf.addDefeat("e", "b", 0.5);
		baf.addDefeat("b", "c",1);
		baf.addDefeat("f", "d",1);
		baf.addSupport("a", "b",1);
		baf.addSupport( "c", "d",1);
		
		ArgSet ae= new ArgSet("a","e");
		Set<BAF> notZeroProbBAF= new HashSet<>();
		double prob=0;
		long t= System.currentTimeMillis();
		System.out.println("TUTTI I POSSIBILI MONDI");
		int k=0;
		for(BAF baffo:baf.pw()) {
			System.out.println(baffo);
			double p=baf.computeProb(baffo);
			prob+=p;
			if(Double.compare(p, 0)!=0)
				notZeroProbBAF.add(baffo);
			System.out.println("N :"+k+++" Prob Cumulativa :"+String.format("%1.2f", prob));
			System.out.println("---------------------");
		}
		System.out.println("Prob totale :"+prob);
		System.out.println("=================================");
		System.out.println("MONDI CON PROBABILITA' NON NULLA");
		System.out.println();
		notZeroProbBAF.stream().forEach(
				b->{System.out.println(b+"\n");
					System.out.println("Prob :"+baf.computeProb(b));
					System.out.println("----------------------");
				}	
				);

		System.out.println("DADMISSIBLE :"+String.format("%1.2f", baf.probDadmissible(ae)));
		System.out.println("Tempo impiegato al calcolo "+( System.currentTimeMillis()-t) +" ms");		
		
	}
}
