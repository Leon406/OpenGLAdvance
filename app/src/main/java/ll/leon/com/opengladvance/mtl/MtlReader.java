package ll.leon.com.opengladvance.mtl;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leon
 */
//读取mtl文件
public class MtlReader {

    public static List<Obj3D> readMultiObj(Context context, String file) {
        boolean isAssets;//判断要读取文件的位置
        ArrayList<Obj3D> data = new ArrayList<>();
        ArrayList<Float> oVs = new ArrayList<>();//原始顶点坐标列表
        ArrayList<Float> oVNs = new ArrayList<>();    //原始顶点法线列表
        ArrayList<Float> oVTs = new ArrayList<>();    //原始贴图坐标列表
        HashMap<String, MtlInfo> mTls = null;
        //存储材质名 和 材质内容数据
        HashMap<String, Obj3D> mObjs = new HashMap<>();
        Obj3D nowObj = null;
        MtlInfo nowMtl = null;
        try {
            String parent;
            InputStream inputStream;
            //确定此字符串的开头是否与指定的字符串匹配。
            if (file.startsWith("assets/")) {//如果在这个文件夹下
                isAssets = true;
                //索引7开始截取(如果是assets文件就用 getAssets来读读取)
                String path = file.substring(7);
                //检索字符串找到“/”的索引位置然后+1
                parent = path.substring(0, path.lastIndexOf("/") + 1);
                //通过方法得到流
                inputStream = context.getAssets().open(path);
                Log.e("obj", parent);
            } else {//如果不在这个文件夹下
                isAssets = false;
                //检索字符串找到“/”的索引位置然后+1
                parent = file.substring(0, file.lastIndexOf("/") + 1);
                //通过流的方式读取文件
                inputStream = new FileInputStream(file);
            }
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String temps;
            //按行读取文件
            while ((temps = br.readLine()) != null) {

                if ("".equals(temps)) {

                } else {
                    String[] tempsa = temps.split("[ ]+");

                    switch (tempsa[0].trim()) {
                        case "mtllib":  //材质
                            InputStream stream;
                            //判断文件位置，读取材质文件
                            if (isAssets) {
                                stream = context.getAssets().open(parent + tempsa[1]);
                            } else {
                                stream = new FileInputStream(parent + tempsa[1]);
                            }
                            //读取材质文件
                            mTls = readMtl(stream);
                            break;
                        case "usemtl":  //采用纹理
                            //判断是否存在材质
                            if (mTls != null) {
                                //按材质名称拿到对应的材质内容
                                nowMtl = mTls.get(tempsa[1]);
                            }
                            //判断是否包含指定的键名
                            if (mObjs.containsKey(tempsa[1])) {
                                //拿到对应键名的材质数据
                                nowObj = mObjs.get(tempsa[1]);
                            } else {
                                //不包含材质
                                //创建材质
                                nowObj = new Obj3D();
                                //拿到 材质内容
                                nowObj.mtl = nowMtl;
                                //指定材质
                                mObjs.put(tempsa[1], nowObj);
                            }
                            break;
                        case "v":       //原始顶点
                            //读取原始顶点并存储顶点
                            read(tempsa, oVs);
                            break;
                        case "vn":      //原始顶点法线
                            //读取原始顶点法线 并 存储顶点法线
                            read(tempsa, oVNs);
                            break;
                        case "vt"://顶点纹理坐标
                            //读取顶点纹理 并 存储顶点纹理
                            read(tempsa, oVTs);
                            break;
                        case "f"://面数据（顶点索引，纹理索引，法向量索引）
                            for (int i = 1; i < tempsa.length; i++) {
                                //按“/”分割字符串成数组
                                String[] fs = tempsa[i].split("/");
                                int index;
                                //第一个是顶点索引
                                if (fs.length > 0) {
                                    //顶点索引
                                    index = Integer.parseInt(fs[0]) - 1;
                                    nowObj.addVert(oVs.get(index * 3));
                                    nowObj.addVert(oVs.get(index * 3 + 1));
                                    nowObj.addVert(oVs.get(index * 3 + 2));
                                }
                                //第二个是纹理索引
                                if (fs.length > 1) {
                                    //贴图
                                    index = Integer.parseInt(fs[1]) - 1;
                                    nowObj.addVertTexture(oVTs.get(index * 2));
                                    nowObj.addVertTexture(oVTs.get(index * 2 + 1));
                                }
                                //第三个法向量索引
                                if (fs.length > 2) {
                                    //法线索引
                                    index = Integer.parseInt(fs[2]) - 1;
                                    nowObj.addVertNorl(oVNs.get(index * 3));
                                    nowObj.addVertNorl(oVNs.get(index * 3 + 1));
                                    nowObj.addVertNorl(oVNs.get(index * 3 + 2));
                                }
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Obj3D> stringObj3DEntry : mObjs.entrySet()) {
            //得到 value
            Obj3D obj = stringObj3DEntry.getValue();
            //把数据添加到集合中
            data.add(obj);
            obj.dataLock();
        }
        return data;
    }

    public static HashMap<String, MtlInfo> readMtl(InputStream stream) {
        //材质集合 <材质名，材质内容>
        HashMap<String, MtlInfo> map = new HashMap<>();
        try {
            //读取文件
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            String temps;
            //创建容器类，储存我们想要的效果
            MtlInfo mtlInfo = new MtlInfo();
            while ((temps = br.readLine()) != null) {
                //去掉空格
                String[] tempsa = temps.split("[ ]+");
                //读取内容
                switch (tempsa[0].trim()) {
                    case "newmtl":  //材质
                        mtlInfo = new MtlInfo();
                        //获取当前材质名称
                        mtlInfo.newmtl = tempsa[1];
                        //将材质名和材质的内容对应存储
                        map.put(tempsa[1], mtlInfo);
                        break;
                    case "illum":     //光照模型
                        //储存  光照模型
                        mtlInfo.illum = Integer.parseInt(tempsa[1]);
                        break;
                    case "Kd"://漫反射
                        read(tempsa, mtlInfo.Kd);
                        break;
                    case "Ka"://环境光
                        read(tempsa, mtlInfo.Ka);
                        break;
                    case "Ks"://镜面反射
                        read(tempsa, mtlInfo.Ks);
                        break;
                    case "Ns"://材质的反射指数
                        mtlInfo.Ns = Float.parseFloat(tempsa[1]);
                    case "map_Kd": //为漫反射指定颜色纹理文件(位图文件)
                        mtlInfo.map_Kd = tempsa[1];
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void read(String[] value, ArrayList<Float> list) {
        //便利并储存原始顶点
        for (int i = 1; i < value.length; i++) {
            list.add(Float.parseFloat(value[i]));
        }
    }

    private static void read(String[] value, float[] fv) {
        for (int i = 1; i < value.length && i < fv.length + 1; i++) {
            fv[i - 1] = Float.parseFloat(value[i]);
        }
    }

}
