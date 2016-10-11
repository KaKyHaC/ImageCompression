package com.divan.imagecompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView after,befor;
    ProgressBar pB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        after=(ImageView)findViewById(R.id.imageView2);
        befor=(ImageView)findViewById(R.id.imageView);
        pB=(ProgressBar)findViewById(R.id.progressBar);
        pB.setMax(100);

        Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(),
                R.drawable.kiev);
        befor.setImageBitmap(bmOriginal);
//async <code></code>


        JPEGcodek jpg = new JPEGcodek();
        jpg.execute(bmOriginal);




    }

    public class JPEGcodek extends AsyncTask<Bitmap,Integer,Bitmap> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            pB.setProgress(values[0]);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            MyImage mi=new MyImage(bitmaps[0]);

            //mi.getYenlMatrix();


            BoxOfDUM bodum=new BoxOfDUM(mi.getYenlMatrix());
            publishProgress(10);
            bodum.dataProcessing();
            publishProgress(30);

            //write to file


            ApplicationOPC AppOPC=ApplicationOPC.getInstance();

            AppOPC.FromDCTtoFile(bodum.getMatrix(),"test");

            Matrix FFTM=AppOPC.FromFileToMatrix("test");

            BoxOfDUM bodum1=new BoxOfDUM(FFTM);

            publishProgress(50);
            bodum1.dataProcessing();
            publishProgress(90);
            MyImage af=new MyImage(bodum1.getMatrix());




            Bitmap res=af.getBitmap();

            af.Clear();
            publishProgress(100);
            return res;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            after.setImageBitmap(result);


        }
    }
}
