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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a layer of indirection, with an offset applied to the dereference
 * of the pointer address. When specified as the second or later argument in the
 * value array for {@link BindAddress}, the {@link #value()} parameter is ignored
 * and the offset is added to the result of dereferencing the previous pointer
 * in the array. If this is the final pointer in the array, the {@link #offset()}
 * parameter signifies that the address should be dereferenced to return the value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pointer {
    long value() default 0L;
    long offset() default 0L;
}
