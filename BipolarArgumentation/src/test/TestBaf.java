package test;

import structures.BAF;

public class TestBaf {
	public static void main(String[] args) {
		BAF baf= new BAF();
		String[] argNames=new String[]{"a","b","c","d","e","f","g","h"};

		baf.addArgs(argNames);
		
		baf.addDefeat("a", "b");
		baf.addDefeat("a", "f");
		baf.addDefeat("c", "e");
		baf.addDefeat("d", "e");
		baf.addDefeat("e", "b");
		baf.addDefeat("g", "f");
		baf.addDefeat("g", "d");
		baf.addDefeat("h", "d");
		
		
		baf.addSupport("b", "d");
		baf.addSupport("a", "g");
		baf.addSupport("g", "e");
		baf.addSupport("g", "h");

		System.out.println(baf);
		
		baf.removeArg("a");
		System.out.println(baf);
		
	}

}
