package ll.leon.com.opengladvance.camera;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * 创建时间：2018/01/18
 *
 * @author Leon
 */

public class Utils {

    private static final String TAG = "Filter";

    public static boolean DEBUG = true;

    /**
     * 程序句柄
     */
    protected static int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected static int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected static int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected static int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected static int mHTexture;

    protected static Resources mRes;


    /**
     * 顶点坐标Buffer
     */
    protected static FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected static FloatBuffer mTexBuffer;
    private static int textureType = 0;      //默认使用Texture2D0
    public static int textureId = 0;
    //顶点坐标
    private static float pos[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f,
    };
    //纹理坐标
    private static float[] coord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private static int mHCoordMatrix;

    private Utils(Resources mRes) {
        this.mRes = mRes;
        initBuffer();
    }

    public static final void create(Resources res) {
        mRes = res;
        initBuffer();
        createProgramByAssetsFile("shader/oes_base_vertex.glsl", "shader/oes_base_fragment.glsl");
        mHCoordMatrix = GLES20.glGetUniformLocation(mProgram, "vCoordMatrix");
    }

    public static void draw() {
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }


    private static final void createProgram(String vertex, String fragment) {
        mProgram = uCreateGlProgram(vertex, fragment);
        mHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
    }

    protected static final void createProgramByAssetsFile(String vertex, String fragment) {
        createProgram(uRes(mRes, vertex), uRes(mRes, fragment));
    }

    /**
     * Buffer初始化
     */
    protected static void initBuffer() {
        ByteBuffer a = ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer = a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b = ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer = b.asFloatBuffer();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    protected static void onUseProgram() {
        GLES20.glUseProgram(mProgram);
    }

    /**
     * 启用顶点坐标和纹理坐标进行绘制
     */
    protected static void onDraw() {
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }

    /**
     * 清除画布
     */
    protected static void onClear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 单位矩阵
     */
    public static final float[] OM = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    //变换矩阵
    private static float[] matrix = Arrays.copyOf(OM, 16);
    private static float[] mCoordMatrix = Arrays.copyOf(OM, 16);

    public static void setMatrix(float[] matrixs) {
        matrix = matrixs;
    }

    /**
     * 设置其他扩展数据
     */
    protected static void onSetExpandData() {
        GLES20.glUniformMatrix4fv(mHCoordMatrix, 1, false, mCoordMatrix, 0);
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
    }

    /**
     * 绑定默认纹理
     */
    protected static void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mHTexture, textureType);
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

    //通过传入图片宽高和预览宽高，计算变换矩阵，得到的变换矩阵是预览类似ImageView的centerCrop 效果
    //centerCrop:
    //均衡的缩放图像（保持图像原始比例），使图片的两个坐标（宽、高）都大于等于 相应的视图坐标（负的内边距）。
    // 图像则位于视图的中央。 在 XML 中可以使用的语法：android:scaleType="centerCrop"。
    public static void getShowMatrix(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            //得到预览宽高比
            float sWhView = (float) viewWidth / viewHeight;
            //得到图片宽高比
            float sWhImg = (float) imgWidth / imgHeight;
            //投影矩阵
            float[] projection = new float[16];
            //照相机矩阵
            float[] camera = new float[16];
            if (sWhImg > sWhView) {
                //设置正交投影
                Matrix.orthoM(projection//生成矩阵元素的 float[] 数组;
                        , 0//矩阵数组的起始偏移量;
                        //近平面的 左, 右, 下, 上 的值;
                        , -sWhView / sWhImg, sWhView / sWhImg, -1, 1,
                        1//近平面 与 视点之间的距离;
                        , 3);//远平面 与 视点之间的距离;
            } else {
                //设置正交投影
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            }
            Matrix.setLookAtM(camera
                    , 0
                    , 0, 0, 1//摄像机位置
                    , 0, 0, 0//摄像机目标点
                    , 0, 1, 0);//摄像机UP向量
            //计算投影和视口变换(将两个矩阵相乘, 并存入到第三个矩阵中)
            Matrix.multiplyMM(matrix, 0//将两个矩阵相乘, 并存入到第三个矩阵中
                    , projection, 0//将两个矩阵相乘, 并存入到第三个矩阵中
                    , camera, 0);//⑤⑥ 参数 : 右矩阵, 结果矩阵起始位移
        }
    }

    /*
     * 旋转操作
     * */
    public static float[] rotate(float[] m, float angle) {
        //第一个0代表偏移量，第二个90.0f代表旋转角度，后面的三个参数依次为按x, y, z轴旋转
        Matrix.rotateM(m, 0, angle, 0, 0, 1);
        return m;
    }

    /*
     * 镜像操作
     * */
    public static float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) {
            //第一个0代表缩放因子，后面三个数据分别代表x, y, z轴
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }

    /*
     * 创建纹理
     * 相机预览使用EXTERNAL_OES纹理，创建方式与2D纹理创建基本相同：
     * */
    public static int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}
