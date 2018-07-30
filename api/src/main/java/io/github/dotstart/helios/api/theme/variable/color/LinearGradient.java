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
package io.github.dotstart.helios.api.theme.variable.color;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a linear gradient with an arbitrary number of stops.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
// TODO: Add support for custom origin and target positions
public class LinearGradient implements Color {

  private final ObjectProperty<Direction> direction = new SimpleObjectProperty<>(
      Direction.LEFT_TO_RIGHT);
  private final BooleanProperty repeat = new SimpleBooleanProperty(false);
  private final ObservableList<Stop> stops = FXCollections.observableArrayList();

  public LinearGradient() {
  }

  public LinearGradient(@NonNull Direction direction, boolean repeat, @NonNull Stop... stops) {
    this.direction.set(direction);
    this.repeat.set(repeat);
    this.stops.setAll(stops);
  }

  public LinearGradient(@NonNull Direction direction, @NonNull Stop... stops) {
    this(direction, false, stops);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public ColorType getType() {
    return ColorType.LINEAR_GRADIENT;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String toCssInstruction() {
    return String.format(
        "linear-gradient(to %s,%s %s)",

        this.direction.get().targetPosition,
        this.repeat.get() ? " repeat," : "",
        this.stops.stream()
            .map(Stop::toCssInstruction)
            .collect(Collectors.joining(", "))
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void read(@NonNull ByteBuf buf) {
    var directionIndex = buf.readUnsignedByte();
    if (directionIndex > Direction.values().length) {
      throw new IllegalArgumentException(
          "Illegal direction index: 0 <= i < " + Direction.values().length + " required but was "
              + directionIndex);
    }
    this.direction.set(Direction.values()[directionIndex]);
    this.repeat.set(buf.readByte() == 1);

    this.stops.clear();
    var stopCount = buf.readUnsignedByte();
    for (var i = 0; i < stopCount; ++i) {
      var stop = new Stop(SolidColor.BLACK, 0);
      stop.read(buf);
      this.stops.add(stop);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull ByteBuf buf) {
    buf.writeByte(this.direction.get().ordinal());
    buf.writeByte(this.repeat.get() ? 1 : 0);
    buf.writeByte(this.stops.size());
    this.stops.forEach((s) -> s.write(buf));
  }

  @NonNull
  public Direction getDirection() {
    return this.direction.get();
  }

  public void setDirection(@NonNull Direction direction) {
    this.direction.set(direction);
  }

  @NonNull
  public ObjectProperty<Direction> directionProperty() {
    return this.direction;
  }

  public boolean isRepeat() {
    return this.repeat.get();
  }

  public void setRepeat(boolean repeat) {
    this.repeat.set(repeat);
  }

  @NonNull
  public BooleanProperty repeatProperty() {
    return this.repeat;
  }

  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableList<Stop> getStops() {
    return this.stops;
  }

  /**
   * Provides a list of directions in which a gradient may be rendered.
   */
  public enum Direction {
    LEFT_TO_RIGHT("right"),
    TOP_TO_BOTTOM("bottom"),
    RIGHT_TO_LEFT("left"),
    BOTTOM_TO_TOP("top"),

    TOP_LEFT_TO_BOTTOM_RIGHT("bottom right"),
    TOP_RIGHT_TO_BOTTOM_LEFT("bottom left"),
    BOTTOM_RIGHT_TO_TOP_LEFT("top left"),
    BOTTOM_LEFT_TO_TOP_RIGHT("top right");

    private final String targetPosition;

    Direction(@NonNull String targetPosition) {
      this.targetPosition = targetPosition;
    }
  }

  /**
   * Represents a single gradient stop within a linear gradient.
   */
  public static class Stop {

    private ObjectProperty<SolidColor> color = new SimpleObjectProperty<>();
    private DoubleProperty position = new SimpleDoubleProperty();

    public Stop(@NonNull SolidColor color, double position) {
      this.color.setValue(color);
      this.position.setValue(position);
    }

    /**
     * Converts this stop into its CSS representation.
     *
     * @return a css instruction.
     */
    @NonNull
    private String toCssInstruction() {
      return String.format(Locale.ENGLISH, "%s %.2f%%", this.color.get().toCssInstruction(),
          this.position.get() * 100);
    }

    private void read(@NonNull ByteBuf buf) {
      this.color.get().read(buf);
      this.position.setValue(buf.readDouble());
    }

    private void write(@NonNull ByteBuf buf) {
      this.color.get().write(buf);
      buf.writeDouble(this.position.get());
    }

    @NonNull
    public SolidColor getColor() {
      return this.color.get();
    }

    public void setColor(@NonNull SolidColor color) {
      this.color.set(color);
    }

    @NonNull
    public ObjectProperty<SolidColor> colorProperty() {
      return this.color;
    }

    public double getPosition() {
      return this.position.get();
    }

    public void setPosition(double position) {
      this.position.set(position);
    }

    @NonNull
    public DoubleProperty positionProperty() {
      return this.position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Stop)) {
        return false;
      }
      Stop stop = (Stop) o;
      return Objects.equals(this.color, stop.color) &&
          Objects.equals(this.position, stop.position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return Objects.hash(this.color, this.position);
    }
  }
}
