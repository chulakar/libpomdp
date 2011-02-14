package libpomdp.common.brl;

import libpomdp.common.CustomVector;

public class LogmaxDiffReward implements RlReward {

	public double get(int state, int action, int nstate, TransitionModelBelief bel) {
		DirichletBelief select=bel.getDirichlet(state,action);
		int th=(int)select.getParameter(nstate);
		double tot=select.getParameterNorm();
		double retval=Math.log(tot) ;
		if (th>1) 
			retval+=(th-1)*Math.log(th/(th-1));
		return retval;
	}

	public CustomVector get(int state, int action, TransitionModelBelief bel) {
		// TODO Auto-generated method stub
		return null;
	}	
}