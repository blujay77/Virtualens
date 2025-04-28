#pragma once

#include "opencv2/core.hpp"
#include "opencv2/imgproc.hpp"

using namespace cv;

void nGrayscale(Mat* matAddr, int cvType);
void nInvert(Mat* matAddr);
void nAddition(Mat* matAddr, float brightnessChange);
void nMultiplication(Mat* matAddr, float brightnessChange);
void nExponentiation(Mat* matAddr, float exponent);
void nSobel(Mat* matAddr, int kernel, int direction, int imageDepth);
void nScharr(Mat* matAddr, int imageDepth);
void nQuantization(Mat* matAddr, int colorAmount, int imageDepth);
void nThreshold(Mat* matAddr, float threshold);
void nBlur(Mat* matAddr, int kernel);
void nDoG(Mat* matAddr, int kernel1, int kernel2);