/**
 * rgbp (rgb petri, pixel fungus)
 * jeremyawon.info
 * jeremy awon, (c) all rights reserved.
 **/
 
/**
 * left click to reseed
 * left click and hold to affect
 * right click and hold to adjust radius
 **/
 
import java.util.Vector;

color bgColor = color(248,248,248);

Grid g;



float mpTS;
int mrX, mrY;

void setup() {
  size(500,500);
  smooth();

  //randomSeed(2);

  g = new Grid();

  cmd_state = new cmdState();
  cmd_state.setCmd_initialize();

  background(255);

  g.Initialize((int)random(width*0.25,width*0.75),(int)random(height*0.25,height*0.75),color(255,0,0));
  cmd_state.setCmd_none();
}

class cmdState {
  final static int CMD_NONE = -1;
  final static int CMD_INITIALIZE = 0;
  final static int CMD_FIELD_EFFECT = 1;
  final static int CMD_ADJUST_FIELD = 2;

  int state;

  cmdState() {
    state = -1;
  }

  void setCmd_none() { 
    state = CMD_NONE; 
  }
  void setCmd_initialize() { 
    state = CMD_INITIALIZE; 
  }
  void setCmd_field_effect() { 
    state = CMD_FIELD_EFFECT; 
  }
  void setCmd_adjust_field() { 
    state = CMD_ADJUST_FIELD; 
  }

  boolean isCmd_none() { 
    return (state==CMD_NONE); 
  }
  boolean isCmd_initialize() { 
    return (state==CMD_INITIALIZE); 
  }
  boolean isCmd_field_effect() { 
    return (state==CMD_FIELD_EFFECT); 
  }
  boolean isCmd_adjust_field() { 
    return (state==CMD_ADJUST_FIELD); 
  }
}

cmdState cmd_state;

void mousePressed() {
  mpTS = millis();

  if(mouseButton==LEFT) {
    cmd_state.setCmd_field_effect();
  } 
  else if(mouseButton==RIGHT) {
    cmd_state.setCmd_adjust_field();
  }
}

void mouseReleased() {
  if((millis()-mpTS)<1000) {
    cmd_state.setCmd_initialize();
    mrX = mouseX;
    mrY = mouseY;
  } 
  else if(cmd_state.isCmd_field_effect()) {
    cmd_state.setCmd_none();
  } 
  else if(cmd_state.isCmd_adjust_field()) {
    cmd_state.setCmd_none();
  }
}

void keyPressed() {
  println("rgbp (rgb petri, pixel fungus) - created by jeremy awon, jeremyawon.info, contact@jeremyawon.info, jeremy.awon@gmail.com");
}

void draw() {
  if(cmd_state.isCmd_initialize()) {
    if(g.getState(mrX,mrY)) {
      g.Initialize(mouseX,mouseY, g.getColor(mrX,mrY));
    }
    else {
      g.Initialize(mouseX,mouseY, color(255,0,0));
    }
    cmd_state.setCmd_none();
  } 
  else if(cmd_state.isCmd_field_effect()) {
    g.Step(g.sayCmd_field_effect());
  } 
  else if(cmd_state.isCmd_adjust_field()) {
    g.Step(g.sayCmd_adjust_field());
  } 
  else if(cmd_state.isCmd_none()) {
    g.Step(g.sayCmd_none());
  }
}

class Grid {
  final int[][] offset = new int[][]{{-1, -1},
                                     {-1,  0},
                                     {-1,  1},
                                     { 0, -1},
                                     { 0,  1},
                                     { 1, -1},
                                     { 1,  0},
                                     { 1,  1}};
  
  PImage _capture;
  int _direction[];
  boolean _state[];
  color _color[];
  Vector _perimeter;

  float _controlX, _controlY, _controlR, _controlDR;
  float _mutationMin, _mutationMax;
  float _approachFactor;
  float _growth, _limit;

  Grid() {
    _direction = new int[width*height];
    _state = new boolean[width*height];
    _color = new color[width*height];
    _capture = new PImage(width, height);
    _controlX = width/2;
    _controlY = height/2;
    _controlR = 80;
    
    _mutationMin = -4;
    _mutationMax = 5;
    _approachFactor = 0.4;
    _growth = 0.2;
    _limit = 4000;
    
    //String param_mmin = param("mmin");
    //String param_mmax = param("mmax");
    //String param_approach = param("a");
    //String param_growth = param("growth");
    //String param_limit = param("limit");
    String param_mmin = "9";
    String param_mmax = "10";
    String param_approach = "10.0";
    String param_growth = "100.1";
    String param_limit = "100";
    
    
    if(param_mmin!=null) {
      _mutationMin = float(param_mmin);
    }
    if(param_mmax!=null) {
      _mutationMax = float(param_mmax);
    }
    if(param_approach!=null) {
      _approachFactor = float(param_approach);
      _approachFactor = max(0,min(1,_approachFactor));
    }
    if(param_growth!=null) {
      _growth = float(param_growth);
      _growth = max(0,min(1,_growth));
    }
    if(param_limit!=null) {
      _limit = float(param_limit);
      _limit = max(0,_limit);
    }
  }

  void Initialize(int x, int y, color cc) {
    background(bgColor);
    
    _perimeter = new Vector((width+height)*3);
    
    for(int i=0;i<width*height;i++) {
      _direction[i] = (int)random(0,8);
    }
    
    for(int i=0;i<width;i++) {
      for(int j=0;j<height;j++) {
        setState(i,j,false);
      }
    }

    setState(x,y,true);
    setColor(x,y,cc);

    _controlX = mouseX;
    _controlY = mouseY;
    _controlDR = 0;
    _caughtImage = false;
  }

  static final int CMD_FIELD_EFFECT = 0;
  static final int CMD_ADJUST_FIELD = 1;
  static final int CMD_NONE = 2;

  int sayCmd_field_effect() { 
    return CMD_FIELD_EFFECT; 
  }
  int sayCmd_adjust_field() { 
    return CMD_ADJUST_FIELD; 
  }
  int sayCmd_none() { 
    return CMD_NONE; 
  }

  boolean _caughtImage;

  void Step(int cmd) {
    int perimeterSize = _perimeter.size();

    if(perimeterSize==0) {
      return;
    }
    
    _controlX += 0.2*(mouseX - _controlX);
    _controlY += 0.2*(mouseY - _controlY);

    if(_caughtImage) {
      if(cmd!=sayCmd_adjust_field()) {
        _caughtImage = false;
      }

      background(_capture);
    }

    if(cmd==sayCmd_adjust_field()) {
      if(!_caughtImage) {
        _capture = get();
        _caughtImage = true;
      }
      
      float rotation = (((pmouseX-_controlX)*(mouseY-_controlY))-((pmouseY-_controlY)*(mouseX-_controlX)))*0.0005;

      _controlDR += rotation;
      _controlDR *= 0.8;
      _controlR += _controlDR;

      float rStep = abs((float)(2*PI)/((int)(_controlR*0.2)));

      if(rStep>0) {
        stroke(0);
        noFill();
        ellipse(_controlX, _controlY, _controlR*2, _controlR*2);
        fill(0);
        for(float r=0;r<=(2*PI);r+=rStep) {
          translate(_controlX,_controlY);
          rotate(r);
          translate(0,abs(_controlR));
          ellipse(0,_controlR*_approachFactor,4,4);
          translate(0,-abs(_controlR));
          rotate(-r);
          translate(-_controlX,-_controlY);
        }

        noStroke();
      }
      return;
    }

    float crS = _controlR*_controlR;
    float crA = (abs(_controlR)+(_controlR*_approachFactor));
    crA *= crA;
    float crD = crA - crS;
    
    float nBstep = min((perimeterSize*_growth),_limit);
    
    for(int p=0;p<nBstep;p++) {
      int selection = (int)random(0,_perimeter.size());

      Position current = (Position)_perimeter.get(selection);

      if(cmd==sayCmd_field_effect()) {
        float distance = distanceSquared(current._x,current._y,mouseX,mouseY);

        if(((crD>=0)==(distance<crA))&&(((distance-crS)/crD)<random(0,1))) {
          continue;
        }
      }

      int i = (int)current._x,
          j = (int)current._y;

      if(_state[i+(j*width)]) {
        float r = red(_color[i+(j*width)]) + random(_mutationMin,_mutationMax),
              g = green(_color[i+(j*width)]) + random(_mutationMin,_mutationMax),
              b = blue(_color[i+(j*width)]) + random(_mutationMin,_mutationMax);
        
        color next = color(r,g,b);
        
        for(int k=0;k<8;k++) {
          int dir = (_direction[i+(j*width)]+k)%8;
          
          if(!getState(i+offset[dir][0], j+offset[dir][1])) {
            setState(i+offset[dir][0], j+offset[dir][1],true);
            setColor(i+offset[dir][0], j+offset[dir][1],next);
            break;
          }
        }
      }
    }
    
    for(int i=0;i<_perimeter.size();i++) {
      Position current = (Position)_perimeter.get(i);
      
      int x = (int)current._x,
          y = (int)current._y;
      
      if(getState(x+1,y+1)&&
         getState(x,y+1)&&
         getState(x-1,y+1)&&
         getState(x+1,y)&&
         getState(x-1,y)&&
         getState(x+1,y-1)&&
         getState(x,y-1)&&
         getState(x-1,y-1)) {
        _perimeter.remove(current);
        i--;
      }
    }
  }

  void setState(int x, int y, boolean value) {
    int nX = max(0,min(width-1,x)),
        nY = max(0,min(height-1,y));
    
    _state[nX+(nY*width)] = value;

    if(value) {
      _perimeter.add(new Position(nX,nY));
    }
  }

  boolean getState(int x, int y) {
    int nX = max(0,min(width-1,x)),
        nY = max(0,min(height-1,y));

    return _state[nX+(nY*width)];
  }

  void setColor(int x, int y, color value) {
    int nX = max(0,min(width-1,x)),
        nY = max(0,min(height-1,y));

    _color[nX+(nY*width)] = value;

    set(nX,nY,_color[nX+(nY*width)]);
  }

  color getColor(int x, int y) {
    int nX = max(0,min(width-1,x)),
        nY = max(0,min(height-1,y));

    return _color[nX+(nY*width)];
  }

}

float distanceSquared(float ax, float ay, float bx, float by) {
  return (((ax-bx)*(ax-bx))+((ay-by)*(ay-by)));
}

class Position {
  float _x;
  float _y;
  
  Position(float x, float y) {
    _x = x;
    _y = y;
  }
}