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
package io.github.dotstart.helios.game.di;

import com.google.inject.MembersInjector;
import io.github.dotstart.helios.game.RemoteGameProcess;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.regex.Pattern;

public class ProcessBindingInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final Pattern regex;

    public ProcessBindingInjector(Field field, Pattern regex) {
        this.field = field;
        this.regex = regex;
    }

    @Override
    public void injectMembers(T instance) {
        Optional<ProcessHandle> handle = ProcessHandle.allProcesses().filter(ProcessHandle::isAlive)
                .filter(process -> regex.matcher(process.info().command().orElse("")).matches())
                .findFirst();
        try {
            if (handle.isPresent()) {
                field.set(instance, new RemoteGameProcess(handle.get()));
            } else {
                field.set(instance, null);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace(); // TODO logger
        }
    }
}
