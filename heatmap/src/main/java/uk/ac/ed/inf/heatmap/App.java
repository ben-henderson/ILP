package uk.ac.ed.inf.heatmap;

import com.mapbox.geojson.*;
import java.util.*;  
import java.io.*;

public class App 
{

	public static void main(String[] args ) throws FileNotFoundException
    {
    	// These are are start and end figures for the absolute dimenstions of the grid in question    	
		double startlat = 55.946233;
		double startlong = -3.192473;
   	 	
   	 	double endlat = 55.942617;
   	 	double endlong = -3.184319;
   	 	
   	 	
   	 	// This finds the difference after each loop that the lat and lng must change by
   	 	double difflat = (endlat - startlat)/10;
   	 	double difflong = (endlong - startlong)/9;
   	 	
   	 	//Takes the argument of the location of the predictions file and feeds that through to the parcer function through features
   	 	String path = args[0];
   	 	
   	 	// Call the feature method and add the returned list it to a list new list 
   	 	List<Feature>  features = features(difflat,  difflong, startlat, startlong, path);
   	 	
   	 	// Creates a feature collection from the features
   	 	FeatureCollection fc = FeatureCollection.fromFeatures(features);
   	 	
   	 	
   	 	//This then allows me to write the file as a .geojson that can then be tested
   	 	try (FileWriter file = new FileWriter("heatmap.geojson")){
			file.write(fc.toJson());
			
			
		} catch (IOException e) {
			System.out.println("Could not create file heatmap.geojson");
		}
   	 	
   	 	
    }
   
	

	public static List<Point> points(double lat, double lng, double difflat, double difflong){
		
		// This method is the call we use to create a list of points for each of the small squares that will each become a polygon
		// The method used is to start in the top left of each box, then the bottom left, bottom right, top right and back to top left
		// This allows the function to keep the latitude the same for the internal for loop
		
		List<Point> points = new ArrayList<Point>();
		
		points.add(Point.fromLngLat(lng, lat));
		
		lng -= difflong;
		
		points.add(Point.fromLngLat(lng, lat));
		
		lat += difflat;
		
		points.add(Point.fromLngLat(lng, lat));
		
		lng += difflong;
		
		points.add(Point.fromLngLat(lng, lat));
		
		lat -= difflat;
		
		points.add(Point.fromLngLat(lng, lat));

		return points;
	}
	
	public static List<Feature> features(double difflat, double difflong, double startlat, double startlong, String path) throws FileNotFoundException{
		
		// This method returns a list of features of each feature being a polygon with the correct properties added to it.
		List<Feature> features = new ArrayList<Feature>();
		String[] colours = parcer(path);
		int k = 0;
		
		for (int i = 0; i < 10; i++) {
	    		 
			// This outside loop keeps track of the latitude which must get changed after each row is done in the inside loop
	    		 double lat = startlat + i * difflat;
	    		 double lng = startlong;
	    		 
	    		 for (int j = 0; j < 10; j++) { 	
	    			 
	    			 lng = startlong + j * difflong;
	    			 //Each new polygon then calls the points method and generates a list of points with specific starting lat and lng
	    			 // A geometry is then created from each new polygon
	    			 //A feature is then made from each geomtry and properties added to it from the parcer method call
	    			 
	    			 
	    			 Polygon polygon = Polygon.fromLngLats(List.of(points(lat, lng, difflat, difflong)));
	    			 Geometry g = (Geometry)polygon;
	    			 Feature f = Feature.fromGeometry(g);
	    			 f.addNumberProperty("fill-opacity", 0.75);
	    			 f.addStringProperty("rgb-string", colours[k]);
	    			 f.addStringProperty("fill", colours[k]);
	    			 
	    			 //Then add each feature to a list of features and return this list.
	    			 features.add(f);
	    			 k++;
	    		 }
	    	
	    		 
		}
	
		return features;
		
	}
	
	public static String[] parcer(String path) throws FileNotFoundException  {
		
		// This method reads the predictions.txt using scanner by passing through the argument from cmd and seperates out each line and each line is seperated by commas
		
		
		String[] colours = new String[100];
		Integer[] numbers = new Integer[10];
		
	   	 File raw = new File(path);

		Scanner predictions = new Scanner(raw);
		int k = 0;
		while(predictions.hasNext()) {
			
		    String nextLine = predictions.nextLine();
		    
		    String[] temp = nextLine.split("\\s*,\\s*");
		    
		    for (int i = 0; i <10; i++) {
		    	
		    	// Using this loop to go through each raw input number and cast it to an integer from a string and using if statements 
		    	// check if what value it is and using that assign its perspective place in the colours array the correct rgb string as per the specifications
		    	// Int k is used to keep track outside of the loop how many numbers out of the 100 have been done.
		    	
		    	 numbers[i] = Integer.parseInt(temp[i]);		    	
		    	 
		    	 if(numbers[i] < 32) {
		    		
		    		 colours[k] = "#00ff00";
		    		 
		    	} else if (numbers[i] < 64) {
		    		
		    		 colours[k] = "#40ff00";
		    		 
		    	} else if (numbers[i] < 96) {
		    		
		    		colours[k] = "#80ff00";
		    		
		    	} else if (numbers[i] < 128) {
		    		
		    		colours[k] = "#c0ff00";
		    		
		    	} else if (numbers[i] < 160) {
		    		
		    		colours[k] = "#ffc000";
		    		
		    	} else if (numbers[i] < 192) {
		    		
		    		colours[k] = "#ff8000";
		    		
		    	} else if (numbers[i] < 224) {
		    		
		    		colours[k] = "#ff4000";
		    		
		    	} else if (numbers[i] < 256) {
		    		
		    		colours[k] = "#ff0000";
		    		
		    	}
		    	
		    	
		    	k++;
		    	
		    }
		    
		    
		}
		
		// This if statment just checks at the end if the number of colours is 100, if it isnt then a statement will print on the command line stating the input is incorrect.
	    if (colours.length != 100) {
	    	
	    	System.out.println("Input incorrect");
	    	
	    }
		predictions.close();

		
		// returns the array of rgb strings 
		return colours;
	}
}

