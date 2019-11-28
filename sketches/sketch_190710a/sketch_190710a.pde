import processing.sound.*;
SoundFile file;

FFT fft;
AudioIn in;
int bands = 2048;
int current_line = 0;
int audio_duration = 0;

int fft_frame_x = 10;
float current_spectrum_sum = 0;
PImage image_asset_1;

ArrayList<float[]> spectrum_matrix = new ArrayList<float[]>();
enum InputType {
  AUDIO_INPUT,
  FILE_INPUT
}

void intializeAudio(InputType type) {
  if(type == InputType.FILE_INPUT) {
    file = new SoundFile(this, "../../resources/audio/test.mp3");
    println("Duration= " + file.duration() + " seconds");
    audio_duration = int(file.duration());
    fft = new FFT(this, bands);
    file.play();
    fft.input(file);
  } else if(type == InputType.AUDIO_INPUT) {
    in = new AudioIn(this, 0);
    fft = new FFT(this, bands);
    in.play();
    fft.input(in);
  }
}

void initializeAssets() {
  image_asset_1 = loadImage("../../resources/image/emil-head.jpg");
} 

void setup() {
  // ================ Setup ====================
  size(1150, 600);
  intializeAudio(InputType.FILE_INPUT);
  initializeAssets();
}

void drawSpectogram(int width, int height, int leftPadding, int topPadding) {
  float[] spectrum = null;
  int matrix_size = spectrum_matrix.size();
  if(matrix_size == width) {
    spectrum_matrix.remove(0);
  }
  spectrum = new float[bands];
  fft.analyze(spectrum);
  spectrum_matrix.add(spectrum.clone());
  current_spectrum_sum = 0;
  float sum = 0;
  for(int x = 0; x < matrix_size; x++) {
    for(int y = 0; y < height; y++) {
      int color_value = int(spectrum_matrix.get(x)[y]*255*5);
      set(x + leftPadding,y + topPadding,color(0,color_value,color_value)); 
      sum += spectrum_matrix.get(x)[y];
    }
  }
  current_spectrum_sum = sum;
}

void drawStats(int leftPadding, int topPadding) {
   textSize(16);
   fill(0, 0, 0);
   text("current_spectrum_sum: "+current_spectrum_sum, leftPadding, topPadding);
}

void drawTestScene(int width, int height, int leftPadding, int topPadding) {
  // for(int x = 0; x < width; x++) {
  //  for(int y = 0; y < height; y++) {
  //    set(x + leftPadding,y + topPadding,color(0,0,0)); 
  //  }
  //}
  fill(255);
  rect(leftPadding, topPadding, width, height);
  float scale = 0;
  current_spectrum_sum -= 2000;
  if(current_spectrum_sum > 3000) scale = 1;
  else {
    scale = current_spectrum_sum / 3000;
  }
  
  float scaledWidth = width * scale;
  float scaledHeight = height * scale;
  float dynamicX = leftPadding + (width/2) - (scaledWidth/2);
  float dynamicY = topPadding + (width/2) - (scaledHeight/2);
  image(image_asset_1, dynamicX, dynamicY, scaledWidth, scaledHeight);
}

void draw() { 
  background(255);
  drawSpectogram(500, 500, 50, 40);
  drawStats(50, 30);
  drawTestScene(500, 500, 600, 40);
}
