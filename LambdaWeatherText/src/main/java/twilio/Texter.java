package twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class Texter {

	public static final String ACCOUNT_SID = "";
	public static final String AUTH_TOKEN = "";

	public static boolean text(String phoneNumber, String text) {

		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message message = Message.creator(
					new com.twilio.type.PhoneNumber("+1" + phoneNumber),
					new com.twilio.type.PhoneNumber("+16083363956"),
					text)
					.create();
			System.out.println("Message sent for +1" + phoneNumber);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
}
