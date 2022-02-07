package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;


public class Move {

	/* This is the main move function that takes the current point and the angle that it has been calculated to travel at
	 It then takes the builds list in as well as the flightpath list. 
	 */
	
	
    public static ArrayList<List<String>> move (Point current, int angle,  List<Feature> buildings, ArrayList<List<String>> flightPath, Point destination) {
    	
    	//Sets the overall length of the move to be 0.0003 degrees as per spec
    	var distance = 0.0003;
    	
    	//Calls the nextPoint function to caoulcate the theoretical nextPoint
    	var nextPoint = nextPoint(angle, current, distance);
    	
    	//This is a list of possible nextPoints that we will use if this point we calculted falls out of bounds
    	var possible = new ArrayList<List<Double>>();
    	var distances = new ArrayList<Double>();
    	
    	

    		//Checks if the move goes out of bounds, if it does then it checks every multuiple of 10 angle to see if it also goes out of bounds
    		if (checkMove(current, buildings, angle)) {
    			
    			for(int j = 0; j < 36; j++) {
    				
    				
    				angle = 10 * j;
        			
    				
    	    		if (checkMove(current, buildings, angle)) {

    	    			// If the point does not go out then a new TreeMap is made of the distance from the next point to the destination
    	    			// this is then sorted by default so that the first entry in the treemap is the closest distance
    	    	    	ArrayList<Double> point = new ArrayList<Double>();
            			nextPoint = nextPoint(angle, current, distance);
    	    	    	point.add(nextPoint.longitude());
            			point.add(nextPoint.latitude());
            			
            			possible.add(point);
            			
            			distances.add(Calculate.distance(destination.longitude(), nextPoint.longitude(), destination.latitude(), nextPoint.latitude()));

    	    		} else {
    	    			//If it does goes out of bounds then nothing happens
            			
    	    		}
    	    		
        	    	
    			}
    			
    			//choices is the treemap of the possible moves with the key being the distance to the destination
    			var choices = Calculate.toVisit(distances, possible);
    			
    			// This just finds the first key value and then the corresponding point 
    	    	var key = choices.keySet().iterator().next();
    		  	var choice = choices.get(key);
    		  	
    		  	//Set nextPoint to be the point from the ket value
    		  	
    		  	nextPoint = Point.fromLngLat(choice.get(0), choice.get(1));
    		  	
    		  	//Calculates the angle that it must have travelled at
    		  	angle = Calculate.getAngle(current, nextPoint);
    			
		
    		}

    		
   
    	
    	// Creates a list of all the details that must be stored to be used in the flightpath text file
    	ArrayList<String> pointDetails = new ArrayList<String>();
    		   	
    	
    	//Adds all of the edetails that are needed in order that they will be needed
    	pointDetails.add(String.valueOf(current.longitude()));
    	pointDetails.add(String.valueOf(current.latitude()));
    	pointDetails.add(String.valueOf(angle));
    	pointDetails.add(String.valueOf(nextPoint.longitude()));
    	pointDetails.add(String.valueOf(nextPoint.latitude()));
    	
    	
    	//Adds this pointdetail list to the flightpath list
    	flightPath.add(pointDetails);
    	

    	//returns the flightpath list
    	return flightPath;
    }
          
    
    	
/* 
 This function is used to calculate the nextPoint needed along a path.
 It has 2 main uses, first it is used to calculate a full move but can also be used to check if a smaller distance along the same line falls into an 
 out of bounds area. To use both these fucntions it takes a double called distance which is the overall length that the move should be
 So in normal moving this distance is inputted as 0.0003
 */
       
    public static Point nextPoint (int angle, Point current, double distance) {
    	
    	Double deltalng;
    	Double deltalat;
    	double lng;
    	double lat;
 
    	/* checks the angle that is inputted as the different angles will need to be manipulated differently to get the correct angle in a 
    	right angled triangle to be able to use triginometery and calculate the delta distances
    	*/ 
    	
    	if (angle <= 90) {
    		
           	deltalng = distance * Math.cos(Math.toRadians(angle));
        	deltalat = distance * Math.sin(Math.toRadians(angle));
        	
        	/*Corresponding to the angle as well depends whether the latitude or longitude is increased or decreased by the delta values calculated
        	 Also being careful to use the math.radian function to change the angle to radians for calculations (Took me a long time to figure that out :( )
        	*/ 
        	
        	lng = current.longitude() + deltalng;
        	lat = current.latitude() + deltalat;
    		
    	} else if (angle <= 180) {
    		
           	deltalng = distance * Math.cos(Math.toRadians(180 - angle));
        	deltalat = distance * Math.sin(Math.toRadians(180 - angle));
        	
        	lng = current.longitude() - deltalng;
        	lat = current.latitude() + deltalat;
    		
    	} else if (angle <= 270) {
    		
           	deltalng = distance * Math.cos(Math.toRadians(angle - 180));
        	deltalat = distance * Math.sin(Math.toRadians(angle - 180));
        	
        	lng = current.longitude() - deltalng;
        	lat = current.latitude() - deltalat;
    		
    	}else {
    		
           	deltalng = distance * Math.cos(Math.toRadians(360 - angle));
        	deltalat = distance * Math.sin(Math.toRadians(360 - angle));
        	
        	lng = current.longitude() + deltalng;
        	lat = current.latitude() - deltalat;
    		
    	}
    	

    	var nextPoint = Point.fromLngLat(lng, lat);
    	
    	return nextPoint;
    	
    }
    
    
    
    /*This function is a boolean  fucntion that tests whether the next point or each of the 100 smaller increments of that full move
      fall into the out of bounds areas which include: the buidlings and outside the square around the george square campus
     */
    
    public static boolean checkMove (Point current, List<Feature> buildings, int angle) {
    	
    	//For each building check if any increment along the path is illgeal
    	for (int j = 0; j < buildings.size(); j++) {
    
    		var building = (Polygon) buildings.get(j).geometry();
    		
    		
    		for (int i = 1; i < 101; i++) {
    			
    			Double distance = ((i * 0.0003/100));
    			
    			var increment = nextPoint(angle, current, distance);
    		
    		    		
    			if (TurfJoins.inside(increment, building) || increment.longitude() >= -3.192473
            			|| increment.longitude() <= -3.184319 || increment.latitude() >= 55.946233
            			|| increment.latitude() <= 55.942617) {
        		
    				// Returns true any point along this full nextPoint line falls within one of the buidlings or outside of the square
    			
                 	return true;            	 
             }
    		
    	}

       
    		
    	}
    	  //returns false if it does not fall in any out of bound areas
    	  return false;

    
    
    }
}
