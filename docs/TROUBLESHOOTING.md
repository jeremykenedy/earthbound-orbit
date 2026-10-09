# Troubleshooting

## The app does not appear in screensaver settings

Confirm installation with `adb -s TV_IP:5555 shell pm path com.jeremykenedy.earthboundorbit`. Some vendor settings menus omit third-party DreamServices or use a separate Ambient display page. The emulator result does not establish that every vendor exposes the same menu.

## The screensaver does not start automatically

Select Earthbound Orbit in the device's system screensaver or ambient display settings and check the device idle timeout. This app does not change selection, sleep timers, or system update settings. A vendor can also restrict third-party screensavers.

## The preview stays black or animation stops

Leave the preview Activity open briefly to allow the first frame. Return to the settings screen and start the preview again. Rendering is paused when the preview or screensaver is no longer visible to reduce idle work.

## A release update is rejected

Keep the original release signing key for future builds. An APK signed with a different certificate cannot update an installed copy. If you lost the signing key, Android requires uninstalling the existing app before installing a differently signed version; this removes app settings.

## Installer cannot find the TV

Enable ADB debugging on the TV, authorize the host, and verify `adb devices`. Pass the exact serial or `TV_IP:5555` using `--serial` when multiple devices are connected.
