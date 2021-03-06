package com.divan.imagecompression.Containers;

import com.divan.imagecompression.Objects.DataOPC;
import com.divan.imagecompression.Utils.DCTMultiThread;

/**
 * Created by Димка on 09.10.2016.
 */

public class BoxOfOPC {

    public DataOPC[][] a,b,c;
    public int width,height; // need private

    public BoxOfOPC(int width, int height,boolean isEnlargement) {
        sizeCalculate(width,height);
        int k=1;
        if(isEnlargement)
            k=2;

        width=this.width;
        height=this.height;
        a=new DataOPC[width][height];
        b=new DataOPC[width/k][height/k];
        c=new DataOPC[width/k][height/k];
    }
    private void sizeCalculate(int Width,int Height) {
        width = Width / DCTMultiThread.SIZEOFBLOCK;
        height = Height / DCTMultiThread.SIZEOFBLOCK;
        if (Width % DCTMultiThread.SIZEOFBLOCK != 0)
            width++;
        if (Height % DCTMultiThread.SIZEOFBLOCK != 0)
            height++;

        //   createMatrix();


    }
}
