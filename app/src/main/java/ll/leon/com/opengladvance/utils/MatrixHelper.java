package ll.leon.com.opengladvance.utils;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

/**
 * @author Leon
 * @Desc   Matrix操作
 */
public class MatrixHelper {
    public static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
    public static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
    public static float[] mMVPMatrix = new float[16];//最后起作用的总变换矩阵
    static float[] currMatrix = new float[16];//当前变换矩阵

    public static float[] lightLocationSun = new float[]{0, 0, 0};//太阳定位光光源位置
    public static FloatBuffer cameraFB, lightPositionFB;
    //标准的后进先出的栈。
    public static Stack<float[]> mStack = new Stack<>();//保护变换矩阵的栈
    public static boolean threadFlag =true;


    //获取不变换初始矩阵
    public static void setInitStack() {
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    //设置沿xyz轴移动
    public static void translate(float x, float y, float z) {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    //设置绕xyz轴转动
    public static void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    /**
     * 缩放
     * @param x
     * @param y
     * @param z
     */
    public static void scale(float x, float y, float z) {
        Matrix.scaleM(currMatrix,0,x,y,z);
    }


    /**
     * 作用：设置摄像机
     * 前三个：摄像机位置x y z
     * 中间三个：摄像机目标点x y z
     * 后三个：摄像机UP向量X分量，摄像机UP向量Y分量，摄像机UP向量Z分量
     */
    public static void setCamera(float cx, float cy, float cz, float tx, float ty, float tz, float upx, float upy, float upz) {
        Matrix.setLookAtM(mVMatrix, 0,
                cx, cy, cz,
                tx, ty, tz,
                upx, upy, upz);

        float[] cameraLocation = new float[3];//摄像机位置
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;

        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);

    }


    /**
     * 作用：设置透视投影参数
     * 前四个：near面的left，right，bottom，top
     * 后两个：near面距离，far面距离
     */
    public static void setProject(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //获取具体物体的总变换矩阵
    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    public static float[] multiplyMM() {
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        return mMVPMatrix;
    }

    public static void orthoM(int left, int right, float bottom, float top, int near, int far) {
        Matrix.orthoM(MatrixHelper.mProjMatrix, 0, left, right, bottom, top, near, far);
    }


    //保护变换矩阵
    public static void pushMatrix() {
        //把项压入堆栈顶部。
        mStack.push(currMatrix.clone());
    }

    //恢复变换矩阵
    public static void popMatrix() {
        //移除堆栈顶部的对象，并作为此函数的值返回该对象。
        currMatrix = mStack.pop();
    }

    /**
     * 获取具体物体的变换矩阵
     */
    public static float[] getMMatrix() {
        return currMatrix;
    }

    /**
     * 设置太阳光源位置的方法
     * @param x,y,z 光源位置
     */
    public static void setLightLocationSun(float x, float y, float z) {
        lightLocationSun[0] = x;
        lightLocationSun[1] = y;
        lightLocationSun[2] = z;

        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        lightPositionFB = llbb.asFloatBuffer();
        lightPositionFB.put(lightLocationSun);
        lightPositionFB.position(0);
    }


}
