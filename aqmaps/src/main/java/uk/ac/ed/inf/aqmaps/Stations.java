package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

public class Stations {

    /*This function takes the destination point, name of the station, the list of all features so far and the weatherStations list
    It is used to then add the feature of the new weather station once it has been reached to the feature list  
    */
    
    public static ArrayList<Feature> readStation (Point destination, String name, ArrayList<AirQualityData> weatherStations, ArrayList<Feature> features) {
    	
    	String symbol;
    	String rgbString;

    	// for loop checks through all stations to find the one matching the name
    	for (int i = 0; i < 33; i++) {
    		
    		if(weatherStations.get(i).location == name) {
    			
    			//Make sure the battery is 10 or over to be able to take a reading from it
    			if (weatherStations.get(i).battery >= 10) {

    			
    			var reading = Double.parseDouble(weatherStations.get(i).reading);
    			// This if chain just checks the reading and assigns the corresponding symbol and rgb string
    			// each if statment then calls the record station function and it returns a feature list which is then returned by this function.
    			
    			if (reading < 32)  {
    				
    				symbol = "lighthouse";
    				rgbString = "#00ff00";
    				return recordStation(destination, symbol, rgbString, features, name);
    				
    			} else if (reading < 64) {
    					
    				symbol = "lighthouse";
    				rgbString = "#40ff00";
    				return recordStation(destination, symbol, rgbString, features, name);
    				
    			} else if (reading < 96) {
					
    				symbol = "lighthouse";
    				rgbString = "#80ff00";
    				return recordStation(destination, symbol, rgbString, features, name);
    				
    			} else if (reading < 128) {
					
    				symbol = "lighthouse";
    				rgbString = "#c0ff00";
    				return recordStation(destination, symbol, rgbString, features, name);
    				
    			} else if (reading < 160) {
					
    				symbol = "danger";
    				rgbString = "#ffc000";
    				return recordStation(destination, symbol, rgbString, features, name);
    				
    			} else if (reading < 192) {
					
    				symbol = "danger";
    				rgbString = "#ff8000";
    				return recordStation(destination, symbol, rgbString, features, name);
    				
    			} else if (reading < 224) {
					
    				symbol = "danger";
    				rgbString = "#ff4000";
    				return recordStation(destination, symbol, rgbString, features, name);
    				
    			} else if (reading < 256) {
				
    				symbol = "danger";
    				rgbString = "#ff0000";
    				return recordStation(destination, symbol, rgbString, features, name);
				
    			}
    			

    		} else {
    			
				symbol = "cross";
				rgbString = "#000000";
				return recordStation(destination, symbol, rgbString, features, name);

    		}
    			
    		}
		
    	}
    	
		return features;
	
    }
    
    
    
    /* This function is called from the ReadStation and creates a feature from the point at which the weather station is located
    There are then the 4 properties added to the feature and then this feature is added to the feature list called features
    This list of features is then returned with the addition
    */
    
    public static ArrayList<Feature> recordStation (Point destination, String symbol, String rgbString, ArrayList<Feature> features, String name) {
    	
    	var feature = Feature.fromGeometry((Geometry)destination);
    	
    	feature.addStringProperty("location", name);
    	feature.addStringProperty("rgb-string", rgbString);
    	feature.addStringProperty("marker-color", rgbString);
    	feature.addStringProperty("marker-symbol", symbol);
    	
    	features.add(feature);
    	
    	return features;
   
    }
    
    
    
    /* This function takes the list of Strings from the flighpath list and adds the what3words name to the list.
    The previous list is then removed from flighpath and the new detaisl replaces it.     
   */
   
   public static ArrayList<List<String>> addNameToFlight (ArrayList<List<String>> flightPath, int moveCount, String name) {
   	
		var details = flightPath.get(moveCount-1);
		details.add(name);
		
		flightPath.remove(moveCount-1);
		
		flightPath.add(details);
		
		return flightPath;
   }
   
   // This function returns a list of features including the missed feature
   public static ArrayList<Feature> missedStation (Point destination, String name, ArrayList<AirQualityData> weatherStations, ArrayList<Feature> features){
		
	  
	   var rgbString = "#aaaaaa";
	   var feature = Feature.fromGeometry((Geometry)destination);
   	
   		feature.addStringProperty("location", name);
   		feature.addStringProperty("rgb-string", rgbString);
   		feature.addStringProperty("marker-color", rgbString);

   	
   		features.add(feature);
   	
   		return features;
	   
	   
   }
}
