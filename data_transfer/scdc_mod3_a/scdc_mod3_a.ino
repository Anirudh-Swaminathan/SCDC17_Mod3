int incomingByte = 0;
int count = 0;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  // Set a new seed value during every run
  randomSeed(analogRead(0));
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available()>0)
  {
    incomingByte = Serial.read();
    if(incomingByte == 's')
    {
      if(count == 0)
      {
        count+=1;
        for(int i=0; i<4; ++i){
          // Generate a random number between 1 and 41.
          // 41 is NON-inclusive.
          int ra = random(1,41);
          String no = String(ra);
          Serial.println(no);
        }
        count = 0;
      }
    }
  }
}
