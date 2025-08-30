import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// retrieve weather data from API, then the GUI will display to the user
public class WeatherApp {
   // fetch weather data for given location
   public static JSONObject getWeatherData(String locationName) {
      JSONArray locationData = getLocationData(locationName);

      // extract longitude and latitude data
      JSONObject location = (JSONObject) locationData.get(0);
      double latitude = (double) location.get("latitude");
      double longitude = (double) location.get("longitude");

      // build API request URL with location coordination
      String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" +
             + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

      try {
         // call the API and get response
         HttpURLConnection connection = fetchApiResponse(urlString);

         // check for response status
         if (connection.getResponseCode() != 200) {
            System.out.println("Error: Could not connect to API for location coordination");

            return null;
         }

         // store result from JSON data
         StringBuilder resultJson = new StringBuilder();
         Scanner scanner  = new Scanner(connection.getInputStream());

         while (scanner.hasNext()) {
            // read and store data into StringBuilder
            resultJson.append(scanner.nextLine());
         }

         scanner.close();

         connection.disconnect();

         // parse the data
         JSONParser parser = new JSONParser();
         JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

         // retrieve hourly data
         JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

         // we want to get the current hour's data => we need to get the index of current hour
         JSONArray time = (JSONArray) hourly.get("time");
         int index = findIndexOfCurrentTime(time);

         // get temperature
         JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
         double temperature = (double) temperatureData.get(index);

         // get weather code
         JSONArray weathercode = (JSONArray) hourly.get("weather_code");
         String weatherCondition = convertWeatherCode((long) weathercode.get(index));

         // get humidity
         JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
         long humidity = (long) relativeHumidity.get(index);

         // get wind speed
         JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
         double windspeed = (double) windspeedData.get(index);

         // build the weather JSON data object that will be accessed in frontend
         JSONObject weatherData = new JSONObject();
         weatherData.put("temperature", temperature);
         weatherData.put("weather_condition", weatherCondition);
         weatherData.put("humidity", humidity);
         weatherData.put("windspeed", windspeed);

         return weatherData;
      }
      catch (Exception e) {
         e.printStackTrace();
      }

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
            System.out.println("Error: Could not connect to API for location name");

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

   private static int findIndexOfCurrentTime(JSONArray timeList) {
      String currentTime = getCurrentTime();

      // iterate through the time list and see which one matches the current time
      for (int i = 0; i < timeList.size(); i++) {
         String time = (String) timeList.get(i);

         if (time.equalsIgnoreCase(currentTime)) {
            return i;
         }
      }

      return 0;
   }

   public static String getCurrentTime() {
      LocalDateTime currentDateTime = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:00");
      String formattedDateTime = currentDateTime.format(formatter);

      return formattedDateTime;
   }

   private static String convertWeatherCode(long weathercode) {
      String weatherCondition = "";

      if (weathercode == 0) {
         weatherCondition = "Clear";
      } else if (weathercode > 0L && weathercode <= 3L) {
         weatherCondition = "Cloudy";
      } else if ((weathercode >= 51L && weathercode <= 67L)
              || (weathercode >= 80L && weathercode <= 99L)) {
         weatherCondition = "Rain";
      } else if (weathercode >= 71L && weathercode <= 77L) {
         weatherCondition = "Snow";
      }

      return weatherCondition;
   }
}