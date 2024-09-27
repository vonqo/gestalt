// Image Line Drawer Tool
// - vonqo

PImage bgImage;
boolean isPressed = false;
int preX;
int preY;
int scale = 1;
int material = 1;

class GLine {
  int material;
  int beginX;
  int beginY;
  int endX;
  int endY;
}

ArrayList<GLine> lineList = new ArrayList<GLine>();

void setup() {
  size(800, 800);
  bgImage = loadImage("leaf5.png");
  noSmooth();
  textSize(12);
}

void draw() {
  background(255);
  image(bgImage, 0, 0, width, height);
  fill(0);
  text("CURRENT MATERIAL: " + material, 10, 20);
  
  for(int i = 0; i < lineList.size(); i++) {
    GLine ln = lineList.get(i);
    if(ln.material == 1) {
      stroke(3, 252, 69);
    } else {
      stroke(252, 250, 93);
    }
    line(ln.beginX, ln.beginY, ln.endX, ln.endY);
  }
  
  if(isPressed) {
    if(material == 1) {
      stroke(252, 3, 3);
    } else {
      stroke(132, 31, 255);
    }
    
    line(preX, preY, mouseX, mouseY);
  } 
}

void mousePressed() {
  if(isPressed) {
    GLine ln = new GLine();
    ln.material = material;
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
  
  // CHANGE MATERIAL INDEX
  else if(key == 'w' || key == 'W') {
    if(material == 1) {material = 2;}
    else if(material == 2) {material = 1;}
  } 
  
  // AUTO BEGIN FROM LAST END
  else if(key == 'e' || key == 'E') {
    if(lineList.size() > 0) {
      GLine last = lineList.get(lineList.size()-1);
      preX = last.endX;
      preY = last.endY;
      isPressed = true;
    }
  }
  
  // CONNECT HEAD AND TAIL
  else if(key == 'c' || key == 'C') {
    if(lineList.size() > 1) {
      GLine ln = new GLine();
      GLine head = lineList.get(0);
      GLine tail = lineList.get(lineList.size()-1);
      ln.material = material;
      ln.beginX = tail.endX;
      ln.beginY = tail.endY;
      ln.endX = head.beginX;
      ln.endY = head.beginY;
      lineList.add(ln);
    }
  }
  
  // SAVE TO FILE
  else if(key == 's' || key == 'S') {
    saveFile();
  } 
}

void saveFile() {
  JSONArray jsonArray = new JSONArray();
  for(int i = 0; i < lineList.size(); i++) {
    GLine ln = lineList.get(i);
    JSONArray objectDetail = new JSONArray();
    
    objectDetail.append(ln.material); // material index
    objectDetail.append(ln.beginX * scale); // start X
    objectDetail.append(ln.beginY * scale); // start Y
    objectDetail.append((ln.endX - ln.beginX) * scale); // dist X
    objectDetail.append((ln.endY - ln.beginY) * scale);  // dist Y
    jsonArray.append(objectDetail);
  }
  saveJSONArray(jsonArray, "data/data_"+millis()+".json");
}
