package com.divan.imagecompression;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener{

    ImageView after,befor;
    ProgressBar pB;
    Button bPath,bStar;
    Switch swQuan,swFile,swNo;
    TextView tv;
    Flag f;
    Intent intentFM;
    String path,name,type;
    FromBMPtoFile fromBMP;
    FromFileToBMP fromFile;
    Chronometer chronometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f=new Flag((byte)0);

        befor=(ImageView)findViewById(R.id.imageView);
        after=befor;
        tv=(TextView)findViewById(R.id.textView);
        pB=(ProgressBar)findViewById(R.id.progressBar);
        pB.setMax(100);
        bStar=(Button)findViewById(R.id.button2);
        bPath=(Button)findViewById(R.id.button);
        swQuan=(Switch)findViewById(R.id.switch1);
        swFile=(Switch)findViewById(R.id.switch2);
        swNo=(Switch)findViewById(R.id.switch3);
        chronometer=(Chronometer)findViewById(R.id.chronometer2);

        type=new String("null");



        swQuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                f.setQuantization(Flag.QuantizationState.First);
                else
                    f.setQuantization(Flag.QuantizationState.Without);
            }
        });
        swFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    f.setOneFile(b);
            }
        });

        swFile.setChecked(true);
        swQuan.setChecked(true);

        intentFM = new Intent(this,FileManager.class);
        bPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(intentFM, 1);
            }
        });

        bStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(type.equals("BMP")||type.equals("bmp")){
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    bStar.setClickable(false);
                    bPath.setClickable(false);
                    fromBMP=new FromBMPtoFile();
                    fromBMP.execute(name,f.toString());
                }
                else if(type.equals("bar")){
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    bStar.setClickable(false);
                    bPath.setClickable(false);
                    fromFile=new FromFileToBMP();
                    fromFile.execute(name);
                }

            }
        });



        /*Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(),
                R.drawable.kiev);
        befor.setImageBitmap(bmOriginal);*/
//async <code></code>


/*        String file="test";

        FromBMPtoFile fbtf=new FromBMPtoFile();
        fbtf.execute(file,f.toString());

        FromFileToBMP toBMP=new FromFileToBMP();
        toBMP.execute(file);*/





    }

    @Override
    public void onClick(View view) {


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        path = data.getStringExtra("path");
        after.setImageBitmap( BitmapFactory.decodeFile(path));
        name=getNameFile(path);
        type=getTypeFile(path);
    }


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


            byte flag=new Byte(strings[1]);
            MyImage mi=new MyImage(bmp,new Flag(Byte.decode(strings[1])));

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

            chronometer.stop();
            bStar.setClickable(true);
            bPath.setClickable(true);
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
            Matrix FFTM=AppOPC.FromFileToMatrix(this,strings[0]);

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

            chronometer.stop();
            bStar.setClickable(true);
            bPath.setClickable(true);
            after.setImageBitmap(result);
        }
    }

    private String getNameFile(String path){
        StringBuilder sb=new StringBuilder();
        for(int i=path.length()-5;i>=0;i--){
            if(path.charAt(i)=='/')
                break;
            sb.append(path.charAt(i));
        }
        sb.reverse();
        return sb.toString();
    }
    private String getTypeFile(String path){
        StringBuilder type=new StringBuilder();
        for(int i=path.length()-3;i<path.length();i++)
            type.append(path.charAt(i));
        return type.toString();
    }
}


