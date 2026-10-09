package com.jeremykenedy.earthboundorbit;

import java.util.Arrays;

public final class OrbitalGeometryAssertions {
  private OrbitalGeometryAssertions() {}

  public static void main(String[] args) {
    verifiesSphereTextureCoordinatesAndBounds();
    verifiesSphereIndicesAndOutwardWinding();
    verifiesSphereRejectsUnsupportedDimensions();
    verifiesStarFieldIsStableAndBounded();
    verifiesCameraOrbitCoordinates();
  }

  private static void verifiesSphereTextureCoordinatesAndBounds() {
    int latitudeSegments = 48;
    int longitudeSegments = 96;
    float[] vertices = OrbitalGeometry.createSphereVertices(latitudeSegments, longitudeSegments);
    int stride = 5;
    int rowLength = (longitudeSegments + 1) * stride;
    check(vertices.length == (latitudeSegments + 1) * rowLength, "sphere vertex count");
    check(vertices[1] == 1f && vertices[4] == 1f, "north pole and texture origin");
    int southPole = latitudeSegments * rowLength;
    check(
        vertices[southPole + 1] == -1f && vertices[southPole + 4] == 0f,
        "south pole and texture end");
    for (int row = 0; row <= latitudeSegments; row++) {
      int first = row * rowLength;
      int last = first + longitudeSegments * stride;
      for (int axis = 0; axis < 3; axis++) {
        check(
            Math.abs(vertices[first + axis] - vertices[last + axis]) < 0.00001f,
            "texture seam closes");
      }
      check(
          vertices[first + 3] == 0f && vertices[last + 3] == 1f, "texture seam has both UV edges");
    }
    for (float vertex : vertices)
      check(!Float.isNaN(vertex) && !Float.isInfinite(vertex), "finite mesh vertex");
  }

  private static void verifiesSphereIndicesAndOutwardWinding() {
    int latitudeSegments = 48;
    int longitudeSegments = 96;
    short[] indices = OrbitalGeometry.createSphereIndices(latitudeSegments, longitudeSegments);
    float[] vertices = OrbitalGeometry.createSphereVertices(latitudeSegments, longitudeSegments);
    check(
        indices.length == latitudeSegments * longitudeSegments * 6, "sphere triangle index count");
    int vertexCount = vertices.length / 5;
    for (short index : indices)
      check((index & 0xffff) < vertexCount, "index references a mesh vertex");
    int cell = 20 * longitudeSegments + 11;
    int first = indices[cell * 6] & 0xffff;
    int second = indices[cell * 6 + 1] & 0xffff;
    int third = indices[cell * 6 + 2] & 0xffff;
    float[] a = position(vertices, first);
    float[] b = position(vertices, second);
    float[] c = position(vertices, third);
    float[] ab = {b[0] - a[0], b[1] - a[1], b[2] - a[2]};
    float[] ac = {c[0] - a[0], c[1] - a[1], c[2] - a[2]};
    float[] cross = {
      ab[1] * ac[2] - ab[2] * ac[1], ab[2] * ac[0] - ab[0] * ac[2], ab[0] * ac[1] - ab[1] * ac[0]
    };
    check(cross[0] * a[0] + cross[1] * a[1] + cross[2] * a[2] > 0f, "sphere face winds outward");
  }

  private static void verifiesSphereRejectsUnsupportedDimensions() {
    expectInvalid(() -> OrbitalGeometry.createSphereVertices(1, 96));
    expectInvalid(() -> OrbitalGeometry.createSphereIndices(48, 2));
    expectInvalid(() -> OrbitalGeometry.createSphereVertices(256, 256));
    expectInvalid(() -> OrbitalGeometry.createSphereIndices(256, 256));
  }

  private static void verifiesStarFieldIsStableAndBounded() {
    float[] empty = new float[0];
    OrbitalGeometry.fillStars(empty, 0, 11L);
    expectInvalid(() -> OrbitalGeometry.fillStars(empty, -1, 11L));
    expectInvalid(() -> OrbitalGeometry.fillStars(new float[4], 1, 11L));
    float[] first = new float[250 * 5];
    float[] sameSeed = new float[250 * 5];
    float[] otherSeed = new float[250 * 5];
    OrbitalGeometry.fillStars(first, 250, 11L);
    OrbitalGeometry.fillStars(sameSeed, 250, 11L);
    OrbitalGeometry.fillStars(otherSeed, 250, 12L);
    check(Arrays.equals(first, sameSeed), "same seed creates the same star field");
    check(!Arrays.equals(first, otherSeed), "different seeds create different star fields");
    for (int offset = 0; offset < first.length; offset += 5) {
      double distance =
          Math.sqrt(
              first[offset] * first[offset]
                  + first[offset + 1] * first[offset + 1]
                  + first[offset + 2] * first[offset + 2]);
      check(distance >= 5d && distance < 12d, "star distance stays in the shell");
      check(first[offset + 3] >= 1.1f && first[offset + 3] <= 3f, "star point size is bounded");
      check(first[offset + 4] >= 0.45f && first[offset + 4] <= 1f, "star opacity is bounded");
    }
  }

  private static void verifiesCameraOrbitCoordinates() {
    float[] camera = new float[3];
    OrbitalGeometry.fillCameraPosition(0f, camera);
    check(camera[0] == 0f && camera[1] == 0f && camera[2] == 3.46f, "steady camera origin");
    OrbitalGeometry.fillCameraPosition((float) (Math.PI / 2d), camera);
    check(Math.abs(camera[0] - 0.32f) < 0.00001f, "camera orbits around the globe");
    check(Math.abs(camera[2] - 3.38f) < 0.00001f, "camera distance changes gently");
    expectInvalid(() -> OrbitalGeometry.fillCameraPosition(0f, new float[2]));
  }

  private static float[] position(float[] vertices, int index) {
    int offset = index * 5;
    return new float[] {vertices[offset], vertices[offset + 1], vertices[offset + 2]};
  }

  private static void expectInvalid(Runnable operation) {
    try {
      operation.run();
      throw new AssertionError("invalid geometry was accepted");
    } catch (IllegalArgumentException expected) {
      check(expected.getMessage() != null, "invalid input is explained");
    }
  }

  private static void check(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
}
