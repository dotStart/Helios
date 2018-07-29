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

import io.github.dotstart.helios.api.game.MemoryBinding;
import io.github.dotstart.helios.api.game.MemoryBindingType;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class DirectMemoryBinding<T> extends MemoryBinding<T> {
    private RemoteGameProcess process;
    private boolean isValid;
    private List<InvalidationListener> invalidationListeners;
    private final Class<T> conversionType;

    protected DirectMemoryBinding(long offset, MemoryBindingType type, long length, long flags, RemoteGameProcess process, Class<T> clazz) {
        super(offset, type, length, flags, process.directAccessor);
        this.process = process;
        this.invalidationListeners = new ArrayList<>();
        this.conversionType = clazz;
        if (!conversionType.isPrimitive()) {
            throw new IllegalArgumentException("memory can only be bound to primitive types.");
        }
    }

    @Override
    protected native void execute0();

    @Override
    public void addListener(ChangeListener changeListener) {

    }

    @Override
    public void removeListener(ChangeListener changeListener) {

    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        invalidationListeners.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        invalidationListeners.remove(invalidationListener);
    }
}
