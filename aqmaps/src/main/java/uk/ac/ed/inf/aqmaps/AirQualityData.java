package uk.ac.ed.inf.aqmaps;

import com.google.gson.annotations.SerializedName;

/* 
 This class is an object for each of the weatherStations to be created using these values from the JSON file
 */

public class AirQualityData {

	@SerializedName("location")
	String location;
	Double battery;
	String reading;
	
}

