package com.divan.imagecompression;


import java.sql.SQLClientInfoException;

/**
 * Created by Димка on 07.08.2016.
 */
enum TypeQuantization{luminosity ,Chromaticity};

public class DataUnit {

    public final static int SIZEOFBLOCK = 8;


    private  TypeQuantization tq;
    private short[][] dateOriginal = new short[SIZEOFBLOCK][SIZEOFBLOCK];
    private short[][] dateProcessed = new short[SIZEOFBLOCK][SIZEOFBLOCK];
   // private long AC;

    DataUnit(){}
    DataUnit(TypeQuantization _tq){tq=_tq;}
    DataUnit(short[][] _dateOriginal){
        dateOriginal=_dateOriginal;
    }



/*---geters and setters-----*/
    void setValue(short _value,int x,int y){dateOriginal[x][y]=_value;}
    short getValueProcessed(int x,int y){
            return dateProcessed[x][y];}



    public void setDateOriginal(short[][] dateOriginal) {
        this.dateOriginal = dateOriginal;
    }
    public short[][] getDateProcessed() {
        return dateProcessed;
    }


    public TypeQuantization getTq() {
        return tq;
    }
    public void setTq(TypeQuantization tq) {
        this.tq = tq;
    }

    /*-------main metode---------*/
    void directDCT() {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                double Ci=(i==0)?1.0/Math.sqrt(2.0):1.0;
                double Cj=(j==0)?1.0/Math.sqrt(2.0):1.0;
                double res=(1.0/Math.sqrt(2.0*SIZEOFBLOCK))*Ci*Cj;
                double sum=0.0;
                for(int x=0;x<SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<SIZEOFBLOCK;y++)
                    {
                        double cos=Cosine.getCos(x,y,i,j);
                        double buf=dateOriginal[x][y];
                        double mul=buf*cos;
                        sum+=mul;
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
    void reverseDCT() {
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
                        double Ci=(i==0)?1.0/Math.sqrt(2.0):1.0;
                        double Cj=(j==0)?1.0/Math.sqrt(2.0):1.0;
                        double buf=Ci*Cj*dateOriginal[i][j]*Cosine.getCos(x,y,i,j);
                        sum+=buf;
                    }
                }
                dateProcessed[x][y]=(short)(0.25*sum);
            }
        }
    }

    private void directQuantization(TypeQuantization _tq){

        if(_tq==TypeQuantization.luminosity)
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                dateProcessed[i][j]/=QuantizationTable.getLuminosity(i,j);
            }
        else if(_tq==TypeQuantization.Chromaticity)
            for(int i=0;i< SIZEOFBLOCK;i++)
                for(int j=0;j<SIZEOFBLOCK;j++)
                {
                    dateProcessed[i][j]/=QuantizationTable.getChromaticity(i,j);
                }

    }
    void directQuantization(){directQuantization(tq);}

    private void reverseQuantization(TypeQuantization _tq){

        if(_tq==TypeQuantization.luminosity)
            for(int i=0;i< SIZEOFBLOCK;i++)
                for(int j=0;j<SIZEOFBLOCK;j++)
                {
                    dateProcessed[i][j]*=QuantizationTable.getLuminosity(i,j);
                }
        else if(_tq==TypeQuantization.Chromaticity)
            for(int i=0;i< SIZEOFBLOCK;i++)
                for(int j=0;j<SIZEOFBLOCK;j++)
                {
                    dateProcessed[i][j]*=QuantizationTable.getChromaticity(i,j);
                }


    }
    void reverseQuantization(){reverseQuantization(tq);}

    // обопщенно позоционное кодирование







}
