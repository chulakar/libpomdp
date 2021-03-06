/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: CatchGen.java
 * Description: the reward is obtained for being collocated with the wumpus
 *              rather than for selecting a tagging action
 *              generate a POMDP specification in the SPUDD format for the
 *              catch problem 
 *              indentation is 2
 * Copyright (c) 2009, 2010 Diego Maniloff 
 --------------------------------------------------------------------------- */

package libpomdp.problems.catchproblem.java;

// imports
import java.io.PrintStream;

public class CatchGen {

    
    // ------------------------------------------------------------------------
    // properties
    // ------------------------------------------------------------------------
    
    // type of grid to generate
    CatchGridProperties gp;

    // type of wumpus we are against
    Wumpus w;

    // kind of sensing functions our agent posesses
    Sensor s;

    // output filename
    private PrintStream out;
    // grid dimensions:
    // for convenience, there will be N = WIDTH * HEIGHT physical locations
    // as we introduce obstacles by means of the wallAhead method,
    // some of them may become unreachable, but they are still part of the env
    //   <------------------->
    //  ^        WIDTH
    //  |
    //  |
    //  | HEIGHT
    //  |
    //  |
    //  v
    public int WIDTH;
    public int HEIGHT;
    // grid size
    private int N;
    // move directions
    public enum Direction {N, S, E, W} 

    // ------------------------------------------------------------------------
    // methods
    // ------------------------------------------------------------------------

    // constructor
    // takes output file stream
    public CatchGen (int width, 
		     int height, 
		     CatchGridProperties gp, 
		     Wumpus wumpus,
		     Sensor s,
		     PrintStream out) {
	this.out = out;
	this.gp  = gp;
	this.w   = wumpus;
	this.s   = s;
	this.WIDTH = width;
	this.HEIGHT = height;
	this.N =  WIDTH * HEIGHT;
    }

    // constructor to call from within the grapher
    public CatchGen() {
    }
	
    // generate the SPUDD file
    public void generate () {
	// generate comments
	out.println("// -------------------------------------------------------------------- *");
	out.println("// libpomdp");
	out.println("// ========");
	out.println("// File: autogenerated by CatchGen.java");
	out.println("// Description: ");
	out.println("// Copyright (c) 2009, 2010 Diego Maniloff");
	out.println("// -------------------------------------------------------------------- *");

	// state variables of the catch problem
	out.println("// state variables");
	out.println("// ---------------");
	out.println("(variables");
	// agent's position
	out.println(ind(2) + varDecl("apos", N)); 
	// wumpus' position
	out.println(ind(2) + varDecl("wpos", N)); 
	out.println(")");

	// observation variables of the catch problem
	out.println("// observation variables");
	out.println("// ---------------------");
	out.println("(observations");
	// location of the agent, since it is fully observable, there is
	// one observation corresponding to each of the possible locations
	out.println(ind(2) + varDecl("aloc", N));
	// sensing of the wumpus - this dictates the kind of sensing our agent has
	// we call it coll since originally this indicated collocation with the wumpus
	// coll0 = wumpus not sensed; coll1 = wumpus sensed
	out.println(ind(2) + varDecl("coll", 2));
	out.println(")");


	// initial belief state
	out.println("// initial belief state");
	out.println("// --------------------");
	out.println("init " + buildInit());       

	// dd declarations
	// wumpus' behaviour
	out.println("// wumpus' behaviour dd");
	out.println("// ----------------------------------------");
	out.println("dd wumpusb");
	out.println(ind(2) + wumpusb());
	out.println("enddd");

	// location observation dd
	out.println("// location observation dd");
	out.println("// -----------------------");
	out.println("dd ldd");
	out.println(ind(2) + ldd());
	out.println("enddd");

	// collocation observation dd 
	out.println("// collocation observation dd");
	out.println("// --------------------------");
	out.println("dd sensingdd");
	out.println(ind(2) + sensingdd());
	out.println("enddd");

	// action declarations
	out.println("// action declarations");
	out.println("// -------------------");

	// north
	out.println("action north");
	out.println(ind(2) + "apos " + move(Direction.N));
	out.println(ind(2) + "wpos (wumpusb)");
	out.println(ind(2) + "observe");
	out.println(ind(2) + "aloc (ldd)");
	out.println(ind(4) + "coll (sensingdd)");
	out.println(ind(2) + "endobserve");
	out.println("cost (1)");
	out.println("endaction");
	out.println();

	// south
	out.println("action south");
	out.println(ind(2) + "apos " + move(Direction.S));
	out.println(ind(2) + "wpos (wumpusb)");
	out.println(ind(2) + "observe");
	out.println(ind(2) + "aloc (ldd)");
	out.println(ind(4) + "coll (sensingdd)");
	out.println(ind(2) + "endobserve");
	out.println("cost (1)");
	out.println("endaction");
	out.println();

	// east
	out.println("action east");
	out.println(ind(2) + "apos " + move(Direction.E));
	out.println(ind(2) + "wpos (wumpusb)");
	out.println(ind(2) + "observe");
	out.println(ind(2) + "aloc (ldd)");
	out.println(ind(4) + "coll (sensingdd)");
	out.println(ind(2) + "endobserve");
	out.println("cost (1)");
	out.println("endaction");
	out.println();

	// west
	out.println("action west");
	out.println(ind(2) + "apos " + move(Direction.W));
	out.println(ind(2) + "wpos (wumpusb)");
	out.println(ind(2) + "observe");
	out.println(ind(2) + "aloc (ldd)");
	out.println(ind(4) + "coll (sensingdd)");
	out.println(ind(2) + "endobserve");
	out.println("cost (1)");
	out.println("endaction");
	out.println();

	// reward for being collocated 
	out.println("// reward for being collocated");
	out.println("// ---------------------------");
	out.println("reward " + collocatedReward());
	out.println();

	// discount and tolerance
	out.println("discount 0.95");
	out.println("tolerance 0.00001");
    }


    // declare a variable - state or observation
    // returns a single line, no carriage returns
    private String varDecl (String varname, int N) {
	String v = "(" + varname;
	for(int c=0; c<N; c++) v = v.concat(" " + varname + c);
	v = v.concat(")");
	return v;
    }
    

    // build the initial belief state
    private String buildInit() {
	int locs     = gp.totPossibleLocations();
	// all possible joint start locations, except those in which
	// the agent and the wumpus are collocated
	int totcases = locs * locs - locs; 
	int cases = 0;
	String v="(apos \n";
	for (int i=0; i<N; i++) {
	    v=v.concat(ind(2) + "(apos"+i+" (wpos\n");
	    for (int j=0; j<N; j++) {
		// make sure this is a valid joint position
		// and that both agents are not in the same location
		if (!gp.isLegalPosition(i) || !gp.isLegalPosition(j) || (i == j)) {
			v=v.concat              (ind(4) + "(wpos"+j + " (0.0))\n");
		    }
		else {
		    // last case - sum up to 1.0
		    if (cases == totcases - 1) {
			v=v.concat(String.format(ind(4) + "(wpos"+j + " (%.14f))\n", 
						 (1.0-((totcases-1.0)/totcases))));
		    }else {
			v=v.concat(String.format(ind(4) + "(wpos"+j + " (%.14f))\n", 
						 (1.0/(totcases))));
		    }
		    cases++;
		}
	    }
	    v=v.concat(ind(2) + "))\n");
	}
	v=v.concat(")\n");
	return v;
    }


    // move actions
    private String move(Direction m) {
	String v = "(apos\n";
	for (int currPos=0;currPos<N;currPos++) {	    
	    if (gp.wallAhead(currPos, m)){ 
		// this call here defines the obstacles, if there is one
		// we simply stay at currPos
		v=v.concat(ind(4) + "(apos"+currPos + " (apos'\n");
		v=v.concat(ind(6) + massX("apos", N, currPos));
		v=v.concat(ind(4) + "))\n");	
	    }else{
		// otherwise, we move deterministically 
		v=v.concat(ind(4) + "(apos"+currPos + " (apos'\n");
		v=v.concat(ind(6) + massX("apos", N, getAdjacentPos(currPos, m)));
		v=v.concat(ind(4) + "))\n");
	    }
	}
	v=v.concat(ind(2) + ")\n");
	return v;
    }

    
    // return adjacent position to p1 in direction move
    // this is called knowing that there is no wall ahead
    private int getAdjacentPos(int p1, Direction move) {
	int x1=gp.getxy(p1)[0]; int y1=gp.getxy(p1)[1];
	switch (move) {
	case N:
	    return getpos (x1, y1 + 1);
	case S:
	    return getpos (x1, y1 - 1);
	case E:
	    return getpos (x1 + 1, y1);
	case W:
	    return getpos (x1 - 1, y1);
	}	
	// never reachable
	return -1;
    }

    // convert (x,y) coordinates into integer position
    private int getpos(int x, int y) {
	return y * WIDTH + x;	
    }

    // // convert absolute position to (x,y) coordinates
//     // xy[0] contains x coordinate
//     // xy[1] contains y coordinate
//     public int[] getxy(int pos) {
// 	int xy[] = new int[2];
// 	// compute row and col
// 	xy[0] = pos % WIDTH;
// 	xy[1] = pos / WIDTH;
// 	// return
// 	return xy;
//     }

    // concentrate all mass in value pos
    private String massX(String varname, int l, int pos) {
	String v = "";
	String prob = "";
	for(int c=0; c<l; c++) {
	    if (c == pos)
		prob = "1.0";
	    else
		prob = "0.0";
	    v = v.concat(" (" + varname + c + " (" + prob + "))");
	}
	return v;
    } // massX

    // uniform dis of values, with one exclusion
    private String unifD(String varname, int l, int exclude) {
	String v="";
	int cs = 0;
	int cases = l-1;
	double prob = 1.0/cases;
	for(int c=0; c<l; c++) {
	    if (c == exclude)
		v = v.concat("(" + varname + c + " (0.0))");
	    else if (cases-1 == cs) {
		// last case
		v = v.concat(String.
			     format("(" + varname + c + " (%.14f))", (1.0 - prob * (cases-1.0))));
		cs++;
		    }
	    else {
		v = v.concat(String.
			     format("(" + varname + c + " (%.14f))", prob));
		cs++;
	    }
	}
	return v;
    } // unifD

    // location decision diagram
    // observation function gives us the location of the robot
    private String ldd () {
	String v="(apos'\n";
	for (int c=0;c<N;c++){
	    v=v.concat(ind(2) + "(apos"+c+"\n");
	    v=v.concat(ind(4) + "(aloc' " + massX("aloc", N, c) + "))\n");
	}
	v=v.concat(")\n");
	return v;	
    }

    // collocation decision diagram
    // when the absolute positions are equal, (coll' (coll0 (0.0)) (coll1 (1.0)))
    private String sensingdd () {
	String v="(apos'\n";
	for (int c=0;c<N;c++){
	    v=v.concat(ind(2) + "(apos"+c+" (wpos'\n");
	    for (int k=0;k<N;k++) {
		// see if the wumpus is in range according to the sensing function...
		if (s.inRange(c,k))
		    v=v.concat(ind(4) + "(wpos"+k+ " (coll' (coll0 (0.0)) (coll1 (1.0))))\n"); 
		else
		    v=v.concat(ind(4) + "(wpos"+k+ " (coll' (coll0 (1.0)) (coll1 (0.0))))\n");
	    }
	    v=v.concat(ind(2) + "))\n");
	}
	v=v.concat(")\n");
	return v;	
    }

    // behaviour of the wumpus
    private String wumpusb() {
	String v      = "(apos\n";
	double actd[] = new double[5];
	for (int c=0;c<N;c++){
	    v=v.concat(ind(2) + "(apos"+c+" (wpos\n");
	    for (int k=0;k<N;k++) {
		v=v.concat(ind(4) + "(wpos"+k+" (wpos'\n" + ind(6));
		actd = w.getActDist(c, k);
		for (int s=0;s<N;s++) {
		    // this assumes the wumpus can only move to its adjacent cells...
		    if(getAdjIndex(k, s) >= 0)			
			v=v.concat("(wpos"+s+" ("+ 
				   String.format("%.5f",actd[getAdjIndex(k, s)]) +
				   "))");
		    else
			v=v.concat("(wpos"+s+" (0.0))");
		}
		v=v.concat(ind(4) + "\n))\n");
	    }
	    v=v.concat(ind(2) + "))\n");
	}
	v=v.concat(")\n");
	return v;
    }
		
    private int getAdjIndex(int currpos, int nextpos) {
	// 0-none, 1-north, 2-east, 3-south, 4-west
	int currx = gp.getxy(currpos)[0];
	int curry = gp.getxy(currpos)[1];
	int nextx = gp.getxy(nextpos)[0];
	int nexty = gp.getxy(nextpos)[1];
	// none
	if(nexty == curry && nextx == currx)
	    return 0;
	// north
	else if (nexty == curry+1 && nextx == currx)
	    return 1;
	// east
	else if (nexty == curry && nextx == currx+1)
	    return 2;
	// south
	else if (nexty == curry-1 && nextx == currx)
	    return 3;
	// west
	else if (nexty == curry && nextx == currx-1)
	    return 4;
	else
	    return -1;
    }    


    // collocation reward
    private String collocatedReward() {
	String v="(apos\n";
	for (int c=0;c<N;c++){
	    v=v.concat(ind(2) + "(apos"+c+" (wpos\n");
	    for (int k=0;k<N;k++) {
		if (k==c)
		    v=v.concat(ind(4) + "(wpos"+k+ " (10.0))\n"); 
		else
		    v=v.concat(ind(4) + "(wpos"+k+ " (0.0)) \n");
	    }
	    v=v.concat(ind(2) + "))\n");
	}
	v=v.concat(")\n");
	return v;	

    }

    // indentation
    private String ind(int i) {
	String v="";
	for(int c=0; c<i; c++) v=v.concat(" ");
	return v;
    }

} // CatchGen