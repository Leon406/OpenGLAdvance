package ll.leon.com.opengladvance.mtl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

import ll.leon.com.opengladvance.utils.BaseGLView;

/**
 *
 * @author Leon
 */

public class FGLView extends BaseGLView {

    public static final String ASSETS_3DRES_DRAGON_OBJ = "assets/3dres/dragon.obj";

    public FGLView(Context context) {
        super(context);
    }

    private List<ObjFilter> filters;

    @Override
    protected void onCreate() {
        List<Obj3D> model = MtlReader.readMultiObj(context, ASSETS_3DRES_DRAGON_OBJ);
        filters = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            ObjFilter f = new ObjFilter(getResources());
            f.setObj3D(model.get(i));
            filters.add(f);
        }
        for (ObjFilter f : filters) {
            f.create();
        }
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void onChange(int width, int height) {
        for (ObjFilter f : filters) {

            float[] matrix = new float[]{
                    1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            };
            Matrix.translateM(matrix, 0, 0, 0f, 0);
            Matrix.rotateM(matrix, 0, -90, 1, 0f, 0);

            Matrix.scaleM(matrix, 0, 0.03f, 0.03f, 0.03f);
            f.setMatrix(matrix);
        }
    }

    @Override
    protected void onFrameDraw() {
        for (ObjFilter f : filters) {
            Matrix.rotateM(f.getMatrix(), 0, 0.5f, 0, 0, 1);
            f.draw();
        }
    }
}
