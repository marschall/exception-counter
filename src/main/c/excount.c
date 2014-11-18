#include <jni.h>
#include <jvmti.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include "fix_atomics.h"
#include "com_github_marschall_excount_ExceptionCounter.h"

/*
 * Various JVMTI links
 * http://stackoverflow.com/questions/23561555/java-exceptions-counter-on-jvm-hotspot#23567931
 * http://docs.oracle.com/javase/7/docs/platform/jvmti/jvmti.html
 * https://blogs.oracle.com/kto/entry/using_vm_agents
 * https://plumbr.eu/blog/migrating-from-javaagent-to-jvmti-our-experience
 * C atomics links
 * http://en.cppreference.com/w/c/atomic
 * JNI links
 * http://nerds-central.blogspot.ch/2011/06/taking-pain-out-of-jni.html
 * http://developer.android.com/training/articles/perf-jni.html
 */

/*
 * Missing compiler support
 * _Atomic int32_t count = ATOMIC_VAR_INIT(0);
 * static atomic_int_fast32_t count;
 */
_Atomic(int32_t) count = ATOMIC_VAR_INIT(0);


JNIEXPORT jint JNICALL
  Java_com_github_marschall_excount_ExceptionCounter_getCount(JNIEnv *env,
                                                              jobject thisObj) {
    return atomic_load(&count);
}

JNIEXPORT jint JNICALL
  Java_com_github_marschall_excount_ExceptionCounter_clearAndGetCount(JNIEnv *env,
                                                                      jobject thisObj) {
    return atomic_exchange(&count, 0);
}


void JNICALL ExceptionCallback(jvmtiEnv *jvmti, JNIEnv *env, jthread thread,
                               jmethodID method, jlocation location, jobject exception,
                               jmethodID catch_method, jlocation catch_location) {
    atomic_fetch_add(&count , 1);
}


JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
    jvmtiEnv* jvmti;
    jvmtiEventCallbacks callbacks;
    jvmtiCapabilities capabilities;

    if ((*vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1_0) != JNI_OK) {
        fprintf(stderr, "GetEnv failed\n");
        return -1;
    }

    memset(&capabilities, 0, sizeof(capabilities));
    capabilities.can_generate_exception_events = 1;
    if ((*jvmti)->AddCapabilities(jvmti, &capabilities) != JVMTI_ERROR_NONE) {
        fprintf(stderr, "AddCapabilities failed\n");
        return -1;
    }

    memset(&callbacks, 0, sizeof(callbacks));
    callbacks.Exception = ExceptionCallback;
    if ((*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks)) != JVMTI_ERROR_NONE) {
        fprintf(stderr, "SetEventCallbacks failed\n");
        return -1;
    }
    if ((*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_EXCEPTION, NULL) != JVMTI_ERROR_NONE) {
        fprintf(stderr, "SetEventNotificationMode failed\n");
        return -1;
    }
    return 0;
}

/* JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* vm, char *options, void *reserved) */
JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm) {
    jvmtiEnv* jvmti;
    jvmtiCapabilities capabilities;

    if ((*vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1_0) != JNI_OK) {
        fprintf(stderr, "GetEnv failed\n");
        return;
    }

    memset(&capabilities, 0, sizeof(capabilities));
    capabilities.can_generate_exception_events = 1;
    if ((*jvmti)->RelinquishCapabilities(jvmti, &capabilities) != JVMTI_ERROR_NONE) {
        fprintf(stderr, "RelinquishCapabilities failed\n");
    }
}

