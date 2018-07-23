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
package io.github.dotstart.helios.api.theme;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.theme.variable.Variable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * <p>Provides an application theme which customizes various variables of the standard stylesheet
 * and and/or otherwise provides custom CSS which is appended to the base.</p>
 *
 * <p>Each theme will typically extend upon the base stylesheet (e.g. a set of sane defaults which
 * rely on various variables in order to provide their respective design). Themes may override these
 * variables and (optionally) add additional CSS to the end of the definition.</p>
 *
 * <p>For these purposes, the base stylesheet consists of two major components: A base definition
 * which covers global styles like the window background, component borders and text colors, and the
 * component styles, which define the default looks of each respective component.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Theme {

  private final StringProperty displayName = new SimpleStringProperty();
  private final StringProperty description = new SimpleStringProperty();
  private final StringProperty version = new SimpleStringProperty();
  private final ObservableList<String> authors = FXCollections.observableArrayList();
  private final ObservableList<Variable> variables = FXCollections.observableArrayList();

  public Theme(@NonNull String displayName) {
    this.displayName.setValue(displayName);
  }

  /**
   * <p>Retrieves this theme's display name.</p>
   *
   * <p>This value is used in order to help users recognize a respective theme and is typically a
   * brand or project name.</p>
   *
   * @return a display name.
   */
  @NonNull
  public String getDisplayName() {
    return this.displayName.get();
  }

  @NonNull
  public StringProperty displayNameProperty() {
    return this.displayName;
  }

  public void setDisplayName(@NonNull String displayName) {
    this.displayName.set(displayName);
  }

  /**
   * <p>Retrieves a human readable description for this theme.</p>
   *
   * <p>This value is meant to provide an extension to the display name and explain the specifics
   * of this theme (e.g. whether it is dark or bright, provides animations, provides built-in
   * support for a given component, etc).</p>
   *
   * @return a description.
   */
  @NonNull
  public String getDescription() {
    return this.description.get();
  }

  @NonNull
  public StringProperty descriptionProperty() {
    return this.description;
  }

  public void setDescription(@NonNull String description) {
    this.description.set(description);
  }

  /**
   * <p>Retrieves a human readable version for this theme.</p>
   *
   * <p>When publishing themes to a directory, it may be useful to provide version information for
   * users so that they can easily compare the version with their local installation. This value is
   * expected to follow the <a href="https://semver.org/">Semantic Versioning</a> specification to
   * permit automatic discovery and updating.</p>
   *
   * @return a version number.
   */
  @NonNull
  public String getVersion() {
    return this.version.get();
  }

  @NonNull
  public StringProperty versionProperty() {
    return this.version;
  }

  public void setVersion(@NonNull String version) {
    this.version.set(version);
  }

  /**
   * <p>Retrieves a list of authors (or contributors) who were involved in creating this theme.</p>
   *
   * <p>Typically each contributor is formatted in one of the following formats:</p>
   *
   * <ul>
   * <li>[first name] "[alias]" [last name]</li>
   * <li>[alias]</li>
   * </ul>
   *
   * @return a list of authors.
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableList<String> getAuthors() {
    return this.authors;
  }

  /**
   * <p>Retrieves a list of variables defined by this theme.</p>
   *
   * <p>Note that this list may contain variables that are unknown to the application (e.g. are
   * provided by modules which are not loaded at the moment). Unknown variables will generally be
   * omitted from the css output.</p>
   *
   * @return a list of variables.
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableList<Variable> getVariables() {
    return this.variables;
  }
}
