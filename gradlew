#!/usr/bin/env sh
# Lightweight Gradle bootstrap for source packages where the binary wrapper JAR
# is intentionally not committed. Works on Linux, macOS and Termux.
set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.9
GRADLE_USER_HOME=${GRADLE_USER_HOME:-"$HOME/.gradle"}
DIST_ROOT="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin-local"
DIST_DIR="$DIST_ROOT/gradle-${GRADLE_VERSION}"
GRADLE_BIN="$DIST_DIR/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$DIST_ROOT"
  ZIP="$DIST_ROOT/gradle-${GRADLE_VERSION}-bin.zip"
  if [ ! -f "$ZIP" ]; then
    URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "$URL" -o "$ZIP"
    elif command -v wget >/dev/null 2>&1; then
      wget -O "$ZIP" "$URL"
    else
      echo "ERROR: curl or wget is required to download Gradle ${GRADLE_VERSION}." >&2
      exit 1
    fi
  fi
  if ! command -v unzip >/dev/null 2>&1; then
    echo "ERROR: unzip is required to extract Gradle ${GRADLE_VERSION}." >&2
    exit 1
  fi
  unzip -q -o "$ZIP" -d "$DIST_ROOT"
fi

exec "$GRADLE_BIN" -p "$APP_HOME" "$@"
