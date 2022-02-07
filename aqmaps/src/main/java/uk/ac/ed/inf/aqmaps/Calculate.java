package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.mapbox.geojson.Point;

public class Calculate {
	
	
    /* This function takes the x and y coordinates from 2 points and returns the euclidian distance between them.
    */
    
    public static double distance(Double x1, Double x2, Double y1, Double y2) {
    	
    	var distance = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    	
    	return distance;
    }
    
    
    
    /* This function takes a list of doubles of the distances between points and a List of a list of the corresponding latitudes and longitudes
    It then places each list of points into a TreeMap with the distance and returns that TreeMap.
    */
    
    public static Map<Double, List<Double>> toVisit (ArrayList<Double> distances, List<List<Double>> coordinates) {
    	
 		Map<Double, List<Double>> toVisit = new TreeMap<>();
 		
 		for(int i = 0; i < distances.size(); i++) {
 			
 			toVisit.put(distances.get(i), coordinates.get(i));
 		}

 		return toVisit;
     }
    
    
    
    /* This function takes 2 points and calculates the angle between them.
     Due to the nature of our angle convention Math.atan2 finds the angle in radians with East = 0
     Math.toDegree then calculates this into a degree angle and then returns the rounded value to the nearest 10 degrees
    */
    
    public static int getAngle (Point current, Point destination) {
    	
    	var angle = Math.toDegrees(Math.atan2(destination.latitude() - current.latitude(), destination.longitude() - current.longitude()));    	
    	angle = (angle + 360) % 360;
    	
    	
    	return 10 * (int)(Math.round(angle/10.0));
    	

    }
    
}
