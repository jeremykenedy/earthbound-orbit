# Architecture

Earthbound Orbit is a native Android application containing a `DreamService`, settings activity, preview activity, and app-owned settings provider. The DreamService presents the scene using OpenGL ES 2.0. It draws an equirectangular Earth texture on a sphere, computes day and night lighting in a fragment shader, adds a thin atmospheric rim, and renders a bounded point-star field behind the globe.

Mesh and star buffers are created once when the scene view is built and reused while it is visible. Earth, camera, and starfield motion use elapsed time, so animation speed does not depend on the render frame count. The GL surface switches to `RENDERMODE_WHEN_DIRTY` when the dream stops. The app uses no video decoding, wake lock, runtime network access, or background service.

`OrbitOptions` resolves persisted settings and random choices into bounded renderer values. `SettingsValues` validates updates through the content provider. Both are pure Java and covered by line and branch tests. Android lifecycle, shaders, and rendered pixels require the emulator checks documented in [verification](VERIFICATION.md).

The separate Python installer retrieves only this repository's release APK and checksum after a user starts it, verifies HTTPS GitHub release assets, trusted redirects, file size, and SHA-256, then invokes ADB. It does not change system screensaver or power settings.
