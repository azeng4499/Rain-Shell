package generateMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MessageDriver {
	
	public String finalMessage = "";
	public String emoji;
	
	public MessageDriver(String currentLocation, double latitude, double longitude) {
		
		try {
			
		String JSONResponce = callAPI(latitude, longitude);
		
		ParseJson json = new ParseJson(JSONResponce);
		
		Temperature temp = new Temperature(json);
		String tempMessage= temp.getTemperatureMessage();
		
		Advice advice = new Advice(json);
		String wardrobeAdvice = advice.getAdviceMessage();

		EmojiGenerator emojigenerator = new EmojiGenerator();
		
		emoji = emojigenerator.getEmjois(json);
		
		if(json.isPrecipToday) {

			Precipitation precip = new Precipitation(json);
			String weatherEventMessage = precip.getPrecipMessage();

			if(json.isPrecipRightNow) {
				finalMessage += ", it's " + json.currentTemp + "° in " + currentLocation + " right now. " 
						+ Utility.capitalizeBeginningOfSentence(weatherEventMessage) + tempMessage + wardrobeAdvice;
			} else {
				finalMessage += ", it's " + json.currentTemp + "° with " + json.currentDescription + " in " + currentLocation + " right now. " 
						+ tempMessage + Utility.capitalizeBeginningOfSentence(weatherEventMessage) + wardrobeAdvice;
			}
		}
		else {
			finalMessage += ", it's " + json.currentTemp + "° with " + json.currentDescription + " in " + currentLocation + " right now. " 
					+ tempMessage + wardrobeAdvice;
		}
		
		} catch (Exception e) {	
			e.printStackTrace();	
		}
	}
	
	public String getMessage() {
		return finalMessage;
	}
	
	public String getEmoji() {
		return emoji;
	}
	
	
	//Call the Open Weather Map API for a given location and returns the JSON
	private static String callAPI(double latitude, double longitude) {
		
		try { 
			String Open_Weather_API_URL = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" 
					+ longitude +"&units=imperial&appid=38ae09de9487e1e4db82918f701db0f7";

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.header("accept", "application/json")
					.uri(URI.create(Open_Weather_API_URL))
					.build();
			HttpResponse <String> responce = client.send(request, HttpResponse.BodyHandlers.ofString());

			return responce.body();

		} catch (Exception e) {
			return null;
		}
	}

}
