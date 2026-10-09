package com.jeremykenedy.earthboundorbit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

final class EarthboundOrbitSceneView extends GLSurfaceView implements GLSurfaceView.Renderer {
  private static final int LATITUDE_SEGMENTS = 48;
  private static final int LONGITUDE_SEGMENTS = 96;
  private static final int MAX_STARS = 950;
  private static final float EARTH_RADIUS = 1f;
  private static final float[] LIGHT_DIRECTION = {-0.45f, 0.32f, 0.84f};
  private static final String EARTH_VERTEX_SHADER =
      "attribute vec3 aPosition;"
          + "attribute vec2 aTexCoord;"
          + "uniform mat4 uMvp;"
          + "uniform mat4 uModel;"
          + "varying vec2 vTexCoord;"
          + "varying vec3 vNormal;"
          + "varying vec3 vPosition;"
          + "void main(){vec4 world=uModel*vec4(aPosition,1.0);"
          + "vPosition=world.xyz;vNormal=mat3(uModel)*aPosition;"
          + "vTexCoord=aTexCoord;gl_Position=uMvp*vec4(aPosition,1.0);} ";
  private static final String EARTH_FRAGMENT_SHADER =
      "precision mediump float;"
          + "uniform sampler2D uTexture;uniform vec3 uLight;uniform float uAtmosphere;"
          + "varying vec2 vTexCoord;varying vec3 vNormal;varying vec3 vPosition;"
          + "void main(){vec3 normal=normalize(vNormal);vec3 viewDir=normalize(-vPosition);"
          + "float lightAmount=dot(normal,normalize(uLight));"
          + "vec3 color=texture2D(uTexture,vTexCoord).rgb;"
          + "color=pow(max(color,vec3(0.0)),vec3(1.12));"
          + "float daylight=0.05+0.95*smoothstep(-0.16,0.26,lightAmount);"
          + "float night=1.0-smoothstep(-0.20,0.04,lightAmount);"
          + "vec3 nightTint=color*vec3(0.025,0.045,0.09);"
          + "color=mix(nightTint,color,daylight);"
          + "float rim=pow(1.0-max(dot(normal,viewDir),0.0),2.6);"
          + "color+=vec3(0.10,0.30,0.70)*rim*uAtmosphere*0.85;"
          + "color+=vec3(0.02,0.05,0.12)*night;"
          + "gl_FragColor=vec4(color,1.0);} ";
  private static final String ATMOSPHERE_FRAGMENT_SHADER =
      "precision mediump float;uniform float uStrength;"
          + "varying vec3 vNormal;varying vec3 vPosition;"
          + "void main(){vec3 n=normalize(vNormal);vec3 v=normalize(-vPosition);"
          + "float rim=pow(1.0-max(dot(n,v),0.0),3.2);"
          + "gl_FragColor=vec4(0.12,0.52,1.0,rim*uStrength*0.7);} ";
  private static final String ATMOSPHERE_VERTEX_SHADER =
      "attribute vec3 aPosition;uniform mat4 uMvp;uniform mat4 uModel;"
          + "varying vec3 vNormal;varying vec3 vPosition;"
          + "void main(){vec4 world=uModel*vec4(aPosition,1.0);"
          + "vPosition=world.xyz;vNormal=mat3(uModel)*aPosition;"
          + "gl_Position=uMvp*vec4(aPosition,1.0);} ";
  private static final String STAR_VERTEX_SHADER =
      "attribute vec3 aPosition;attribute float aSize;attribute float aAlpha;"
          + "uniform mat4 uMvp;varying float vAlpha;"
          + "void main(){gl_Position=uMvp*vec4(aPosition,1.0);"
          + "gl_PointSize=aSize;vAlpha=aAlpha;} ";
  private static final String STAR_FRAGMENT_SHADER =
      "precision mediump float;varying float vAlpha;"
          + "void main(){float r=length(gl_PointCoord-vec2(0.5));"
          + "float edge=1.0-smoothstep(0.28,0.5,r);"
          + "gl_FragColor=vec4(vec3(0.68,0.80,1.0),edge*vAlpha);} ";

  private final OrbitOptions options;
  private final FloatBuffer sphereVertices;
  private final FloatBuffer starVertices;
  private final ShortBuffer sphereIndices;
  private final int indexCount;
  private final float[] projection = new float[16];
  private final float[] view = new float[16];
  private final float[] camera = new float[16];
  private final float[] model = new float[16];
  private final float[] atmosphereModel = new float[16];
  private final float[] cameraPosition = new float[3];
  private final float[] mvp = new float[16];
  private final float[] starModel = new float[16];
  private final float[] starMvp = new float[16];
  private final long startedAt = System.nanoTime();
  private int earthProgram;
  private int atmosphereProgram;
  private int starProgram;
  private int earthPosition;
  private int earthTexCoord;
  private int earthMvp;
  private int earthModelUniform;
  private int earthLight;
  private int earthAtmosphere;
  private int earthSampler;
  private int atmospherePosition;
  private int atmosphereMvp;
  private int atmosphereModelUniform;
  private int atmosphereStrength;
  private int starPosition;
  private int starSize;
  private int starAlpha;
  private int starMvpLocation;
  private int earthTexture;
  private int width;
  private int height;
  private volatile boolean running;

  EarthboundOrbitSceneView(Context context) {
    super(context);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    options =
        OrbitOptions.resolve(
            preferences.getString("stars", "balanced"),
            preferences.getString("spin", "natural"),
            preferences.getString("atmosphere", "subtle"),
            preferences.getString("camera", "orbit"),
            preferences.getBoolean("randomize_all", false),
            new Random(System.currentTimeMillis()));
    sphereVertices =
        directFloatBuffer(
            OrbitalGeometry.createSphereVertices(LATITUDE_SEGMENTS, LONGITUDE_SEGMENTS));
    short[] indices = OrbitalGeometry.createSphereIndices(LATITUDE_SEGMENTS, LONGITUDE_SEGMENTS);
    sphereIndices = directShortBuffer(indices);
    indexCount = indices.length;
    float[] stars = new float[options.starCount * 5];
    OrbitalGeometry.fillStars(stars, options.starCount, 20261008L);
    starVertices = directFloatBuffer(stars);
    setEGLContextClientVersion(2);
    setPreserveEGLContextOnPause(true);
    setRenderer(this);
    setRenderMode(RENDERMODE_WHEN_DIRTY);
  }

  void start() {
    running = true;
    setRenderMode(RENDERMODE_CONTINUOUSLY);
  }

  void stop() {
    running = false;
    setRenderMode(RENDERMODE_WHEN_DIRTY);
  }

  @Override
  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    GLES20.glClearColor(0.005f, 0.009f, 0.025f, 1f);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    GLES20.glEnable(GLES20.GL_CULL_FACE);
    earthProgram = createProgram(EARTH_VERTEX_SHADER, EARTH_FRAGMENT_SHADER);
    atmosphereProgram = createProgram(ATMOSPHERE_VERTEX_SHADER, ATMOSPHERE_FRAGMENT_SHADER);
    starProgram = createProgram(STAR_VERTEX_SHADER, STAR_FRAGMENT_SHADER);
    cacheLocations();
    earthTexture = loadEarthTexture();
  }

  @Override
  public void onSurfaceChanged(GL10 unused, int width, int height) {
    this.width = width;
    this.height = height;
    GLES20.glViewport(0, 0, width, height);
    Matrix.perspectiveM(projection, 0, 38f, (float) width / Math.max(1, height), 0.1f, 40f);
  }

  @Override
  public void onDrawFrame(GL10 unused) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    if (!running || width == 0 || height == 0) return;
    float time = (System.nanoTime() - startedAt) / 1_000_000_000f;
    drawStars(time);
    drawEarth(time);
    if (options.atmosphereStrength > 0f) drawAtmosphere();
  }

  private void drawStars(float time) {
    Matrix.setIdentityM(starModel, 0);
    Matrix.rotateM(starModel, 0, time * 0.05f, 0.1f, 1f, 0.2f);
    Matrix.multiplyMM(view, 0, projection, 0, cameraView(time), 0);
    Matrix.multiplyMM(starMvp, 0, view, 0, starModel, 0);
    GLES20.glUseProgram(starProgram);
    GLES20.glUniformMatrix4fv(starMvpLocation, 1, false, starMvp, 0);
    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
    GLES20.glDepthMask(false);
    starVertices.position(0);
    GLES20.glEnableVertexAttribArray(starPosition);
    GLES20.glVertexAttribPointer(starPosition, 3, GLES20.GL_FLOAT, false, 20, starVertices);
    starVertices.position(3);
    GLES20.glEnableVertexAttribArray(starSize);
    GLES20.glVertexAttribPointer(starSize, 1, GLES20.GL_FLOAT, false, 20, starVertices);
    starVertices.position(4);
    GLES20.glEnableVertexAttribArray(starAlpha);
    GLES20.glVertexAttribPointer(starAlpha, 1, GLES20.GL_FLOAT, false, 20, starVertices);
    GLES20.glDrawArrays(GLES20.GL_POINTS, 0, options.starCount);
    GLES20.glDisableVertexAttribArray(starPosition);
    GLES20.glDisableVertexAttribArray(starSize);
    GLES20.glDisableVertexAttribArray(starAlpha);
    GLES20.glDepthMask(true);
    GLES20.glDisable(GLES20.GL_BLEND);
  }

  private void drawEarth(float time) {
    Matrix.setIdentityM(model, 0);
    Matrix.rotateM(model, 0, 23.4f, 0f, 0f, 1f);
    Matrix.rotateM(model, 0, time * options.spinDegreesPerSecond, 0f, 1f, 0f);
    Matrix.multiplyMM(view, 0, projection, 0, cameraView(time), 0);
    Matrix.multiplyMM(mvp, 0, view, 0, model, 0);
    GLES20.glUseProgram(earthProgram);
    GLES20.glUniformMatrix4fv(earthMvp, 1, false, mvp, 0);
    GLES20.glUniformMatrix4fv(earthModelUniform, 1, false, model, 0);
    GLES20.glUniform3f(earthLight, LIGHT_DIRECTION[0], LIGHT_DIRECTION[1], LIGHT_DIRECTION[2]);
    GLES20.glUniform1f(earthAtmosphere, options.atmosphereStrength);
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, earthTexture);
    GLES20.glUniform1i(earthSampler, 0);
    drawMesh(earthProgram);
  }

  private void drawAtmosphere() {
    System.arraycopy(model, 0, atmosphereModel, 0, 16);
    Matrix.scaleM(atmosphereModel, 0, 1.025f, 1.025f, 1.025f);
    Matrix.multiplyMM(mvp, 0, view, 0, atmosphereModel, 0);
    GLES20.glUseProgram(atmosphereProgram);
    GLES20.glUniformMatrix4fv(atmosphereMvp, 1, false, mvp, 0);
    GLES20.glUniformMatrix4fv(atmosphereModelUniform, 1, false, atmosphereModel, 0);
    GLES20.glUniform1f(atmosphereStrength, options.atmosphereStrength);
    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
    GLES20.glDepthMask(false);
    drawMesh(atmosphereProgram);
    GLES20.glDepthMask(true);
    GLES20.glDisable(GLES20.GL_BLEND);
  }

  private float[] cameraView(float time) {
    float phase = time * options.cameraOrbitSpeed;
    OrbitalGeometry.fillCameraPosition(phase, cameraPosition);
    Matrix.setLookAtM(
        camera, 0, cameraPosition[0], cameraPosition[1], cameraPosition[2], 0f, 0f, 0f, 0f, 1f, 0f);
    return camera;
  }

  private void drawMesh(int program) {
    sphereVertices.position(0);
    int position = program == earthProgram ? earthPosition : atmospherePosition;
    GLES20.glEnableVertexAttribArray(position);
    GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 20, sphereVertices);
    sphereVertices.position(3);
    int texCoord = program == earthProgram ? earthTexCoord : -1;
    if (texCoord >= 0) {
      GLES20.glEnableVertexAttribArray(texCoord);
      GLES20.glVertexAttribPointer(texCoord, 2, GLES20.GL_FLOAT, false, 20, sphereVertices);
    }
    sphereIndices.position(0);
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, sphereIndices);
    GLES20.glDisableVertexAttribArray(position);
    if (texCoord >= 0) GLES20.glDisableVertexAttribArray(texCoord);
  }

  private int loadEarthTexture() {
    BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
    decodeOptions.inScaled = false;
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.earth_clouds, decodeOptions);
    if (bitmap == null) throw new IllegalStateException("Earth texture could not be decoded");
    int[] textures = new int[1];
    GLES20.glGenTextures(1, textures, 0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
    GLES20.glTexParameteri(
        GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    bitmap.recycle();
    return textures[0];
  }

  private static FloatBuffer directFloatBuffer(float[] values) {
    FloatBuffer buffer =
        ByteBuffer.allocateDirect(values.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    buffer.put(values).position(0);
    return buffer;
  }

  private static ShortBuffer directShortBuffer(short[] values) {
    ShortBuffer buffer =
        ByteBuffer.allocateDirect(values.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
    buffer.put(values).position(0);
    return buffer;
  }

  private static int createProgram(String vertexSource, String fragmentSource) {
    int vertex = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource);
    int fragment = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
    int program = GLES20.glCreateProgram();
    GLES20.glAttachShader(program, vertex);
    GLES20.glAttachShader(program, fragment);
    GLES20.glLinkProgram(program);
    int[] status = new int[1];
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
    GLES20.glDeleteShader(vertex);
    GLES20.glDeleteShader(fragment);
    if (status[0] == 0) throw new IllegalStateException(GLES20.glGetProgramInfoLog(program));
    return program;
  }

  private static int compileShader(int type, String source) {
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, source);
    GLES20.glCompileShader(shader);
    int[] status = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
    if (status[0] == 0) throw new IllegalStateException(GLES20.glGetShaderInfoLog(shader));
    return shader;
  }

  private void cacheLocations() {
    earthPosition = GLES20.glGetAttribLocation(earthProgram, "aPosition");
    earthTexCoord = GLES20.glGetAttribLocation(earthProgram, "aTexCoord");
    earthMvp = GLES20.glGetUniformLocation(earthProgram, "uMvp");
    earthModelUniform = GLES20.glGetUniformLocation(earthProgram, "uModel");
    earthLight = GLES20.glGetUniformLocation(earthProgram, "uLight");
    earthAtmosphere = GLES20.glGetUniformLocation(earthProgram, "uAtmosphere");
    earthSampler = GLES20.glGetUniformLocation(earthProgram, "uTexture");
    atmospherePosition = GLES20.glGetAttribLocation(atmosphereProgram, "aPosition");
    atmosphereMvp = GLES20.glGetUniformLocation(atmosphereProgram, "uMvp");
    atmosphereModelUniform = GLES20.glGetUniformLocation(atmosphereProgram, "uModel");
    atmosphereStrength = GLES20.glGetUniformLocation(atmosphereProgram, "uStrength");
    starPosition = GLES20.glGetAttribLocation(starProgram, "aPosition");
    starSize = GLES20.glGetAttribLocation(starProgram, "aSize");
    starAlpha = GLES20.glGetAttribLocation(starProgram, "aAlpha");
    starMvpLocation = GLES20.glGetUniformLocation(starProgram, "uMvp");
  }
}
