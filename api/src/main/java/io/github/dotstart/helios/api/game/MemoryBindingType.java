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

/**
 * Represents a strategy used to access memory in a remote process
 */
public enum MemoryBindingType {

    /**
     * The read operation causes a breakpoint in the target process and copies the
     * memory at the given address into the Helios process.
     */
    DIRECT,

    /**
     * Creates a memory-mapped file handle shared between the Helios process and the
     * target game.
     */
    MMAP,

    /**
     * Injects a jump at the target address to a function which will signal the
     * Helios process to perform an action.
     */
    REVERSE,
}
