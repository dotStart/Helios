/*
 * Copyright 2018 Hex <hex@hex.lc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.dotstart.helios.game;

import io.github.dotstart.helios.api.game.GameProcess;
import io.github.dotstart.helios.api.game.MemoryBinding;
import io.github.dotstart.helios.api.game.MemoryBindingType;
import io.github.dotstart.helios.api.game.MemoryReader;
import io.github.dotstart.helios.api.game.MemoryWriter;

public class RemoteGameProcess implements GameProcess {
    private final ProcessHandle handle;
    private long process;

    DirectMemoryAccessor directAccessor;

    private static final boolean isWin32 = System.getProperty("os.name").toLowerCase().contains("windows");

    public RemoteGameProcess(ProcessHandle handle) {
        this.handle = handle;
        attach();
        directAccessor = new DirectMemoryAccessor(process);
    }

    @Override
    public ProcessHandle getHandle() {
        return handle;
    }

    @Override
    public long getBaseAddress() {
        if (isWin32) {
            return process; // On windows the process handle pointer should be the same as the base address.
        } else {
            throw new UnsupportedOperationException("I'd just like to interject for a moment");
        }
    }

    @Override
    public <T> MemoryBinding<T> getOrCreateBinding(long offset, MemoryBindingType type) {
        return null;
    }

    @Override
    public boolean removeBinding(long offset) {
        return false;
    }

    private native void attach();


    static class DirectMemoryAccessor implements MemoryReader, MemoryWriter {
        private long process;

        DirectMemoryAccessor(long process) {
            this.process = process;
        }

        @Override
        public native byte[] read(long offset, long length);

        @Override
        public native byte[] write(long offset, byte[] value);
    }
}
