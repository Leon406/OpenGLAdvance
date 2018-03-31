package ll.leon.com.opengladvance.mtl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 *
 * @author Leon
 */

//obj文件工具类
public class Obj3D {
    public FloatBuffer vert, vertNorl, vertTexture;
    public int vertCount;
    public MtlInfo mtl;

    private ArrayList<Float> tempVert, tempVertNorl, tempVertTexture;

    //添加顶点数据
    public void addVert(float d) {
        if (tempVert == null) {
            tempVert = new ArrayList<>();
        }
        tempVert.add(d);
    }

    //添加顶点纹理数据
    public void addVertTexture(float d) {
        if (tempVertTexture == null) {
            tempVertTexture = new ArrayList<>();
        }
        tempVertTexture.add(d);
    }

    public void addVertNorl(float d) {
        if (tempVertNorl == null) {
            tempVertNorl = new ArrayList<>();
        }
        tempVertNorl.add(d);
    }

    //加载数据缓冲
    public void dataLock() {
        if (tempVert != null) {
            setVert(tempVert);
            tempVert.clear();
            tempVert = null;
        }
        if (tempVertTexture != null) {
            setVertTexture(tempVertTexture);
            tempVertTexture.clear();
            tempVertTexture = null;
        }
        if (tempVertNorl != null) {
            setVertNorl(tempVertNorl);
            tempVertNorl.clear();
            tempVertNorl = null;
        }
    }

    //===================================================================================
//===================================================================================
//===================================================================================
    private void setVert(ArrayList<Float> data) {
        int size = data.size();
        ByteBuffer buffer = ByteBuffer.allocateDirect(size * 4);
        buffer.order(ByteOrder.nativeOrder());
        vert = buffer.asFloatBuffer();
        for (int i = 0; i < size; i++) {
            vert.put(data.get(i));
        }
        vert.position(0);
        vertCount = size / 3;
    }

    private void setVertNorl(ArrayList<Float> data) {
        int size = data.size();
        ByteBuffer buffer = ByteBuffer.allocateDirect(size * 4);
        buffer.order(ByteOrder.nativeOrder());
        vertNorl = buffer.asFloatBuffer();
        for (int i = 0; i < size; i++) {
            vertNorl.put(data.get(i));
        }
        vertNorl.position(0);
    }

    private void setVertTexture(ArrayList<Float> data) {
        int size = data.size();
        ByteBuffer buffer = ByteBuffer.allocateDirect(size * 4);
        buffer.order(ByteOrder.nativeOrder());
        vertTexture = buffer.asFloatBuffer();
        for (int i = 0; i < size; ) {
            vertTexture.put(data.get(i));
            i++;
            vertTexture.put(data.get(i));
            i++;
        }
        vertTexture.position(0);
    }

}
