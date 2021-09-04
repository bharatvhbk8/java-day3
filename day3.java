import java.util.*;
import java.io.*;

class FareCalculator {
    // class constants
    public static final int STANDARD = 0;
    public static final int UNDEFINED = -1;
    public static final int CAB_PP = 1;
    
    
    public static final int[] capacities = {4,6};
	public static DistanceEstimator ds;

    // testing inputs
    //public static int[] passengers = {2,12,12};
    //public static String[] origin = {"NUS","NUS","NUS"};
    //public static String[] destination = {"Vivocity","ChangiAirport","NUS"};
    
    public static int fareEstimator (String o, String d, int cabType) throws Exception {
		int dist = 0;
		int fare;

		try {
			// DistanceEstimator x = new DistanceEstimator();
			// dist = x.estimateDistance(o,d);
			dist = ds.estimateDistance(o,d);
		} catch (Exception e) {
			throw e;
		}

		// The first 1000 m or less: S$2.50
		// S$0.10 for every 200 m
		// After 10 km - S$0.10 for every 175m
		// if (true) { System.out.println("distance " + o + " " + d + " = " + dist); }

		// initial fare
		fare = (cabType == STANDARD) ? 250 : 500;
		if (dist <= 1000) { 
			;
		} else if (dist <= 10000) {
			// initial distance
			dist -= 1000;

			fare += (dist / 200) * 10;
			fare += (dist % 200 != 0) ? 10 : 0;			   // correct rounding error
		} else {
			// initial distance
			dist -= 10000;
			fare += (9000/200) * 10;

			fare += (dist / 175) * 10;
			fare += (dist % 175 != 0) ? 10 : 0;			   // correct rounding error
		}
		return fare;
    }

    public static int partA (int p, String o, String d) throws Exception{
		int fare = fareEstimator(o,d,STANDARD);
		int numCabs = (p/capacities[STANDARD]);
		numCabs += (p % capacities[STANDARD] != 0) ? 1 : 0;
		
		return (numCabs * fare);
    }

    public static int partB (int p, String o, String d) throws Exception{
		int fare = fareEstimator(o,d,CAB_PP);
		
		int numCabs = (p/capacities[CAB_PP]);
		
		numCabs += (p % capacities[CAB_PP] != 0) ?1 : 0;
		
		return (numCabs * fare);
    }

    /**
     * Non-recursive version
     */
    public static int partCNonRecursive (int p, String o, String d) throws Exception{
		int fareS = fareEstimator(o,d,STANDARD);
		int fareC = fareEstimator(o,d,CAB_PP);
		int minFare = UNDEFINED;
		int currentFare;
		int s = 0;
		int c = 0;
		
		// max # of standard cabs to use
		int maxS = (p % capacities[STANDARD] == 0) ? (p / capacities[STANDARD]) :
		    ((p / capacities[STANDARD]) + 1);
		
		// loop for # standard cabs
		for (s = 0; s <= maxS; s++) {
		    // calculate fare so far
		    currentFare = fareS * s;
		    // calculate remaining passengers that need to be taken by cab++'s
		    int passengersRemaining = p - (s * capacities[STANDARD]);
		    
		    if (passengersRemaining > 0) {
		        // calculate # of cab++'s needed
				c = (passengersRemaining % capacities[CAB_PP] == 0) ? (passengersRemaining / capacities[CAB_PP]) : 
			     	    ((passengersRemaining / capacities[CAB_PP]) + 1);
			
				// revise fare	   
				currentFare += fareC * c;
		    } else { 
		        c = 0;
		    }
		
		    //  System.out.println("Current Fare for (pr=" + passengersRemaining + 
		    //       ",s=" + s + ",c=" + c + "): " + currentFare);
		
		    if (minFare > currentFare || minFare == UNDEFINED) {
		       minFare = currentFare;
		    }	       
		}
		return minFare;
    }

    /**
     * Recursive version
     */
    public static int partCRecursive (int fareAccumulated, int p, String o, String d) throws Exception{
		int fareS = fareEstimator(o,d,STANDARD);
		int fareC = fareEstimator(o,d,CAB_PP);
	
		// base cases
		if (p <= 0) { 
		    return (fareAccumulated);
		} else if (p <= capacities[STANDARD]) {
		    return (fareS + fareAccumulated);
	
	        // induction
		} else {
		    int resultS = partCRecursive(fareAccumulated + fareS, p - capacities[STANDARD], o, d);
		    int resultC = partCRecursive(fareAccumulated + fareC, p - capacities[CAB_PP], o, d);
		    if (resultS < resultC) { return (resultS); }
		    return (resultC);
		}
    }

    public static String formatDollars (int num) {
		String s = "" + num/100 + ".";
		s += (num%100 >= 10) ? num%100 : ("0" + num%100);
	
		return s;
    }

    public static void main (String[] args) {

		//args.length is used to find out the number of arguments the user supply to the program
		//i.e.
		//java ProgramClass arg1 arg2 arg3
		//arg1, arg2, arg3 are the arguments supplied to the program during runtime
		if(args.length != 2) {		
			System.out.println("Expecting two parameters...");
			
			//if don't have enough parameter should return from this main method(i.e. exit)
			//or System.exit(?)
			//? could be 0 if you thinks that this program terminates normally
			//use numbers other than 0 for abnormal program termination
			return;
		}
		
		try{
			//instantiate the DistanceEstimator object
			//since the constructor throws an Exception : 
			//public DistanceEstimator(String fileName) throws Exception{ ... }
			//we have to try-catch
			ds = new DistanceEstimator(args[0]);	
		}catch(Exception e){
			System.out.println("Invalid distance file...");
			return;
		}
			
		String s;
		
		try
		{
			//everything you declare a new BufferedReader
			//you will have to catch the IOException
			//or throws IOException
			//this would also meant that you have to place
			//new BufferedReader(...) in a method instead of just a class body
			
			BufferedReader br = new BufferedReader(new FileReader(args[1]));

			while ((s = br.readLine()) != null) {
				// read a line of input
				StringTokenizer st = new StringTokenizer(s,",");
				char task = st.nextToken().charAt(0);
				if(task != 'a' && task != 'b' && task != 'c')
					throw new Exception();
				
				int p = Integer.parseInt(st.nextToken());
				String o = st.nextToken();
				String d = st.nextToken();

//				System.out.println(o);
//				System.out.println(d);

				// process it 
				try
				{
					switch (task) {
						case 'a':
							int partA = partA(p, o, d);
							System.out.println("Cost for " + p + " passengers from " + o + " to " + d + " using standard cabs: " + formatDollars(partA));
							break;
						case 'b':
							int partB = partB(p, o, d);
							System.out.println("Cost for " + p + " passengers from " + o + " to " + d + "  using Cab++'s: " + formatDollars(partB));
							break;
						case 'c':
							// int partC = partCRecursive(0, p, o, d);
							int partC = partCNonRecursive(p, o, d);
							System.out.println("Lowest possible cost for " + p + " passengers from " + o + " to " + d + " : " + formatDollars(partC));
							// partC = partCRecursive(0, p, o, d);
							// System.out.println("Lowest possible cost: " + formatDollars(partC));
							break;
						default:
					}
				}catch(Exception e){
					System.out.println("Unknown location");
				}
			} 
			br.close();
		}catch (Exception e)
		{
			System.out.println("Invalid request file...");
		}
	}
}


class DistanceEstimator {

	public int n;

    public String[] places;

    public int[][] distanceMatrix;

	public DistanceEstimator(String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		n = Integer.parseInt(br.readLine());
		places = new String[n];
		distanceMatrix = new int[n][];

		String s = br.readLine();
		StringTokenizer st = new StringTokenizer(s,",");

		for(int i=0; i<n; i++)
		{
			places[i] = st.nextToken();
//			System.out.println(places[i]);
		}

		for(int i=0; i<n-1; i++)
		{
			s=br.readLine();
			st = new StringTokenizer(s);

			distanceMatrix[i] = new int[n-i-1];
			
			for(int j=0; j<n-i-1; j++)
				distanceMatrix[i][j] = Integer.parseInt(st.nextToken());
		}

		br.close();
	}

    public int estimateDistance(String origin, String destination) throws Exception {
		boolean invalidOrigin = true;
		boolean invalidDestination = true;
//		System.out.println(origin);
//		System.out.println(destination);
		int o;
		int d;

		//look for origin location
		for (o = 0; o < places.length; o++) {
			if (places[o].equals(origin)) {
				invalidOrigin = false;
				break;
			}
		}

		//look for destination location
		for (d = 0; d < places.length; d++) {
			if (places[d].equals(destination)) {
				invalidDestination = false;
				break;
			}
		}

		if (invalidDestination || invalidOrigin) { throw new Exception(); }

		if (origin.equals(destination)) { return 0; }
		
		
		//find the distance by going to the right location
		if (o < d) { return distanceMatrix[o][d-o-1]; }	
		return distanceMatrix[d][o-d-1];
    }

    /*
    public static void main (String[] args) {
	System.out.println(estimateDistance("NUS","ChangiAirport"));
	System.out.println(estimateDistance("NUS","Vivocity"));
    }
    */
}
