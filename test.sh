#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build/tests"
mkdir -p "$OUT"
javac -source 8 -target 8 -d "$OUT" \
  "$ROOT/src/com/jeremykenedy/earthboundorbit/OrbitOptions.java" \
  "$ROOT/src/com/jeremykenedy/earthboundorbit/OrbitalGeometry.java" \
  "$ROOT/src/com/jeremykenedy/earthboundorbit/SettingsValues.java" \
  "$ROOT/tests/OrbitOptionsAssertions.java" \
  "$ROOT/tests/OrbitalGeometryAssertions.java"
java -ea -cp "$OUT" com.jeremykenedy.earthboundorbit.OrbitOptionsAssertions
python3 -m unittest -v tests.test_installer
