// Event Tide - Cosmos visualize

float kMax;
float step;
int n = 1000;
float radius = 620;

float inter = 0.55;
// float inter = 0.75;
float strokeWeight = 6;
// float strokeWeight = 2;
int maxNoise = 2200;
PImage bg;
PShader blur;

int darkThreshold = 20;
int red[] = new int[1000];
int green[] = new int[1000];
int blue[] = new int[1000];
 
void setup() {
  size(6000, 8000);
  // size(3000, 4000);
  smooth(10);
  noFill();
  noLoop();
  kMax = random(1, 1.5);
  step = random(0.01, 0.03);
  // bg = loadImage("cosmos_2000000_tiled_planet.png");
  String[] lines = loadStrings("color.txt");
  for(int i = 0; i < lines.length; i++) {
    String[] col = lines[i].split(" ");
    red[i] = Integer.parseInt(col[0]);
    green[i] = Integer.parseInt(col[1]);
    blue[i] = Integer.parseInt(col[2]);
  }
}

void draw() {
  background(10);
  // background(bg);
  // filter(blur);
  // colorMode(HSB, 1);
  
  int e = 0;
  for(float i = 0; i < n; i++, e++) {
    //if(i > 100 && i < 400) {
    //  colorMode(RGB,70,255,150,255);
    //} else {
    //  colorMode(RGB,255,255,255,255);
    //}
    colorMode(RGB,255,255,255,255);
    // float alpha = 1 - i/n;
    float alpha = 0.75 - (0.0005 * i);
    if(red[e] < darkThreshold && green[e] < darkThreshold && blue[e] < darkThreshold) {
      
    } else {
      stroke((color(red[e],green[e],blue[e])), alpha * 255);
      strokeWeight(strokeWeight);
      float size = radius + i*inter;
      float k = kMax * i/n;
      float noisiness = maxNoise * noiseProg(i/n);
      blob(size, width/2, height/2, k, i * step, noisiness);
    }
  }
  saveFrame(); 
}

float noiseProg(float x) {
  return x * x;
} 

void blob(float size, float xCenter, float yCenter, float k, float t, float noisiness) {
  beginShape();
  
  for(float theta = 0; theta < 2 * PI; theta += 0.01) {
    float r1,r2;
    if(theta < PI/2) {
      r1 = cos(theta);
      r2 = 1;
    } else if(theta < PI) {
      r1 = 0;
      r2 = sin(theta);
    } else if(theta < 3 * PI / 2) {
      r1 = sin(theta);
      r2 = 0;
    } else {
      r1 = 1;
      r2 = cos(theta);
    }
    float r = size + noise(k * r1, k * r2, t) * noisiness;
    float x = xCenter + r * cos(theta);
    float y = yCenter + r * sin(theta);
    curveVertex(x,y);
  }
  
  endShape(CLOSE);
}
