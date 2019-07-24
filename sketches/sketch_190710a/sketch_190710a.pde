import processing.sound.*;
SoundFile file;

FFT fft;
AudioIn in;
int bands = 2048;
int current_line = 0;
int audio_duration = 0;
ArrayList<float[]> spectrum_matrix = new ArrayList<float[]>();

void setup() {
  size(1000, 500);
  background(255);
  
  file = new SoundFile(this, "/Users/eirenevon/Desktop/test.mp3");
  println("Duration= " + file.duration() + " seconds");
  audio_duration = int(file.duration());
  
  fft = new FFT(this, bands);
  in = new AudioIn(this, 0);
  
  file.play();
  fft.input(file);
}

void draw() { 
  background(255);
  float[] spectrum = null;
  int matrix_size = spectrum_matrix.size();
  if(matrix_size == 1000) {
    spectrum_matrix.remove(0);
  }
  spectrum = new float[bands];
  fft.analyze(spectrum);
  spectrum_matrix.add(spectrum.clone());
  for(int i = 0; i < matrix_size; i++) {
    for(int e = 0; e < 500; e++) {
      int color_value = int(spectrum_matrix.get(i)[e]*255*5);
      set(i,e,color(0,color_value,color_value));
    }
  }
}
