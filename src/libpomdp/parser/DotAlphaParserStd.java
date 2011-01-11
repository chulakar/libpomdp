/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: dotpomdpParserSparseMTJ.java
 * Description: Simple class to parse a .POMDP file and return
 *              an object of type pomdpSpecSparseMTJ with all the problem
 *              parameters
 * Copyright (c) 2009, 2010 Diego Maniloff 
 * W3: http://www.cs.uic.edu/~dmanilof
 --------------------------------------------------------------------------- */

package libpomdp.parser;

// imports
import libpomdp.general.java.valueFunction;
import libpomdp.general.java.valueFunctionFlat;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

public class DotAlphaParserFlat {    

    static Integer actions[];
    static Double  alphas[][];

    public static void parse (String filename) throws Exception {
	DotAlphaLexer lex = new DotAlphaLexer(new ANTLRFileStream(filename));
       	CommonTokenStream tokens = new CommonTokenStream(lex);
        DotAlphaParser parser = new DotAlphaParser(tokens);

        try {
            parser.dotalpha();
        } catch (RecognitionException e)  {
            e.printStackTrace();
        }
	
	actions = parser.getActions();
	alphas  = parser.getAlphas();
    }

    public valueFunction getValueFunction() {
	int s = actions.length;
	int d = alphas[0].length;
	int a[] = new int[s];
	double v[][] = new double[s][d];

	// convert from Integer to int and Double to double
	for (int i=0; i<s; i++) {
	    a[i] = actions[i].intValue();
	    for (int j=0; j<d; j++) v[i][j] = alphas[i][j].doubleValue();
	}
	// generate flat value function
	return new valueFunctionFlat(v, a);
    }

}