#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

String host = "http://vinegar-container.appspot.com/";
String ssid = "GoAway";
String pwd = "DontComeNearMe";
String register_payload = "{ \"name\":\"BIGFATBUTTON\",\"description\":\"oh yes you heard right fucker\"}";

int inputPin = D7;
int val = 0;
String uid;

void setup() {
  pinMode(BUILTIN_LED, OUTPUT);
  pinMode(inputPin, INPUT_PULLUP);

    
  Serial.begin(115200);                        

  uid = registerNode();

}

void loop() {
  val = digitalRead(inputPin);
  digitalWrite(BUILTIN_LED, !val);

  if (val == 1) {
    defuseAlarm(uid);
  }
  delay(100);

}



String sendPostRequest(String host, String payload){
    if (WiFi.status() == WL_CONNECTED) { 
    HTTPClient http;    

    http.begin(host);      
    http.addHeader("Content-Type", "application/json");  

    int httpCode = http.POST(payload);  
    String return_payload = http.getString();     
                 
    Serial.println(httpCode);  
    
    http.end();  
    
    return return_payload;
  }
}

String registerNode(){
  WiFi.begin(ssid,pwd);  //WiFi connection

  while (WiFi.status() != WL_CONNECTED) {  //Wait for the WiFI connection completion

    delay(500);
    Serial.println("Waiting for connection...");
  }
  // Register
  Serial.println("Register button...");
  String post_reply = sendPostRequest(host + "register",register_payload);
  Serial.println(post_reply);
  
  return post_reply;
}

void defuseAlarm(String uid){

  Serial.println("Defusing Alarm...");
  String defuse_payload = "{ \"uid\":\"" + uid + "\"}";
  String post_reply = sendPostRequest(host + "defuse",defuse_payload);
  Serial.println(post_reply);
}
