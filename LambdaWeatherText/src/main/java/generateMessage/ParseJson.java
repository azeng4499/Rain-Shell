package generateMessage;

import java.util.ArrayList;
import com.jayway.jsonpath.JsonPath;


public class ParseJson {

	public ArrayList<String> idGroups = new ArrayList<String>();
	public int currentTime, timeAtHigh, timeAtLow, todaysHigh, todaysLow, currentTemp, hoursOfPrecip, currentCode;
	public boolean isPrecipToday = false, isPrecipRightNow = false, isPrecipTomorrow = false, willRain = false, willSnow = false, willStorm = false;
	public String currentLocation, currentDescription, dayOfWeek, tempTrend = "";

	public ParseJson(String responseBody){

		try {
			// Parses current temperature and time from JSON to class variables
			String currentEpocheTime = Utility.objectToString(JsonPath.read(responseBody, "$.current.dt"));
			String currentTimeZone = Utility.objectToString(JsonPath.read(responseBody, "$.timezone"));

			currentTime = Integer.valueOf(Utility.getTime(currentEpocheTime, currentTimeZone));
			timeAtHigh = currentTime;
			timeAtLow = currentTime;
			currentTemp = (int)Utility.objectToDouble(JsonPath.read(responseBody, "$.current.temp"));
			todaysLow = (int)currentTemp;
			todaysHigh = (int)currentTemp;
			Utility.setDayOfWeek(currentEpocheTime, currentTimeZone);
			Utility.setDate(currentEpocheTime, currentTimeZone);

			ArrayList<Integer> currentCodes = new ArrayList<Integer>();
			int startingIDIndex = 0;
			double averagePercentOfRain = 0.0;
			int elementCount = 0;

			//Parses the temperature data from each hour for the next 24 hours and calculates temperature high and low of the day
			double thisTemp;
			for(int i = 0; i < 24; i ++) {
				thisTemp = Utility.objectToDouble(JsonPath.read(responseBody, "$.hourly[" + i + "].temp"));
				int timeForThisTemp = Utility.convertToValidIntTime(i, currentTime);

				if(thisTemp >= todaysHigh && timeForThisTemp >= currentTime && timeForThisTemp < 24) {
					todaysHigh = (int)thisTemp;
					timeAtHigh = timeForThisTemp;
				}
				if (thisTemp <= todaysLow && timeForThisTemp >= currentTime && timeForThisTemp < 24) {
					todaysLow = (int)thisTemp;
					timeAtLow = timeForThisTemp;
				}

				/* Parses the id codes from each hour for the next 24 hours; categorizes each id in groups, and adds to an ArrayList
				 * Data is stored into an element in the following format:
				 * 
				 * TypeOfWeather startingTime:endingTime [id]{percentChanceOfPrecipitation}
				 * 
				 * Example:
				 * 
				 * Rain 23:5 [501]{0.55}
				 * Clear 6:22 [803]{0.21}
				 */

				Integer thisCode = Utility.objectToInteger(JsonPath.read(responseBody, "$.hourly[" + i + "].weather[0].id"));
				double thisCodePrecipPercentage = Utility.objectToDouble(JsonPath.read(responseBody, "$.hourly[" + i + "].pop"));

				String currentDescription = getCorrectCodeDescription(thisCode, thisCodePrecipPercentage, i);

				if(thisCode/100 == 8 && thisCodePrecipPercentage >= 0.3) {
					thisCode = 505;
				}

				if(idGroups.size() >= 1) {
					if(idGroups.get(idGroups.size()-1).charAt(0) != currentDescription.charAt(0)) {
						idGroups.set((idGroups.size()-1), String.format(idGroups.get(idGroups.size()-1), 
								Utility.convertToValidIntTime(startingIDIndex, currentTime), 
								Utility.convertToValidIntTime(i-1, currentTime),
								Utility.findMode(currentCodes),
								(elementCount != 0)? Utility.round(averagePercentOfRain/elementCount, 2) : 0.0));
						currentCodes.clear();
						currentCodes.add(thisCode);
						idGroups.add(currentDescription);
						averagePercentOfRain = 0.0;
						elementCount = 0;
						averagePercentOfRain += thisCodePrecipPercentage;
						elementCount++;
						startingIDIndex = i;
					} else {
						currentCodes.add(thisCode);
						averagePercentOfRain += thisCodePrecipPercentage;
						elementCount++;
					}
				} else {
					idGroups.add(currentDescription);
					currentCodes.add(thisCode);
					averagePercentOfRain += thisCodePrecipPercentage;
					elementCount++;
				}
			}

			idGroups.set((idGroups.size()-1), String.format(idGroups.get(idGroups.size()-1), 
					Utility.convertToValidIntTime(startingIDIndex, currentTime), 
					currentTime - 1,
					Utility.findMode(currentCodes),
					(elementCount != 0)? Utility.round(averagePercentOfRain/elementCount, 2) : 0.0));

			//Parses the amount of precipitation for the next hour and determines the current description
			double highestPrecipValue = 0;

			for(int i = 0; i < 60; i++) {
				double precipValueForThisMin = Utility.objectToDouble(JsonPath.read(responseBody, "$.minutely[" + i + "].precipitation"));
				if(precipValueForThisMin > highestPrecipValue) {
					highestPrecipValue = precipValueForThisMin;
				}
			}

			isPrecipRightNow = (highestPrecipValue > 0)? true : false;
			currentDescription =  Utility.getCodeDescription(Utility.objectToInteger(JsonPath.read(responseBody, "$.current.weather[0].id")));
			currentCode = Utility.objectToInteger(JsonPath.read(responseBody, "$.current.weather[0].id"));

			/* Establishes the temperature trend for the day. The String consists of two symbols (either =, +, or -)
			 * The first symbol represents the trend of the temperature up to the extreme of the day.
			 * Usually, the weather warms up to a temperature high somewhere around Noon. In this case, the first symbol would be +.
			 * The second symbol represents the trend of the temperature after the extreme of the day.
			 * Usually after the high, the temperature will drop for the rest of the day. In this case, the second symbol would be -.
			 * If there is not much change in the temperature in either segment, a = will replace the +/-.
			 */
			if(Math.abs(todaysHigh - currentTemp) < 5) {
				tempTrend = "=";
			} else if (todaysHigh - currentTemp >= 5) {
				tempTrend = "+";
			} else {
				tempTrend = "-";
			}
			if (todaysHigh - todaysLow < 5) {
				tempTrend += "=";
			} else if (todaysHigh - todaysLow >= 5) {
				tempTrend += "-";
			} else {
				tempTrend += "+";
			}

			//printData();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Returns the correct description with the corresponding code value 
	public String getCorrectCodeDescription(int code, double percentageOfPrecip, int timeindex) {

		String description;

		if(percentageOfPrecip < 0.3 && code/100 == 5 || code/100 == 3 || code/100 == 2) {
			return "Clear %s:%s [%s]{%s}";
		} else if(code/100 == 8 && percentageOfPrecip < 0.3) {
			return "Clear %s:%s [%s]{%s}";
		} else if (code/100 == 8 && percentageOfPrecip >= 0.3) {
			description = "Rain %s:%s [%s]{%s}";
			willRain = true;
		} else if (code/100 == 6) {
			description =  "Snow %s:%s [%s]{%s}";
			willSnow = true;
		} else if (code/100 == 5) {
			description =  "Rain %s:%s [%s]{%s}";
			willRain = true;
		} else if (code/100 == 3) {
			description = "Drizzle %s:%s [%s]{%s}";
			willRain = true;
		} else if (code/100 == 2) {
			description =  "ThunderStorm %s:%s [%s]{%s}";
			willStorm = true;
		} else {
			description = "Other %s:%s [%s]{%s}";
		}	

		if(Utility.isPrecip(description) && Utility.convertToValidIntTime(timeindex, currentTime) >= currentTime) {
			isPrecipToday = true;
		} else {
			isPrecipTomorrow = true;
		}

		hoursOfPrecip++;

		return description;

	}

	//Prints data
	public void printData() {

		System.out.println("Current Time: " + currentTime + "\nCurrent Temp: " + currentTemp);
		System.out.println("High: " + todaysHigh + " at " + timeAtHigh + "\nLow: " + todaysLow + " at " + timeAtLow);
		System.out.println();

		for(String x: idGroups) {
			System.out.println(x);
		}

		System.out.println();

	}

}
