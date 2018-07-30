#include "helios_game.h"
#include <stdbool.h>

struct _direct_memory_binding_class JDirectMemoryBinding;
struct _remote_game_process_class JRemoteProcess;

void helios_init(JNIEnv *env) {
    static bool initialized = false;

    if (initialized) {
        (*env) -> FatalError(env, "multiple initialization of Helios game native library");
    }

    JDirectMemoryBinding.clazz = (*env) -> FindClass(env, "io/github/dotstart/helios/game/DirectMemoryBinding");
    JDirectMemoryBinding.is_valid = (*env) -> GetFieldID(env, JDirectMemoryBinding.clazz, "isValid", "Z");

    JRemoteProcess.clazz = (*env) -> FindClass(env, "io/github/dotstart/helios/game/RemoteGameProcess");
    JRemoteProcess.java_lang_ProcessHandle = (*env) -> FindClass(env, "java/lang/ProcessHandle");
    JRemoteProcess.java_lang_ProcessHandle_pid = (*env) -> GetMethodID(env, JRemoteProcess.java_lang_ProcessHandle, "pid", "()J");
    JRemoteProcess.direct_accessor = (*env) -> GetFieldID(env, JRemoteProcess.clazz, "directAccessor", "Lio/github/dotstart/helios/game/RemoteGameProcess/DirectMemoryAccessor;");
    JRemoteProcess.process = (*env) -> GetFieldID(env, JRemoteProcess.clazz, "process", "J");
    JRemoteProcess.process_handle = (*env) -> GetFieldID(env, JRemoteProcess.clazz, "handle", "Ljava/lang/ProcessHandle;");
}