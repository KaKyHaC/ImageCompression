package com.divan.imagecompression;

import android.graphics.Matrix;

/**
 * Created by Димка on 08.08.2016.
 */

public class DataUnitMatrix {


    private State state ;
    private short[][] dataOrigin;
    private short[][] dataProcessed;
    private long AC;
    private int Width, Height;
    private int duWidth, duHeight;
    private TypeQuantization tq;


    public DataUnitMatrix(short[][] dataOrigin, int width, int height,State state, TypeQuantization tq) {
        this.dataOrigin = dataOrigin;
        dataProcessed=dataOrigin;
        Width = width;
        Height = height;
        this.tq = tq;

        this.state=state;
        sizeCalculate();


    }



    private void sizeCalculate() {
        duWidth = Width / DataUnit.SIZEOFBLOCK;
        duHeight = Height / DataUnit.SIZEOFBLOCK;
        if (Width % DataUnit.SIZEOFBLOCK != 0)
            duWidth++;
        if (Height % DataUnit.SIZEOFBLOCK != 0)
            duHeight++;

     //   createMatrix();


    }


    public void dataProcessing() {
        short[][] buf = new short[DataUnit.SIZEOFBLOCK][DataUnit.SIZEOFBLOCK];
        DataUnit DU=new DataUnit(tq);
        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                for (int x = 0; x < DataUnit.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DataUnit.SIZEOFBLOCK; y++) {
                        short value = 0;
                        int curX=i * DataUnit.SIZEOFBLOCK + x;
                        int curY=j * DataUnit.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            value = dataOrigin[curX][curY];
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);
                    }
                }
                DU.setDateOriginal(buf);
                if(state==State.YBR) {

                    DU.directDCT();
                }
                else if(state==State.DCT)
                {
                    DU.reverseDCT();
                }
                //-------------------directQuantization
                for (int x = 0; x < DataUnit.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DataUnit.SIZEOFBLOCK; y++) {

                        int curX = i * DataUnit.SIZEOFBLOCK + x;
                        int curY = j * DataUnit.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            dataProcessed[curX][curY] = DU.getValueProcessed(x,y);
                    }
                }


            }
        }
        if(state==State.YBR) {

            state=State.DCT;
        }
        else if(state==State.DCT)
        {
            state=State.YBR;
        }
    }



    public int getWidth() {
        return Width;
    }

    public int getHeight() {
        return Height;
    }


    public short[][] getDataProcessed() {
        return dataProcessed;
    }

    public TypeQuantization getTq() {
        return tq;
    }

    public State getState() {
        return state;
    }
}
