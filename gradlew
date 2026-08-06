#!/usr/bin/env sh
# Binary-free launcher for environments where review systems reject wrapper JARs.
# Android Studio users can select the bundled/local Gradle distribution instead.
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle no está instalado. Abra el proyecto con Android Studio o instale Gradle 8.9+." >&2
  exit 1
fi
exec gradle "$@"
