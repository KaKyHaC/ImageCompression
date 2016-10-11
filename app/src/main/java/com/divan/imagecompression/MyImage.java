package com.divan.imagecompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.Settings;
import android.view.PixelCopy;

/**
 * Created by Димка on 07.08.2016.
 */
enum  State {RGB,YBR,Yenl,bitmap,DCT};


public class MyImage {



    private Matrix matrix;


    private static final int SIZEOFBLOCK = 8;

    private int cWidth,cHeight;
    private int Width,Height;
    private Bitmap bitmap;
    private String NameOFFile;

    private short[][] R,G,B;
    private short[][] Y,Cb,Cr;
    private short[][] enlCb,enlCr;

    //TODO string constructor
    private void Factory()
    {
        Y=matrix.a;
        Cb=matrix.b;
        Cr=matrix.c;

        R=matrix.a;
        G=matrix.b;
        B=matrix.c;

        enlCb=matrix.b;
        enlCr=matrix.c;

        Width=matrix.Width;
        Height=matrix.Height;

        cWidth=Width/2;
        cHeight=Height/2;
        System.gc();
    }

    MyImage(Bitmap _b){
        bitmap=_b;



        matrix=new Matrix(bitmap.getWidth(),bitmap.getHeight());
        matrix.state=State.bitmap;
        Factory();

    }

    MyImage(Matrix matrix) {
        this.matrix = matrix;
        Factory();

        bitmap= Bitmap.createBitmap(Width,Height, Bitmap.Config.ARGB_4444);
    }

   private void FromBitmapToYCbCr() {

       if (matrix.state == State.bitmap)
       {
           for (int i = 0; i < Width; i++) {
               for (int j = 0; j < Height; j++) {
                   int pixelColor = bitmap.getPixel(i, j);
                   // получим цвет каждого пикселя
                   double pixelRed = Color.red(pixelColor);
                   double pixelGreen = Color.green(pixelColor);
                   double pixelBlue = Color.blue(pixelColor);


               /* double vy=((77.0/256.0)*pixelRed+(150.0/256.0)*pixelGreen+(29.0/256)*pixelBlue);
                double vcb=(((44.0/256.0)*pixelRed-(87.0/256.0)*pixelGreen+(131.0/256)*pixelBlue)+128.0);
                double vcr=(((131.0/256.0)*pixelRed-(110.0/256.0)*pixelGreen-(21.0/256)*pixelBlue)+128.0);*/

                   double vy = (0.299 * pixelRed) + (0.587 * pixelGreen) + (0.114 * pixelBlue);
                   double vcb = 128 - (0.168736 * pixelRed) - (0.331264 * pixelGreen) + (0.5 * pixelBlue);
                   double vcr = 128 + (0.5 * pixelRed) - (0.418688 * pixelGreen) - (0.081312 * pixelBlue);

                   Y[i][j] = (short) vy;
                   Cb[i][j] = (short) vcb;
                   Cr[i][j] = (short) vcr;
               }
           }
           matrix.state = State.YBR;
       }

    }
    private void FromBitmapToRGB() {

   if(matrix.state==State.bitmap) {
            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
                    int pixelColor = bitmap.getPixel(i, j);
                    // получим цвет каждого пикселя
                    R[i][j] = (short) (Color.red(pixelColor));
                    G[i][j] = (short) (Color.green(pixelColor));
                    B[i][j] = (short) (Color.blue(pixelColor));

                }
            }
            matrix.state=State.RGB;
        }
    }

    private void FromYBRtoRGB(){

        if(matrix.state==State.YBR) {
        for(int i=0;i<Width;i++)
        {
            for(int j=0;j<Height;j++)
            {
                double r,g,b;
                r=(short)(Y[i][j]+1.402*(Cr[i][j]-128));
                g=(short)(Y[i][j]-0.34414*(Cb[i][j]-128)-0.71414*(Cr[i][j]-128));
                b=(short)(Y[i][j]+1.772*(Cb[i][j]-128));

                if(g<0)g=0;//new
                if(r<0)r=0;
                if(b<0)b=0;
                R[i][j]=(short)r;
                G[i][j]=(short)g;
                B[i][j]=(short)b;
            }
        }
        matrix.state=State.RGB;
        }
    }
    private void FromRGBtoYBR() {

        if (matrix.state == State.RGB){
            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
                    // получим цвет каждого пикселя
                    double pixelRed = R[i][j];
                    double pixelGreen = G[i][j];
                    double pixelBlue = B[i][j];

                    double vy = (0.299 * pixelRed) + (0.587 * pixelGreen) + (0.114 * pixelBlue);
                    double vcb = 128 - (0.168736 * pixelRed) - (0.331264 * pixelGreen) + (0.5 * pixelBlue);
                    double vcr = 128 + (0.5 * pixelRed) - (0.418688 * pixelGreen) - (0.081312 * pixelBlue);

                    Y[i][j] = (short) vy;
                    Cb[i][j] = (short) vcb;
                    Cr[i][j] = (short) vcr;
                }
            }
        matrix.state=State.YBR;
        }
    }

    private void PixelEnlargement(){
        if(matrix.state==State.YBR) {
            enlCb=new short[cWidth][cHeight];
            enlCr=new short[cWidth][cHeight];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    enlCb[i][j] = (short)( (Cb[i * 2][j * 2] + Cb[i * 2 + 1][j * 2] + Cb[i * 2][j * 2 + 1] + Cb[i * 2 + 1][j * 2 + 1])/4);
                    enlCr[i][j] = (short)( (Cr[i * 2][j * 2] + Cr[i * 2 + 1][j * 2] + Cr[i * 2][j * 2 + 1] + Cr[i * 2 + 1][j * 2 + 1])/4);
                }
            }
            matrix.b=enlCb;
            matrix.c=enlCr;
            Factory();
            matrix.state=State.Yenl;
        }

    }
    private void PixelRestoration() {

        if(matrix.state==State.Yenl) {
            Cb=new short[Width][Height];
            Cr=new short[Width][Height];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    Cb[i * 2][j * 2] = Cb[i * 2 + 1][j * 2] = Cb[i * 2][j * 2 + 1] = Cb[i * 2 + 1][j * 2 + 1] = enlCb[i][j];
                    Cr[i * 2][j * 2] = Cr[i * 2 + 1][j * 2] = Cr[i * 2][j * 2 + 1] = Cr[i * 2 + 1][j * 2 + 1] = enlCr[i][j];
                }
            }
            matrix.b=Cb;
            matrix.c=Cr;

            Factory();
            matrix.state=State.YBR;
        }
    }

    private void FromRGBtoBitmap(){
        if(matrix.state==State.RGB)
        {
            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++)
                {

                    int pixelAlpha=255;
                    int pixelBlue=B[i][j]&0xFF;
                    int pixelRed=R[i][j]&0xFF;
                    int pixelGreen=G[i][j]&0xFF;
                    int newPixel= Color.argb(
                            pixelAlpha, pixelRed, pixelGreen, pixelBlue);
                    // полученный результат вернём в Bitmap
                    bitmap.setPixel(i, j, newPixel);
                }
            }
            matrix.state=State.bitmap;
        }
    }




    public int getWidth() {
        return Width;
    }
    public int getHeight() {
        return Height;
    }
    public Bitmap getBitmap() {
        switch(matrix.state)
        {
            case RGB: FromRGBtoBitmap();
                break;
            case YBR: FromYBRtoRGB();FromRGBtoBitmap();
                break;
            case Yenl:PixelRestoration();FromYBRtoRGB();FromRGBtoBitmap();
                break;
            case bitmap:
                break;
        }

        return bitmap;
    }
    private Matrix getMatrix() {
        return matrix;
    }

   /** public Matrix getRGBmatrix(){
        if(matrix.state==State.RGB)
        {
        }
        else if(matrix.state==State.bitmap)
        {
            FromBitmapToRGB();
        }
        else if(matrix.state==State.YBR)
        {
            FromYBRtoRGB();
        }
        else
        {
            return null;
        }
       return matrix;
    }*/
    public Matrix getYBRmatrix(){
        switch (matrix.state)
        {

            case RGB: FromRGBtoYBR();
                break;
            case YBR:
                break;
            case Yenl:PixelRestoration();
                break;
            case bitmap:FromBitmapToYCbCr();
                break;
            case DCT:return null;

        }
        return matrix;
    }
    public Matrix getYenlMatrix()
    {
        switch (matrix.state)
        {

            case RGB: FromRGBtoYBR();PixelEnlargement();
                break;
            case YBR:PixelEnlargement();
                break;
            case Yenl:
                break;
            case bitmap:FromBitmapToYCbCr();PixelEnlargement();
                break;
            case DCT:return null;

        }
        return matrix;
    }
    public void Clear()
    {
        matrix.a=null;
        matrix.b=null;
        matrix.c=null;
        Factory();
        System.gc();
    }

}

 class Matrix
{
    protected short [][] a,b,c;
    int Width,Height;
    protected State state;

    public Matrix(int width, int height) {
        Width = width;
        Height = height;

        a=new short[width][height];
        b=new short[width][height];
        c=new short[width][height];


    }
}
