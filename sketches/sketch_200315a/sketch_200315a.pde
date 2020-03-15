

void drawCircleByLine(int x, int y, int radius, int lineCount) {
  double theta = PI;
  double unitSpace = 2*PI / lineCount;
  
  int preX = -1;
  int preY = -1;
  
  for(int i = 0; i <= lineCount; i++, theta += unitSpace) {
    int postX = (int)(Math.cos(theta) * radius) + x;
    int postY = (int)(Math.sin(theta) * radius) + y;
    
    if(preX > 0 && preY > 0) {
      line(preX,preY,postX,postY);
      stroke(color(0));
    }
    
    preX = postX;
    preY = postY;
  }
}

int findOptimalLineCount(int radius) {
  double p = 2 * PI * radius;
  print(p);
  int lines = (int)(p/10);
  print("\nlines:"+lines);
  return lines;
}

void drawCircle(int x, int y, int radius) {
  circle(x, y, radius*2);
}

void setup() {
  background(255);
  size(750, 500);
  int r = 100;
  drawCircle(r, r, r);
  drawCircleByLine(r*3, r, r, findOptimalLineCount(r));
}

void draw() {
}
