/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: belStateAdd.java
 * Description: implements belState via an ADD
 *              uses Popuart's implementation from Symbolic Perseus
 * Copyright (c) 2009, 2010 Diego Maniloff 
 --------------------------------------------------------------------------- */

package libpomdp.common.java.add;

import libpomdp.common.java.BeliefState;
import libpomdp.common.java.CustomVector;
import libpomdp.common.java.add.symbolic.DD;
import libpomdp.common.java.add.symbolic.OP;
 
public class BeliefStateAdd implements BeliefState {
    
    // main property is the DD itself
    public DD bAdd;

    // probability of reaching this belief when computing tao
    private double poba = -1.0;

    // plain id that supports this belief point
    private int planid = -1;

    // we need the state variable ids to call convert2array
    private int staIds[];

    // constructor
    // in case this is the init belief, poba = 0.0
    public BeliefStateAdd(DD b, int staIds[], double poba) {
	this.bAdd   = b;
	this.staIds = staIds;
	this.poba   = poba;
    }

    // compute this only if we actually need it
    @Override
    public CustomVector getPoint() {
    	return(new CustomVector(OP.convert2array(bAdd, staIds)));
    }

    @Override
    public double getPoba() {
	return poba;
    }

    @Override
    public void setPoba(double poba) {
	this.poba = poba;
    }

    @Override
    public int getAlpha() {
	return planid;
    }

    @Override
    public void setAlpha(int planid) {
	this.planid = planid;
    }

    @Override
    public double getEntropy() {
	// TODO Auto-generated method stub
	return 0;
    }

} // BelStateAdd