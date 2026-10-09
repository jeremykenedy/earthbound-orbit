# Testing and coverage

Run all local checks from the repository root:

```bash
bash test.sh
bash scripts/test-python-coverage.sh
bash scripts/test-coverage.sh
bash build.sh
python3 scripts/check-docs.py
python3 scripts/check-privacy.py
bash scripts/check-style.sh
```

The Java coverage task measures the pure settings resolver, provider-value validator, sphere mesh and star-field geometry, and camera-position math. JaCoCo requires 100% line and branch coverage for those classes. The installer has a separate 100% line and branch coverage gate. Android framework, OpenGL calls, and shaders are not executed by the host JVM; they are checked through APK metadata validation, emulator installation, settings interaction, provider query/update, DreamService activation, and inspected screenshots.

The tests check supported and unknown setting values, individual and global randomization, checksum parsing and rejection, release URL restrictions, trusted redirect handling, ambiguous devices, cancellation, explicit serial selection, install/update, and protected non-interactive uninstall. They do not claim physical vendor-menu or 4K hardware coverage.
