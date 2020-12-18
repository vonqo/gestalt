float increment = 0.01;

void setup() {
  size(500, 600);
  background(0);
  smooth();
  
  float xs = 50;
  float ys = 20;
  
  float margin = 10;
  
  for(int i = 1; i <= 50; i++) {
    float w = rand(10)/2;  
    fill(255);
    rect(xs, ys + (i * margin)-w, xs+350, w);
  }
  
  loadPixels();
  // noise
  float xoff = 0.0; // Start xoff at 0
  float detail = map(mouseX, 0, width, 0.1, 0.6);
  noiseDetail(8, detail);
  // For every x,y coordinate in a 2D space, calculate a noise value and produce a brightness value
  for (int x = 0; x < width; x++) {
    xoff += increment;   // Increment xoff 
    float yoff = 0.0;   // For every xoff, start yoff at 0
    for (int y = 0; y < height; y++) {
      yoff += increment; // Increment yoff
      
      // Calculate noise and scale by 255
      float bright = noise(xoff, yoff) * 255;

      // Try using this line instead
      //float bright = random(0,255);
      
      // Set each pixel onscreen to a grayscale value
      pixels[x+y*width] = (pixels[x+y*width] - color(bright));
      // pixels[x+y*width] = color(bright, 50);
    }
  }
  updatePixels();
  
  save("test.png");
}

float rand(int x) {
  // return 4;
  return random(x-4) + 4;
  // return noise(x);
}
