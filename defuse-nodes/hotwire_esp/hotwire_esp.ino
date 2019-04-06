#include <FastLED.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

#define UPDATES_PER_SECOND 100
#define LED_PIN     D4
#define NUM_LEDS    20
#define BRIGHTNESS  64
#define LED_TYPE    WS2811
#define COLOR_ORDER GRB
#define WIRE_INPUT D7
#define STOP_INPUT D6

String host = "http://vinegar-container.appspot.com/";
String ssid = "GoAway";
String pwd = "DontComeNearMe";
String register_payload = "{ \"name\":\"HOTWIRE\",\"description\":\"LIGMA\"}";


CRGB leds[NUM_LEDS];

String uid;
int running_toggle = 0;
int countdown = NUM_LEDS;
int cnt_divider = 0;
int cnt_divider2 = 0;
int cnt = 0;
int failed = 0;


void setup() {
  delay( 3000 ); // power-up safety delay
  FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
  FastLED.setBrightness(BRIGHTNESS);

  pinMode(WIRE_INPUT, INPUT_PULLUP);
  //  pinMode(START_INPUT, INPUT_PULLUP);
  pinMode(STOP_INPUT, INPUT_PULLUP);


  pinMode(BUILTIN_LED, OUTPUT);

  Serial.begin(115200);

  uid = registerNode();

}


void loop()
{
  if (running_toggle == 0) {
    if (cnt_divider2 < 100) {
      cnt_divider2 = cnt_divider2 + 1;
    }
    else {
      cnt_divider2 = 0;

      int alarm = checkAlarms();
      if (alarm == 1 && failed==0) {
          running_toggle = 1;
          countdown = NUM_LEDS;
      }
      else if(alarm == 1 && failed==1){
        running_toggle=0;
      }
      else if(alarm == 0 && failed==1){
        failed =0;
        Serial.println("Resetted!");
      }
    }
  }


  // Defuse
  if (digitalRead(STOP_INPUT) == 0 && running_toggle == 1) {
    running_toggle = 0;
    defuseAlarm(uid);

    for (int i = 0; i < 5; i++) {
      for (int idx = 0; idx < NUM_LEDS; idx++) {
        leds[idx] = CRGB::Green;
      }

      FastLED.show();
      delay(200);

      for (int idx = 0; idx < NUM_LEDS; idx++) {
        leds[idx] = CRGB::Black;
      }

      FastLED.show();
      delay(200);
    }
    Serial.println("DEFUSED!");
  }

  if (digitalRead(WIRE_INPUT) == 0 && running_toggle == 1) {
    running_toggle = 0;
    failed=1;
    cnt = 0;
    for (int i = 0; i < 5; i++) {
      for (int idx = 0; idx < NUM_LEDS; idx++) {
        leds[idx] = CRGB::Red;
      }

      FastLED.show();
      delay(200);

      for (int idx = 0; idx < NUM_LEDS; idx++) {
        leds[idx] = CRGB::Black;
      }

      FastLED.show();
      delay(200);
    }
    Serial.println("FAILED!");
  }
  



  // Countdown is over
  if (countdown == 0 && running_toggle == 1) {
    Serial.println("Countdown done");
    running_toggle = 0;
    failed=1;
    for (int i = 0; i < 5; i++) {
      for (int idx = 0; idx < NUM_LEDS; idx++) {
        leds[idx] = CRGB::Red;
      }

      FastLED.show();
      delay(200);

      for (int idx = 0; idx < NUM_LEDS; idx++) {
        leds[idx] = CRGB::Black;
      }

      FastLED.show();
      delay(200);
    }
  }

  // Countdown
  if (running_toggle == 1) {
    if (cnt_divider < 100) {
      cnt_divider = cnt_divider + 1;
    }
    else {
      countdown = countdown - 1;
      cnt_divider = 0;
    }
    for (int idx; idx < countdown; idx++) {
      leds[idx] = CRGB::Red;
    }
    for (int idx = countdown; idx <= NUM_LEDS; idx++) {
      leds[idx] = CRGB::Black;
    }
  }


  FastLED.show();
  FastLED.delay(1000 / UPDATES_PER_SECOND);

}




String sendPostRequest(String host, String payload) {
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

String sendGetRequest(String host) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    http.begin(host);
    http.addHeader("Content-Type", "application/json");

    int httpCode = http.GET();
    String return_payload = http.getString();

    Serial.println(httpCode);

    http.end();

    return return_payload;
  }
}

int checkAlarms() {
  Serial.println("Checking for alarms...");
  String return_payload = sendGetRequest(host + "state");
  Serial.println(return_payload);

  if(return_payload != "{}"){
    return 1;
  }
  else{
    return 0;
  }
}

String registerNode() {
  WiFi.begin(ssid, pwd); //WiFi connection

  while (WiFi.status() != WL_CONNECTED) {  //Wait for the WiFI connection completion

    delay(500);
    Serial.println("Waiting for connection...");
  }
  // Register
  Serial.println("Register button...");
  String post_reply = sendPostRequest(host + "register", register_payload);
  Serial.println(post_reply);

  return post_reply;
}

void defuseAlarm(String uid) {

  Serial.println("Defusing Alarm...");
  String defuse_payload = "{ \"uid\":\"" + uid + "\"}";
  String post_reply = sendPostRequest(host + "defuse", defuse_payload);
  Serial.println(post_reply);
}
