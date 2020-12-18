//////////////////////////////////////////////////////////////////////////
//                       //                                             //
//   -~=Manoylov AC=~-   //               Son of Ra 37                  //
//                       //                                             //
//////////////////////////////////////////////////////////////////////////
//                                                                      //
// Based on:                                                            //
//    #37 Son of Ra                                    //
//    http://geometrydaily.tumblr.com/image/17155048079                 //
//////////////////////////////////////////////////////////////////////////
//                                                                      //
// Controls                                                             //
//    mouse                                                             //
//       click: redraw                                                  //
//                                                                      //
//    keyboard                                                          //
//        'z': remove one strip                                         //
//        'x': add one strip                                            //
//////////////////////////////////////////////////////////////////////////
//                                                                      //
// Contacts:                                                            //
//    http://manoylov.tumblr.com/                                       //
//    https://codepen.io/Manoylov/                                      //
//    https://www.openprocessing.org/user/23616/                        //
//    https://www.facebook.com/epistolariy                              //
//////////////////////////////////////////////////////////////////////////

int sw = 2;           
int sz = 1200;
float diam = 1200/1.8;
float start;
int iter = 50;
float step = diam/iter;
int [] ch = { -1, 1 };
int wdt = 40;

void setup() {
  size(1200, 1200);
  colorMode(HSB, 100, 100, 100);
  noLoop();
  rectMode(CENTER);
  noStroke();
}

void draw() {
  background(10, 3, 90);
  start = diam + diam/2; 

  float H = random(0, 100);
  float S = random(90, 100);
  float B = 10;

  for (int i = 0; i<iter; ++i) {
    pushMatrix();
    translate(width/2, start-wdt/2);
    rotate(radians(random(7, 22)* ch[(int)random(ch.length)]));
    fill (H, S-i*(100/iter), B+i*((100-8)/iter));
    rect(0, 0, sz, wdt);
    start-= step - random(-step/2, step/2);
    popMatrix();
  }

  stroke(10, 3, 90);
  noFill();
  strokeWeight(300);
  ellipse(width/2-7, height/2, (diam+sz/2), (diam+sz/2));

  paper(10);  // browser value
}

void keyPressed() {
  if (key == 'z') {
    iter--;
  }
  if (key == 'x') {
    iter++;
  }
  iter = constrain(iter, 3, 30);
  step = diam/iter;
  redraw();
}

void paper(int in_val) {
  noStroke();
  for (int i = 0; i<width-1; i+=2) {
    for (int j = 0; j<height-1; j+=2) {
      fill(random(85-10, 85+10), in_val);
      rect(i, j, 2, 2);
    }
  }

  for (int i = 0; i<30; i++) {
    fill(random(40, 60), random(in_val*2.5, in_val*3));
    rect(random(0, width-2), random(0, height-2), random(1, 3), random(1, 3));
  }
}
