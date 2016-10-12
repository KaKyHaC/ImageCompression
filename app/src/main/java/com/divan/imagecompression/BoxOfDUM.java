package com.divan.imagecompression;

import android.graphics.*;

/**
 * Created by Димка on 19.09.2016.
 */
public class BoxOfDUM {
    private Matrix matrix;
    private DataUnitMatrix a,b,c;

    public BoxOfDUM(Matrix matrix) {
        this.matrix = matrix;
        if(matrix.state==State.Yenl)//new code . Does it is needed ?
            matrix.state=State.YBR;
            a=new DataUnitMatrix(matrix.a,matrix.a.length,matrix.a[0].length,matrix.state,TypeQuantization.luminosity,matrix.qs);
            b = new DataUnitMatrix(matrix.b, matrix.b.length, matrix.b[0].length, matrix.state, TypeQuantization.Chromaticity,matrix.qs);
            c = new DataUnitMatrix(matrix.c, matrix.c.length, matrix.c[0].length, matrix.state, TypeQuantization.Chromaticity,matrix.qs);

    }

    public void dataProcessing() {
        if(matrix.state==State.Yenl)//new code . Does it is needed ?
            matrix.state=State.YBR;
        a.dataProcessing();
        b.dataProcessing();
        c.dataProcessing();
        matrix.state=a.getState();
        if(matrix.a.length>matrix.b.length&&matrix.state==State.YBR)
            matrix.state=State.Yenl;
    }

    public Matrix getMatrix() {
        return matrix;
    }
}
