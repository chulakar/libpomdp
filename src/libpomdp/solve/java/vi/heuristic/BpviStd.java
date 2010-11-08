/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: blindFlat.java
 * Description: blind policy value function approximation with a flat
 *              representation
 *              do not try on large problems as this will run out of mem
 * Copyright (c) 2009, 2010 Diego Maniloff 
 * W3: http://www.cs.uic.edu/~dmanilof
 --------------------------------------------------------------------------- */

package libpomdp.solve.java.vi.heuristic;

// imports
import java.util.ArrayList;

import libpomdp.common.java.CustomVector;
import libpomdp.common.java.Utils;
import libpomdp.common.java.std.PomdpStd;
import libpomdp.common.java.std.ValueFunctionStd;
import libpomdp.solve.java.Criteria;
import libpomdp.solve.java.vi.ValueIterationStats;
import libpomdp.solve.java.vi.ValueIterationStd;


public class BpviStd extends ValueIterationStd {
	
	public BpviStd(PomdpStd pomdp){
		long inTime = System.currentTimeMillis();
		this.pomdp=pomdp;
		iterationStats=new ValueIterationStats(pomdp);
		stopCriterias= new ArrayList<Criteria>();
		// Blind is |A| x |S| - initialize each \alpha^a_{0} to \min_s {R(s,a)/(1-\gamma)}
		ValueFunctionStd iniv = new ValueFunctionStd(pomdp.nrStates());
		for(int a=0; a<pomdp.nrActions(); a++) {
			CustomVector vr=pomdp.getRewardValues(a).copy();
			double factor=1.0/(1.0-pomdp.getGamma());
			vr.scale(factor);
			double varr[]=vr.getArray();
			int idx=Utils.argmin(varr);
		    iniv.push(CustomVector.getHomogene(pomdp.nrStates(),vr.get(idx)),a);
		}
		current=iniv.copy();
		iterationStats.init_time = System.currentTimeMillis() - inTime;
	}
	
    // parameters
    //final int    MAX_ITER = 500;
    //final double EPSILON  = 1e-4;

    // ------------------------------------------------------------------------
    // methods
    // ------------------------------------------------------------------------

	@Override
	public ValueIterationStats iterate() {
		long inTime = System.currentTimeMillis();
		//System.out.println("== Iteration "+iterationStats.iterations+" ==");
		old=current.copy();
    	for(int a=0; a<pomdp.nrActions(); a++) {
    	    CustomVector vec=current.getVectorRef(a);
    	    CustomVector res=pomdp.getTransitionProbs(a).mult(pomdp.getGamma(),vec);
    	    res.add(pomdp.getRewardValues(a));
    	    vec.set(res);
    	}
    	iterationStats.register(System.currentTimeMillis() - inTime, current.size());
    	return iterationStats;
	}

    
    
}