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

import com.google.inject.BindingAnnotation;

import javax.annotation.Nonnegative;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a binding to a particular memory address. May annotate either a
 * <p>byte[]</p> field with a fixed size, or any java primitive. In the latter case,
 * the equivalent of a C++ <p>reinterpret_cast</p> will be applied to a memory
 * region the same size or smaller than the byte-aligned size of the primitive.
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface BindAddress {
    /**
     * The offset from the process's base address.
     *
     * @return an array of offsets.
     */
    long[] value();
}
