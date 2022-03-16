package generateMessage;

public class Temperature {

	private String tempTrend;
	private String tempMessage = "";
	private int todaysHigh, 
	todaysLow, 
	timeAtHigh, 
	timeAtLow,
	currentTemp,
	currentTime;

	//Generates a message for the temperature today
	Temperature(ParseJson json) {

		tempTrend = json.tempTrend;
		todaysHigh = json.todaysHigh;
		todaysLow = json.todaysLow;
		timeAtHigh = json.timeAtHigh;
		timeAtLow = json.timeAtLow;
		currentTemp = json.currentTemp;
		currentTime = json.currentTime;

		//Messages for days where the temperature trend will only go one way
		if(tempTrend.equals("--")) {
			tempMessage = "The temperature will drop throughout the day. ";

		} else if (tempTrend.equals("++")){
			tempMessage = "The temperature will rise throughout the day. ";

		} else if (tempTrend.equals("==")){
			tempMessage = "The temperature will stay about the same throughout the day. ";
		} 
		
		else if (tempTrend.startsWith("=")) {
			
			tempMessage = "The temperature will stay about the same until the " + Utility.convertTimeToDescriptor(timeAtHigh, currentTime) + ". ";
		}
		//Messages for days where the temperature trend begins with increasing hours
		else if (tempTrend.startsWith("+")) {
			if((todaysHigh - currentTemp) >= 15) {
				tempMessage = "The temperature will shoot to " + todaysHigh + "° by the " 
						+  Utility.convertTimeToDescriptor(timeAtHigh, currentTime) + ". ";
			} else {
				tempMessage = "The temperature will reach a high of " + todaysHigh + "° by the " 
						+  Utility.convertTimeToDescriptor(timeAtHigh, currentTime) + ". ";
			}
		} 
		//Messages for days where the temperature trend begins with decreasing hours
		else if (tempTrend.startsWith("-")) {
			if((currentTemp - todaysLow) >= 15) {
				tempMessage = "The temperature will fall to " + todaysHigh + "° by the " 
						+  Utility.convertTimeToDescriptor(timeAtLow, currentTime) + ". ";
			} else {
				tempMessage = "The temperature will to reach a low of " + todaysHigh + "° by the " 
						+  Utility.convertTimeToDescriptor(timeAtLow, currentTime) + ". ";
			}

		}
	}

	public String getTemperatureMessage() {
		return tempMessage;
	}

}
