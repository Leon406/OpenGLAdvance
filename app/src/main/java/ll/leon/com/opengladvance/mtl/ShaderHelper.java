package ll.leon.com.opengladvance.mtl;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import android.view.View;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 *  @author Leon
 */
public abstract class ShaderHelper {

    private final String TAG = "ES20_ERROR";
    public static final float[] OM = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };
    protected Resources mRes;

    public ShaderHelper(Resources mRes) {
        this.mRes = mRes;

    }

    protected int mProgram//程序句柄
            , mHPosition //顶点坐标句柄
            , mHCoord //纹理坐标句柄
            , mHMatrix //总变换矩阵句柄
            , mHTexture;//默认纹理贴图句柄

    private float[] matrix = Arrays.copyOf(OM, 16);

    private int textureType = 0;      //默认使用Texture2D0
    private int textureId = 0;


    protected final void createMyProgram(String vertexRes, String fragmentRes) {
        mProgram = createProgram(mRes,vertexRes, fragmentRes);
        mHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
    }

    /**
     * @desc    获取shader中 attribute 及 uniform 的引用
     */
    protected abstract void onCreate();
    public final void create() {
        onCreate();
    }


    public void draw() {
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }
    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public float[] getMatrix() {
        return matrix;
    }


    public final int getTextureId() {
        return textureId;
    }

    public final void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    protected void onUseProgram() {
        GLES20.glUseProgram(mProgram);
    }
    protected abstract void onDraw();

    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData() {
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId());
        GLES20.glUniform1i(mHTexture, textureType);
    }
    public final void checkGLError(String op) {

        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
//            throw new RuntimeException(op + ": glError " + error);
        }
    }

    /**
     * 获取attribution 变量id
     *
     * @param program
     * @param name
     * @return id
     */
    public final int getAttrId(int program, String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    /**
     * 获取uniform 变量id
     *
     * @param program
     * @param name
     * @return id
     */
    public final int getUniformId(int program, String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    public final int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader:" + shaderType);
                Log.e(TAG, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }


    public final int createProgram(String vertexSource, String fragmentSource) {
        int vertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            checkGLError("Attach Vertex Shader");
            GLES20.glAttachShader(program, fragment);
            checkGLError("Attach Fragment Shader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    public final int createProgram(Resources res, String vertexRes, String fragmentRes) {
        return createProgram(loadFromAssetsFile(vertexRes, res), loadFromAssetsFile(fragmentRes, res));
    }

    private String loadFromAssetsFile(String fname, Resources res) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = res.getAssets().open(fname);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }


    public final FloatBuffer getFloatBuffer(float[] vt) {
        ByteBuffer bb = ByteBuffer.allocateDirect(vt.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = bb.asFloatBuffer();
        floatBuffer.put(vt);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public final ByteBuffer getByteBuffer(byte[] indices) {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length);

        byteBuffer.put(indices);
        byteBuffer.position(0);

        return byteBuffer;
    }

    public final ShortBuffer getShortBuffer(short[] indices) {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length * 2);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(indices);
        shortBuffer.position(0);

        return shortBuffer;
    }


}
