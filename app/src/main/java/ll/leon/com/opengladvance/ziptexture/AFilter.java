package ll.leon.com.opengladvance.ziptexture;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;


public abstract class AFilter {

    public static final int TYPE_FITXY = 0;
    public static final int TYPE_CENTERCROP = 1;
    public static final int TYPE_CENTERINSIDE = 2;
    public static final int TYPE_FITSTART = 3;
    public static final int TYPE_FITEND = 4;
    public static final int TYPE = 0x01;
    private static final String TAG = "Filter";

    public static boolean DEBUG = true;
    /**
     * 单位矩阵
     */
    public static final float[] OM = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };
    ;
    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;
    protected Resources mRes;

    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;


    public float[] matrix = Arrays.copyOf(OM, 16);

    //顶点坐标
    private float pos[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f,
    };

    //纹理坐标
    private float[] coord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };


    //（三）1初始化顶点坐标和纹理坐标
    public AFilter(Resources mRes) {
        this.mRes = mRes;
        //初始化顶点坐标和纹理坐标
        mVerBuffer = initBuffer(pos);
        mTexBuffer = initBuffer(coord);
    }

    /**
     * Buffer初始化
     */
    protected FloatBuffer initBuffer(float[] vc) {
        ByteBuffer a = ByteBuffer.allocateDirect(vc.length * 4);
        a.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = a.asFloatBuffer();
        buffer.put(vc);
        buffer.position(0);
        return buffer;
    }


    // 获取并加载着色器 然后创建创建GL程序 的 方法（Start）
//    =======================================================================================
//    =======================================================================================
//    =======================================================================================
    protected final void createProgramByAssetsFile(String vertex, String fragment) {
        createProgram(uRes(mRes, vertex), uRes(mRes, fragment));
    }

    private final void createProgram(String vertex, String fragment) {
        mProgram = uCreateGlProgram(vertex, fragment);
        mHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
    }

    //创建GL程序
    private static int uCreateGlProgram(String vertexSource, String fragmentSource) {
        int vertex = uLoadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = uLoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            GLES20.glAttachShader(program, fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                glError(1, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    //加载shader
    private static int uLoadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                glError(1, "Could not compile shader:" + shaderType);
                glError(1, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private static void glError(int code, Object index) {
        if (DEBUG && code != 0) {
            Log.e(TAG, "glError:" + code + "---" + index);
        }
    }

    //通过路径加载Assets中的文本内容
    private static String uRes(Resources mRes, String path) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = mRes.getAssets().open(path);
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

    //=======================================================================================
    //=======================================================================================
    //=======================================================================================


    public int textureId = 0;
    public int[] texture;

    public void createEtcTexture() {
        texture=new int[2];
        //3.1.1生成纹理
        GLES20.glGenTextures(2, texture, 0);
        for (int i = 0; i < texture.length; i++) {
            //3.1.2生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i]);
            //3.1.3设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //3.1.4设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //3.1.5设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //3.1.6设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //3.1.7根据以上指定的参数，生成一个2D纹理
        }
        textureId = (texture[0]);
    }

    public static void getMatrix(float[] matrix, int type, int imgWidth, int imgHeight, int viewWidth,
                                 int viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (type == TYPE_FITXY) {
                Matrix.orthoM(projection, 0, -1, 1, -1, 1, 1, 3);
                Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
                Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
            }
            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            if (sWhImg > sWhView) {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 1, 1 - 2 * sWhImg / sWhView, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, -1, 1, -1, 2 * sWhImg / sWhView - 1, 1, 3);
                        break;
                }
            } else {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 2 * sWhView / sWhImg - 1, -1, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, 1 - 2 * sWhView / sWhImg, 1, -1, 1, 1, 3);
                        break;
                }
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

}
