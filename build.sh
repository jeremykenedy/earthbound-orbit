#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
TOOLS="$SDK/build-tools/36.0.0"
ANDROID_JAR="$SDK/platforms/android-36/android.jar"
OUT="$ROOT/build"
KEYSTORE="${EARTHBOUND_ORBIT_KEYSTORE:-$HOME/.android/earthbound-orbit.jks}"
KEYPASS="${EARTHBOUND_ORBIT_KEYPASS:-$HOME/.android/earthbound-orbit.pass}"
VERSION_NAME="${VERSION_NAME:-1.0.0}"
VERSION_CODE="${VERSION_CODE:-1}"
SIGN_APK="${SIGN_APK:-true}"

for tool in javac keytool openssl zip; do
  command -v "$tool" >/dev/null || { echo "Missing required tool: $tool" >&2; exit 1; }
done
[[ -f "$ANDROID_JAR" && -x "$TOOLS/aapt2" ]] || {
  echo "Install Android SDK platform 36 and build-tools 36.0.0." >&2; exit 1;
}
mkdir -p "$OUT"
if [[ "$SIGN_APK" == "true" ]]; then mkdir -p "$(dirname "$KEYSTORE")"; fi
rm -rf "$OUT/classes" "$OUT/dex" "$OUT/generated"
mkdir -p "$OUT/classes" "$OUT/dex"

if [[ "$SIGN_APK" == "true" && ! -f "$KEYSTORE" ]]; then
  [[ ! -e "$KEYPASS" ]] || { echo "Signing key is missing but its password file exists; restore the key." >&2; exit 1; }
  (umask 077 && openssl rand -hex 24 > "$KEYPASS")
  keytool -genkeypair -keystore "$KEYSTORE" -storepass:file "$KEYPASS" -alias earthbound-orbit \
    -keyalg RSA -keysize 3072 -validity 10950 -dname "CN=Earthbound Orbit Release"
  chmod 600 "$KEYSTORE"
fi
if [[ "$SIGN_APK" == "true" ]]; then
  [[ -s "$KEYPASS" ]] || { echo "Restore the signing-key password file." >&2; exit 1; }
fi

"$TOOLS/aapt2" compile --dir "$ROOT/res" -o "$OUT/res.zip"
"$TOOLS/aapt2" link -o "$OUT/unsigned.apk" -I "$ANDROID_JAR" \
  --manifest "$ROOT/AndroidManifest.xml" --min-sdk-version 23 --target-sdk-version 36 \
  --version-code "$VERSION_CODE" --version-name "$VERSION_NAME" \
  --java "$OUT/generated" "$OUT/res.zip"
find "$ROOT/src" "$OUT/generated" -name '*.java' > "$OUT/sources.txt"
javac -nowarn -Xlint:-options -source 8 -target 8 -bootclasspath "$ANDROID_JAR" \
  -d "$OUT/classes" @"$OUT/sources.txt"
find "$OUT/classes" -name '*.class' > "$OUT/classes.txt"
"$TOOLS/d8" --release --lib "$ANDROID_JAR" --min-api 23 --output "$OUT/dex" @"$OUT/classes.txt"
(cd "$OUT/dex" && zip -q -j "$OUT/unsigned.apk" classes.dex)
"$TOOLS/zipalign" -f 4 "$OUT/unsigned.apk" "$OUT/aligned.apk"
if [[ "$SIGN_APK" == "true" ]]; then
  "$TOOLS/apksigner" sign --ks "$KEYSTORE" --ks-pass "file:$KEYPASS" --ks-key-alias earthbound-orbit \
    --out "$OUT/earthbound-orbit.apk" "$OUT/aligned.apk"
  "$TOOLS/apksigner" verify "$OUT/earthbound-orbit.apk"
  (cd "$OUT" && shasum -a 256 earthbound-orbit.apk > earthbound-orbit.apk.sha256)
  printf 'Built signed APK: %s\n' "$OUT/earthbound-orbit.apk"
  cat "$OUT/earthbound-orbit.apk.sha256"
else
  cp "$OUT/aligned.apk" "$OUT/earthbound-orbit-unsigned.apk"
  printf 'Built unsigned APK: %s\n' "$OUT/earthbound-orbit-unsigned.apk"
fi
