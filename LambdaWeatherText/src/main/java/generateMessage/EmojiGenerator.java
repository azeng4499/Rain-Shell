package generateMessage;

public class EmojiGenerator {
	
	private String emojis = "";
	
	public String getEmjois(ParseJson json) {
		
		if (json.willSnow) {
			emojis += "❄️️";
		} else if(json.willStorm) {
			emojis += "⛈️️";
		} else if (json.willRain) {
			emojis += "☔️";
		} else if (json.currentCode == 800) {
			emojis += "☀️️";
		} else if (json.currentCode == 801 || json.currentCode == 802 || json.currentCode == 803) {
			emojis += "🌤";
		} else if (json.currentCode == 804){
			emojis += "☁️";
		} else {
			emojis += "?";
		}
		
		return emojis;	
	}

}
