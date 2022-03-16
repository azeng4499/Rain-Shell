package generateMessage;

public class Advice {
	
	private boolean isPrecipToday;
	private int todaysHigh,todaysLow, hoursOfPrecip;
	public String wardrobeAdvice = "";
	
	//Generates a message for what to wear today
	Advice(ParseJson json) {
		
		isPrecipToday = json.isPrecipToday;
		todaysHigh = json.todaysHigh;
		todaysLow = json.todaysLow;
		hoursOfPrecip = json.hoursOfPrecip;

		if(isPrecipToday && hoursOfPrecip > 3) {
			wardrobeAdvice = "Remember to bring a raincoat/umbrella today.";
		} else {
			String [] levelsOfClothes = {"a shirt", "a long sleeve shirt", "a sweatshirt", "a heavy coat"};
			int dayAdviceIndex, nightAdviceIndex;

			dayAdviceIndex = getIndex(todaysHigh);
			nightAdviceIndex = getIndex(todaysLow);
			
			if((isPrecipToday && dayAdviceIndex <= 1) || (dayAdviceIndex <= 1 && nightAdviceIndex >= 2)) {
				wardrobeAdvice = String.format("Wear %s today, but bring extra layers.", levelsOfClothes[dayAdviceIndex]);
			} else {
				wardrobeAdvice = String.format("Wear %s today.", levelsOfClothes[dayAdviceIndex]);
			}
		}
	}
	
	public String getAdviceMessage() {
		return wardrobeAdvice;
	}
	
	//Returns an index for the temperature being passed in
	public static int getIndex(int temp) {
		if(temp >= 65) {
			return 0;
		} else if (temp < 65 && temp >= 55) {
			return 1;
		} else if (temp < 55 && temp >= 40) {
			return 2;
		} else  {
			return 3;
		}
	}

}
