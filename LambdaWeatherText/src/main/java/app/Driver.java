package app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.jayway.jsonpath.JsonPath;

import dynamoDB.Client;
import dynamoDB.DynamoDBController;
import generateMessage.MessageDriver;
import generateMessage.ParseJson;
import generateMessage.Utility;
import twilio.Texter;

public class Driver implements RequestHandler<Object,Object>{
	
	public Object handleRequest(Object input, Context context) {
		String[] locations = {"Adelphi"};
		try {
			DynamoDBController.initilize();
			for(String location: locations) {
				List <Client> searchResults = DynamoDBController.queryForLocation(location);
				createAndSend(searchResults);	
			}
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "failed";
	}
	
	//Generates an API_Caller object to retrieve the message for a specific location, personalizes it, and then sends to Texter class to be sent 
	private static void createAndSend(List<Client> clients) throws Exception {
			double[] coords = getCoords(clients.get(0).getZipCode());
			double lat = coords[0], lon = coords[1];
			String location = clients.get(0).getLocation();

			MessageDriver createMessage = new MessageDriver(location, lat , lon);
	
			for(Client client: clients) {
				Texter.text(client.getPhoneNumber(),  createMessage.getEmoji() + " " + client.getFirstName() + createMessage.getMessage());
			}
	}


	//Returns the coordinates for a given Zip Code
	private static double[] getCoords(String zipCode) throws Exception {	
			String Direct_Geo_URL = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zipCode + ",US&appid=0f215f03ca3a27ffa728790940379ed9";

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.header("accept", "application/json")
					.uri(URI.create(Direct_Geo_URL))
					.build();
			HttpResponse <String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			double[] coords = new double[2];

			coords[0] = Double.valueOf(JsonPath.read(response.body(), "$.lat").toString());
			coords[1] = Double.valueOf(JsonPath.read(response.body(), "$.lon").toString());

			return coords;
	}

}
