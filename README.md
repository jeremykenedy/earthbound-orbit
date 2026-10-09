<p align="center">
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="art/banner-dark.svg">
        <source media="(prefers-color-scheme: light)" srcset="art/banner-light.svg">
        <img src="art/banner-light.svg" alt="Earthbound Orbit, an animated Earth from space with no ads, analytics, or tracking" width="800">
    </picture>
</p>

<p align="center">A continuously animated view of Earth from orbit for Fire TV, Android TV, and Google TV.</p>

<p align="center">
    <a href="https://github.com/jeremykenedy/earthbound-orbit/releases"><img src="https://img.shields.io/github/downloads/jeremykenedy/earthbound-orbit/total" alt="GitHub release downloads"></a>
    <a href="https://github.com/jeremykenedy/earthbound-orbit/releases"><img src="https://img.shields.io/github/v/release/jeremykenedy/earthbound-orbit?label=latest%20release" alt="Latest release"></a>
    <a href="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/ci.yml"><img src="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/ci.yml/badge.svg" alt="Build, tests, and coverage"></a>
    <a href="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/style.yml"><img src="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/style.yml/badge.svg" alt="Code style"></a>
    <a href="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/docs.yml"><img src="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/docs.yml/badge.svg" alt="Documentation"></a>
    <a href="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/security.yml"><img src="https://github.com/jeremykenedy/earthbound-orbit/actions/workflows/security.yml/badge.svg" alt="Privacy and source checks"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache--2.0-blue.svg" alt="Apache-2.0 license"></a>
    <a href="https://github.com/jeremykenedy"><img src="https://img.shields.io/github/followers/jeremykenedy?label=Follow&style=social" alt="Follow on GitHub"></a>
    <a href="https://github.com/jeremykenedy/earthbound-orbit" title="Open the repository and click Star"><img src="https://img.shields.io/badge/Star-this%20repo-yellow?logo=github&style=social" alt="Star this repo"></a>
    <a href="https://github.com/jeremykenedy/earthbound-orbit/stargazers"><img src="https://img.shields.io/github/stars/jeremykenedy/earthbound-orbit?style=social" alt="Star count"></a>
    <a href="https://github.com/sponsors/jeremykenedy"><img src="https://img.shields.io/badge/Sponsor-jeremykenedy-EA4AAA?logo=githubsponsors&logoColor=white" alt="Sponsor"></a>
</p>

Show some love by starring this repository on GitHub.

## Table of contents

- [Privacy](#privacy)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Fire TV Toolkit](#fire-tv-toolkit)
- [Configuration](#configuration)
- [Screenshots](#screenshots)
- [Device verification](#device-verification)
- [Building and testing](#building-and-testing)
- [Documentation](#documentation)
- [Release notes](#release-notes)
- [License](#license)

## Privacy

The screensaver has no `INTERNET` permission and makes no network requests. It contains no ads, analytics, telemetry, crash reporting, tracking, or reporting code. Settings stay on the device. The standalone installer contacts GitHub only when you run it to download a release APK and checksum; it does not send device or usage data.

## Features

- A textured Earth rotates continuously beneath an illuminated atmosphere.
- Stars drift gently in the background while the camera can remain steady, drift, or orbit.
- Set star density, Earth rotation speed, atmosphere strength, and camera movement independently.
- Choose Random for any setting or randomize all choices at each screensaver start.
- Open a remote-friendly settings page and full-screen preview from the TV launcher.
- The renderer uses OpenGL ES 2.0 and a bundled NASA Earth texture. It has no video decoder or runtime dependency.

## Requirements

- Android 6.0 (API 23) or newer with DreamService and OpenGL ES 2.0 support.
- ADB for installation from a computer.
- Android SDK platform 36 and build-tools 36.0.0 for local builds.

See [device verification](docs/VERIFICATION.md) for the tested Android TV emulator and the current Fire TV, Android TV, and Google TV test requests. Emulator results do not establish physical-device behavior, 4K composition, or Fire OS idle startup.

## Installation

Connect the TV to the same network as the computer, enable ADB debugging, and run:

```bash
python3 install.py --serial TV_IP:5555
```

The installer downloads the latest signed release and verifies its SHA-256 checksum. It installs or updates the app but does not change system screensaver selection, sleep timers, app update settings, or OS protections. Select Earthbound Orbit in the device's screensaver or ambient display settings. See [installation and removal](docs/INSTALLATION.md) for manual, update, and uninstall steps.

## Fire TV Toolkit

[Fire TV Toolkit](https://github.com/jeremykenedy/fire-tv-toolkit) provides a guided way to install, update, remove, and select screensavers. It also offers Fire TV commands for sleep and screensaver timers, launcher and Home behavior, supported screensaver safeguards, the Alexa deep-sleep fix, and restoring managed settings. Those controls are device- and Fire OS-dependent; the Toolkit does not make this app change system settings or guarantee Amazon cannot revert them.

Clone the Toolkit and run its setup:

```bash
git clone https://github.com/jeremykenedy/fire-tv-toolkit.git
cd fire-tv-toolkit
node setup.js
```

Install and select Earthbound Orbit:

```bash
firetv-screensavers --install=earthbound-orbit --yes
screensaver --set=earthbound-orbit
```

Repeat the install command to update it. To remove it from a script, run:

```bash
firetv-screensavers --uninstall=earthbound-orbit --force
```

The `--force` flag is required for non-interactive removal. See the Toolkit's [command reference](https://github.com/jeremykenedy/fire-tv-toolkit/blob/main/docs/COMMANDS.md) for prompts and device-specific options.

## Configuration

Earthbound Orbit stores its settings locally. Each choice supports Random, and global randomization selects all settings again each time the screensaver starts. See [configuration](docs/CONFIGURATION.md) for defaults and the host settings provider.

## Screenshots

<p align="center">
    <img src="docs/screenshots/earthbound-orbit-preview.png" alt="Animated Earth with blue atmosphere and stars in orbit" width="49%">
    <img src="docs/screenshots/settings-android-tv.png" alt="Earthbound Orbit settings on an Android TV emulator" width="49%">
</p>

These are direct captures from the running app. The device and settings are listed in [verification](docs/VERIFICATION.md).

## Device verification

| Platform | Result |
|---|---|
| Android TV emulator | Preview animation, settings screen, and settings provider verified on Android 12/API 31 at 1920x1080. DreamService idle activation was not available in the emulator image. Details are in [verification](docs/VERIFICATION.md). |
| Physical Fire TV | Not tested. We are looking for a Fire TV owner to test installation, selection, idle activation, and remote settings. Please report model, Fire OS/API, resolution, and results in the [issue tracker](https://github.com/jeremykenedy/earthbound-orbit/issues). |
| Physical Android TV | Not tested. We are looking for an Android TV owner to test installation, selection, idle activation, and remote settings. Please report model, Android version/API, resolution, and results in the [issue tracker](https://github.com/jeremykenedy/earthbound-orbit/issues). |
| Physical Google TV | Not tested. We are looking for a Google TV owner to test installation, selection, idle activation, and remote settings. Please report model, Android version/API, resolution, and results in the [issue tracker](https://github.com/jeremykenedy/earthbound-orbit/issues). |

No physical TV will be used for testing without Jeremy's request. No claim is made about physical 4K rendering or thermal behavior until those tests are performed.

## Building and testing

```bash
bash build.sh
bash test.sh
bash scripts/test-python-coverage.sh
bash scripts/test-coverage.sh
```

Host tests verify setting resolution and provider validation with 100% line and branch coverage. Installer logic has its own 100% line and branch coverage gate. Android framework and OpenGL rendering are verified through APK inspection and emulator tests, not counted in host coverage. See [testing](docs/TESTING.md), [building](docs/BUILDING.md), and [architecture](docs/ARCHITECTURE.md).

## Documentation

- [Installation and removal](docs/INSTALLATION.md)
- [Configuration and host settings](docs/CONFIGURATION.md)
- [Building](docs/BUILDING.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Testing and coverage](docs/TESTING.md)
- [Device verification](docs/VERIFICATION.md)
- [CI](docs/CI.md)
- [Troubleshooting](docs/TROUBLESHOOTING.md)
- [Release process](docs/RELEASING.md)
- [Release notes](docs/releases/v1.0.0.md)

## Release notes

See [Earthbound Orbit 1.0.0](docs/releases/v1.0.0.md) for the initial stable release.

## License

Earthbound Orbit is licensed under the [Apache License, Version 2.0](LICENSE). NASA source imagery is credited in [NOTICE](NOTICE).
