package generator;

import structures.ArgSet;
import structures.PrBAF;
import java.io.*;
import java.util.Scanner;

public class PrBAFGenerator {

	static PrBAF generate(int numarg, double percatt, double percsup) {
		PrBAF prbaf = new PrBAF();

		for (int i = 0; i < numarg; i++)
			prbaf.addArg("a" + i, Math.random());

		for (int i = 0; i < numarg; i++)
			for (int j = 0; j < numarg; j++)
				if (Math.random() <= percatt)
					prbaf.addDefeat("a" + i, "a" + j, Math.random());

		for (int i = 0; i < numarg; i++)
			for (int j = 0; j < numarg; j++)
				if (Math.random() <= percsup)
					prbaf.addSupport("a" + i, "a" + j, Math.random());
		return prbaf;
	}

	static public void printPrBAF(PrBAF prbaf, PrintWriter f) {
		f.println(prbaf.args.size());
		for (String a : prbaf.args) {
			f.println(a + " " + prbaf.argProb.get(a));
		}
		int numdefeats = 0;
		for (String a : prbaf.defeats.keySet())
			for (String c : prbaf.defeats.get(a)) {
				numdefeats++;
			}
		f.println(numdefeats);
		for (String a : prbaf.defeats.keySet()) {
			for (String c : prbaf.defeats.get(a)) {
				f.println(a + " " + c + " " + prbaf.defProb.get(a).get(c));
			}
		}
		int numsupports = 0;
		for (String a : prbaf.supports.keySet())
			for (String c : prbaf.supports.get(a)) {
				numsupports++;
			}
		f.println(numsupports);
		for (String a : prbaf.supports.keySet()) {
			for (String c : prbaf.supports.get(a)) {
				f.println(a + " " + c + " " + prbaf.supProb.get(a).get(c));
			}
		}
	}

	static public PrBAF readPrBA(InputStream f) {
		PrBAF prbaf = new PrBAF();
		Scanner sc = new Scanner(System.in);
		int numarg = sc.nextInt();
		for (int i = 0; i < numarg; i++) {
			String arg = sc.next();
			String sprob = sc.next();
			double prob = Float.parseFloat(sprob);
			prbaf.addArg(arg, prob);
		}
		int numdef = sc.nextInt();
		for (int i = 0; i < numdef; i++) {
			String arg1 = sc.next();
			String arg2 = sc.next();
			String sprob = sc.next();
			double prob = Float.parseFloat(sprob);
			prbaf.addDefeat(arg1, arg2, prob);
		}
		int numsup = sc.nextInt();
		for (int i = 0; i < numsup; i++) {
			String arg1 = sc.next();
			String arg2 = sc.next();
			String sprob = sc.next();
			double prob = Float.parseFloat(sprob);
			prbaf.addSupport(arg1, arg2, prob);
		}
		sc.close();
		return prbaf;
	}

	public static void main(String[] args) {
		// PrBAF prbaf = readPrBA(System.in);
		int[] numargs = { 6, 8, 10, 12, 14, 16, 18, 20 };
		for (int i : numargs) {
			for (int j = 0; j < 10; j++) {
				PrBAF prbaf = generate(i, .2, .03);
				try {
					PrintWriter out = new PrintWriter("prbafs/" + i + "prbaf-"+j+".txt", "UTF-8");
					printPrBAF(prbaf, out);
					out.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
