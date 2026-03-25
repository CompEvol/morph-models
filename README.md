morph-models
============

Models for discrete morphological character data in [BEAST 3](https://github.com/CompEvol/beast3).

Implements the Lewis MK and MKv substitution models (Lewis, 2001), along with ordinal and nested ordinal variants for ordered character data.

## Substitution models

| Class | Description |
|-------|-------------|
| `LewisMK` | Equal or user-specified frequency Mk model |
| `Ordinal` | Tridiagonal rate matrix for ordered characters |
| `NestedOrdinal` | Nested ordinal rate matrix (state 0 transitions to all others) |

## Building

BEAST 3 dependencies are resolved from [Maven Central](https://central.sonatype.com/namespace/io.github.compevol) — no extra configuration needed.

```bash
mvn compile
mvn test
```

To develop against an unreleased SNAPSHOT, install BEAST 3 from source:

```bash
cd ~/Git/beast3
mvn install -DskipTests
```

## Running

```bash
# Validate an XML
mvn exec:exec -Dbeast.args="-validate examples/M3982.xml"

# Run an analysis
mvn exec:exec -Dbeast.args="-overwrite examples/M3982.xml"
```

## Examples

- `examples/M3982.xml` — Anolis lizard morphological analysis using BEAST 3 spec classes
- `examples/legacy-2.7/` — original BEAST 2.7 XML files (some require external packages)

## Releasing

### ZIP / CBAN

The `release.sh` script builds a BEAST package ZIP and optionally
creates a GitHub release for submission to [CBAN](https://github.com/CompEvol/CBAN):

```bash
./release.sh            # build ZIP only
./release.sh --release  # build ZIP + create GitHub release
```

### Maven Central

Pushing a `v*` tag triggers the CI workflow to publish to Maven Central.

BEAST 3 users can then install with:

```
Package Manager > Install from Maven > io.github.compevol:morph-models:1.3.0
```

Or from the command line:

```bash
packagemanager -maven io.github.compevol:morph-models:1.3.0
```

## References

Lewis, P. O. (2001). A likelihood approach to estimating phylogeny from discrete morphological character data. *Systematic Biology*, 50(6), 913–925.
