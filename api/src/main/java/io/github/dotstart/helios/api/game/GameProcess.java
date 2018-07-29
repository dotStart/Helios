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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the remote game process whose memory is being scanned.
 */
public interface GameProcess {

    /**
     * @return the handle for the given process
     */
    @Nonnull
    ProcessHandle getHandle();

    /**
     * @return the base memory address of the process to bypass ASLR
     */
    @Nonnegative
    long getBaseAddress();

    /**
     * Create a binding with a direct scanning policy.
     *
     * @param offset memory offset, added to base address
     * @param <T> the primitive box type to cast the memory to
     * @return a new or reused memory binding
     */
    @Nullable
    default <T> MemoryBinding<T> getOrCreateBinding(@Nonnegative long offset) {
        return getOrCreateBinding(offset, MemoryBindingType.DIRECT);
    }

    /**
     * Create a memory binding with the given scanning policy. If a binding exists
     * with a different binding type, that binding is still returned and the given
     * argument is ignored.
     *
     * @param offset memory offset, added to base address
     * @param type method of memory access
     * @param <T> the primitive box type to cast the memory to
     * @return a anew or reused memory binding
     */
    @Nullable
    <T> MemoryBinding<T> getOrCreateBinding(@Nonnegative long offset, @Nonnull MemoryBindingType type);

    /**
     * Removes a binding
     * @param offset the offset of the memory binding
     * @return true if the binding was removed, false otherwise
     */
    boolean removeBinding(@Nonnegative long offset);
}
