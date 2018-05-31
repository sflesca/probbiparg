package test;

import java.util.Iterator;

import structures.ArgSet;
import structures.BAF;
import structures.PrBAF;

public class TestPrBAF {
	public static void main(String[] args) {

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
		ArgSet ae = new ArgSet("a", "e");
		double prob = 0;
		long t = System.currentTimeMillis();
		System.out.println("TUTTI I POSSIBILI MONDI");
		int k = 0;
		for (BAF baffo : baf.pw()) {
			System.out.println(baffo);
			double p = baf.computeProb(baffo);
			prob += p;
			System.out.println("N :" + k++ + " Prob Cumulativa :" + String.format("%1.2f", prob));
			System.out.println("---------------------");
		}
		System.out.println("Prob totale :" + prob);
		System.out.println("=================================");


		long t3 = System.currentTimeMillis();
		System.out.println("Tempo impiegato al calcolo " + (t3 - t) + " ms");

		long t2 = System.currentTimeMillis();
		prob = 0;
		k = 0;
		Iterator<BAF> it = baf.worlds();
		while (it.hasNext()) {
			BAF baffo = it.next();
			System.out.println(baffo);
			double p = baf.computeProb(baffo);
			prob += p;
			System.out.println("N :" + k + " Prob Cumulativa :" + String.format("%1.2f", prob));
			System.out.println("---------------------");
			k++;

		}
		System.err.println("N :" + (k - 1) + " Prob Cumulativa :" + String.format("%1.2f", prob));
		System.out.println("---------------------");
		System.out.println("Tempo impiegato al calcolo con iteratore a complessità spaziale esponenziale " + (t3 - t) + " ms");
		long t4 = System.currentTimeMillis();

		System.out.println("Tempo impiegato al calcolo con iteratore a complessità spaziale costante  " + (t4 - t2) + " ms");
		System.out.printf("%1.3f\n", (t3 - t) * 1.0 / (t4 - t2));
		System.out.println("=================================");
		System.out.println("MONDI CON PROBABILITA' NON NULLA");

		Iterator<BAF> notZero = baf.notZeroProbPossibleWorlds();
		while (notZero.hasNext()) {
			BAF b = notZero.next();
			System.out.println(b + "\n");
			System.out.println("Prob :" + baf.computeProb(b));
			System.out.println("----------------------");
		}
		System.out.println("AMMISSIBILITA' RISPETTO "+ae);
		System.out.println("DADMISSIBLE :" + String.format("%1.2f", baf.probDadmissible(ae)));
		System.out.println("CADMISSIBLE :" + String.format("%1.2f", baf.probCadmissible(ae)));
		System.out.println("SADMISSIBLE :" + String.format("%1.2f", baf.probSadmissible(ae)));
		
		
		baf = new PrBAF();
		baf.addArg("a", .8);
		baf.addArg("b",.3);
		baf.addArg("c",.5);
		
		baf.addDefeat("b", "a", .7);
		baf.addDefeat("c", "a", .2);
		System.out.println("MONDI CON PROBABILITA' NON NULLA");
		prob=0;
		k=0;
		notZero = baf.notZeroProbPossibleWorlds();
		while (notZero.hasNext()) {
			BAF baffo = notZero.next();
			System.out.println(baffo);
			double p = baf.computeProb(baffo);
			prob += p;
			System.out.println("N :" + k + "Prob "+p+" Prob Cumulativa :" + String.format("%1.2f", prob));
			System.out.println("---------------------");
			k++;
		}
//		baf = new PrBAF();
//		char[] ch="abcdefghijklmnopqrstuvz".toCharArray();
//		for(char c:ch)
//			baf.addArg(c+"",Math.random());
//		for(int i=0;i<5;i++)
//			baf.addDefeat(ch[(int) (Math.random()*8)]+"", ch[(int) (Math.random()*8+8)]+"");
//		for(int i=0;i<6;i++)
//			baf.addSupport(ch[(int) (Math.random()*8+8)]+"", ch[(int) (Math.random()*8+10)]+"");
//		long t5 = System.currentTimeMillis();
//		notZero = baf.worlds();
//		prob=0;
//		k=0;
//		while (notZero.hasNext()) {
//			BAF b = notZero.next();
////			System.out.println(b + "\n");
//			double p=baf.computeProb(b);
//			k++;
////			System.out.println("Prob ["+k+++"]: " +p);
//			prob+=p;
////			System.out.println("----------------------");
//		}
//		System.err.println((System.currentTimeMillis()-t5)/1000d +" secondi alla computazione di "+ k+" mondi");
//		System.err.println(prob);
//		
	}
}
