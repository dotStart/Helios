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
package io.github.dotstart.helios.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.helios.api.layout.ComponentRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents a third party or built-in module which provides a limited set of additional
 * functionality to the application.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface HeliosModule {

  /**
   * <p>Retrieves a human readable name for this module.</p>
   *
   * <p>This will typically be a brand name or a reference to the contained functionality and is
   * purely used for logging and other informational purposes within the application interface.</p>
   *
   * @return a display name.
   */
  @NonNull
  String getName();

  /**
   * <p>Retrieves the version number for this module.</p>
   *
   * <p>This value is expected to follow the <a href="https://semver.org/">Semantic Versioning</a>
   * specification as the application may automatically compare it against other available revisions
   * of the module when update checks are enabled.</p>
   *
   * @return a version number.
   */
  @NonNull
  String getVersion();

  /**
   * <p>Retrieves a human readable description for this module.</p>
   *
   * <p>This field explains the provided functionality in short and is provided as an addition to
   * the display name in order to clarify functionality in cases where a brand name may be chosen as
   * a name instead of something more descriptive.</p>
   *
   * @return a description.
   */
  @NonNull
  String getDescription();

  /**
   * <p>Retrieves a listing of all categories in which this module fits.</p>
   *
   * <p>These categories identify the functionality that is implemented by this particular module
   * and are purely used to help users identify the functionality provided by a given module.</p>
   *
   * @return a set of categories.
   */
  @NonNull
  Set<Category> getCategories();

  /**
   * <p>Retrieves a listing of all involved active developers (e.g. project maintainers and regular
   * contributors).</p>
   *
   * <p>This field should be regularly updated to properly credit active developers within the
   * project as it may help users find the right people to contact. The recommended formats for this
   * field are:</p>
   *
   * <ul>
   * <li>[first name] "[alias]" [last name]</li>
   * <li>[alias]</li>
   * </ul>
   *
   * @return a list of developers.
   */
  @NonNull
  List<String> getDevelopers();

  /**
   * <p>Retrieves a complete listing of all involved contributors.</p>
   *
   * <p>This field is an extension to the {@link #getDevelopers()} field and typically contains a
   * set of people who contributed to the project but are not regular contributors.</p>
   *
   * <p>Note that this list should not duplicate any elements within the developers list.
   * Contributors are to be sorted into either category but never both.</p>
   *
   * @return a list of contributors.
   */
  @NonNull
  default List<String> getContributors() {
    return Collections.emptyList();
  }

  /**
   * <p>Retrieves the website URL for this module.</p>
   *
   * <p>This should typically point to a landing page of sorts where users may locate all necessary
   * resources (such as downloads, the project license, the bugtracker and source code).</p>
   *
   * <p>This field is entirely optional and may be left empty when no website is available yet.</p>
   *
   * @return a website url.
   */
  @Nullable
  default String getWebsite() {
    return null;
  }

  /**
   * <p>Retrieves the URL of the bugtracker for this module.</p>
   *
   * <p>This will typically refer to a landing page which displays a listing of all open issues and
   * (optionally) instructions on how to report new issues.</p>
   *
   * @return an issue tracker url.
   */
  @Nullable
  default String getIssueTracker() {
    return null;
  }

  /**
   * <p>Performs a complete module initialization and registration of application components.</p>
   *
   * <p>For the purposes of simplification, instances of most module accessible registries will be
   * provided via the context attribute.</p>
   *
   * @param ctx a context.
   */
  void initialize(@NonNull Context ctx);

  enum Category {
    COMPONENT,
    GAME_INTEGRATION
  }

  interface Context {

    /**
     * <p>Retrieves the version string for the executing instance of Helios.</p>
     *
     * <p>This value typically follows the <a href="https://semver.org/">Semantic Versioning</a>
     * scheme and may thus be compared against in a consistent manner if necessary.</p>
     *
     * @return a version number.
     */
    @NonNull
    String getHeliosVersion();

    /**
     * Retrieves the component registry which is responsible for storing and maintaining component
     * instances for this application instance.
     *
     * @return a component registry.
     */
    @NonNull
    ComponentRegistry componentRegistry();
  }
}
