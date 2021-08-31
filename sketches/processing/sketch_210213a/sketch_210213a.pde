// Image Line Drawer Tool
// - vonqo

PImage bgImage;
boolean isPressed = false;
int preX;
int preY;
int scale = 2;

class GLine {
  int beginX;
  int beginY;
  int endX;
  int endY;
}

ArrayList<GLine> lineList = new ArrayList<GLine>();

void setup() {
  size(800, 670);
  bgImage = loadImage("turing_fixed.png");
}

void draw() {
  background(255);
  image(bgImage, 0, 0, width, height);
  smooth();
  
  stroke(3, 252, 69);
  for(int i = 0; i < lineList.size(); i++) {
    GLine ln = lineList.get(i);
    line(ln.beginX, ln.beginY, ln.endX, ln.endY);
  }
  
  stroke(252, 3, 3);
  if(isPressed) {
    line(preX, preY, mouseX, mouseY);
  } 
}

void mousePressed() {
  if(isPressed) {
    GLine ln = new GLine();
    ln.beginX = preX;
    ln.beginY = preY;
    ln.endX = mouseX;
    ln.endY = mouseY;
    lineList.add(ln);
    isPressed = false;
  } else {
    preX = mouseX;
    preY = mouseY;
    isPressed = true;
  }
}

void keyPressed() {
  // UNDO 
  if(key == 'z' || key == 'Z') {
    if(lineList.size() >= 1) {
      lineList.remove(lineList.size()-1);
    }
  } 
  
  // SAVE TO FILE
  else if(key == 's' || key == 'S') {
    JSONArray jsonArray = new JSONArray();
    for(int i = 0; i < lineList.size(); i++) {
      GLine ln = lineList.get(i);
      JSONArray objectDetail = new JSONArray();
      
      objectDetail.append(0); // material index
      objectDetail.append(ln.beginX * scale); // start X
      objectDetail.append(ln.beginY * scale); // start Y
      objectDetail.append((ln.endX - ln.beginX) * scale); // dist X
      objectDetail.append((ln.endY - ln.beginY) * scale);  // dist Y
      jsonArray.append(objectDetail);
    }
    saveJSONArray(jsonArray, "data/data_"+millis()+".json");
  }
}
