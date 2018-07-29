#include "helios_game.h"
#include "io_github_dotstart_helios_game_RemoteGameProcess.h"
#include <windows.h>

JNIEXPORT void JNICALL Java_io_github_dotstart_helios_game_RemoteGameProcess_attach(JNIEnv *env, jobject this) {
    jobject j_process_handle = (*env) -> GetObjectField(env, this, JRemoteProcess.process_handle);
    jlong pid = (*env) -> CallLongMethod(env, j_process_handle, JRemoteProcess.java_lang_ProcessHandle_pid);

    HANDLE process_handle = OpenProcess(PROCESS_ALL_ACCESS, TRUE, (DWORD) pid);

    (*env) -> SetLongField(env, this, JRemoteProcess.process, (jlong) process_handle);
}
