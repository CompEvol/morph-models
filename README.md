morph-models
============

Models for discrete morphological character data in [BEAST 3](https://github.com/CompEvol/beast3).

Implements the Lewis MK and MKv substitution models (Lewis, 2001), along with ordinal and nested ordinal variants for ordered character data.

## Modules

- **beast-morph-models** — core substitution models and alignment classes (depends on `beast-base`)
- **beast-morph-models-fx** — BEAUti integration (depends on `beast-fx`)

## Substitution models

| Class | Description |
|-------|-------------|
| `LewisMK` | Equal or user-specified frequency Mk model |
| `Ordinal` | Tridiagonal rate matrix for ordered characters |
| `NestedOrdinal` | Nested ordinal rate matrix (state 0 transitions to all others) |

## Building

BEAST 3 dependencies are resolved from [GitHub Packages](https://github.com/CompEvol/beast3/packages) (or Maven Central, if published there). For GitHub Packages, add a [personal access token](https://github.com/settings/tokens) (classic) with `read:packages` scope to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PAT</password>
    </server>
  </servers>
</settings>
```

Then build morph-models:

```bash
cd ~/Git/morph-models
mvn compile
mvn test -pl beast-morph-models
```

Alternatively, you can install BEAST 3 from source (no GitHub auth needed):

```bash
cd ~/Git/beast3
mvn install -DskipTests
```

## Running

```bash
# Validate an XML
mvn -pl beast-morph-models exec:exec -Dbeast.args="-validate examples/M3982.xml"

# Run an analysis
mvn -pl beast-morph-models exec:exec -Dbeast.args="-overwrite examples/M3982.xml"
```

## Examples

- `examples/M3982.xml` — Anolis lizard morphological analysis using BEAST 3 spec classes
- `examples/legacy-2.7/` — original BEAST 2.7 XML files (some require external packages)

## Releasing

### ZIP / CBAN

The existing `release.sh` script builds a BEAST package ZIP and optionally
creates a GitHub release for submission to [CBAN](https://github.com/CompEvol/CBAN):

```bash
./release.sh            # build ZIP only
./release.sh --release  # build ZIP + create GitHub release
```

### Maven Central

```bash
mvn clean deploy -Prelease
```

This builds the JARs (with `version.xml` embedded for service discovery), generates
sources and javadoc JARs, signs everything with GPG, and uploads to Maven Central.

BEAST 3 users can then install with:

```
Package Manager > Install from Maven > io.github.compevol:beast-morph-models:1.3.0
```

Or from the command line:

```bash
packagemanager -maven io.github.compevol:beast-morph-models:1.3.0
```

## References

Lewis, P. O. (2001). A likelihood approach to estimating phylogeny from discrete morphological character data. *Systematic Biology*, 50(6), 913–925.
