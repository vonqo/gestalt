int l, n;
float t;

ArrayList points;

public void setup() {
    size(1000, 1000, P3D);
    background(255);

    noStroke();

    l = 6; //Spacing between points
    n = 100; //Number of points
    noLoop();
}

public void draw() {
    t = 10;

    background(200);

    camera(width/2, height-100, 200, width/2, height/2, 0, 0, 1, 0);
    lights();

    points = new ArrayList();

    translate(width/2, height/2, 0);
    //rotateX(((float)mouseY/(float)height));
    //rotateZ(TWO_PI-(((float)mouseX/(float)width)*TWO_PI+PI));
    
    //rotateX(((float)mouseY/(float)height));
    //rotateZ(TWO_PI-(((float)mouseX/(float)width)*TWO_PI+PI));

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            int x2 = (l*i+l/2-l*n/2);
            int y2 = (l*j+l/2-l*n/2);

            float distance = dist(0, 0, x2, y2);
            float z = exp(-distance/50)*sin(((distance)/10)-t)*25;

            points.add(new PVector(x2, y2, z));
        }
    }

    noFill();
    stroke(100);

    for (int i = 0; i < n-1; i++) {
        beginShape(QUAD_STRIP);
        
        
        
        PVector v;
        for (int j = 0; j < n-1; j++) {
            v = (PVector)points.get(j+n*i);
            stroke(127+v.z*10, 127+v.z*5, 127);
            vertex(v.x, v.y, v.z);
            v = (PVector)points.get(j+n*(i+1));
            vertex(v.x, v.y, v.z);
        }
        endShape();
    }

    fill(255, 0, 0);
    noStroke();
}
