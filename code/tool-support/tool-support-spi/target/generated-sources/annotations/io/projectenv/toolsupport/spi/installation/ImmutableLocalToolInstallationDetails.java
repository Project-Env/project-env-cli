package io.projectenv.toolsupport.spi.installation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link LocalToolInstallationDetails}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableLocalToolInstallationDetails.builder()}.
 */
@Generated(from = "LocalToolInstallationDetails", generator = "Immutables")
@SuppressWarnings({"all"})
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
public final class ImmutableLocalToolInstallationDetails
    implements LocalToolInstallationDetails {
  private final File binariesRoot;
  private final File primaryExecutable;
  private final List<File> pathElements;
  private final Map<String, File> environmentVariables;
  private final List<Pair<File, File>> fileOverwrites;

  private ImmutableLocalToolInstallationDetails(
      File binariesRoot,
      File primaryExecutable,
      List<File> pathElements,
      Map<String, File> environmentVariables,
      List<Pair<File, File>> fileOverwrites) {
    this.binariesRoot = binariesRoot;
    this.primaryExecutable = primaryExecutable;
    this.pathElements = pathElements;
    this.environmentVariables = environmentVariables;
    this.fileOverwrites = fileOverwrites;
  }

  /**
   * @return The value of the {@code binariesRoot} attribute
   */
  @Override
  public Optional<File> getBinariesRoot() {
    return Optional.ofNullable(binariesRoot);
  }

  /**
   * @return The value of the {@code primaryExecutable} attribute
   */
  @Override
  public Optional<File> getPrimaryExecutable() {
    return Optional.ofNullable(primaryExecutable);
  }

  /**
   * @return The value of the {@code pathElements} attribute
   */
  @Override
  public List<File> getPathElements() {
    return pathElements;
  }

  /**
   * @return The value of the {@code environmentVariables} attribute
   */
  @Override
  public Map<String, File> getEnvironmentVariables() {
    return environmentVariables;
  }

  /**
   * @return The value of the {@code fileOverwrites} attribute
   */
  @Override
  public List<Pair<File, File>> getFileOverwrites() {
    return fileOverwrites;
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link LocalToolInstallationDetails#getBinariesRoot() binariesRoot} attribute.
   * @param value The value for binariesRoot
   * @return A modified copy of {@code this} object
   */
  public final ImmutableLocalToolInstallationDetails withBinariesRoot(File value) {
    File newValue = Objects.requireNonNull(value, "binariesRoot");
    if (this.binariesRoot == newValue) return this;
    return new ImmutableLocalToolInstallationDetails(
        newValue,
        this.primaryExecutable,
        this.pathElements,
        this.environmentVariables,
        this.fileOverwrites);
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link LocalToolInstallationDetails#getBinariesRoot() binariesRoot} attribute.
   * A shallow reference equality check is used on unboxed optional value to prevent copying of the same value by returning {@code this}.
   * @param optional A value for binariesRoot
   * @return A modified copy of {@code this} object
   */
  @SuppressWarnings("unchecked") // safe covariant cast
  public final ImmutableLocalToolInstallationDetails withBinariesRoot(Optional<? extends File> optional) {
    File value = optional.orElse(null);
    if (this.binariesRoot == value) return this;
    return new ImmutableLocalToolInstallationDetails(
        value,
        this.primaryExecutable,
        this.pathElements,
        this.environmentVariables,
        this.fileOverwrites);
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link LocalToolInstallationDetails#getPrimaryExecutable() primaryExecutable} attribute.
   * @param value The value for primaryExecutable
   * @return A modified copy of {@code this} object
   */
  public final ImmutableLocalToolInstallationDetails withPrimaryExecutable(File value) {
    File newValue = Objects.requireNonNull(value, "primaryExecutable");
    if (this.primaryExecutable == newValue) return this;
    return new ImmutableLocalToolInstallationDetails(this.binariesRoot, newValue, this.pathElements, this.environmentVariables, this.fileOverwrites);
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link LocalToolInstallationDetails#getPrimaryExecutable() primaryExecutable} attribute.
   * A shallow reference equality check is used on unboxed optional value to prevent copying of the same value by returning {@code this}.
   * @param optional A value for primaryExecutable
   * @return A modified copy of {@code this} object
   */
  @SuppressWarnings("unchecked") // safe covariant cast
  public final ImmutableLocalToolInstallationDetails withPrimaryExecutable(Optional<? extends File> optional) {
    File value = optional.orElse(null);
    if (this.primaryExecutable == value) return this;
    return new ImmutableLocalToolInstallationDetails(this.binariesRoot, value, this.pathElements, this.environmentVariables, this.fileOverwrites);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link LocalToolInstallationDetails#getPathElements() pathElements}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableLocalToolInstallationDetails withPathElements(File... elements) {
    List<File> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
    return new ImmutableLocalToolInstallationDetails(
        this.binariesRoot,
        this.primaryExecutable,
        newValue,
        this.environmentVariables,
        this.fileOverwrites);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link LocalToolInstallationDetails#getPathElements() pathElements}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of pathElements elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableLocalToolInstallationDetails withPathElements(Iterable<? extends File> elements) {
    if (this.pathElements == elements) return this;
    List<File> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
    return new ImmutableLocalToolInstallationDetails(
        this.binariesRoot,
        this.primaryExecutable,
        newValue,
        this.environmentVariables,
        this.fileOverwrites);
  }

  /**
   * Copy the current immutable object by replacing the {@link LocalToolInstallationDetails#getEnvironmentVariables() environmentVariables} map with the specified map.
   * Nulls are not permitted as keys or values.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param entries The entries to be added to the environmentVariables map
   * @return A modified copy of {@code this} object
   */
  public final ImmutableLocalToolInstallationDetails withEnvironmentVariables(Map<String, ? extends File> entries) {
    if (this.environmentVariables == entries) return this;
    Map<String, File> newValue = createUnmodifiableMap(true, false, entries);
    return new ImmutableLocalToolInstallationDetails(this.binariesRoot, this.primaryExecutable, this.pathElements, newValue, this.fileOverwrites);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link LocalToolInstallationDetails#getFileOverwrites() fileOverwrites}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  @SafeVarargs @SuppressWarnings("varargs")
  public final ImmutableLocalToolInstallationDetails withFileOverwrites(Pair<File, File>... elements) {
    List<Pair<File, File>> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
    return new ImmutableLocalToolInstallationDetails(
        this.binariesRoot,
        this.primaryExecutable,
        this.pathElements,
        this.environmentVariables,
        newValue);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link LocalToolInstallationDetails#getFileOverwrites() fileOverwrites}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of fileOverwrites elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableLocalToolInstallationDetails withFileOverwrites(Iterable<? extends Pair<File, File>> elements) {
    if (this.fileOverwrites == elements) return this;
    List<Pair<File, File>> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
    return new ImmutableLocalToolInstallationDetails(
        this.binariesRoot,
        this.primaryExecutable,
        this.pathElements,
        this.environmentVariables,
        newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableLocalToolInstallationDetails} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableLocalToolInstallationDetails
        && equalTo((ImmutableLocalToolInstallationDetails) another);
  }

  private boolean equalTo(ImmutableLocalToolInstallationDetails another) {
    return Objects.equals(binariesRoot, another.binariesRoot)
        && Objects.equals(primaryExecutable, another.primaryExecutable)
        && pathElements.equals(another.pathElements)
        && environmentVariables.equals(another.environmentVariables)
        && fileOverwrites.equals(another.fileOverwrites);
  }

  /**
   * Computes a hash code from attributes: {@code binariesRoot}, {@code primaryExecutable}, {@code pathElements}, {@code environmentVariables}, {@code fileOverwrites}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + Objects.hashCode(binariesRoot);
    h += (h << 5) + Objects.hashCode(primaryExecutable);
    h += (h << 5) + pathElements.hashCode();
    h += (h << 5) + environmentVariables.hashCode();
    h += (h << 5) + fileOverwrites.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code LocalToolInstallationDetails} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("LocalToolInstallationDetails{");
    if (binariesRoot != null) {
      builder.append("binariesRoot=").append(binariesRoot);
    }
    if (primaryExecutable != null) {
      if (builder.length() > 29) builder.append(", ");
      builder.append("primaryExecutable=").append(primaryExecutable);
    }
    if (builder.length() > 29) builder.append(", ");
    builder.append("pathElements=").append(pathElements);
    builder.append(", ");
    builder.append("environmentVariables=").append(environmentVariables);
    builder.append(", ");
    builder.append("fileOverwrites=").append(fileOverwrites);
    return builder.append("}").toString();
  }

  /**
   * Creates an immutable copy of a {@link LocalToolInstallationDetails} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable LocalToolInstallationDetails instance
   */
  public static ImmutableLocalToolInstallationDetails copyOf(LocalToolInstallationDetails instance) {
    if (instance instanceof ImmutableLocalToolInstallationDetails) {
      return (ImmutableLocalToolInstallationDetails) instance;
    }
    return ImmutableLocalToolInstallationDetails.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableLocalToolInstallationDetails ImmutableLocalToolInstallationDetails}.
   * <pre>
   * ImmutableLocalToolInstallationDetails.builder()
   *    .binariesRoot(java.io.File) // optional {@link LocalToolInstallationDetails#getBinariesRoot() binariesRoot}
   *    .primaryExecutable(java.io.File) // optional {@link LocalToolInstallationDetails#getPrimaryExecutable() primaryExecutable}
   *    .addPathElements|addAllPathElements(java.io.File) // {@link LocalToolInstallationDetails#getPathElements() pathElements} elements
   *    .putEnvironmentVariables|putAllEnvironmentVariables(String =&gt; java.io.File) // {@link LocalToolInstallationDetails#getEnvironmentVariables() environmentVariables} mappings
   *    .addFileOverwrites|addAllFileOverwrites(org.apache.commons.lang3.tuple.Pair&amp;lt;java.io.File, java.io.File&amp;gt;) // {@link LocalToolInstallationDetails#getFileOverwrites() fileOverwrites} elements
   *    .build();
   * </pre>
   * @return A new ImmutableLocalToolInstallationDetails builder
   */
  public static ImmutableLocalToolInstallationDetails.Builder builder() {
    return new ImmutableLocalToolInstallationDetails.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableLocalToolInstallationDetails ImmutableLocalToolInstallationDetails}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "LocalToolInstallationDetails", generator = "Immutables")
  public static final class Builder {
    private File binariesRoot;
    private File primaryExecutable;
    private List<File> pathElements = new ArrayList<File>();
    private Map<String, File> environmentVariables = new LinkedHashMap<String, File>();
    private List<Pair<File, File>> fileOverwrites = new ArrayList<Pair<File, File>>();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code LocalToolInstallationDetails} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(LocalToolInstallationDetails instance) {
      Objects.requireNonNull(instance, "instance");
      Optional<File> binariesRootOptional = instance.getBinariesRoot();
      if (binariesRootOptional.isPresent()) {
        binariesRoot(binariesRootOptional);
      }
      Optional<File> primaryExecutableOptional = instance.getPrimaryExecutable();
      if (primaryExecutableOptional.isPresent()) {
        primaryExecutable(primaryExecutableOptional);
      }
      addAllPathElements(instance.getPathElements());
      putAllEnvironmentVariables(instance.getEnvironmentVariables());
      addAllFileOverwrites(instance.getFileOverwrites());
      return this;
    }

    /**
     * Initializes the optional value {@link LocalToolInstallationDetails#getBinariesRoot() binariesRoot} to binariesRoot.
     * @param binariesRoot The value for binariesRoot
     * @return {@code this} builder for chained invocation
     */
    public final Builder binariesRoot(File binariesRoot) {
      this.binariesRoot = Objects.requireNonNull(binariesRoot, "binariesRoot");
      return this;
    }

    /**
     * Initializes the optional value {@link LocalToolInstallationDetails#getBinariesRoot() binariesRoot} to binariesRoot.
     * @param binariesRoot The value for binariesRoot
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder binariesRoot(Optional<? extends File> binariesRoot) {
      this.binariesRoot = binariesRoot.orElse(null);
      return this;
    }

    /**
     * Initializes the optional value {@link LocalToolInstallationDetails#getPrimaryExecutable() primaryExecutable} to primaryExecutable.
     * @param primaryExecutable The value for primaryExecutable
     * @return {@code this} builder for chained invocation
     */
    public final Builder primaryExecutable(File primaryExecutable) {
      this.primaryExecutable = Objects.requireNonNull(primaryExecutable, "primaryExecutable");
      return this;
    }

    /**
     * Initializes the optional value {@link LocalToolInstallationDetails#getPrimaryExecutable() primaryExecutable} to primaryExecutable.
     * @param primaryExecutable The value for primaryExecutable
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder primaryExecutable(Optional<? extends File> primaryExecutable) {
      this.primaryExecutable = primaryExecutable.orElse(null);
      return this;
    }

    /**
     * Adds one element to {@link LocalToolInstallationDetails#getPathElements() pathElements} list.
     * @param element A pathElements element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addPathElements(File element) {
      this.pathElements.add(Objects.requireNonNull(element, "pathElements element"));
      return this;
    }

    /**
     * Adds elements to {@link LocalToolInstallationDetails#getPathElements() pathElements} list.
     * @param elements An array of pathElements elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addPathElements(File... elements) {
      for (File element : elements) {
        this.pathElements.add(Objects.requireNonNull(element, "pathElements element"));
      }
      return this;
    }


    /**
     * Sets or replaces all elements for {@link LocalToolInstallationDetails#getPathElements() pathElements} list.
     * @param elements An iterable of pathElements elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder pathElements(Iterable<? extends File> elements) {
      this.pathElements.clear();
      return addAllPathElements(elements);
    }

    /**
     * Adds elements to {@link LocalToolInstallationDetails#getPathElements() pathElements} list.
     * @param elements An iterable of pathElements elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllPathElements(Iterable<? extends File> elements) {
      for (File element : elements) {
        this.pathElements.add(Objects.requireNonNull(element, "pathElements element"));
      }
      return this;
    }

    /**
     * Put one entry to the {@link LocalToolInstallationDetails#getEnvironmentVariables() environmentVariables} map.
     * @param key The key in the environmentVariables map
     * @param value The associated value in the environmentVariables map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putEnvironmentVariables(String key, File value) {
      this.environmentVariables.put(
          Objects.requireNonNull(key, "environmentVariables key"),
          Objects.requireNonNull(value, "environmentVariables value"));
      return this;
    }

    /**
     * Put one entry to the {@link LocalToolInstallationDetails#getEnvironmentVariables() environmentVariables} map. Nulls are not permitted
     * @param entry The key and value entry
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putEnvironmentVariables(Map.Entry<String, ? extends File> entry) {
      String k = entry.getKey();
      File v = entry.getValue();
      this.environmentVariables.put(
          Objects.requireNonNull(k, "environmentVariables key"),
          Objects.requireNonNull(v, "environmentVariables value"));
      return this;
    }

    /**
     * Sets or replaces all mappings from the specified map as entries for the {@link LocalToolInstallationDetails#getEnvironmentVariables() environmentVariables} map. Nulls are not permitted
     * @param entries The entries that will be added to the environmentVariables map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder environmentVariables(Map<String, ? extends File> entries) {
      this.environmentVariables.clear();
      return putAllEnvironmentVariables(entries);
    }

    /**
     * Put all mappings from the specified map as entries to {@link LocalToolInstallationDetails#getEnvironmentVariables() environmentVariables} map. Nulls are not permitted
     * @param entries The entries that will be added to the environmentVariables map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putAllEnvironmentVariables(Map<String, ? extends File> entries) {
      for (Map.Entry<String, ? extends File> e : entries.entrySet()) {
        String k = e.getKey();
        File v = e.getValue();
        this.environmentVariables.put(
            Objects.requireNonNull(k, "environmentVariables key"),
            Objects.requireNonNull(v, "environmentVariables value"));
      }
      return this;
    }

    /**
     * Adds one element to {@link LocalToolInstallationDetails#getFileOverwrites() fileOverwrites} list.
     * @param element A fileOverwrites element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addFileOverwrites(Pair<File, File> element) {
      this.fileOverwrites.add(Objects.requireNonNull(element, "fileOverwrites element"));
      return this;
    }

    /**
     * Adds elements to {@link LocalToolInstallationDetails#getFileOverwrites() fileOverwrites} list.
     * @param elements An array of fileOverwrites elements
     * @return {@code this} builder for use in a chained invocation
     */
    @SafeVarargs @SuppressWarnings("varargs")
    public final Builder addFileOverwrites(Pair<File, File>... elements) {
      for (Pair<File, File> element : elements) {
        this.fileOverwrites.add(Objects.requireNonNull(element, "fileOverwrites element"));
      }
      return this;
    }


    /**
     * Sets or replaces all elements for {@link LocalToolInstallationDetails#getFileOverwrites() fileOverwrites} list.
     * @param elements An iterable of fileOverwrites elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder fileOverwrites(Iterable<? extends Pair<File, File>> elements) {
      this.fileOverwrites.clear();
      return addAllFileOverwrites(elements);
    }

    /**
     * Adds elements to {@link LocalToolInstallationDetails#getFileOverwrites() fileOverwrites} list.
     * @param elements An iterable of fileOverwrites elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllFileOverwrites(Iterable<? extends Pair<File, File>> elements) {
      for (Pair<File, File> element : elements) {
        this.fileOverwrites.add(Objects.requireNonNull(element, "fileOverwrites element"));
      }
      return this;
    }

    /**
     * Builds a new {@link ImmutableLocalToolInstallationDetails ImmutableLocalToolInstallationDetails}.
     * @return An immutable instance of LocalToolInstallationDetails
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableLocalToolInstallationDetails build() {
      return new ImmutableLocalToolInstallationDetails(
          binariesRoot,
          primaryExecutable,
          createUnmodifiableList(true, pathElements),
          createUnmodifiableMap(false, false, environmentVariables),
          createUnmodifiableList(true, fileOverwrites));
    }
  }

  private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
    ArrayList<T> list;
    if (iterable instanceof Collection<?>) {
      int size = ((Collection<?>) iterable).size();
      if (size == 0) return Collections.emptyList();
      list = new ArrayList<>();
    } else {
      list = new ArrayList<>();
    }
    for (T element : iterable) {
      if (skipNulls && element == null) continue;
      if (checkNulls) Objects.requireNonNull(element, "element");
      list.add(element);
    }
    return list;
  }

  private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
    switch(list.size()) {
    case 0: return Collections.emptyList();
    case 1: return Collections.singletonList(list.get(0));
    default:
      if (clone) {
        return Collections.unmodifiableList(new ArrayList<>(list));
      } else {
        if (list instanceof ArrayList<?>) {
          ((ArrayList<?>) list).trimToSize();
        }
        return Collections.unmodifiableList(list);
      }
    }
  }

  private static <K, V> Map<K, V> createUnmodifiableMap(boolean checkNulls, boolean skipNulls, Map<? extends K, ? extends V> map) {
    switch (map.size()) {
    case 0: return Collections.emptyMap();
    case 1: {
      Map.Entry<? extends K, ? extends V> e = map.entrySet().iterator().next();
      K k = e.getKey();
      V v = e.getValue();
      if (checkNulls) {
        Objects.requireNonNull(k, "key");
        Objects.requireNonNull(v, "value");
      }
      if (skipNulls && (k == null || v == null)) {
        return Collections.emptyMap();
      }
      return Collections.singletonMap(k, v);
    }
    default: {
      Map<K, V> linkedMap = new LinkedHashMap<>(map.size());
      if (skipNulls || checkNulls) {
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
          K k = e.getKey();
          V v = e.getValue();
          if (skipNulls) {
            if (k == null || v == null) continue;
          } else if (checkNulls) {
            Objects.requireNonNull(k, "key");
            Objects.requireNonNull(v, "value");
          }
          linkedMap.put(k, v);
        }
      } else {
        linkedMap.putAll(map);
      }
      return Collections.unmodifiableMap(linkedMap);
    }
    }
  }
}
