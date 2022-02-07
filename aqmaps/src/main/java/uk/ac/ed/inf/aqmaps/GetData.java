package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetData {

    
    /* This function makes an HTTP request to the server using the rawUrl produced earlier.
    It then returns the value of the geojson file as a jsonstring       
    */

    public static String getBuildings(String rawUrl) throws IOException, InterruptedException {
    	
       	var urlString = rawUrl+"/buildings/no-fly-zones.geojson";
    	var client = HttpClient.newHttpClient();
    	var request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
    	var response = client.send(request, BodyHandlers.ofString());
    	var buildings = response.body();
    	    	
    	return buildings;

    }
    
    
    
    /* This function makes an HTTP request to the server using the rawUrl produced earlier.
    It then returns the value of the json file as a jsonstring for that particular day      
    */
    
   public static ArrayList<AirQualityData> getAirQualityJSON(String rawUrl, String day, String month, String year) throws IOException, InterruptedException {
    	
       	var urlString = rawUrl+"/maps/"+ year + "/" + month + "/" + day + "/air-quality-data.json";
    	var client = HttpClient.newHttpClient();
    	var request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
    	var response = client.send(request, BodyHandlers.ofString());
    	var list = response.body();
    	
    	Type listType = new TypeToken<ArrayList<AirQualityData>>() {}.getType();
    	ArrayList<AirQualityData> weatherStations = new Gson().fromJson(list, listType);
    	
    	return weatherStations;

    }
    
    
    
   /* This function takes the String of the what3words name for the weather station
   It then seperates it out by the dot and makes an HTTP request using the array of words and the rawUrl
   It then returns the value of the json file as a jsonstring for that particular what3word name      
   */
    
    public static WordsDetails getDetails(String rawUrl, String location) throws IOException, InterruptedException {
    	
    	String what3words [] = new String[3];
    	
    	//Splits the 3 words up into seperate array sections
    	
    	for (int i = 0; i < 3; i++) {
    		
    		what3words [i] = location.split("\\.")[i];
    		
    	}
   
       	var urlString = rawUrl+"/words/"+ what3words[0] + "/" + what3words[1] + "/" + what3words[2] + "/details.json";
    	var client = HttpClient.newHttpClient();
    	var request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
    	var response = client.send(request, BodyHandlers.ofString());
    	var list = response.body();
    	
    	var details = new Gson().fromJson(list, WordsDetails.class);

    	return details;

    }
    
}
