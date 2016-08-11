package com.divan.imagecompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.PixelCopy;

/**
 * Created by Димка on 07.08.2016.
 */
public class MyImage {

    private static final int SIZEOFBLOCK = 8;

    private int cWidth,cHeight;
    private int Width,Height;
    private Bitmap bitmap;
    private String NameOFFile;

    private short[][] R,G,B;
    private short[][] Y,Cb,Cr;
    private short[][] enlCb,enlCr;

    //TODO string constructor

    MyImage(Bitmap _b){
        bitmap=_b;
        Width=bitmap.getWidth();
        Height=bitmap.getHeight();
        cWidth=Width/2;
        cHeight=Height/2;

        Y=new short[Width][Height];
        Cb=new short[Width][Height];
        Cr=new short[Width][Height];

        R=new short[Width][Height];
        G=new short[Width][Height];
        B=new short[Width][Height];

        enlCb=new short[cWidth][cHeight];
        enlCr=new short[cWidth][cHeight];
    }
    public MyImage(int width, int height, short[][] y, short[][] cb, short[][] cr) {
        Width = width;
        Height = height;
        Y = y;
        Cb = cb;
        Cr = cr;

        R=new short[Width][Height];
        G=new short[Width][Height];
        B=new short[Width][Height];

        enlCb=new short[cWidth][cHeight];
        enlCr=new short[cWidth][cHeight];

        bitmap= Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_4444);
    }

    void BitmapToYCbCr(){


        for(int i=0;i<Width;i++)
        {
            for(int j=0;j<Height;j++)
            {
                int pixelColor = bitmap.getPixel(i, j);
                // получим цвет каждого пикселя
                double pixelRed = Color.red(pixelColor);
                double pixelGreen = Color.green(pixelColor);
                double pixelBlue = Color.blue(pixelColor);


               /* double vy=((77.0/256.0)*pixelRed+(150.0/256.0)*pixelGreen+(29.0/256)*pixelBlue);
                double vcb=(((44.0/256.0)*pixelRed-(87.0/256.0)*pixelGreen+(131.0/256)*pixelBlue)+128.0);
                double vcr=(((131.0/256.0)*pixelRed-(110.0/256.0)*pixelGreen-(21.0/256)*pixelBlue)+128.0);*/

                double vy=(0.299*pixelRed)+(0.587*pixelGreen)+(0.114*pixelBlue);
                double vcb=128-(0.168736*pixelRed)-(0.331264*pixelGreen)+(0.5*pixelBlue);
                double vcr=128+(0.5*pixelRed)-(0.418688*pixelGreen)-(0.081312*pixelBlue);

                Y[i][j]=(short)vy;
                Cb[i][j]=(short)vcb;
                Cr[i][j]=(short)vcr;
            }
        }
    }
    private void BitmapToRGB() {


        for(int i=0;i<Width;i++)
        {
            for(int j=0;j<Height;j++)
            {
                int pixelColor = bitmap.getPixel(i, j);
                // получим цвет каждого пикселя
                R[i][j] = (short)(Color.red(pixelColor));
                G[i][j] = (short)(Color.green(pixelColor));
                B[i][j] = (short)(Color.blue(pixelColor));

            }
        }
    }

    void FromYBRtoRGB(){


        for(int i=0;i<Width;i++)
        {
            for(int j=0;j<Height;j++)
            {

/*
                R[i][j]=(short)(Y[i][j]+1.402*(Cr[i][j]-128));
                G[i][j]=(short)(Y[i][j]-0.698*(Cr[i][j]-128)-0.336*(Cb[i][j]-128));
                B[i][j]=(short)(Y[i][j]+1.732*(Cb[i][j]-128));*/

                R[i][j]=(short)(Y[i][j]+1.402*(Cr[i][j]-128));
                G[i][j]=(short)(Y[i][j]-0.34414*(Cb[i][j]-128)-0.71414*(Cr[i][j]-128));
                B[i][j]=(short)(Y[i][j]+1.772*(Cb[i][j]-128));
            }
        }
    }
    private void FromRGBtoYBR(){


        for(int i=0;i<Width;i++)
        {
            for(int j=0;j<Height;j++)
            {
                int pixelColor = bitmap.getPixel(i, j);
                // получим цвет каждого пикселя
                double pixelRed =R[i][j];
                double pixelGreen = G[i][j];
                double pixelBlue = B[i][j];

                Y[i][j]=(byte)((77.0/256.0)*pixelRed+(150.0/256.0)*pixelGreen+(29.0/256)*pixelBlue);
                Cb[i][j]=(byte)(((44.0/256.0)*pixelRed-(87.0/256.0)*pixelGreen+(131.0/256)*pixelBlue)+128.0);
                Cr[i][j]=(byte)(((131.0/256.0)*pixelRed-(110.0/256.0)*pixelGreen-(21.0/256)*pixelBlue)+128.0);
            }
        }
    }

    void PixelEnlargement(){
        for(int i=0;i<cWidth;i++) {
            for (int j = 0; j < cHeight; j++) {
                enlCb[i][j]=(byte)(Cb[i*2][j*2]+Cb[i*2+1][j*2]+Cb[i*2][j*2+1]+Cb[i*2+1][j*2+1]);
                enlCr[i][j]=(byte)(Cr[i*2][j*2]+Cr[i*2+1][j*2]+Cr[i*2][j*2+1]+Cr[i*2+1][j*2+1]);
            }
        }

    }
    void PixelRestoration() {

        for (int i = 0; i < cWidth; i++) {
            for (int j = 0; j < cHeight; j++) {
                Cb[i * 2][j * 2] = Cb[i * 2 + 1][j * 2] = Cb[i * 2][j * 2 + 1] = Cb[i * 2 + 1][j * 2 + 1] = enlCb[i][j];
                Cr[i * 2][j * 2] = Cr[i * 2 + 1][j * 2] = Cr[i * 2][j * 2 + 1] = Cr[i * 2 + 1][j * 2 + 1] = enlCr[i][j];
            }
        }
    }

    void FromRGBtoBitmap(){
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
    }


    public int getWidth() {
        return Width;
    }
    public int getHeight() {
        return Height;
    }
    public short[][] getY() {
        return Y;
    }
    public short[][] getCb() {
        return Cb;
    }
    public short[][] getCr() {
        return Cr;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public short[][] getR() {
        return R;
    }

    public short[][] getG() {
        return G;
    }

    public short[][] getB() {
        return B;
    }
}
