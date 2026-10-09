package com.jeremykenedy.earthboundorbit;

import java.util.Random;

public final class OrbitOptionsAssertions {
  private OrbitOptionsAssertions() {}

  public static void main(String[] args) {
    verifiesStarDensityRanges();
    verifiesEarthRotationRates();
    verifiesAtmosphereStrengths();
    verifiesCameraMotionRates();
    verifiesRandomAndFallbackSelections();
    verifiesSettingsProviderValidation();
    OrbitalGeometryAssertions.main(new String[0]);
    System.out.println("Earthbound Orbit settings tests passed.");
  }

  private static void verifiesStarDensityRanges() {
    check(OrbitOptions.countFor("sparse") == 150, "sparse stars");
    check(OrbitOptions.countFor("balanced") == 360, "balanced stars");
    check(OrbitOptions.countFor("dense") == 620, "dense stars");
    check(OrbitOptions.countFor("packed") == 950, "packed stars");
    check(OrbitOptions.countFor("other") == 360, "fallback star density");
  }

  private static void verifiesEarthRotationRates() {
    check(OrbitOptions.spinFor("slow") == 0.35f, "slow rotation");
    check(OrbitOptions.spinFor("natural") == 1.4f, "natural rotation");
    check(OrbitOptions.spinFor("fast") == 5.5f, "fast rotation");
    check(OrbitOptions.spinFor("other") == 1.4f, "fallback rotation");
  }

  private static void verifiesAtmosphereStrengths() {
    check(OrbitOptions.atmosphereFor("off") == 0f, "atmosphere off");
    check(OrbitOptions.atmosphereFor("subtle") == 0.58f, "subtle atmosphere");
    check(OrbitOptions.atmosphereFor("bright") == 1f, "bright atmosphere");
    check(OrbitOptions.atmosphereFor("other") == 0.58f, "fallback atmosphere");
  }

  private static void verifiesCameraMotionRates() {
    check(OrbitOptions.cameraFor("steady") == 0f, "steady camera");
    check(OrbitOptions.cameraFor("drift") == 0.008f, "gentle camera drift");
    check(OrbitOptions.cameraFor("orbit") == 0.024f, "camera orbit");
    check(OrbitOptions.cameraFor("other") == 0.024f, "fallback camera motion");
  }

  private static void verifiesRandomAndFallbackSelections() {
    Random random = new Random(42L);
    OrbitOptions selected =
        OrbitOptions.resolve("sparse", "fast", "bright", "steady", false, random);
    check(
        selected.starCount == 150
            && selected.spinDegreesPerSecond == 5.5f
            && selected.atmosphereStrength == 1f
            && selected.cameraOrbitSpeed == 0f,
        "selected values are resolved");
    OrbitOptions fallback = OrbitOptions.resolve("bad", "bad", "bad", "bad", false, random);
    check(
        fallback.starCount == 360
            && fallback.spinDegreesPerSecond == 1.4f
            && fallback.atmosphereStrength == 0.58f
            && fallback.cameraOrbitSpeed == 0.024f,
        "invalid values use defaults");
    for (int index = 0; index < 200; index++) {
      OrbitOptions randomOptions =
          OrbitOptions.resolve("random", "random", "random", "random", false, random);
      check(
          randomOptions.starCount >= 150
              && randomOptions.starCount <= 950
              && randomOptions.spinDegreesPerSecond > 0f
              && randomOptions.atmosphereStrength >= 0f
              && randomOptions.atmosphereStrength <= 1f
              && randomOptions.cameraOrbitSpeed >= 0f
              && randomOptions.cameraOrbitSpeed <= 0.024f,
          "individual random choices stay within bounds");
    }
    OrbitOptions globalRandom =
        OrbitOptions.resolve("sparse", "slow", "off", "steady", true, random);
    check(
        globalRandom.starCount >= 150
            && globalRandom.spinDegreesPerSecond > 0f
            && globalRandom.atmosphereStrength >= 0f
            && globalRandom.cameraOrbitSpeed >= 0f,
        "global randomization overrides saved choices");
    check(
        OrbitOptions.choose("natural", false, random, "slow", "natural", "fast").equals("natural"),
        "valid choice is preserved");
  }

  private static void verifiesSettingsProviderValidation() {
    check(!SettingsValues.isSupported(null, "balanced"), "null key rejected");
    check(!SettingsValues.isSupported("stars", null), "null value rejected");
    check(SettingsValues.isSupported("stars", "packed"), "star density accepted");
    check(SettingsValues.isSupported("spin", "fast"), "spin accepted");
    check(SettingsValues.isSupported("atmosphere", "bright"), "atmosphere accepted");
    check(SettingsValues.isSupported("camera", "orbit"), "camera setting accepted");
    check(SettingsValues.isSupported("randomize_all", "true"), "global random accepted");
    check(!SettingsValues.isSupported("stars", "many"), "invalid density rejected");
    check(!SettingsValues.isSupported("unknown", "x"), "unknown key rejected");
  }

  private static void check(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
}
