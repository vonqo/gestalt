void setup() {
  size(1100,1000);
  background(0);
 
  float x = 50;
  float y = 20;
  float margin = 10;
  int wtf = 0;
  for(int i = 1; i <= 160; i ++) {
    for(int mood = 1; mood <= 1000; mood++, wtf+=0.1) {
      float lx = mood + x;
      float ly = margin * i + y;
      float wv = random1(wtf) * 5;
      float wvSide = wv / 2;
      line(lx, ly-wvSide, lx, ly+wvSide);
      // stroke(random(255), random(255), random(255), random(255));
      stroke(random(255));
      
    }
  }
  smooth();
}

float random1(float x) {
  // return noise(x);
  return random(1);
}
