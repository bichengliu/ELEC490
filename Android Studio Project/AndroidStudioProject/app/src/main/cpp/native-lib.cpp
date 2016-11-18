#include <jni.h>
#include <string>
#include <stdio.h>

extern "C"
jstring
Java_elec490_airphone_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++, the output is: ";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_elec490_airphone_MainActivity_somefunc(JNIEnv *env, jobject instance, jint a, jint b) {
    jint c = a + b;
    return c;
}