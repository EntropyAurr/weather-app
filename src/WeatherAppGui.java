import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
   private JSONObject weatherData;

   // create GUI constructor
   public WeatherAppGui() {
      super("Weather App");

      // configure GUI to end the program's process once it has been closed
      setDefaultCloseOperation(EXIT_ON_CLOSE);

      // set the size of the GUI (in pixels)
      setSize(450, 650);

      // move the GUI to the center of the screen
      setLocationRelativeTo(null);

      // make the layout manager null to manually position the components within the GUI
      setLayout(null);

      // prevent the resize of the GUI
      setResizable(false);

      addGuiComponents();
   }

   private void addGuiComponents() {
      // search field
      JTextField searchTextField = new JTextField();
      searchTextField.setBounds(25, 25, 340, 45);
      searchTextField.setFont(new Font("Poppins", Font.PLAIN, 24));
      add(searchTextField);

      // weather image
      JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
      weatherConditionImage.setBounds(0, 115,450, 217);
      add(weatherConditionImage);

      // temperature text
      JLabel temperatureText = new JLabel("10ºC");
      temperatureText.setBounds(0, 340, 450, 54);
      temperatureText.setFont(new Font("Poppins", Font.BOLD, 48));

      // center the text
      temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
      add(temperatureText);

      // weather condition description
      JLabel weatherConditionDesc = new JLabel("Cloudy");
      weatherConditionDesc.setBounds(0, 400, 450, 36);
      weatherConditionDesc.setFont(new Font("Poppins", Font.PLAIN, 32));
      weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
      add(weatherConditionDesc);

      // humidity image
      JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
      humidityImage.setBounds(15, 500, 74, 66);
      add(humidityImage);

      // humidity text
      JLabel humidityText = new JLabel("<html><b>Humidity</b> 100 %</html>");
      humidityText.setBounds(90, 500, 85, 55);
      humidityText.setFont(new Font("Poppins", Font.PLAIN, 16));
      add(humidityText);

      // windspeed image
      JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
      windSpeedImage.setBounds(220, 500, 74, 66);
      add(windSpeedImage);

      // windspeed text
      JLabel windSpeedText = new JLabel("<html><b>Wind Speed</b> 15 km/h</html>");
      windSpeedText.setBounds(310, 500, 105, 55);
      windSpeedText.setFont(new Font("Poppins", Font.PLAIN, 16));
      add(windSpeedText);

      // search button
      JButton searchButton = new JButton(loadImage("src/assets/search.png"));

      // change the cursor to hand cursor when hovering over the button
      searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      searchButton.setBounds(375, 25, 47, 45);

      searchButton.addActionListener(e -> {
         // get location from user
         String userInput = searchTextField.getText();

         // validate input - remove whitespace to ensure non-empty text
         if (userInput.replaceAll("\\s", "").isEmpty()) {
            return;
         }

         // retrieve data from WeatherApp
         weatherData = WeatherApp.getWeatherData(userInput);

         assert weatherData != null;
         String weatherCondition = (String) weatherData.get("weather_condition");

         // update weather image
         switch (weatherCondition) {
            case "Clear":
               weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
               break;
            case "Cloudy":
               weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
               break;
            case "Rain":
               weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
               break;
            case "Snow":
               weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
               break;
         }

         // update temperature text
         double temperature = (double) weatherData.get("temperature");
         temperatureText.setText(temperature + "ºC");

         // update humidity text
         long humidity = (long) weatherData.get("humidity");
         humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

         // update windspeed text
         double windSpeed = (double) weatherData.get("windspeed");
         windSpeedText.setText("<html><b>Wind Speed</b> " + windSpeed + "km/h</html>");
      });
      add(searchButton);
   }

   private ImageIcon loadImage(String imagePath) {
      try {
         BufferedImage image = ImageIO.read(new File(imagePath));

         // return an image icon so that the component can render it
         return new ImageIcon(image);
      }
      catch (IOException e) {
         e.printStackTrace();
      }

      System.out.println("Could not find the image");
      return null;
   }
}
