#include "imagefilterapp.h"
#include "opencv2/core/hal/hal.hpp"

void nGrayscale(Mat* matAddr, int cvType) {
    Mat& mat = *matAddr;
    //cvtColor(mat, mat, COLOR_RGBA2GRAY);
    //cvtColor(mat, mat, COLOR_GRAY2RGBA);

    if (cvType == CV_8U) {
        MatIterator_<Vec4b> it, end;

        for (it = mat.begin<Vec4b>(), end = mat.end<Vec4b>(); it != end; it++) {
            float value = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;

            for (int i = 0; i < 3; i++) {
                (*it)[i] = value;
            }
        }
    }
    else if (cvType == CV_32F) {
        MatIterator_<Vec4f> it, end;

        for (it = mat.begin<Vec4f>(), end = mat.end<Vec4f>(); it != end; it++) {
            float value = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;

            for (int i = 0; i < 3; i++) {
                (*it)[i] = value;
            }
        }
    }
    /*
    switch (cvType) {
        case CV_8U: {
            MatIterator_<Vec4b> it, end;

            for (it = mat.begin<Vec4b>(), end = mat.end<Vec4b>(); it != end; it++) {
                float value = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;

                for (int i = 0; i < 3; i++) {
                    (*it)[i] = value;
                }
            }
        }
        case CV_32F: {
            MatIterator_<Vec4f> it, end;

            for (it = mat.begin<Vec4f>(), end = mat.end<Vec4f>(); it != end; it++) {
                float value = ((*it)[0] + (*it)[1] + (*it)[2]) / 3;

                for (int i = 0; i < 3; i++) {
                    (*it)[i] = value;
                }
            }
        }
    }*/
}

void nInvert(Mat* matAddr) {
    Mat& mat = *matAddr;
    subtract(255, mat, mat);
}

void nAddition(Mat* matAddr, float brightnessChange) {
    Mat& mat = *matAddr;
    brightnessChange = round(brightnessChange * 255);

    add(mat, brightnessChange, mat);
}

void nMultiplication(Mat* matAddr, float brightnessChange) {
    Mat& mat = *matAddr;
    mat = mat * brightnessChange;
}

void nExponentiation(Mat* matAddr, float exponent) {
    Mat& mat = *matAddr;

    pow(mat, exponent, mat);
}

// direction: XY == 0 ; X == 1 ; Y == 2
void nSobel(Mat* matAddr, int kernel, int direction, int imageDepth) {
    Mat& mat = *matAddr;

    switch (direction) {
        case 0: {
            Mat sobelX = Mat(mat.rows, mat.cols, imageDepth);
            // mat acts as sobelY to save memory

            Sobel(mat, sobelX, imageDepth, 1, 0, kernel);
            Sobel(mat, mat, imageDepth, 0, 1, kernel);

            pow(sobelX, 2, sobelX);
            pow(mat, 2, mat);

            add(sobelX, mat, mat);
            break;
        }
        case 1: {
            Sobel(mat, mat, imageDepth, 1, 0, kernel);
            //pow(mat, 2, mat);
        }
        case 2: {
            Sobel(mat, mat, imageDepth, 0, 1, kernel);
            //pow(mat, 2, mat);
        }
        default:
            return;
    }
}

void nScharr(Mat* matAddr, int imageDepth) {
    Mat& mat = *matAddr;

    Mat scharrX = Mat(mat.rows, mat.cols, imageDepth);

    Scharr(mat, scharrX, imageDepth, 1, 0);
    Scharr(mat, mat, imageDepth, 0, 1);

    pow(scharrX, 2, scharrX);
    pow(mat, 2, mat);

    add(scharrX, mat, mat);
}

void nQuantizationLol(Mat* matAddr, int colorAmount) {
    /*
     * get blurred image
     * convert to hsv
     * get min, max hue
     * divide into colorAmount hues, leaving buffer at start and end
     * assign each pixel of original image to the hue closest to it
     * convert to rgb
     */

    Mat& mat = *matAddr;

    Mat blurredMat = Mat(mat.rows, mat.cols, CV_8U);

    int kernelSize = (int) round(((mat.rows + mat.cols) / 2.0) / 20);
    if (kernelSize % 2 != 1 ) {
        kernelSize++;
    }

    GaussianBlur(mat, blurredMat, Size_<int>(kernelSize, kernelSize), 0);

    cvtColor(blurredMat, blurredMat, COLOR_RGBA2RGB);
    cvtColor(blurredMat, blurredMat, COLOR_RGB2HSV);

    Mat hue = Mat(blurredMat.rows, blurredMat.cols, blurredMat.type());
    extractChannel(blurredMat, hue, 0);

    double minHue = 0;
    double maxHue = 0;
    minMaxLoc(hue, &minHue, &maxHue);
    int hueDiff = maxHue - minHue;

    int hueToColor[360];

    int count = 0;
    for (int i = 0; i < minHue; i++) {
        hueToColor[count] = minHue;
        count++;
    }
    for (int i = 0; i < colorAmount; i++) {
        int maxIndex = minHue + (hueDiff * ((float)(i + 1) / colorAmount));
        int color = minHue + (hueDiff * (float)((float)((i * 2) + 1) / (colorAmount * 2)));
        while (count <= maxIndex) {
            hueToColor[count] = color;
            count++;
        }
    }
    for (; count < 360; count++) {
        hueToColor[count] = maxHue;
    }

    ///////////////////////////////
    Mat sat = Mat(blurredMat.rows, blurredMat.cols, blurredMat.type());
    extractChannel(blurredMat, sat, 1);

    double minSat = 0;
    double maxSat = 0;
    minMaxLoc(sat, &minSat, &maxSat);
    int satDiff = maxSat - minSat;

    int satToSaturation[256];

    int satCount = 0;
    for (int i = 0; i < minSat; i++) {
        satToSaturation[satCount] = minSat;
        satCount++;
    }
    for (int i = 0; i < colorAmount; i++) {
        int maxIndex = minSat + (satDiff * ((float)(i + 1) / colorAmount));
        int color = minSat + (satDiff * (float)((float)((i * 2) + 1) / (colorAmount * 2)));
        while (satCount <= maxIndex) {
            satToSaturation[satCount] = color;
            satCount++;
        }
    }
    for (; satCount < 256; satCount++) {
        satToSaturation[satCount] = maxSat;
    }

    Mat val = Mat(blurredMat.rows, blurredMat.cols, blurredMat.type());
    extractChannel(blurredMat, val, 2);

    double minVal = 0;
    double maxVal = 0;
    minMaxLoc(val, &minVal, &maxVal);
    int valDiff = maxVal - minVal;

    int valToValue[256];

    int valCount = 0;
    for (int i = 0; i < minVal; i++) {
        valToValue[valCount] = minVal;
        valCount++;
    }
    for (int i = 0; i < colorAmount; i++) {
        int maxIndex = minVal + (valDiff * ((float)(i + 1) / colorAmount));
        int color = minVal + (valDiff * (float)((float)((i * 2) + 1) / (colorAmount * 2)));
        while (valCount <= maxIndex) {
            valToValue[valCount] = color;
            valCount++;
        }
    }
    for (; valCount < 256; valCount++) {
        valToValue[valCount] = maxVal;
    }
    ////////////

    cvtColor(mat, mat, COLOR_RGBA2RGB);
    cvtColor(mat, mat, COLOR_RGB2HSV);

    MatIterator_<Vec4b> it, end;

    for (it = mat.begin<Vec4b>(), end = mat.end<Vec4b>(); it != end; it++) {
        (*it)[0] = hueToColor[(*it)[0]];
        (*it)[1] = satToSaturation[(*it)[1]];
        (*it)[2] = valToValue[(*it)[2]];
    }

    cvtColor(mat, mat, COLOR_HSV2RGB);
    cvtColor(mat, mat, COLOR_RGB2RGBA);
}

void nQuantizationBinary(Mat* matAddr, int colorAmount) {
    Mat&  mat = *matAddr;
    mat.convertTo(mat, CV_8U);

    MatIterator_<Vec4b> it, end;

    for (it = mat.begin<Vec4b>(), end = mat.end<Vec4b>(); it != end; it++) {
        for (int i = 0; i < 3; i++) {
            if ((*it)[i] < 128) {
                (*it)[i] = 0;
            }
            else {
                (*it)[i] = 155;
            }
        }
    }
}

void nQuantization(Mat* matAddr, int colorAmount, int imageDepth) {
    Mat& mat = *matAddr;
    if (imageDepth != CV_8U) {
        mat.convertTo(mat, CV_8U);
    }
    cvtColor(mat, mat, COLOR_RGBA2RGB);

    Mat rgb[3] = { Mat(mat.rows, mat.cols, mat.type()) };
    split(mat, rgb);

    int quantizedTable[3][256];

    for (int i = 0; i < 3; i++) {
        double min = 0;
        double max  = 255;
        minMaxLoc(rgb[i], &min, &max);

        int diff = max - min;

        int count = min;
        for (int j = 0; j < colorAmount; j++) {
            int maxIndex = min + (diff * ((float)(j + 1) / colorAmount));
            int color = min + (diff * (float)((float)(j * 2 + 1) / (colorAmount * 2)));

            while (count <= maxIndex) {
                quantizedTable[i][count] = color;
                count++;
            }
        }
    }

    MatIterator_<Vec3b> it, end;

    for (it = mat.begin<Vec3b>(), end = mat.end<Vec3b>(); it != end; it++) {
        (*it)[0] = quantizedTable[0][(*it)[0]];
        (*it)[1] = quantizedTable[1][(*it)[1]];
        (*it)[2] = quantizedTable[2][(*it)[2]];
    }

    cvtColor(mat, mat, COLOR_RGB2RGBA);

    if (imageDepth != CV_8U) {
        mat.convertTo(mat, CV_32F);
    }
}

void nThreshold(Mat* matAddr, float threshold) {
    Mat& mat = *matAddr;

    cv::threshold(mat, mat, threshold * 255, 255, THRESH_BINARY);
}

void nBlur(Mat* matAddr, int kernel) {
    Mat& mat = *matAddr;

    GaussianBlur(mat, mat, Size_<int>(kernel, kernel), 0);
}

void nDoG(Mat* matAddr, int kernel1, int kernel2) {
    Mat& mat = *matAddr;

    Mat mat2 = Mat(mat.rows, mat.cols, mat.type());

    if ( kernel2 != 0 ) {
        GaussianBlur(mat, mat2, Size_<int>(kernel2, kernel2), 0);
    }
    else {
        mat.copyTo(mat2);
    }

    if (kernel1 != 0) {
        GaussianBlur(mat, mat, Size_<int>(kernel1, kernel1), 0);
    }

    subtract(mat, mat2, mat);
}