
package com.tz.rock.opengl_vip_beauty.utils;

import android.content.res.Resources;

public class NoFilter extends AFilter {

    public NoFilter(Resources res) {
        super(res);
    }

    @Override
    public void create() {
        //用来渲染输出的着色器
        createProgramByAssetsFile("shader/base_vertex.glsl",
            "shader/base_fragment.glsl");
    }


}
