#include <jni.h>
#include <stdio.h>
#include <string>
#include <sstream>
#include <math.h>
#include <stdlib.h>


int size_fft = 8;
double pi = 4*atan(1);
extern "C"
int addtwonum(int a, int b);

extern "C"
double multpi(int a, int b);

extern "C"
void my_fft(double *fft_out, double *in, int size_in);

extern "C"
jstring
Java_elec490_airphone_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::ostringstream val;
    int i,j;
    double re,imag,pwr_re,pwr_imag;
    double some_array[8] = {1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0};
    double fft_out_arr[8] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    my_fft(fft_out_arr, some_array, 8);

    for(i = 0; i < 8; i++)
        val << fft_out_arr[i] << " ";
    std::string hello = "Hello from C++, the output is: " + val.str();
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_elec490_airphone_MainActivity_somefunc(JNIEnv *env, jobject instance, jint a, jint b) {
    jint c = a + b;
    return c;
}

extern "C"
int addtwonum(int a, int b)
{
    int c = a + b;
    return c;
}

extern "C"
double multpi(int a, int b)
{
    double c = addtwonum(a,b)*3.14;
    return c;
}

extern "C" //fft without the imaginary component
void my_fft(double *fft_out, double *in, int size_in){
    int i,j;
    double re,imag,pwr_re,pwr_imag;
    for(i = 0; i < size_fft; i++) {
        re = 0;
        imag = 0;
        for (j = 0; j < 8; j++) {
            re = re + in[j] * cos(2.0 * pi * (i*1.0) * (j*1.0) / 8.0);
            imag = imag + (-1.0 * in[j] * sin(2.0 * pi * (i*1.0) * (j*1.0)  / 8.0));
            pwr_re = fabs(re) * fabs(re);
            pwr_imag = fabs(imag) * fabs(imag);
        }
        fft_out[i] = pwr_re + pwr_imag;
    }
}