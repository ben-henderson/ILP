package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.*;
import java.util.*;  
import java.io.*;


public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException{
    	
    	
    	//Initilising all of the inital variables needed
    	var day = args[0];
    	var month = args[1];
    	var year = args[2];
    	var startlat = Double.parseDouble(args[3]);
    	var startlng = Double.parseDouble(args[4]);
    	var seed = Integer.parseInt(args[5]);
    	var rawUrl = "http://localhost:"+ args[6];
    	var moveCount = 0;
    	
    	//Creates lists that will be needed
		var flightPath = new ArrayList<List<String>>();  
		var features = new ArrayList<Feature>();
    	
		//Creates lists of each of these objects that will be passed through later on
    	var buildings = FeatureCollection.fromJson(GetData.getBuildings(rawUrl)).features(); 	
    	var weatherStations = GetData.getAirQualityJSON(rawUrl, day, month, year);
    	

		
		//Start of drone program
		var distances = new ArrayList<Double>();
		var coordinates = new ArrayList<List<Double>>();  
		
		Map<Double, String> names = new TreeMap<>();
		
    	for(int i = 0; i < weatherStations.size(); i++) {
    		
    		// for each weather station it gets the longtitudes and latitudes and the distances from the start to location to each
    		
    		var coords = new ArrayList<Double>();
    		
    		var w3w = weatherStations.get(i).location;
    		
    		var lng = GetData.getDetails(rawUrl, w3w).coordinates.lng;
    		var lat = GetData.getDetails(rawUrl, w3w).coordinates.lat;
    		
    		var distance = Calculate.distance(startlng, lng, startlat, lat);
    		
    		//longtitude and latitude is added to the coords array
    		
    		coords.add(lng);
    		coords.add(lat);
    		
    		//the distance and the w3w name is added to the names TreeMap
    		
    		names.put(distance, w3w);
    		
    		
    		distances.add(distance);  

    		coordinates.add(coords);
    	}
    	
    	//Calculates a TreeMap of all the distances and list of coordinates to all the weatherStation
    	// A TreeMap is used becasue it auto sorts by the key so the first value is by default the closest
    	
    	var toVisit = Calculate.toVisit(distances, coordinates);
    	
    	//Gets the distance (key) to the closest weather station
    	var distanceToFirstStation = toVisit.keySet().iterator().next();
    	
    	//Gets the name of the first station from the key
    	var name = names.get(distanceToFirstStation);
    	
    	//Gets the coordinates of the closest station
    	var  closest = toVisit.get(distanceToFirstStation);
    	
    	//Remove this key from both of the TreeMaps
    	toVisit.remove(distanceToFirstStation);
    	names.remove(distanceToFirstStation);

    	//Set the current location to the start
    	var current = Point.fromLngLat(startlng, startlat);
    	
    	//Set the destination to be the closest weather station
    	var destination = Point.fromLngLat(closest.get(0), closest.get(1));
    	
    	//Calculate angle requried
    	var angle = Calculate.getAngle(current, destination);

    	
    	//While loop cycles as long as the move count is less than 151 (to allow for 150 moves)
    	while(moveCount < 151) {

    	//First check is if the moveCount is 0, meaning we must move - cannot record station first
    	if (moveCount == 0) {
    		
    		//Calls the move function
    		flightPath = Move.move(current, angle, buildings, flightPath, destination);

    		//Sets the new current point to the poinnt calculated in the move function
    		current = Point.fromLngLat(Double.parseDouble(flightPath.get(moveCount).get(3)), Double.parseDouble(flightPath.get(moveCount).get(4)));
    		
    		moveCount++;
    		
    		
    		// We then check if the toVisit list is empty and if the current location and the destination is within range
    		// This means the next destination is the start position
    	} else if (toVisit.isEmpty() && ((Calculate.distance(current.longitude(), destination.longitude(), current.latitude(), destination.latitude()) < 0.0002))) {
    		
    		//Read Stations
    		Stations.readStation(destination, name, weatherStations, features);
    		
    		//Since a station has been read we must addName to the corresponding flighpath
    		flightPath = Stations.addNameToFlight(flightPath, moveCount, name);
    		
    		//Set destination to the start
    		destination = Point.fromLngLat(startlng, startlat);
    		
    		//Calculate new angle needed
    		angle = Calculate.getAngle(current, destination);
    		
    		
    		//Check if the current point is within range of the destination
    	} else if (Calculate.distance(current.longitude(), destination.longitude(), current.latitude(), destination.latitude()) < 0.0002) {
    		
    		//Read the stations
    		Stations.readStation(destination, name, weatherStations, features);
    		
    		//Since a station has been read we must addName to the corresponding flighpath
        	flightPath = Stations.addNameToFlight(flightPath, moveCount, name);
        	
    		//We must now recalculate all of the distances to the weather stations again along with the names
        	// 
    		coordinates = new ArrayList<List<Double>>();  
    		distances = new ArrayList<Double>();
    		var updatedNames = new ArrayList<String>();
    		
    		//Loop to move all of the coorindates of the stations into a new list
    		while (toVisit.size() > 0) {
    			
    		  	var key = toVisit.keySet().iterator().next();
    		  	updatedNames.add(names.get(key));
    		  	
    		  	coordinates.add(toVisit.get(key));
    			toVisit.remove(key);
    			names.remove(key);
    		}
    		
    		//Calculates the distances from the current point to all of the remaining weather stations
    		for (int i = 0; i < updatedNames.size(); i++) {
    			
    			var distance = Calculate.distance(current.longitude(), coordinates.get(i).get(0), current.latitude(), coordinates.get(i).get(1));
    			distances.add(distance);
    			
    			//Add the names and distances to the names TreeMap which self orders
    			names.put(distances.get(i), updatedNames.get(i));

    			
    		}
    		
    		//Produces a new TreeMap of coordinates and distances
			toVisit = Calculate.toVisit(distances, coordinates);
			
			//Finds the key which is the distance to the closest weather station
	    	var distanceToNext = toVisit.keySet().iterator().next();
	    	
	    	//Get the coordinates and name of the closest station and removes it from the corresponding TreeMap
	    	closest = toVisit.get(distanceToNext);
	    	name = names.get(distanceToNext);
	    	toVisit.remove(distanceToNext);
	    	names.remove(distanceToNext);
	    	
	    	//Destination set to be the closest station
	    	destination = Point.fromLngLat(closest.get(0), closest.get(1));
	    	

					
    		//Checks if the movecount is 150 or the current point is within range of the beginging with the start point being equal to the destination
    	} else if (moveCount == 150 || (((Calculate.distance(current.longitude(), startlng, current.latitude(), startlat) < 0.0003 ) && destination.equals(Point.fromLngLat(startlng, startlat))))) {
    		
    		//Calls the end function, which closes the java application
    		end(features, flightPath, day, month, year, toVisit, name, closest, names, weatherStations); 
    		
    	}
    	
    	
    	
    
    	else  {
    		
    		// Otherwise we just calculate the angle  required and call the move function
    		angle = Calculate.getAngle(current, destination);
    		
    		flightPath = Move.move(current, angle, buildings, flightPath, destination);
    		
    		// Set the current point to be the value calculated in move function
    		current = Point.fromLngLat(Double.parseDouble(flightPath.get(moveCount-1).get(3)), Double.parseDouble(flightPath.get(moveCount-1).get(4)));
    		
    		//Incremment the move count
    		moveCount++;
    	}
    }
    
    
    }
       
       
    
    /* This function is the finale of the code
    It takes the list of features which contains all of the markers and their properties as well as the List of lists flighPath
    A list of points is then produced from flightPath and a lineString produced from this list
    This lineString is then added to the feature collection and is written to disk using the naming covention as asked.
    
    The flightPath list is then used to create a text file and write that to disk using each of the elements in the list on a given line 
    */
    
    public static void end (ArrayList<Feature> features, ArrayList<List<String>> flightPath, String day, String month, String year, 
    		Map<Double, List<Double>> toVisit, String name, List<Double> closest, Map<Double, String> names, ArrayList<AirQualityData> weatherStations) {
    	
    	//Checks if all of the statiosn were reached
   	 	if(features.size() < 33) {
   	 		//take the previously calculated closest station and record it through  the function missed station
   	 		var destination = Point.fromLngLat(closest.get(0), closest.get(1));
   	 		
   	 		features = Stations.missedStation(destination, name, weatherStations, features);
   	 		
   	 	} if(!toVisit.isEmpty()) {
   	 		
   	 		while(!toVisit.isEmpty()) {
   	 			
   	 		var nextStation = toVisit.keySet().iterator().next();
	    	
	    	//Get the coordinates and name of the remaining unvisted stations and add them to the featurelist through missed stations
	    	closest = toVisit.get(nextStation);
	    	name = names.get(nextStation);
	    	toVisit.remove(nextStation);
	    	names.remove(nextStation);
   	 			
	    	var destination = Point.fromLngLat(closest.get(0), closest.get(1));
   	 		features = Stations.missedStation(destination, name, weatherStations, features);
   	 		}
   	 		
   	 	}
    	
    	
    	/* This section takes the flightpath list and takes all of the first 2 elements of each list and makes
    	 them into a point. This point is the added to list of points to be used for the line String
    	 
    	 */
    	var points = new ArrayList<Point>();
    	for (int i = 0; i < flightPath.size(); i++)
    	{
    		
    		Point current = Point.fromLngLat(Double.parseDouble(flightPath.get(i).get(0)), Double.parseDouble(flightPath.get(i).get(1)));
    		points.add(current);
    		
    	}
    	
    	/* The previous for loop only took the starting locations for each of the moves so we must add the final move which is the 3rd and 4th
    	 element of the last list in the flighpath list of lists
    	 */
    	Point end = Point.fromLngLat(Double.parseDouble(flightPath.get(flightPath.size()-1).get(3)), Double.parseDouble(flightPath.get(flightPath.size()-1).get(4)));
    	
    	points.add(end);
    	
    	// This then prints how many moves the drone made
    	System.out.println("Moves: " + flightPath.size());
    	
    	//A line string is produced from the list of points created earlier
    	LineString line = LineString.fromLngLats(points);
    	
    	//This lineString is then turned into a feature and added to the feature list
    	var feature = Feature.fromGeometry((Geometry)line);
    	features.add(feature);
    	
    	
    	
    	//The feature list is then turned into a feature collection 
    	final var map = FeatureCollection.fromFeatures(features);
    	
    	//The feature collection called map is then made into a geojson file and written to disk with the required formatting in the name
   	 	try (FileWriter json = new FileWriter("readings-" + day + "-" + month + "-" + year + ".geojson")){
			json.write(map.toJson());
			
			
		} catch (IOException e) {
			System.out.println("Could not create geojson file");
		
		//This  print writer object is used to print the flightpath list into a text document as required	
		} try (PrintWriter txt = new PrintWriter("flightpath-" + day + "-" + month + "-" + year + ".txt", "UTF-8")){
			
			//We must cycle through all of the flighpath list to get all of the mvoes that were made
			for(int i = 0; i < flightPath.size(); i++) {
				
				//This if statement checks whether the name of the station visited was added, if it had been added then there would be 6 elements in the list
				if(flightPath.get(i).size() == 5) {
				
					
					//Since it did not meet a station on these moves the final comment should be null as per the specs
					txt.println(String.valueOf(i + 1) + "," + flightPath.get(i).get(0) + "," + 
						flightPath.get(i).get(1) + "," + flightPath.get(i).get(2) + "," + 
						flightPath.get(i).get(3) + "'" + flightPath.get(i).get(4) + ",null");
				
			} else {
				
				//As the drone did meet a station the 5th array element should be used which contains the name.
				txt.println(String.valueOf(i + 1) + "," + flightPath.get(i).get(0) + "," + 
						flightPath.get(i).get(1) + "," + flightPath.get(i).get(2) + "," + 
						flightPath.get(i).get(3) + "'" + flightPath.get(i).get(4) + "," +
						flightPath.get(i).get(5));
		
			}			
			}
			
			//This creates the file
			txt.close();
		} catch (IOException e) {
			System.out.println("Could not create text file");
		
		} 

		//Exits the java program
   	 	System.exit(0);
    }     

    
}