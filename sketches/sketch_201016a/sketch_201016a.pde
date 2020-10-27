int red[] = new int[1000];
int green[] = new int[1000];
int blue[] = new int[1000];
int padding = 40;
float percent = 1;

void setup() {
  size(1200, 1320);
  noSmooth();
  noLoop();
  String[] lines = loadStrings("color.txt");
  for(int i = 0; i < lines.length; i++) {
    String[] col = lines[i].split(" ");
    red[i] = Integer.parseInt(col[0]);
    green[i] = Integer.parseInt(col[1]);
    blue[i] = Integer.parseInt(col[2]);
  }
  percent = 255 / padding;
}

void draw() {
  for(int r = 1, colorIndex = 0; r <= 33; r++) {
    for(int c = 1; c <= 30; c++, colorIndex++) {
      int preCornerHeight = padding * (r-1);
      int preCornerWidth = padding * (c-1);
      int nextCornerHeight = padding * r;
      int nextCornerWidth = padding * c;
      for(int y = preCornerHeight; y < nextCornerHeight; y++) {
        for(int x = preCornerWidth; x < nextCornerWidth; x++) {
          
          float distanceFromTopLeft = dist(x, y, preCornerWidth, preCornerHeight);
          float distanceFromTopRight = dist(x, y, nextCornerWidth, preCornerHeight);
          float distanceFromBottomLeft = dist(x, y, preCornerWidth, nextCornerHeight);
          float distanceFromBottomRight = dist(x, y, nextCornerWidth, nextCornerHeight);
          
          // =================== COLOR ====================== //
          
          float currentRed = red[colorIndex];
          float currentBlue = blue[colorIndex];
          float currentGreen = green[colorIndex];
          
          float nextRed = red[colorIndex+1];
          float nextBlue = blue[colorIndex+1];
          float nextGreen = green[colorIndex+1];
          
          float belowRed = 0;
          float belowBlue = 0;
          float belowGreen = 0;
          
          float belowNextRed = 0;
          float belowNextBlue = 0;
          float belowNextGreen = 0;
          
          if(r < 33) {
            belowRed = red[colorIndex + 30];
            belowBlue = blue[colorIndex + 30];
            belowGreen = green[colorIndex + 30];
          
            belowNextRed = red[colorIndex + 30];
            belowNextBlue = blue[colorIndex + 30];
            belowNextGreen = green[colorIndex + 30];
          }
          
          float redDiff = currentRed - nextRed;
          float blueDiff = currentBlue - nextBlue;
          float greenDiff = currentGreen - nextGreen;
          
          stroke(
            (distanceFromTopRight), // RED
            (distanceFromTopRight), // GREEN
            (distanceFromTopRight)  // BLUE
          );
          
          //100 - 150 = -50
          //100 - 94 = 6
          //100 - - 50 = 150
          //100 - 6 = 94
          
          
          
          //float currentRed = red[colorIndex];
          //float currentBlue = blue[colorIndex];
          //float currentGreen = green[colorIndex];
          
          //float currentRed = red[colorIndex];
          //float currentBlue = blue[colorIndex];
          //float currentGreen = green[colorIndex];
          
          
          // float top
          
          
          //print("==========================");
          //print(distanceFromTopLeft);
          //print(distanceFromTopRight);
          //print(distanceFromBottomLeft);
          //print("==========================");
          
          //float distanceFromTopLeft = dist(x, y, 0, 0);
          //float distanceFromTopRight = dist(x, y, width, 0);
          //float distanceFromBottomLeft = dist(x, y, 0, height);
          
          //stroke(
          //  distanceFromTopLeft * percent, // RED
          //  distanceFromTopRight * percent, // GREEN
          //  distanceFromBottomLeft * percent // BLUE
          //);
          //stroke(
          //  currentRed, // RED
          //  currentGreen, // GREEN
          //  currentBlue // BLUE
          //);
          point(x, y);
        }
      }
    }
  }
  
  //for(int y = 0; y < height; y++){
  //  for(int x = 0; x < width; x++){
      //float distanceFromTopLeft = dist(x, y, 0, 0);
      //float distanceFromTopRight = dist(x, y, width, 0);
      //float distanceFromBottomLeft = dist(x, y, 0, height);
  //    stroke(distanceFromTopLeft, distanceFromTopRight, distanceFromBottomLeft);
  //    point(x, y);
  //  }
  //}
}
