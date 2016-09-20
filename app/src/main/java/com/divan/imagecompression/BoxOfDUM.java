package com.divan.imagecompression;

import android.graphics.*;

/**
 * Created by Димка on 19.09.2016.
 */
public class BoxOfDUM {
    private MyImage.Matrix matrix;
    private DataUnitMatrix a,b,c;

    public BoxOfDUM(MyImage.Matrix matrix) {
        this.matrix = matrix;

        if(matrix.state==State.YBR) {
            a=new DataUnitMatrix(matrix.a,matrix.Width,matrix.Height,matrix.state,TypeQuantization.luminosity);
            b = new DataUnitMatrix(matrix.b, matrix.Width, matrix.Height, matrix.state, TypeQuantization.Chromaticity);
            c = new DataUnitMatrix(matrix.c, matrix.Width, matrix.Height, matrix.state, TypeQuantization.Chromaticity);
        }
        else if(matrix.state==State.Yenl)
        {
            matrix.state=State.YBR;
            a=new DataUnitMatrix(matrix.a,matrix.Width,matrix.Height,matrix.state,TypeQuantization.luminosity);
            b = new DataUnitMatrix(matrix.b, matrix.Width/2, matrix.Height/2, matrix.state, TypeQuantization.Chromaticity);
            c = new DataUnitMatrix(matrix.c, matrix.Width/2, matrix.Height/2, matrix.state, TypeQuantization.Chromaticity);
        }
    }

    public void dataProcessing() {
        a.dataProcessing();
        b.dataProcessing();
        c.dataProcessing();
        matrix.state=a.getState();
        if(matrix.a.length>matrix.b.length&&matrix.state==State.YBR)
            matrix.state=State.Yenl;
    }

    public MyImage.Matrix getMatrix() {
        return matrix;
    }
}
