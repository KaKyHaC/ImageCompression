package com.divan.imagecompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
//async <code></code>

        JPEGcodek jpg = new JPEGcodek();
        jpg.execute(bmOriginal);





    }

    public class JPEGcodek extends AsyncTask<Bitmap,Void,Bitmap> {


        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            MyImage mi=new MyImage(bitmaps[0]);
            mi.BitmapToYCbCr();//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
          //  mi.PixelEnlargement();

            DataUnitMatrix bduY = new DataUnitMatrix(mi.getY(),mi.getWidth(),mi.getHeight(),TypeQuantization.luminosity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
            DataUnitMatrix aduY = new DataUnitMatrix(bduY.getDataDCT(),bduY.getAC(),bduY.getWidth(),bduY.getHeight(),TypeQuantization.luminosity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!

            DataUnitMatrix bduB = new DataUnitMatrix(mi.getCb(),mi.getWidth(),mi.getHeight(),TypeQuantization.Chromaticity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
            DataUnitMatrix aduB = new DataUnitMatrix(bduB.getDataDCT(),bduB.getAC(),bduB.getWidth(),bduB.getHeight(),TypeQuantization.Chromaticity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!

            DataUnitMatrix bduR = new DataUnitMatrix(mi.getCr(),mi.getWidth(),mi.getHeight(),TypeQuantization.Chromaticity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
            DataUnitMatrix aduR = new DataUnitMatrix(bduR.getDataDCT(),bduR.getAC(),bduR.getWidth(),bduR.getHeight(),TypeQuantization.Chromaticity);//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!



            MyImage af=new MyImage(aduY.getWidth(),aduY.getHeight(),aduY.getDataOrigin(),aduB.getDataOrigin(),aduR.getDataOrigin());//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
          //  af.PixelRestoration();
            af.FromYBRtoRGB();//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!
            af.FromRGBtoBitmap();//TODO optimization !!!!!!!!!!!!!!!!!!!!!!!!!





            return af.getBitmap();

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            after.setImageBitmap(result);


        }
    }
}
