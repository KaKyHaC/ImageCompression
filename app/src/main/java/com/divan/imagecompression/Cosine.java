package com.divan.imagecompression;

/**
 * Created by Димка on 07.08.2016.
 */
public class Cosine {
    final private static int SIZEOFBLOCK=8;

    private static Cosine instance=new Cosine();
    private static double[][] cosine;
    private static double[][][][] value;


    private Cosine()
    {
        cosine=new double[SIZEOFBLOCK][SIZEOFBLOCK];
        value = new double[SIZEOFBLOCK][SIZEOFBLOCK][SIZEOFBLOCK][SIZEOFBLOCK];

        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                double data=((2.0*i+1.0)*j* Math.PI)/(2*SIZEOFBLOCK);
                double cos=Math.cos((data));//toRadianse
                cosine[i][j] = cos;

            }
        }

        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                for(int x=0;x<SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<SIZEOFBLOCK;y++)
                    {
                        value[i][j][x][y]=cosine[y][j]*cosine[x][i];
                    }
                }
            }
        }
    }

    public static double getCos(int x,int i){return cosine[x][i];}
    public static double getCos(int x,int y,int i,int j){return value[i][j][x][y];}// it's true ?

}
