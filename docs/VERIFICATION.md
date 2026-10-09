# Device verification

## Emulator checks

| Device | OS/API | Resolution | Verified |
|---|---|---:|---|
| Google Android TV emulator (`sdk_google_atv64_arm64`) | Android 12 / API 31 | 1920x1080 | APK installed; OpenGL ES preview rendered; settings screen opened; provider schema and current settings queried; a supported setting update applied and reset; unsupported value rejected. Direct DreamService idle activation was unavailable in this emulator image. |

The application was compiled with Android SDK platform 36. The preview and settings screenshots below are direct emulator captures. The Android TV image did not resolve the system screensaver settings activity or start a DreamService through its available idle controls, so automatic activation remains unverified. The emulator's original Backdrop selection, enabled state, activation-on-sleep setting, and screen-off timeout were restored after the checks.

| Capture | SHA-256 |
|---|---|
| [Animated preview](screenshots/earthbound-orbit-preview.png) | `0540119ce868f92af07485be7ba2ccbf74f194dbfdb4abeae929d26a035ebf8f` |
| [Settings screen](screenshots/settings-android-tv.png) | `5d0ed719e603f08f125b34ee8d563411636d4211ce7d27c8d4e34a4a29197f87` |

## Physical device matrix

| Platform | Status | Test request |
|---|---|---|
| Fire TV | Untested on physical hardware | We are looking for a Fire TV owner to test install, screensaver selection and idle activation, remote settings, update, uninstall, and sleep/wake behavior. Please report model, Fire OS/API, display resolution, steps, and results through the [issue tracker](https://github.com/jeremykenedy/earthbound-orbit/issues). |
| Android TV | Untested on physical hardware | We are looking for an Android TV owner to test install, screensaver selection and idle activation, remote settings, update, uninstall, and sleep/wake behavior. Please report model, Android version/API, display resolution, steps, and results through the [issue tracker](https://github.com/jeremykenedy/earthbound-orbit/issues). |
| Google TV | Untested on physical hardware | We are looking for a Google TV owner to test install, ambient display selection and idle activation, remote settings, update, uninstall, and sleep/wake behavior. Please report model, Android version/API, display resolution, steps, and results through the [issue tracker](https://github.com/jeremykenedy/earthbound-orbit/issues). |

No physical TV has been used for testing. A device emulator does not establish Fire OS behavior, native 4K output, thermal behavior, or vendor-specific idle activation.
