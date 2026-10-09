package com.jeremykenedy.earthboundorbit;

public final class SettingsValues {
  private SettingsValues() {}

  public static boolean isSupported(String key, String value) {
    if (key == null || value == null) return false;
    if ("stars".equals(key)) return oneOf(value, "sparse", "balanced", "dense", "packed", "random");
    if ("spin".equals(key)) return oneOf(value, "slow", "natural", "fast", "random");
    if ("atmosphere".equals(key)) return oneOf(value, "off", "subtle", "bright", "random");
    if ("camera".equals(key)) return oneOf(value, "steady", "drift", "orbit", "random");
    if ("randomize_all".equals(key)) return oneOf(value, "true", "false");
    return false;
  }

  private static boolean oneOf(String value, String... allowed) {
    for (String option : allowed) if (option.equals(value)) return true;
    return false;
  }
}
