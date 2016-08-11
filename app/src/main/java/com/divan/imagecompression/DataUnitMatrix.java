package com.divan.imagecompression;

/**
 * Created by Димка on 08.08.2016.
 */
public class DataUnitMatrix {

    private DataUnit[][] DU;
    private short[][] dataOrigin;
    private long[][]dataDCT;
    private long AC;
    private int Width,Height;
    private int duWidth,duHeight;
    TypeQuantization tq;


    public DataUnitMatrix(short[][] dataOrigin, int width, int height, TypeQuantization tq) {
        this.dataOrigin = dataOrigin;
        Width = width;
        Height = height;
        this.tq = tq;

        sizeCalculate();
        dataDCT=new long[duWidth*DataUnit.SIZEOFBLOCK][duHeight*DataUnit.SIZEOFBLOCK];

        packageOrigin();
    }
    public DataUnitMatrix(long[][] dataDCT, long AC, int width, int height, TypeQuantization tq) {
        this.dataDCT = dataDCT;
        this.AC = AC;
        Width = width;
        Height = height;
        this.tq = tq;

        sizeCalculate();
        dataOrigin=new short[width][height];

        packageDCT();
    }

    private void packageOrigin()
    {
        fromOriginalToDU();
        directDCT();
        fromDuToDct();
    }
    private void packageDCT()
    {
        fromDctToDu();
        reversDCT();
        fromDuToOriginal();
    }

    private void sizeCalculate() {
        duWidth=Width/DataUnit.SIZEOFBLOCK;
        duHeight=Height/DataUnit.SIZEOFBLOCK;
        if(Width%DataUnit.SIZEOFBLOCK!=0)
            duWidth++;
        if(Height%DataUnit.SIZEOFBLOCK!=0)
            duHeight++;

        createMatrix();


    }
    private void createMatrix()
    {
        DU=new DataUnit[duWidth][duHeight];
    }


    private void fromOriginalToDU(){
        for(int i=0;i<duWidth;i++)
        {
            for(int j=0;j<duHeight;j++)
            {
               short [][] buf=new short[DataUnit.SIZEOFBLOCK][DataUnit.SIZEOFBLOCK];
                for(int x=0;x<DataUnit.SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<DataUnit.SIZEOFBLOCK;y++)
                    {
                        int size=8;
                        short value=0;
                        int posa=(i*size);
                        int posb=(j*size);
                        posa+=x;
                        posb+=y;
                        if(i*DataUnit.SIZEOFBLOCK+x<Width&&j*DataUnit.SIZEOFBLOCK+y<Height)
                            value=dataOrigin[posa][posb];
                        buf[x][y]=value;
                       // DU[i][j].setValue(val,x,y);
                    }
                }
                DU[i][j]=new DataUnit(buf);
               /* if(i==0&&j==0)
                    AC=DU[i][j].getAC();

                DU[i][j].setAC(AC-DU[i][j].getAC());*/

            }
        }
    }
    private void fromDuToOriginal(){
        for(int i=0;i<duWidth;i++)
        {
            for(int j=0;j<duHeight;j++)
            {
                for(int x=0;x<DataUnit.SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<DataUnit.SIZEOFBLOCK;y++)
                    {
                        if(i*DataUnit.SIZEOFBLOCK+x<Width&&j*DataUnit.SIZEOFBLOCK+y<Height)
                            dataOrigin[i*DataUnit.SIZEOFBLOCK+x][j*DataUnit.SIZEOFBLOCK+y]= DU[i][j].getValue(x,y);
                    }
                }

               // DU[i][j].setAC(AC+DU[i][j].getAC());

            }
        }
    }

    private void fromDctToDu(){
        for(int i=0;i<duWidth;i++)
        {
            for(int j=0;j<duHeight;j++)
            {
                short [][] buf=new short[DataUnit.SIZEOFBLOCK][DataUnit.SIZEOFBLOCK];
                for(int x=0;x<DataUnit.SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<DataUnit.SIZEOFBLOCK;y++)
                    {
                        long val=0;
                       // if(i*DataUnit.SIZEOFBLOCK+x<Width&&j*DataUnit.SIZEOFBLOCK+y<Height)
                            val=dataDCT[i*DataUnit.SIZEOFBLOCK+x][j*DataUnit.SIZEOFBLOCK+y];
                        buf[x][y]=(short)val;
                       // DU[i][j].setValueDCT(val,x,y);
                    }
                }
                long ac=buf[0][0];
                buf[0][0]=0;
                DU[i][j]=new DataUnit(ac,buf);


            }
        }
    }
    private void fromDuToDct(){
        for(int i=0;i<duWidth;i++)
        {
            for(int j=0;j<duHeight;j++)
            {
                for(int x=0;x<DataUnit.SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<DataUnit.SIZEOFBLOCK;y++)
                    {
                       // if(i*DataUnit.SIZEOFBLOCK+x<Width&&j*DataUnit.SIZEOFBLOCK+y<Height)
                            dataDCT[i*DataUnit.SIZEOFBLOCK+x][j*DataUnit.SIZEOFBLOCK+y]= DU[i][j].getValueDCT(x,y);
                    }
                }

                // DU[i][j].setAC(AC+DU[i][j].getAC());

            }
        }
    }

    private void directDCT(){
        for(int i=0;i<duWidth;i++)
        {
            for(int j=0;j<duHeight;j++)
            {
               DU[i][j].directDCT();
              //  DU[i][j].directQuantization(tq);// <--------------slow
                if(i==0&&j==0)
                    AC=DU[i][j].getAC();
                DU[i][j].setAC(AC-DU[i][j].getAC());

            }
        }
    }
    private void reversDCT(){
        for(int i=0;i<duWidth;i++)
        {
            for(int j=0;j<duHeight;j++)
            {

                DU[i][j].setAC(AC-DU[i][j].getAC());
              // DU[i][j].reverseQuantization(tq);// <--------------slow
                DU[i][j].reverseDCT();

            }
        }
    }


    public long getAC() {
        return AC;
    }
    public int getWidth() {
        return Width;
    }
    public int getHeight() {
        return Height;
    }
    public short[][] getDataOrigin() {
        return dataOrigin;
    }
    public long[][] getDataDCT() {
        return dataDCT;
    }
    public TypeQuantization getTq() {
        return tq;
    }
}
