# Rain-Shell
This is a weather service that sends you a text every morning with a brief summary of the weather as well as a suggestion on what to wear for the day. 
Weather data is first called for a specific location via the OpenWeatherMap API and parsed into data segments.
Then, the raw data goes through a series of filters each responsible for a different aspect of the weather (i.e high/low temperature, wind, rain, etc).
Finally, the serivce takes the message and sends it to everyone in that location according to the AWS DynamoDB NoSQL Databse.
The service runs on AWS Lambda with an AWS CloudWatch CRON job that runs the program at 8 am everyday. 

This project was developed independently by Aaron Zeng from Sept 2021 - Nov 2021.
