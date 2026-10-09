# Configuration

Open Earthbound Orbit from the TV launcher or device screensaver settings. The remote-friendly settings page includes a full-screen preview. Values persist locally and apply the next time a screensaver session starts.

| Setting | Default | Choices |
|---|---|---|
| Star density | Balanced | Sparse, Balanced, Dense, Packed, Random |
| Earth rotation | Natural | Slow, Natural, Fast, Random |
| Atmosphere glow | Subtle | Off, Subtle, Bright, Random |
| Camera movement | Slow orbit | Steady, Gentle drift, Slow orbit, Random |
| Randomize all settings each start | Off | Off, On |

Each setting can be randomized on its own. Enabling global randomization selects every setting again when the DreamService starts.

The app's settings provider exposes its schema and current values at:

- `content://com.jeremykenedy.earthboundorbit.settings/schema`
- `content://com.jeremykenedy.earthboundorbit.settings/settings`

The schema cursor has `key`, `title`, `type`, `default`, `choices`, and `randomAllowed` columns. The settings cursor has `key` and `value`. To update one value, call `ContentResolver.update()` on `/settings` with `ContentValues` containing `key` and `value`. The provider rejects unknown keys and values. `randomize_all` uses a boolean string; other settings use strings from the listed choices.

These app-specific controls are available through this activity and provider. The Fire TV UI's screensaver picker selects installed dreams but does not currently expose a generic settings editor.
