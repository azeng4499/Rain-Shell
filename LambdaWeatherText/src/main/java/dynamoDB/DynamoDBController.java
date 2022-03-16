package dynamoDB;

import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

public class DynamoDBController {

	private static final String AWSAcessKeyID = "";
	private static final String AWSSecretAcessKey = "";
	private static DynamoDBMapper mapper;

	
	public static void initilize() {

		try {
			AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
					new BasicAWSCredentials(AWSAcessKeyID, AWSSecretAcessKey)
					);

			AmazonDynamoDB ddbClient = AmazonDynamoDBClientBuilder.standard()
					.withCredentials(credentials)
					.withRegion("us-east-2")
					.build();

			mapper = new DynamoDBMapper(ddbClient);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static Client loadClient() {
		Client client = new Client();
		client.setLocation("College Park");
		client.setLastName("Zeng");

		Client result = mapper.load(client);

		return result;

	}
	
	public static List<Client> queryForLocation(String location) {
		Client client = new Client();
		client.setLocation(location);
		
		DynamoDBQueryExpression <Client> query = new DynamoDBQueryExpression <Client>()
				.withHashKeyValues(client);
		
		List <Client> result = mapper.query(Client.class, query);
		
		return result;
 
	}

}
