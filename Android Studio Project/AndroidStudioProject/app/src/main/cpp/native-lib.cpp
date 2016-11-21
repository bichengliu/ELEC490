#include <jni.h>
#include <string>
#include <sstream>

extern "C"
int addtwonum(int a, int b);


extern "C"
jstring
Java_elec490_airphone_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::ostringstream val;
    val << addtwonum(4,2);
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