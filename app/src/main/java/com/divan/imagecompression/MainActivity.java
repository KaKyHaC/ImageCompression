package com.divan.imagecompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView after,befor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        after=(ImageView)findViewById(R.id.imageView2);
        befor=(ImageView)findViewById(R.id.imageView);

        Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(),
                R.drawable.kiev);
        befor.setImageBitmap(bmOriginal);

        MyImage mi=new MyImage(bmOriginal);
        mi.BitmapToYCbCr();//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!

        DataUnitMatrix bdu =new DataUnitMatrix(mi.getY(),mi.getWidth(),mi.getHeight(),TypeQuantization.luminosity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
        DataUnitMatrix adu =new DataUnitMatrix(bdu.getDataDCT(),bdu.getAC(),bdu.getWidth(),bdu.getHeight(),TypeQuantization.luminosity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!

        MyImage af=new MyImage(adu.getWidth(),adu.getHeight(),adu.getDataOrigin(),mi.getCb(),mi.getCr());//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
        af.FromYBRtoRGB();//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
        af.FromRGBtoBitmap();//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!

        after.setImageBitmap(af.getBitmap());

/*
        int xyi;
        xyi=10;
        Random rand=new Random();
        short[][] bt=new short[xyi][xyi];
        for(int i=0;i<xyi;i++)
        {
            for(int j=0;j<xyi;j++)
            {
                int ran=rand.nextInt(255);
                bt[i][j]=(short)(ran);
            }
        }


        DataUnitMatrix dumOriginal=new DataUnitMatrix(bt,xyi,xyi,TypeQuantization.luminosity);
        DataUnitMatrix dumDCT=new DataUnitMatrix(dumOriginal.getDataDCT(),dumOriginal.getAC(),xyi,xyi,dumOriginal.getTq());
        short res[][]=dumDCT.getDataOrigin();*/


    }
}
