package com.jeremykenedy.earthboundorbit;

import java.util.Random;

final class OrbitalGeometry {
  private static final int STAR_STRIDE = 5;

  private OrbitalGeometry() {}

  static float[] createSphereVertices(int latitudeSegments, int longitudeSegments) {
    float[] vertices = new float[vertexCount(latitudeSegments, longitudeSegments) * STAR_STRIDE];
    int offset = 0;
    for (int row = 0; row <= latitudeSegments; row++) {
      double latitude = Math.PI * row / latitudeSegments;
      for (int column = 0; column <= longitudeSegments; column++) {
        double longitude = Math.PI * 2d * column / longitudeSegments;
        vertices[offset++] = (float) (Math.sin(latitude) * Math.cos(longitude));
        vertices[offset++] = (float) Math.cos(latitude);
        vertices[offset++] = (float) (Math.sin(latitude) * Math.sin(longitude));
        vertices[offset++] = (float) column / longitudeSegments;
        vertices[offset++] = 1f - (float) row / latitudeSegments;
      }
    }
    return vertices;
  }

  static short[] createSphereIndices(int latitudeSegments, int longitudeSegments) {
    vertexCount(latitudeSegments, longitudeSegments);
    short[] indices = new short[latitudeSegments * longitudeSegments * 6];
    int offset = 0;
    for (int row = 0; row < latitudeSegments; row++) {
      for (int column = 0; column < longitudeSegments; column++) {
        int first = row * (longitudeSegments + 1) + column;
        int second = first + longitudeSegments + 1;
        indices[offset++] = (short) first;
        indices[offset++] = (short) (first + 1);
        indices[offset++] = (short) second;
        indices[offset++] = (short) (first + 1);
        indices[offset++] = (short) (second + 1);
        indices[offset++] = (short) second;
      }
    }
    return indices;
  }

  private static int vertexCount(int latitudeSegments, int longitudeSegments) {
    if (latitudeSegments < 2 || longitudeSegments < 3) {
      throw new IllegalArgumentException("Sphere tessellation is too small");
    }
    long vertexCount = ((long) latitudeSegments + 1L) * ((long) longitudeSegments + 1L);
    if (vertexCount > Short.MAX_VALUE + 1L) {
      throw new IllegalArgumentException("Sphere exceeds 16-bit index capacity");
    }
    return (int) vertexCount;
  }

  static void fillStars(float[] destination, int count, long seed) {
    if (count < 0 || destination.length < (long) count * STAR_STRIDE) {
      throw new IllegalArgumentException("Star buffer is too small");
    }
    Random random = new Random(seed);
    for (int index = 0; index < count; index++) {
      float vertical = random.nextFloat() * 2f - 1f;
      float angle = random.nextFloat() * (float) (Math.PI * 2d);
      float radius = 5f + random.nextFloat() * 7f;
      float horizontal = (float) Math.sqrt(1f - vertical * vertical);
      int offset = index * STAR_STRIDE;
      destination[offset] = horizontal * (float) Math.cos(angle) * radius;
      destination[offset + 1] = vertical * radius;
      destination[offset + 2] = horizontal * (float) Math.sin(angle) * radius;
      destination[offset + 3] = 1.1f + random.nextFloat() * 1.9f;
      destination[offset + 4] = 0.45f + random.nextFloat() * 0.55f;
    }
  }

  static void fillCameraPosition(float phase, float[] destination) {
    if (destination.length < 3)
      throw new IllegalArgumentException("Camera buffer needs three values");
    destination[0] = (float) Math.sin(phase) * 0.32f;
    destination[1] = (float) Math.sin(phase * 0.61f) * 0.12f;
    destination[2] = 3.38f + (float) Math.cos(phase) * 0.08f;
  }
}
