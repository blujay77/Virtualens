#include <jni.h>

#include "imagefilterapp.h"

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nGrayscale(JNIEnv* env,jobject /* this */,
    jlong matAddr,
    jint cvType
) {
    nGrayscale(reinterpret_cast<Mat *>(matAddr), cvType);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nInvert(JNIEnv* env,jobject /* this */,
   jlong matAddr
) {
    nInvert(reinterpret_cast<Mat *>(matAddr));
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nAddition(JNIEnv* env,jobject /* this */,
   jlong matAddr,
   jfloat brightnessChange
) {
    nAddition(reinterpret_cast<Mat *>(matAddr), brightnessChange);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nMultiplication(JNIEnv* env,jobject /* this */,
    jlong matAddr,
    jfloat brightnessChange
) {
    nMultiplication(reinterpret_cast<Mat *>(matAddr), brightnessChange);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nExponentiation(JNIEnv* env,jobject /* this */,
   jlong matAddr,
   jfloat exponent
) {
    nExponentiation(reinterpret_cast<Mat *>(matAddr), exponent);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nSobel(JNIEnv* env,jobject /* this */,
   jlong matAddr,
   jint kernel,
   jint direction,
   jint imageDepth
) {
    nSobel(reinterpret_cast<Mat *>(matAddr), kernel, direction, imageDepth);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nScharr(JNIEnv* env,jobject /* this */,
  jlong matAddr,
  jint imageDepth
) {
    nScharr(reinterpret_cast<Mat *>(matAddr), imageDepth);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nQuantization(JNIEnv* env,jobject /* this */,
    jlong matAddr,
    jint colorAmount,
    jint imageDepth
) {
    nQuantization(reinterpret_cast<Mat *>(matAddr), colorAmount, imageDepth);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nThreshold(JNIEnv* env,jobject /* this */,
     jlong matAddr,
     jfloat threshold
) {
    nThreshold(reinterpret_cast<Mat *>(matAddr), threshold);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nBlur(JNIEnv* env,jobject /* this */,
    jlong matAddr,
    jint kernel
) {
    nBlur(reinterpret_cast<Mat *>(matAddr), kernel);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_imagefilterapp_ImageProcessor_nDoG(JNIEnv* env,jobject /* this */,
    jlong matAddr,
    jint kernel1,
    jint kernel2
) {
    nDoG(reinterpret_cast<Mat *>(matAddr), kernel1, kernel2);
}