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

/**
 * Represents a method of memory access
 *
 * @param <T> a java primitive type to convert memory to.
 */
@FunctionalInterface
public interface MemoryReader<T> {
    /**
     * Synchronously perform a read operation.
     *
     * @param offset the memory offset to read at
     * @param length the number of bytes to read
     *
     * @return a primitive box
     */
    @Nonnull
    T read(@Nonnegative long offset, @Nonnegative long length);
}
