/*
 * Copyright 2018 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
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
package io.github.dotstart.helios.api.theme.variable.size;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.theme.variable.AbstractVariable;
import io.github.dotstart.helios.api.utility.SerializationUtility;
import io.netty.buffer.ByteBuf;
import java.net.URI;
import java.util.Locale;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * Represents a theme variable for arbitrary single-value sizes.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 * @see BorderSizeVariable
 */
public class SizeVariable extends AbstractVariable {

  private final DoubleProperty value = new SimpleDoubleProperty();
  private final ObjectProperty<SizeUnit> unit = new SimpleObjectProperty<>();

  public SizeVariable(@NonNull URI uri, double defaultValue, @NonNull SizeUnit defaultUnit) {
    super(uri);

    this.value.set(defaultValue);
    this.unit.set(defaultUnit);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Node createSettingsNode() {
    throw new UnsupportedOperationException(); // TODO
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public SizeVariable duplicate() {
    return new SizeVariable(this.uri, this.value.get(), this.unit.get());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void read(@NonNull ByteBuf buf) {
    var suffix = SerializationUtility.readString(buf);
    var unit = SizeUnit.bySuffix(suffix)
        .orElseThrow(() -> new IllegalArgumentException(
            "Illegal size suffix: Type '" + suffix + "' is unsupported"));

    this.unit.set(unit);
    this.value.set(buf.readDouble());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull ByteBuf buf) {
    SerializationUtility.writeString(buf, this.unit.get().getSuffix());
    buf.writeDouble(this.value.get());
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String toCss() {
    var val = this.value.get();
    if (val == 0) {
      return "0";
    }

    return String.format(Locale.ENGLISH, "%.4f%s", val, this.unit.get().getSuffix());
  }
}
