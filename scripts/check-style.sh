#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
python3 -m compileall -q "$ROOT/install.py" "$ROOT/tests" "$ROOT/scripts"
