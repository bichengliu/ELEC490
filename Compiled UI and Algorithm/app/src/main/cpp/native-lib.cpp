#include <jni.h>
#include <stdio.h>
#include <string>
#include <sstream>
#include <math.h>
#include <stdlib.h>


int size_fft = 8;
int thresh = 2;
double alpha = 1.05;
double pi = 4*atan(1);
extern "C"
int addtwonum(int a, int b);

extern "C"
double multpi(int a, int b);

extern "C"
void my_fft(double *fft_out, double *in, int size_in);

extern "C"
void nrg_sum(double *sum_array, double *in, int size_in);

extern "C"
void nrg_avg(double *avg_array, double *in, int size_in, int total_crunch);

extern "C"
int decision(double *avg_array, double *in, int size_in);

extern "C"
jstring
Java_elec490_airphone_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::ostringstream val;
    int i,j;
    double data[] = {1, 1, 1, 1, 0, 0, 0, 0};
    double data2[] = {2, 2, 2, 2, 1, 1, 1, 1};
    double data3[] = {0,0,0,0,1,1,1,1};
    double in[] = {0, 0, 0, 0, 0, 0, 0, 0}; // this holds fft output
    double nrg_array[] = {0,0,0,0,0,0,0,0}; // this holds all the sum of all the energy at the corresponding samples
    double nrg_avg_array[] = {0,0,0,0,0,0,0,0}; // this holds the average energy of all the samples
    double outp[] = {0,0,0,0,0,0,0,0};


    my_fft(in, data, 8);
    nrg_sum(nrg_array, in, 8);
    nrg_avg(nrg_avg_array, nrg_array, 8, 1);
    j = decision(nrg_avg_array, in, 8);
    val << j << " ";

    my_fft(in, data2, 8);
    nrg_sum(nrg_array, in, 8);
    nrg_avg(nrg_avg_array, nrg_array, 8, 2);
    j = decision(nrg_avg_array, in,8);
    val << j << " ";

    my_fft(in, data3, 8);
    nrg_sum(nrg_array, in, 8);
    nrg_avg(nrg_avg_array, nrg_array, 8, 3);
    j = decision(nrg_avg_array, in,8);
    val << j << " ";
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
        for (j = 0; j < size_fft; j++) {
            re = re + in[j] * cos(2.0 * pi * (i*1.0) * (j*1.0) / size_fft);
            imag = imag + (-1.0 * in[j] * sin(2.0 * pi * (i*1.0) * (j*1.0)  / size_fft));
            pwr_re = fabs(re) * fabs(re);
            pwr_imag = fabs(imag) * fabs(imag);
        }
        fft_out[i] = pwr_re + pwr_imag;
    }
}

void nrg_sum(double *sum_array, double *in, int size_in) { // finds the sum of the energy components and stores them
    int i;
    for(i = 0; i < size_in; i++){sum_array[i] = sum_array[i] + in[i];}
}

void nrg_avg(double *avg_array, double *in, int size_in, int total_crunch){ // finds the average energy in each spectrum
    int i;
    for(i = 0; i < size_in; i++){avg_array[i] = in[i]/total_crunch;}
}

int decision(double *avg_array, double *in, int size_in){ // makes decision based on the current energy
    int i;
    int ctr = 0;
    for(i = 0; i < size_in; i++){if(in[i] > avg_array[i]*alpha){ctr++;}}
    if(ctr > thresh)
        return 1;
    else
        return 0;

}