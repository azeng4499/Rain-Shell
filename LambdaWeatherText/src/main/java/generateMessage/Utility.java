package generateMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Utility {
	
	public static String currentDayOfWeek, currentDate;
	
	//Converts an Object JSON data type to a String
	public static String objectToString(Object object) {
		return object.toString();
	}

	//Converts an Object JSON data type to a double
	public static double objectToDouble(Object object) {
		return Double.valueOf(object.toString());
	}

	//Converts an Object JSON data type to an Integer
	public static Integer objectToInteger(Object object) {
		return Integer.valueOf(object.toString());
	}

	//Converts a for loop counter to a valid time based on the current time
	public static int convertToValidIntTime(int i, int currentTime) {
		i += currentTime;
		if(i > 23) {
			i -= 24;
			return i;	
		}
		else if (i < 0) {
			i += 24;
			return i;
		}
		return i;
	}

	//Rounds a double to an amount of decimal places
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	//Returns the description value of idGroup element
	public static String parseGroupElementDescriptor(String element) {

		int startingIndex = 0, endingIndex = element.length() - 1;

		for(int i = 0; i < element.length(); i ++) {
			if(element.charAt(i) == '[') {
				startingIndex = i+1;
			}
			else if(element.charAt(i) == ']') {
				endingIndex = i;
			}
		}

		return element.substring(startingIndex, endingIndex);

	}
	
	//Returns the precipitation value of idGroup element
	public static double parseGroupElementPrecipValue(String element) {
		int startingIndex = 0, endingIndex = element.length() - 1;
		for(int i = 0; i < element.length(); i ++) {
			if(element.charAt(i) == '{') {
				startingIndex = i+1;
			}
			else if(element.charAt(i) == '}') {
				endingIndex = i;
			}
		}
		return Double.valueOf(element.substring(startingIndex, endingIndex));
	}

	//Returns the start and end time of idGroup element
	public static int parseGroupElementTime(String element, boolean wantStart) {

		int colonIndex = 0;
		StringBuffer number = new StringBuffer();

		for(int i = 0; i < element.length(); i ++) {
			if(element.charAt(i) == ':') {
				colonIndex = i;
			}
		}

		for(int i = 1; i < 3; i ++) {	
			int currentIndex = (wantStart)? (colonIndex - i): (colonIndex + i);	
			if(Character.isDigit(element.charAt(currentIndex))) {
				if(wantStart) {
					number.insert(0, element.charAt(currentIndex));
				} else {
					number.append(element.charAt(currentIndex));
				}
			}
		}

		return Integer.valueOf(number.toString());

	}

	//Returns the correct description for the time of day for a specific time
	public static String convertTimeToDescriptor(int time, int currentTime) {
		
		if(time < currentTime) { 
			return "tomorrow morning";
		} else if (time >= 6 && time <= 8) {
			return "early morning";
		} else if (time >= 9 && time <= 12) {
			return "late morning";
		} else if (time >= 13 && time <= 15) {
			return "early afternoon";
		} else if (time >= 16 && time <= 18) {
			return "late afternoon";
		} else if (time >= 19 && time <= 22) {
			return "evening";
		} else {
			return "late evening";
		}

	}

	//Converts military time to normal time
	public static String convertTimeToRegTime(int time) {
		if(time < 12 && time > 0) {
			return time + " AM";
		} else if (time == 0) {
			return "midnight";
		} else if (time == 12) {
			return "noon";
		} else {
			time -= 12;
			return time + " PM";
		}
	}

	//Returns the mode out of an ArrayList of integers
	public static int findMode(ArrayList<Integer> codes){
		int maxValue = 0, maxCount = 0, i, j;

		for (i = 0; i < codes.size(); ++i) {
			int count = 0;
			for (j = 0; j < codes.size(); ++j) {
				if (codes.get(j) == codes.get(i))
					++count;
			}

			if (count > maxCount) {
				maxCount = count;
				maxValue = codes.get(i);
			}
		}
		return maxValue;
	}
	
	//Capitalizes the beginning of a sentence
	public static String capitalizeBeginningOfSentence(String sentence) {
		if(!sentence.startsWith(" w")) {
			return Character.toUpperCase(sentence.charAt(0)) + sentence.substring(1, sentence.length());
		} else {
			return sentence;
		}
	}
	
	//Checks if current idGroup element is raining
	public static boolean isPrecip(String element) {
		if(element.charAt(0) == 'R' || element.charAt(0) == 'D' || element.charAt(0) == 'T' || element.charAt(0) == 'S') {
			return true;
		}
		return false;
	}

	//Converts from Unix to Date Time and returns the current date	
	public static void setDate(Object read, String timeZone) {
		Date date = new Date ();
		Integer dateInInteger = Integer.valueOf(read.toString());
		@SuppressWarnings("removal")
		Long dateInLong = new Long(dateInInteger)*1000;
		SimpleDateFormat timeFormat = new SimpleDateFormat("L/d");
		timeFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		date.setTime(dateInLong);
		currentDate = timeFormat.format(date).toString();
	}
	
	//Converts from Unix to Date Time and returns the current time	
	public static String getTime(Object read, String timeZone) {
		Date date = new Date ();
		Integer dateInInteger = Integer.valueOf(read.toString());
		@SuppressWarnings("removal")
		Long dateInLong = new Long(dateInInteger)*1000;
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH");
		timeFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		date.setTime(dateInLong);
		return (timeFormat.format(date)).toString();
	}
	
	//Converts from Unix to Date Time and returns the day of the week	
	public static void setDayOfWeek(Object read, String timeZone) {
		Date date = new Date ();
		Integer dateInInteger = Integer.valueOf(read.toString());
		@SuppressWarnings("removal")
		Long dateInLong = new Long(dateInInteger)*1000;
		SimpleDateFormat timeFormat = new SimpleDateFormat("E");
		timeFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		date.setTime(dateInLong);
		currentDayOfWeek = timeFormat.format(date).toString();
	}
	
	//Returns the correct description for a certain code
	public static String getCodeDescription(int code) {
		
		switch(code) {
			case 800:
				return "clear skies";
			case 801:
			case 802:
			case 803:
				return "partial clouds";
			case 804:
				return "overcast clouds";
			case 600:
			case 601:
			case 620:
			case 621:
				return "snow";
			case 602:
			case 622:
				return "heavy snow";
			case 611:
			case 612:
			case 613:
			case 511:
				return "sleet";
			case 615:
			case 616:
				return "rain and snow";
			case 500:
			case 520:
			case 302:
			case 311:
			case 312:
			case 313:
			case 314:
			case 321:
				return "light rain";
			case 501:
			case 505:
			case 521:
				return "rain";
			case 502:
			case 503:
			case 504:
			case 522:
			case 531:
				return "heavy rain";
			case 300:
			case 301:
			case 310:
				return "light drizzling";
			case 200:
			case 210:
			case 230:
				return "light thunderstorming";
			case 201:
			case 211:
			case 231:
				return "thunderstorming";
			case 202:
			case 212:
			case 232:
				return "heavy thunderstorming";
			default:
				return "inclement conditions";
		}
	}
}