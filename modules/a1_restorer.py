## GESTALT - A_RESTORER 
## Restore Amou's expression images 

import cv2 as cv
import numpy as np

def rgbSave():
	img = cv.imread('exmpl.jpg',0)
	img = cv.cvtColor(img,cv.COLOR_GRAY2RGB)
	b,g,r = cv.split(img)
	img = cv.merge((b,g,r))
	cv.imwrite('filtered.jpg', img)

def applyClahe(img):
	clahe = cv.createCLAHE(clipLimit=3, tileGridSize=(8,8))
	return clahe.apply(img)

def equilizeHistogram(img):
	equ = cv.equalizeHist(img)
	return np.hstack((img,equ))

def restore(file):
	img = cv.imread(file,0)

	# img = applyClahe(img);
	# cv.imwrite('clahe.jpg', (img))

	kernel = np.ones((13,13),np.uint8)
	top_hat = cv.morphologyEx(img, cv.MORPH_BLACKHAT, kernel)
	img = cv.cvtColor(top_hat,cv.COLOR_GRAY2RGB)

	# b,g,r = cv.split(img)
	# img = cv.merge((b,g,r))
	# gray_image = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
	imagem = cv.bitwise_not(img)
	imagem = cv.fastNlMeansDenoisingColored(imagem,None,1,10,5,17)

	cv.imwrite(file, (imagem))

def extract(file):
	image = cv.imread(file);
	b = image.copy()
	# set green and red channels to 0
	b[:, :, 1] = 0
	b[:, :, 2] = 0
	cv.imwrite('b2.jpg', b)

	g = image.copy()
	# set blue and red channels to 0
	g[:, :, 0] = 0
	g[:, :, 2] = 0
	cv.imwrite('g2.jpg', g)

	r = image.copy()
	# set blue and green channels to 0
	r[:, :, 0] = 0
	r[:, :, 1] = 0
	cv.imwrite('r2.jpg', r)

	# img = cv.merge((b,g,r))
	# cv.imwrite('rgb.jpg', img)

# extract('filtered.jpg')
# rgbSave()
stack = [
	'angry.jpg',
	'cried.jpg',
	'wise.jpg',
	'bored.jpg',
	'smiled.jpg',
	'peaceful.jpg',
	'observing.jpg',
	'speculated.jpg',
	'disappointed.jpg',
	'excited.jpg'
]

for file in stack:
	print(file)
	restore(file)
