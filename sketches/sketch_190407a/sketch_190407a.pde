import processing.sound.*;
AudioIn in;

void setup() {
  // fullScreen();
  size(800, 800);
}

void draw() {
  background(25);
  drawHand(width/2, 0, 1);
  drawHand(width/2, 0, 2);
  drawHand(width/2, 0, 3);
  drawHand(width/2, 0, 4);
}

void drawHand(float x, float y, int direction) {
  int dir_x = 1, dir_y = 1;
  boolean rotateAxis = false;
  switch(direction) {
    case 1: 
      stroke(0, 255, 0);
      break; 
    case 2:
      rotateAxis = true;
      y = height - y; dir_y = -1;
      stroke(255, 0, 0);
      break;
    case 3:
      y = height - y; dir_y = -1;
      stroke(0, 0, 255);
      break;
    case 4:
      rotateAxis = true;
      stroke(255);
      break;
  }
  line2(0, 0, 0, 150, x, y, dir_x, dir_y, rotateAxis);
}

void line2(float x1, float y1, float x2, float y2, float sx, float sy, int dir_x, int dir_y, boolean rotate_axis) {
  if(rotate_axis) {
    float c = x1; x1 = y1; y1 = c;
    c = x2; x2 = y2; y2 = c;
    c = sx; sx = sy; sy = c;
    int c2 = dir_x; dir_x = dir_y; dir_y = c2;
  }
  line(dir_x * x1+sx, dir_y * y1+sy , dir_x * x2+sx, dir_y * y2+sy);
}
