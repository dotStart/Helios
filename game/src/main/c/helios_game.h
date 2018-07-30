#ifndef HELIOSGAME_HELIOS_GAME_H
#define HELIOSGAME_HELIOS_GAME_H

#include <jni.h>

struct _direct_memory_binding_class {
    jclass clazz;

    jfieldID is_valid;
};
extern struct _direct_memory_binding_class JDirectMemoryBinding;

struct _remote_game_process_class {
    jclass clazz;

    jclass java_lang_ProcessHandle;
    jmethodID java_lang_ProcessHandle_pid;

    jfieldID process;
    jfieldID direct_accessor;
    jfieldID process_handle;
};
extern struct _remote_game_process_class JRemoteProcess;

void helios_init(JNIEnv *);

#endif //HELIOSGAME_HELIOS_GAME_H
