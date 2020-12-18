
// Prototyping - TORNADO

void setup() {
  background(255);
  size(1000, 1000);
  int r = 100;
  int rr = 500;
  double theta = PI;
  // double theta2 = theta;
  double theta2 = PI - (PI/2);
  double unitSpace = 2*PI / 1000;
  print(unitSpace);
  for(int i = 0; i < 1000; i++, theta += unitSpace, theta2 += unitSpace) {
    int e = 500-r;
    int ee = 500-rr;
    
    int x = (int)(Math.cos(theta) * r) + r + e;
    int y = (int)(Math.sin(theta) * r) + r + e;
   
    int x2 = (int)(Math.cos(theta2) * rr) + rr + ee;
    int y2 = (int)(Math.sin(theta2) * rr) + rr + ee;
    
    line(x,y,x2,y2);
    stroke(color(0));
    set(x2,y2,color(0));
    set(x,y,color(0));
  }
}

void draw() {

}
