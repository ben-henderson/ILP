package uk.ac.ed.inf.aqmaps;


/*This is a class for the JSON files of each of the what 3 words names in the system.
Including the coordinates, country and the what 3 words
*/
public class WordsDetails {
	
	String country;
	Location shape;
	
	public static class Location {
		
		Corner southwest;
		Corner northeast;
	}
	
	public static class Corner {
		
		Double lng;
		Double lat;
		
	}
	
	String nearestPlace;
	Coordinates coordinates;
	
	public static class Coordinates {
		
		Double lng;
		Double lat;
		
	}
	String words;
	String language;
	String map;
	
	
	

}
