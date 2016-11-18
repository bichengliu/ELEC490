#include <jni.h>
#include <string>
#include <stdio.h>

extern "C"
jstring
Java_elec490_airphone_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++, the output is: " + 3;
    return env->NewStringUTF(hello.c_str());
}

