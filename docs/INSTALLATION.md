# Installation and removal

The standalone installer downloads the latest stable APK and its SHA-256 file from this repository, verifies the checksum, then installs or updates Earthbound Orbit. It accepts release URLs from this repository and constrained GitHub asset redirect hosts. The Android app does not have network permission.

## Guided install or update

Connect the TV to ADB, then run:

```bash
python3 install.py --serial TV_IP:5555
```

The installer summarizes the operation and asks before making the change. It does not select the screensaver, alter power behavior, or modify automatic system updates. Select Earthbound Orbit in the device's screensaver or ambient display settings after installation.

## Local APK and unattended install

```bash
python3 install.py --serial TV_IP:5555 --apk build/earthbound-orbit.apk
python3 install.py --serial TV_IP:5555 --yes
```

`--yes` confirms install/update only. To remove the app non-interactively, both explicit destructive flags are required:

```bash
python3 install.py --serial TV_IP:5555 --uninstall --yes --force
```

Without these flags, uninstall asks for confirmation. Removal deletes Earthbound Orbit and its app-private settings, but leaves unrelated apps and system settings alone. Since the installer does not change system screensaver or power settings, no system values need restoration.

## Manual installation

Download `earthbound-orbit.apk` and `earthbound-orbit.apk.sha256` from a release. Verify the checksum with `shasum -a 256 -c earthbound-orbit.apk.sha256`, then install using `adb install -r earthbound-orbit.apk`. The installer preserves the package identity and signing certificate across updates.

## Platform limits

Amazon Fire OS and Android control screensaver activation, app updates, and power states. Earthbound Orbit does not override system protections or claim to prevent an OS update or rollback. Fire TV Toolkit offers separate reversible device controls; review its current documentation and preserve the original settings before changing any system behavior. Device menus and available controls vary by model and OS.
