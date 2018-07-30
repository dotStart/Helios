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
import io.github.dotstart.helios.api.theme.variable.Variable;
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
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 * @see SizeVariable
 */
public class BorderSizeVariable extends AbstractVariable {

  private final DoubleProperty top = new SimpleDoubleProperty();
  private final DoubleProperty right = new SimpleDoubleProperty();
  private final DoubleProperty bottom = new SimpleDoubleProperty();
  private final DoubleProperty left = new SimpleDoubleProperty();
  private final ObjectProperty<SizeUnit> unit = new SimpleObjectProperty<>();

  public BorderSizeVariable(
      @NonNull URI uri,
      double defaultTopValue,
      double defaultRightValue,
      double defaultBottomValue,
      double defaultLeftValue,
      @NonNull SizeUnit defaultUnit) {
    super(uri);

    this.top.set(defaultTopValue);
    this.right.set(defaultRightValue);
    this.bottom.set(defaultBottomValue);
    this.left.set(defaultLeftValue);
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
  public Variable duplicate() {
    return new BorderSizeVariable(
        this.uri, this.top.get(), this.right.get(),
        this.bottom.get(), this.left.get(), this.unit.get()
    );
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
    this.top.set(buf.readDouble());
    this.right.set(buf.readDouble());
    this.bottom.set(buf.readDouble());
    this.left.set(buf.readDouble());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull ByteBuf buf) {
    SerializationUtility.writeString(buf, this.unit.get().getSuffix());
    buf.writeDouble(this.top.get());
    buf.writeDouble(this.right.get());
    buf.writeDouble(this.bottom.get());
    buf.writeDouble(this.left.get());
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String toCss() {
    var top = this.top.get();
    var right = this.right.get();
    var bottom = this.bottom.get();
    var left = this.left.get();
    var suffix = this.unit.get().getSuffix();

    if (Math.abs(top - bottom) <= 0.0001 && Math.abs(left - right) <= 0.0001
        && Math.abs(top - left) <= 0.0001) {
      return String.format(Locale.ENGLISH, "%2$.4f%1$s", suffix, top);
    }
    if (Math.abs(top - bottom) <= 0.0001 && Math.abs(left - right) <= 0.0001) {
      return String.format(Locale.ENGLISH, "%2$.4f%1$s %3$.4f%1$s", suffix, top, left);
    }
    if (Math.abs(left - right) < 0.0001) {
      return String
          .format(Locale.ENGLISH, "%2$.4f%1$s %3$.4f%1$s %4$.4f%1$s", suffix, top, left, bottom);
    }
    return String
        .format(Locale.ENGLISH, "%2$.4f%1$s %3$.4f%1$s %4$.4f%1$s %5$.4f%1$s", suffix, top, right,
            bottom, left);
  }
}
