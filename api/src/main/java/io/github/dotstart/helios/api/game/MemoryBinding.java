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
package io.github.dotstart.helios.api.game;

import javafx.beans.value.ObservableValue;

import javax.annotation.Nullable;

public abstract class MemoryBinding<T> implements ObservableValue<T> {
    public static final long PROT_READ = 1;
    public static final long PROT_WRITE = 1 << 1;
    public static final long PROT_EXEC = 1 << 2;

    protected final long offset;
    protected final MemoryBindingType type;
    protected final long length;
    protected long flags;

    protected MemoryReader reader;
    protected MemoryWriter writer;

    protected MemoryBinding(long offset, MemoryBindingType type, long length, long flags,
                            MemoryReader reader, MemoryWriter writer) {
        this.offset = offset;
        this.type = type;
        this.length = length;
        this.flags = flags;
        this.reader = reader;
        this.writer = writer;
    }

    public long getOffset() {
        return offset;
    }

    public MemoryBindingType getType() {
        return type;
    }

    public long getLength() {
        return length;
    }

    // TODO this is a placeholder, I don't like having this in API
    protected abstract void execute0();

    public boolean execute() {
        if ((flags & PROT_EXEC) == PROT_EXEC) {
            return false;
        }
        execute0();
        return true;
    }

    @Nullable
    public byte[] read() {
        if ((flags & PROT_READ) == PROT_READ) {
            return null;
        }
        return reader.read(offset, length);
    }

    @Nullable
    public byte[] write(byte[] value) {
        if ((flags & PROT_WRITE) == PROT_WRITE) {
            return null;
        }
        return writer.write(offset, value);
    }
}
