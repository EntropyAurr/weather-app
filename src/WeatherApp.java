import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

// retreive weather data from API, then the GUI will display to the user
public class WeatherApp {
   // fetch weather data for given location
   public static JSONObject getWeatherData(String locationName) {
      JSONArray locationData = getLocationData(locationName);

      return null;
   }

   public static JSONArray getLocationData(String locationName) {
      // replace any whitespace in location name to '+' to adhere to API's request format
      locationName = locationName.replaceAll(" ", "+");

      // build API url with location parameter
      String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
              locationName + "&count=10&language=en&format=json";

      try {
         HttpURLConnection connection = fetchApiResponse(urlString);

         // check response status
         if (connection.getResponseCode() != 200) {
            System.out.println("Error: Could not connect to API");

            return null;
         } else {
            // store the API results
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());

            while (scanner.hasNext()) {
               resultJson.append(scanner.nextLine());
            }

            scanner.close();

            // close url connection
            connection.disconnect();

            // parse JSON string to JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // get the list of location data that the API generated from the location name
            JSONArray locationData = (JSONArray) resultJsonObj.get("results");
            return locationData;
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      // could not find the location
      return null;
   }

   private static HttpURLConnection fetchApiResponse(String urlString) {
      try {
         // attempt to create connection
         URI uri = new URI(urlString);
         URL url = uri.toURL();
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         // set request method to get
         connection.setRequestMethod("GET");

         // connect to the API
         connection.connect();
         return connection;
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }

      // could not make connection
      return null;
   }
}
