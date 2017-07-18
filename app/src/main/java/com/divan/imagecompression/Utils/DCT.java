package com.divan.imagecompression.Utils;


import com.divan.imagecompression.Constants.Cosine;
import com.divan.imagecompression.Constants.QuantizationTable;
import com.divan.imagecompression.Objects.TypeQuantization;

;

public class DCT {//singelton

    public final static int SIZEOFBLOCK = 8;


    private static DCT tmp = new DCT();
    private static TypeQuantization tq;
    private static short[][] dateOriginal ;
    private static short[][] dateProcessed ;
    // private long DC;

    private DCT() {

    }

    public static DCT getInstanse() {
        return new DCT();
    }


    public static short[][] directDCT(short[][] date){
        dateOriginal=date;
        dateProcessed=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        tmp.directDCT();
        return dateProcessed;
    }
    public static short[][] reverseDCT(short[][] date){
        dateOriginal=date;
        dateProcessed=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        tmp.reverseDCT();
        return dateProcessed;
    }

    public static void directQuantization(TypeQuantization _tq) {

        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    //dateProcessed[i][j]/=QuantizationTable.getSmart(1,i,j);
                    dateProcessed[i][j] /= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dateProcessed[i][j]/=QuantizationTable.getSmart(3,i,j);
                    dateProcessed[i][j] /= QuantizationTable.getChromaticity(i, j);
                }

    }

    public static void reverseQuantization(TypeQuantization _tq) {

        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dateProcessed[i][j]*=QuantizationTable.getSmart(1,i,j);
                    dateProcessed[i][j] *= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dateProcessed[i][j]*=QuantizationTable.getSmart(3,i,j);
                    dateProcessed[i][j] *= QuantizationTable.getChromaticity(i, j);
                }


    }

    /*-------main metode---------*/
    private void directDCT() {
       // minus128();//test
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                double res= Cosine.getDCTres(i,j);
                double sum=0.0;
                for(int x=0;x<SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<SIZEOFBLOCK;y++)
                    {
                        /*double cos=Cosine.getCos(x,y,i,j);
                        double buf=dateOriginal[x][y];
                        double mul=buf*cos;
                        sum+=mul;*/
                        sum += dateOriginal[x][y] * Cosine.getCos(x, y, i, j);
                        // sum+=Cosine.getDirectDCTres(i,j,x,y,dateOriginal[x][y]);
                    }
                }
                res*=sum;
                dateProcessed[i][j]=(short)res;
                /*if(i==0&&j==0)
                    AC=(long)res;
                else if (res<256&&res>-256)
                    dateDCT[i][j]=(byte)res;
                else getAC();*/


            }
        }
    }

    private void reverseDCT() {
        double OneDivadMathsqrt2=1.0/Math.sqrt(2.0);

        for(int x=0;x<SIZEOFBLOCK;x++)
        {
            for(int y=0;y<SIZEOFBLOCK;y++)
            {
               // double res=1.0/4.0;
                double sum=0.0;
                for(int i=0;i<SIZEOFBLOCK;i++)
                {
                    for(int j=0;j<SIZEOFBLOCK;j++)
                    {
                        double Ci=(i==0)?OneDivadMathsqrt2:1.0;
                        double Cj=(j==0)?OneDivadMathsqrt2:1.0;
                        double buf=Ci*Cj*dateOriginal[i][j]*Cosine.getCos(x,y,i,j);
                        sum+=buf;
                    }
                }
                dateProcessed[x][y]=(short)(0.25*sum);
            }
        }
       // plus128();//
    }

    private void minus128 (){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                dateOriginal[i][j]-=(short)128;
            }
    }
    private void plus128 (){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                dateProcessed[i][j]+=(short)128;
            }
    }

    // обопщенно позоционное кодирование







}
