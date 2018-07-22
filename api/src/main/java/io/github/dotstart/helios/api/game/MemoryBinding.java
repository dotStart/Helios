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

public abstract class MemoryBinding<T> implements ObservableValue<T> {
    public static final long PROT_READ = 1;
    public static final long PROT_WRITE = 1 << 1;
    public static final long PROT_EXEC = 1 << 2;

    private final long offset;
    private final MemoryBindingType type;
    private final long length;
    private long flags;

    protected MemoryBinding(long offset, MemoryBindingType type, long length, long flags) {
        this.offset = offset;
        this.type = type;
        this.length = length;
        this.flags = flags;
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
}
