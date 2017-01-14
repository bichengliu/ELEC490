//
// Created by Branden on 2017-01-12.
//

// in Java please make sure to have a     public native int algorithm(float data[], float alpha); somewhere


#include <jni.h>
#include <stdio.h>
#include <string>
#include <sstream>
#include <math.h>
#include <stdlib.h>


int size_fft = 8;
int thresh = 3;
double pi = 4*atan(1);

extern "C"
void my_fft(double *fft_out, double *in, int size_in);

extern "C"
void nrg_sum(double *sum_array, double *in, int size_in);

extern "C"
void nrg_avg(double *avg_array, double *in, int size_in, int total_crunch);

extern "C"
int decision(double *avg_array, double *in, int size_in, double alpha);

extern "C"
jint
Java_elec490_airphone_MainActivity_algorithm( JNIEnv* env, double data[], double alpha)
{
    double fftoutput[] = {0, 0, 0, 0, 0, 0, 0, 0}; // this holds fft output
    double nrg_array[] = {0,0,0,0,0,0,0,0};
    double nrg_avg_array[] = {100,100,100,100,100,100,100,100};
    jint j;
    my_fft(fftoutput, data, 8); //find fft energy directly
    nrg_sum(nrg_array, fftoutput, 8); // find energy sums
    nrg_avg(nrg_avg_array, nrg_array, 8, 1); // hold historical average
    j = decision(nrg_avg_array, fftoutput, 8,alpha); // returns decision from algorithm
    return j;
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

int decision(double *avg_array, double *in, int size_in, double alpha){ // makes decision based on the current energy
    int i;
    int ctr = 0;
    for(i = 0; i < size_in; i++){if(in[i] > avg_array[i]*alpha){ctr++;}}
    if(ctr > thresh)
        return 1;
    else
        return 0;

}