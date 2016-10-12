package com.divan.imagecompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public ImageView after,befor;
    public ProgressBar pB;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        befor=(ImageView)findViewById(R.id.imageView);
        after=befor;
        tv=(TextView)findViewById(R.id.textView);
        pB=(ProgressBar)findViewById(R.id.progressBar);
        pB.setMax(100);

        /*Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(),
                R.drawable.kiev);
        befor.setImageBitmap(bmOriginal);*/
//async <code></code>


       /* JPEGcodek jpg = new JPEGcodek();
        jpg.execute(bmOriginal);*/
        String file="test";

        FromBMPtoFile fbtf=new FromBMPtoFile();
        fbtf.execute(file);

        FromFileToBMP toBMP=new FromFileToBMP();
        toBMP.execute(file);




    }
/*
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
*/
    public class FromBMPtoFile extends AsyncTask<String,Integer,String> {

        final String DIR_SD = "ImageCompresion";
        final String LOG_TAG = "myLogs";

        public void  Update(int val){
            publishProgress(val);
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);




            pB.setProgress(values[0]);
            switch (values[0]){
                case 10:tv.setText("Преобразование в цветность-яркость");break;
                case 20:tv.setText("ДКП");break;
                case 50:tv.setText("ОПК");break;
                case 70:tv.setText("запись в файл");break;
                case 100:tv.setText("готово");break;
            }

        }
        @Override
        protected String doInBackground(String... strings) {

            File sdPath = Environment.getExternalStorageDirectory();
            // добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
            // создаем каталог
            sdPath.mkdirs();
            // формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, strings[0]+".bmp");

            Bitmap bmp= BitmapFactory.decodeFile(sdFile.toString());

            MyImage mi=new MyImage(bmp);

            //mi.getYenlMatrix();


            publishProgress(10);
            Matrix matrix=mi.getYenlMatrix();

    /*        if(true)//TODO Qantization type
                matrix.qs= Matrix.QuantizationState.First;*/

            BoxOfDUM bodum=new BoxOfDUM(matrix);
            publishProgress(20);
            bodum.dataProcessing();
            publishProgress(50);

            //write to file


            ApplicationOPC AppOPC=ApplicationOPC.getInstance();

            AppOPC.FromDCTtoFile(this,bodum.getMatrix(),strings[0]+"OPC");
            publishProgress(100);
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);





        }
    }

    public class FromFileToBMP extends AsyncTask<String,Integer,Bitmap>{
        final String DIR_SD = "ImageCompresion";
        final String LOG_TAG = "myLogs";

        public void Update(int val){
            publishProgress(val);
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            pB.setProgress(values[0]);
            switch (values[0]){
                case 10:tv.setText("Считывание из файла");break;
                case 30:tv.setText("ОПК");break;
                case 50:tv.setText("обратное ДКП");break;
                case 70:tv.setText("из ДКП в битмап");break;
                case 90:tv.setText("сохранение битмапа в ПНГ");break;
                case 100:tv.setText("готово");break;
            }

        }
        @Override
        protected Bitmap doInBackground(String... strings) {

            File sdPath = Environment.getExternalStorageDirectory();
            // добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
            // создаем каталог
            sdPath.mkdirs();
            // формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, strings[0]);

            ApplicationOPC AppOPC=ApplicationOPC.getInstance();

            publishProgress(10);
            Matrix FFTM=AppOPC.FromFileToMatrix(this,strings[0]+"OPC");

           /* if(true)//TODO Qantization type
               FFTM.qs= Matrix.QuantizationState.First;
*/
            publishProgress(50);

            BoxOfDUM bodum1=new BoxOfDUM(FFTM);

            publishProgress(50);
            bodum1.dataProcessing();
            publishProgress(70);
            MyImage af=new MyImage(bodum1.getMatrix());




            Bitmap res=af.getBitmap();
            publishProgress(90);

            try{
                FileOutputStream fOut = new FileOutputStream(sdFile+"res.bmp");
                res.compress(Bitmap.CompressFormat.PNG,100,fOut);
            }catch (IOException r){

            }
            publishProgress(100);

            return res;
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            after.setImageBitmap(result);




        }
    }
}


