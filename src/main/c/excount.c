#include <jni.h>
#include <jvmti.h>
#include <string.h>
#include <stdio.h>

// http://stackoverflow.com/questions/23561555/java-exceptions-counter-on-jvm-hotspot#23567931
// http://docs.oracle.com/javase/7/docs/platform/jvmti/jvmti.html
// https://blogs.oracle.com/kto/entry/using_vm_agents
// https://plumbr.eu/blog/migrating-from-javaagent-to-jvmti-our-experience
// interlocked functions on windows
// https://developer.apple.com/library/mac/documentation/cocoa/Conceptual/Multithreading/ThreadSafety/ThreadSafety.html

static volatile int count = 0;

void JNICALL ExceptionCallback(jvmtiEnv* jvmti, JNIEnv* env, jthread thread,
                               jmethodID method, jlocation location, jobject exception,
                               jmethodID catch_method, jlocation catch_location) {
    __sync_fetch_and_add(&count , i);
}


JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* vm, char* options, void* reserved) {
    jvmtiEnv* jvmti;
    jvmtiEventCallbacks callbacks;
    jvmtiCapabilities capabilities;

    (*vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1_0);

    memset(&capabilities, 0, sizeof(capabilities));
    capabilities.can_generate_exception_events = 1;
    (*jvmti)->AddCapabilities(jvmti, &capabilities);

    memset(&callbacks, 0, sizeof(callbacks));
    callbacks.Exception = ExceptionCallback;
    (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
    (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_EXCEPTION, NULL);

    return 0;
}

