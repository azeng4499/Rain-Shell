package dynamoDB;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "WeatherTextDB")
public class Client {
	
	@DynamoDBHashKey(attributeName = "Location")
	private String location;
	
	@DynamoDBRangeKey(attributeName = "Last Name")
	@DynamoDBIndexHashKey(attributeName = "Last Name", globalSecondaryIndexName = "LastName-Index")
	private String lastName;
	
	@DynamoDBAttribute(attributeName = "First Name")
	private String firstName;
	
	@DynamoDBAttribute(attributeName = "Phone Number")
	private String phoneNumber;
	
	@DynamoDBAttribute(attributeName = "State")
	private String state;
	
	@DynamoDBAttribute(attributeName = "Zip Code")
	private String zipCode;
	
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String toString() {
		return "Location : " + location 
				+ "\nLast Name : " + lastName 
				+ "\nFirst Name : " + firstName 
				+ "\nPhone Number : " + phoneNumber 
				+ "\nState : " + state
				+"\nZip Code : " + zipCode;
	}
	
	
}