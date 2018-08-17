package support;


public class Pair {
	private String a;
	private String b;
	private double probability;
	
	
	public Pair() {
	}
	
	public Pair(String a, String b) {
		this.a = a;
		this.b = b;
	}
	
	public Pair(String a, String b, double probability) {
		this.a = a;
		this.b = b;
		this.setProbability(probability);
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	

}
