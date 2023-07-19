let pg;
let album;
let bars;
let font;
let font2;

function preload() {
  album = loadImage('assets/album.jpg');
  bars = loadImage('assets/bars.png');
  font = loadFont('assets/JetBrainsMono-Regular.ttf');
  font2 = loadFont('assets/RobotoMono-Regular.ttf');
}

// 300 + 100 + 1122
function setup() {
  createCanvas(1100, 1522);
  pg = createGraphics(1100, 1522);
  background(0);
  noLoop();
}

function draw() {
  smooth();
  pg.background(0);
  pg.smooth();
  
  pg.image(album,0,0,height,height);
  pg.filter(BLUR, 35);
  
  drawGrid(pg);
  
  pg.textFont(font);
  pg.fill(210);
  pg.textSize(42);
  pg.text('Metro Boomin', 400, 100);
  pg.fill(255);
  pg.textSize(52);
  pg.text('METRO BOOMIN PRESENTS \nSPIDER-MAN: ACROSS THE \nSPIDER-VERSE', 397, 170);
  pg.textSize(30);
  pg.fill(210);
  pg.text('color//note//code >', 400, 346);
  
  pg.drawingContext.shadowOffsetX = 0;
  pg.drawingContext.shadowOffsetY = 0;
  pg.drawingContext.shadowBlur = 20;
  pg.drawingContext.shadowColor = 'rgba(0, 0, 0, 0.65)';
  pg.image(bars,50,350);
  
  image(pg, 0, 0);
  granulateWithSet(30);
  
  drawingContext.shadowOffsetX = 0;
  drawingContext.shadowOffsetY = 0;
  drawingContext.shadowBlur = 70;
  drawingContext.shadowColor = 'rgba(0, 0, 0, 0.75)';
  image(album,50,50,300,300);
}

function drawGrid(pg) {
  pg.fill(128,128,128,50);
  pg.textSize(22);
  pg.textFont();
  for(let i = 0; i < 50; i++) {
    for(let e = 0; e < 50; e++) {
      pg.text('✕', i * 50, e * 50);
    }
  }
}

function granulateWithSet(amount) {
    loadPixels();
    for (let x = 0; x < width; x++) {
        for (let y = 0; y < height; y++) {
            const pixel = get(x, y);
            const granulatedColor = color(
                pixel[0] + random(-amount, amount),
                pixel[1] + random(-amount, amount),
                pixel[2] + random(-amount, amount)
                // comment in, if you want to granulate the alpha value
                // pixel[3] + random(-amount, amount),
            );
            set(x, y, granulatedColor);
        }
    }
    updatePixels();
}
