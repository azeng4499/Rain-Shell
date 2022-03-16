package generateMessage;

import java.util.ArrayList;

public class Precipitation {

	private boolean isPrecipToday, 
					isPrecipRightNow,
					isPrecipTomorrow;
	private ArrayList<String> idGroups;
	private String weatherEventMessage = "";
	private int currentTime;


	Precipitation(ParseJson json) {
		
		isPrecipToday = json.isPrecipToday;
		isPrecipRightNow = json.isPrecipRightNow;
		isPrecipTomorrow = json.isPrecipTomorrow;
		currentTime = json.currentTime;
		idGroups = json.idGroups;

		String typeOfEvent = "";
		StringBuffer messageBuilder = new StringBuffer();
		ArrayList <Integer> eventTimes = new ArrayList<Integer>();
		ArrayList <Integer> chancesOf = new ArrayList<Integer>();
		boolean isRain = false, isSnow = false;

		//Adds the times and respective chances of precipitation to array lists; Determines the type of event that will be happening
		for(String currentID: idGroups){
			int startTimeForThisID = Utility.parseGroupElementTime(currentID, true);
			if(Utility.isPrecip(currentID) && startTimeForThisID< 24 && startTimeForThisID >= currentTime) {
				String descriptor = Utility.getCodeDescription(Integer.valueOf(Utility.parseGroupElementDescriptor(currentID)));
				if(descriptor.contains("rain") || descriptor.contains("storm") || descriptor.contains("drizzle")) {
					typeOfEvent = descriptor + " ";
					isRain = true;
				} else if (descriptor.contains("snow")) {
					typeOfEvent = descriptor + " ";
					isSnow = true;
				} else {
					typeOfEvent = "precipitation ";
				}
				eventTimes.add(Utility.parseGroupElementTime(currentID, true));
				eventTimes.add(Utility.parseGroupElementTime(currentID, false));
				chancesOf.add((int)Math.round(Utility.parseGroupElementPrecipValue(currentID)*100));
			}
		}
		
		if(isRain && isSnow) {
			typeOfEvent = "rain/snow ";
		}

		//Establishes starting time to the first time in the array and ending time to the last time in the array
		int startingTime = eventTimes.get(0);
		int endingTime = eventTimes.get(eventTimes.size()-1);

		//Messages for rain that will exclusively happen tomorrow
		if(!isPrecipToday && isPrecipTomorrow) {
			messageBuilder.append("tomorrow morning starting at " 
			+ Utility.convertTimeToRegTime(eventTimes.get(0))
			+ format(chancesOf.get(0)) 
			+ ". ");

		}
		//Messages for rain that will happen at 2 separate times throughout the day
		else if(isPrecipRightNow && eventTimes.size() == 4 && (eventTimes.get(2) - eventTimes.get(1)) >= 2) {
			messageBuilder.append("now - " + Utility.convertTimeToRegTime(eventTimes.get(1)) 
			+ format(chancesOf.get(0))
			+ " and " + Utility.convertTimeToRegTime(eventTimes.get(2)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(3))
			+ format(chancesOf.get(1)) + ". ");

		} else if (eventTimes.size() == 4 && (eventTimes.get(2) - eventTimes.get(1)) >= 2) {
			messageBuilder.append(Utility.convertTimeToRegTime(eventTimes.get(0)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(1))
			+ format(chancesOf.get(0)) 
			+ " and " + Utility.convertTimeToRegTime(eventTimes.get(2)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(3))
			+ format(chancesOf.get(1)) + ". ");

		}
		//Messages for rain that will happen at 3 separate times throughout the day
		else if (isPrecipRightNow && eventTimes.size() == 6 && (eventTimes.get(4) - eventTimes.get(3)) >= 2 
				&& (eventTimes.get(2) - eventTimes.get(1)) >= 2) {
			messageBuilder.append("now - " + Utility.convertTimeToRegTime(eventTimes.get(1))
			+ format(chancesOf.get(0)) 
			+ ", " + Utility.convertTimeToRegTime(eventTimes.get(2)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(3))
			+ format(chancesOf.get(1)) 
			+ ", and " + Utility.convertTimeToRegTime(eventTimes.get(4)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(5))
			+ format(chancesOf.get(2)) + ". ");

		} else if (eventTimes.size() == 6 && (eventTimes.get(4) - eventTimes.get(3)) >= 2 && (eventTimes.get(2) - eventTimes.get(1)) >= 2) {
			messageBuilder.append(Utility.convertTimeToRegTime(eventTimes.get(0)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(1))
			+ format(chancesOf.get(0)) 
			+ ", " + Utility.convertTimeToRegTime(eventTimes.get(2)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(3))
			+ format(chancesOf.get(1)) 
			+ ", and " + Utility.convertTimeToRegTime(eventTimes.get(4)) + " - " + Utility.convertTimeToRegTime(eventTimes.get(5))
			+ format(chancesOf.get(2)) + ". ");

		}
		//Messages for rain is happening right now
		else if (isPrecipRightNow && endingTime - startingTime > 0 ) {
			messageBuilder.append("now - " + Utility.convertTimeToRegTime(endingTime) + format(chancesOf.get(0)) + ". ");

		} else if (isPrecipRightNow && endingTime - startingTime <= 0 ) {
			messageBuilder.append("now - end of day" + format(chancesOf.get(0)) + ". ");

		} 
		//Messages for rain that will happen later
		else if (!isPrecipRightNow && endingTime - startingTime == 0 && eventTimes.size() == 2 && idGroups.size() > 1) {
			messageBuilder.append("at " + Utility.convertTimeToRegTime(startingTime) + format(chancesOf.get(0)) + ". ");

		} else if(!isPrecipRightNow && endingTime - startingTime <= 0){
			messageBuilder.append(Utility.convertTimeToRegTime(startingTime) + " - end of day" + format(chancesOf.get(0)) + ". ");

		} else {
			messageBuilder.append(Utility.convertTimeToRegTime(startingTime) + " - " 
					+ Utility.convertTimeToRegTime(endingTime) 
					+ format(chancesOf.get(0)) 
					+ ". ");

		}
		//Adds the type of precipitation to the message
		if(!isPrecipToday && isPrecipTomorrow) {
			messageBuilder.insert(0, typeOfEvent);
		} else if (isPrecipRightNow) {
			messageBuilder.insert(0, "Expect " + typeOfEvent);
		} else {
			messageBuilder.insert(0, "Lookout for " + typeOfEvent);
		}
		weatherEventMessage += messageBuilder.toString();

	}
	
	public String getPrecipMessage() {
		return weatherEventMessage;
	}

	//Formats the percentages to be used in the Message
	public static String format(int percent) {
		return " (" + percent + "%)";
	}

}
