package ll.leon.com.opengladvance.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import android.view.View;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Description:
 */
public abstract class ShaderUtils {

    public static final String TAG = "ES20_ERROR";
    protected int mProgram;//自定义渲染管线着色器程序id

    public abstract void initData();

    /**
     * 初始化着色器
     * 1. 创建program
     * 2. 获取shader中变量ID
     *
     * @param v 获取上下文需要
     */
    public abstract void initShader(View v);

    /**
     * 绘制
     *
     * @param textureId
     */
    public abstract void drawSelf(int textureId);

    public void checkGLError(String op) {

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
    public int getAttrId(int program, String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    /**
     * 获取uniform 变量id
     *
     * @param program
     * @param name
     * @return id
     */
    public int getUniformId(int program, String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    /**
     * 启用画笔
     *
     * @param ids
     */
    public void enableVertexAttribArray(int... ids) {
        for (int id : ids) {
            GLES20.glEnableVertexAttribArray(id);
        }
    }

    /**
     * 取消画笔
     *
     * @param ids
     */
    public void disableVertexAttribArray(int... ids) {
        for (int id : ids) {
            GLES20.glDisableVertexAttribArray(id);
        }
    }

    public void uniformMatrix4fv(int ids, float[] data) {
        GLES20.glUniformMatrix4fv(ids, 1, false, data, 0);
    }

    public void uniform3fv(int ids, FloatBuffer data) {
        GLES20.glUniform3fv(ids, 1, data);
    }

    public void vertexAttribPointer(int ids, FloatBuffer data) {
        GLES20.glVertexAttribPointer
                (
                        ids,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        0,
                        data
                );
    }

    /**
     * 设置纹理参数
     */
    public void setTextureParam(){
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    /**
     * 角度余弦值
     *
     * @param degree
     * @return
     */
    public double cos(double degree) {
        return Math.cos(Math.toRadians(degree));
    }

    /**
     * 角度正弦值
     *
     * @param degree
     * @return
     */
    public double sin(double degree) {
        return Math.sin(Math.toRadians(degree));
    }

    public int loadShader(int shaderType, String source) {
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

    public int createProgram(String vertexSource, String fragmentSource) {
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

    public int createProgram(Resources res, String vertexRes, String fragmentRes) {
        return createProgram(loadFromAssetsFile(vertexRes, res), loadFromAssetsFile(fragmentRes, res));
    }

    public String loadFromAssetsFile(String fname, Resources res) {
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

    public FloatBuffer getFloatBuffer(float[] vt) {
        ByteBuffer bb = ByteBuffer.allocateDirect(vt.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = bb.asFloatBuffer();
        floatBuffer.put(vt);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public ByteBuffer getByteBuffer(byte[] indices) {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length);
        byteBuffer.put(indices);
        byteBuffer.position(0);

        return byteBuffer;
    }

    public ShortBuffer getShortBuffer(short[] indices) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length * 2);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(indices);
        shortBuffer.position(0);
        return shortBuffer;
    }


}
