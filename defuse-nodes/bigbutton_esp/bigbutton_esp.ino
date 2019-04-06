#include <ESP8266WiFi.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

//const size_t capacity = JSON_OBJECT_SIZE(3) + 30;
//DynamicJsonDocument doc(capacity);
StaticJsonDocument<200> doc;

int inputPin = D7;
int val = 0;
String uid;

void setup() {
  pinMode(BUILTIN_LED, OUTPUT);
  pinMode(inputPin, INPUT_PULLUP);

    
  Serial.begin(115200);                                  //Serial connection
  WiFi.begin("GoAway", "DontComeNearMe");  //WiFi connection

  while (WiFi.status() != WL_CONNECTED) {  //Wait for the WiFI connection completion

    delay(500);
    Serial.println("Waiting for connection...");

  }
  // Register
  Serial.println("Register button...");
  if (WiFi.status() == WL_CONNECTED) { //Check WiFi connection status

    HTTPClient http;    //Declare object of class HTTPClient

    http.begin("http://vinegar-container.appspot.com/register");      //Specify request destination
    http.addHeader("Content-Type", "application/json");  //Specify content-type header

    int httpCode = http.POST("{ \"name\":\"BIGFATBUTTON\",\"description\":\"oh yes you heard right fucker\"}");   //Send the request
    String payload = http.getString();                  //Get the response payload

    Serial.println(httpCode);   //Print HTTP return code
    Serial.println(payload);    //Print request response payload
    uid = payload;
    http.end();  //Close connection

  }
  Serial.println("Done!");
 


}

void loop() {
  val = digitalRead(inputPin);
  digitalWrite(BUILTIN_LED, !val);

  if (val == 1) {
    if (WiFi.status() == WL_CONNECTED) { //Check WiFi connection status

      HTTPClient http;    //Declare object of class HTTPClient

      http.begin("http://vinegar-container.appspot.com/defuse");      //Specify request destination
      http.addHeader("Content-Type", "application/json");  //Specify content-type header

      String post = "{ \"uid\":" + uid + "}";
      int httpCode = http.POST(post);   //Send the request
      String payload = http.getString();                  //Get the response payload

      Serial.println(httpCode);   //Print HTTP return code
      Serial.println(payload);    //Print request response payload

      http.end();  //Close connection

    } else {

      Serial.println("Error in WiFi connection");

    }
  }
  delay(100);

}
