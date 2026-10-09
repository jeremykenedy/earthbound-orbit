package com.jeremykenedy.earthboundorbit;

import java.util.Random;

public final class OrbitOptions {
  public final int starCount;
  public final float spinDegreesPerSecond;
  public final float atmosphereStrength;
  public final float cameraOrbitSpeed;

  private OrbitOptions(
      int starCount, float spinDegreesPerSecond, float atmosphereStrength, float cameraOrbitSpeed) {
    this.starCount = starCount;
    this.spinDegreesPerSecond = spinDegreesPerSecond;
    this.atmosphereStrength = atmosphereStrength;
    this.cameraOrbitSpeed = cameraOrbitSpeed;
  }

  public static OrbitOptions resolve(
      String stars,
      String spin,
      String atmosphere,
      String camera,
      boolean randomizeAll,
      Random random) {
    String selectedStars =
        choose(stars, randomizeAll, random, "balanced", "sparse", "dense", "packed");
    String selectedSpin = choose(spin, randomizeAll, random, "natural", "slow", "fast");
    String selectedAtmosphere = choose(atmosphere, randomizeAll, random, "subtle", "off", "bright");
    String selectedCamera = choose(camera, randomizeAll, random, "orbit", "steady", "drift");
    return new OrbitOptions(
        countFor(selectedStars),
        spinFor(selectedSpin),
        atmosphereFor(selectedAtmosphere),
        cameraFor(selectedCamera));
  }

  static String choose(String selected, boolean randomizeAll, Random random, String... values) {
    if (randomizeAll || "random".equals(selected)) return values[random.nextInt(values.length)];
    for (String value : values) if (value.equals(selected)) return selected;
    return values[0];
  }

  static int countFor(String density) {
    if ("sparse".equals(density)) return 150;
    if ("dense".equals(density)) return 620;
    if ("packed".equals(density)) return 950;
    return 360;
  }

  static float spinFor(String speed) {
    if ("slow".equals(speed)) return 0.35f;
    if ("fast".equals(speed)) return 5.5f;
    return 1.4f;
  }

  static float atmosphereFor(String choice) {
    if ("off".equals(choice)) return 0f;
    if ("bright".equals(choice)) return 1f;
    return 0.58f;
  }

  static float cameraFor(String motion) {
    if ("steady".equals(motion)) return 0f;
    if ("drift".equals(motion)) return 0.008f;
    return 0.024f;
  }
}
