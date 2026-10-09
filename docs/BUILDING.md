# Building

## Requirements

- JDK 17 or newer
- Android SDK platform 36 and build-tools 36.0.0
- Python 3.10 or newer for the installer tests
- Gradle 8.14.3 is downloaded by the coverage script and checked against a pinned SHA-256

The Android application itself has no third-party runtime dependency. `build.sh` compiles resources and Java source with the Android SDK and aligns the APK. By default it signs a local build with the persistent local key and writes its SHA-256 file. Set `SIGN_APK=false` to build an unsigned review APK, as CI does.

```bash
bash build.sh
```

For local builds, the script creates a unique release keystore and password under `~/.android/earthbound-orbit.*` if one does not exist. Back up both files securely. Never commit or publish them. CI creates disposable credentials and publishes no signed artifact.

Use `VERSION_NAME` and `VERSION_CODE` for a development build when needed. The public release keystore is maintained outside the repository and must be restored before signing future updates.

See [testing](TESTING.md) for the complete verification commands.
