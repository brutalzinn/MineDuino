
 
#include <ESP8266WiFi.h>


 
//Server connect to WiFi Network
const char *ssid = "paes";  //Enter your wifi SSID
const char *password = "garfield laranja";  //Enter your wifi Password



int pin = -1;
String mode = "";
int value = -1;
IPAddress server(192,168,0,52);
int port = 8888;
WiFiClient client;
//=======================================================================
//                    Power on setup
//=======================================================================
//=======================================================================
void setup() 
{
  Serial.begin(115200); 

   client.setTimeout(5);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password); //Connect to wifi
 
  // Wait for connection  
  Serial.println("Connecting to Wifi");
  while (WiFi.status() != WL_CONNECTED) {   
    delay(500);
    Serial.print(".");
    delay(500);
  }
 
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
 
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  
  client.connect(server,port);
  Serial.print("Open Telnet and connect to IP:");
  Serial.print(WiFi.localIP());
  Serial.print(" on port ");
  Serial.println(port);
   if (client) {
    if(client.connected())
    {
      Serial.println("Client Connected");
    }

   }
}
void wifi() 
{
//  WiFiClient client = server.available();
  
  if (client) {
    if(client.connected())
    {
      Serial.println("Client Connected");
    }
    
    while(client.connected()){      
      while(client.available()>0){
        // read data from the connected client
        Serial.write(client.read()); 
      }
      //Send Data to connected client
      while(Serial.available()>0)
      {
        client.write(Serial.read());
      }
    }
   // client.stop();
    //erial.println("Client disconnected");    
  }
}


//=======================================================================
//                    Loop
//=======================================================================
 void loop(){
  char data[11];
  byte size = client.readBytes(data, 10);
  data[size] = NULL;
  if(data[0] != NULL)
  {
    char* entries = strtok(data, ";");
    int index = 0;
    while(entries != NULL)
    {
      if(index == 0) { pin = getPin(entries); }
      if(index == 1) { mode = entries; }
      if(index == 2) { value = atoi(entries); }
      entries = strtok(NULL, ";");
      index += 1;
    }
  }

  if(pin != -1 && mode == "ir" && value != -1)
  {
    handleInterrupt();
    pin = -1;
    mode = "";
    value = -1;
  }
  else if(pin != -1 && mode != "" && value != -1)
  {
    writeToPin();
    pin = -1;
    mode = "";
    value = -1;
  }
  else if(pin != -1 && mode != "" && value == -1)
  {
    readFromPin();
    pin = -1;
    mode = "";
  }
//wifi();
  }

//You need to edit this if your board has more, less or different interrupt pins than the Arduino UNO. Make sure that those pins can use the CHANGE mode.
void handleInterrupt(){
  if(value == 1)
  {
    attachInterrupt(digitalPinToInterrupt(pin), pin == 2 ? pin2 : pin3, CHANGE); //You need to add the additional ISRs here
  }
  else
  {
    detachInterrupt(digitalPinToInterrupt(pin));
  }
}

void writeToPin(){
  pinMode(pin, OUTPUT);
  if(mode == "dw")
  {
    digitalWrite(pin, value);
  }
  else
  {
    analogWrite(pin, value);
  }
}

void readFromPin(){
  int val = -1;
  if(mode == "ar")
  {
    pinMode(pin, INPUT);
    val = analogRead(pin);
    val = map(val, 0, 1023, 0, 15);
  }
  else if(mode == "drp")
  {
    pinMode(pin, INPUT_PULLUP);
    val = digitalRead(pin);
  }
  else
  {
    pinMode(pin, INPUT);
    val = digitalRead(pin);
  }
  String out = String(pin) + String(";") + mode + String(";") + String(val);
  client.println(out);
}

void pin2(){
  String out = String("2;ir;") + String(digitalRead(2));
  client.println(out);
}

//You need to duplicate this method for additional interrupt pins
void pin3(){
  String out = String("3;ir;") + String(digitalRead(3));
  client.println(out);
}

//You need to change this if your board has more or less analog inputs than the Arduino UNO.
int getPin(char* p){
  if(p == "A0"){ return A0; }
//  if(p == "A1"){ return A1; }
//  if(p == "A2"){ return A2; }
//  if(p == "A3"){ return A3; }
//  if(p == "A4"){ return A4; }
//  if(p == "A5"){ return A5; 

  return atoi(p);
}
