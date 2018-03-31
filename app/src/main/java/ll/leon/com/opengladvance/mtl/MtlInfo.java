package ll.leon.com.opengladvance.mtl;

/**
 *
 * @author Leon
 */
//Mtl文件的Bean
public class MtlInfo {
    public String newmtl;
    public float[] Ka = new float[3];     //阴影色(环境反射)
    public float[] Kd = new float[3];     //固有色(漫反射)
    public float[] Ks = new float[3];     //高光色(镜反射)
    public float Ns;                    //shininess
    public String map_Kd;               //固有纹理贴图(//为漫反射指定颜色纹理文件(位图文件))
    public String map_Ks;               //高光纹理贴图(//镜反射指定颜色纹理文件)
    public String map_Ka;               //阴影纹理贴图(//为环境反射指定颜色纹理文件)

    public int illum;
}
