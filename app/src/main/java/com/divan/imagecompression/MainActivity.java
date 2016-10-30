package com.divan.imagecompression;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.media.Image;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.password;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener{

    ImageView after,befor;
    ProgressBar pB;
    Button bPath,bStar;
    Switch sw1,sw2,sw3;
    TextView tv,tvOriginal,tvReceived;
    Flag f;
    Intent intentFM;
    String path,name,type,PathAndName;
    FromBMPtoFile fromBMP;
    FromFileToBMP fromFile;
    Chronometer chronometer;
    FloatingActionButton floatingActionButton,floatingSetting;
    SharedPreferences sp;
    AlertDialog alertDialogPassword;
    int n,m;
    String password="0";
    boolean isFromBar;

    private final int CAMERA_RESULT = 0;
    private final int FILE_RESULT = 1;

    private static int TAKE_PICTURE = 2;
    private Uri mOutputFileUri;
    String photoPath="/sdcard/ImageCompresion/photo.jpg";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initializate();

        SwitchInitializate();

        SetClickListener();

        AlertDialogPasswordCreate();

        f.setDC(true);
        f.setOneFile(true);
        f.setQuantization(Flag.QuantizationState.Without);
        //f.setLongCode(true);
        //f.setGlobalBase(true);
        //f.setQuantization(Flag.QuantizationState.Without);
       // f.setAlignment(true);







    }


    public class FromBMPtoFile extends AsyncTask<String,Integer,String> {//path,flag,key

        final String DIR_SD = "ImageCompresion";
        final String LOG_TAG = "myLogs";
        final int GiveKey=1,GiveSize=2;


        public void  Update(int val){
            publishProgress(val);
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            if(values[0]==GiveKey)
            {
            }
            else {
                pB.setProgress(values[0]);
                switch (values[0]) {
                    case 10:
                        tv.setText("Преобразование в цветность-яркость");
                        break;
                    case 20:
                        tv.setText("ДКП");
                        break;
                    case 50:
                        tv.setText("ОПК");
                        break;
                    case 70:
                        tv.setText("запись в файл");
                        break;
                    case 100:
                        tv.setText("готово");
                        break;
                }
            }

        }
        @Override
        protected String doInBackground(String... strings) {
            Bitmap bmp= BitmapFactory.decodeFile(strings[0]);

            MyImage mi=new MyImage(bmp,new Flag(Short.decode(strings[1])));

            publishProgress(10);
            Matrix matrix=mi.getYenlMatrix();

            BoxOfDUM bodum=new BoxOfDUM(matrix);
            publishProgress(20);
            bodum.dataProcessing();
            publishProgress(50);

            ApplicationOPC AppOPC=ApplicationOPC.getInstance();
            AppOPC.setSizeofblock(n,m);

            AppOPC.FromMatrixToFile(this,bodum.getMatrix(),getPathAndName(strings[0]));
            publishProgress(100);
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            File info=new File(PathAndName+".bar");
            tvReceived.setText(String.valueOf(info.length()/1024)+" Kb");
            onAsyncFinish();
        }

        public String GiveMeKey(){
            //publishProgress(GiveKey);
            return password;
        }
        public int [] GiveMeSize(){
            int [] size=new int [2];
            size[0]=n;
            size[1]=m;
            return size;
        }
    }

    public class FromFileToBMP extends AsyncTask<String,Integer,Bitmap>{//path,key
        final String DIR_SD = "ImageCompresion";
        final String LOG_TAG = "myLogs";
        final int GiveKey=-1;


        public void Update(int val){
            publishProgress(val);
        }

        public String GiveMeKey(){
            return password;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if(values[0]!=GiveKey) {
                pB.setProgress(values[0]);
                switch (values[0]) {
                    case 10:
                        tv.setText("Считывание из файла");
                        break;
                    case 30:
                        tv.setText("ОПК");
                        break;
                    case 50:
                        tv.setText("обратное ДКП");
                        break;
                    case 70:
                        tv.setText("из ДКП в битмап");
                        break;
                    case 90:
                        tv.setText("сохранение битмапа в ПНГ");
                        break;
                    case 100:
                        tv.setText("готово");
                        break;
                }
            }
            else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("PASSWORD");
                alertDialog.setMessage("Enter Password");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                password = input.getText().toString();

                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

            }

        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            ApplicationOPC AppOPC=ApplicationOPC.getInstance();
            AppOPC.setSizeofblock(n,m);

            publishProgress(10);
            Matrix FFTM=AppOPC.FromFileToMatrix(this,strings[0]);//TODO path
            publishProgress(50);
            BoxOfDUM bodum1=new BoxOfDUM(FFTM);
            publishProgress(50);
            bodum1.dataProcessing();
            publishProgress(70);
            MyImage af=new MyImage(bodum1.getMatrix());

            Bitmap res=af.getBitmap();
            publishProgress(90);

            try{
                AndroidBmpUtil.save(res,strings[0]+"res.bmp");
                /*FileOutputStream fOut = new FileOutputStream(strings[0]+"res.bmp");
                res.compress(Bitmap.CompressFormat.PNG,100,fOut);*/
            }catch (IOException r){
            }
            publishProgress(100);
            return res;
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

           onAsyncFinish();
            after.setImageBitmap(result);

            File info=new File(PathAndName+"res.bmp");
            tvReceived.setText(String.valueOf(info.length()/1024)+" Kb");
        }
    }


    public void AlertDialogPasswordCreate(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("PASSWORD");
        alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        password = input.getText().toString();
                        if(password==null)
                            password="0";
                        if(!isFromBar) {
                            onAsyncStart();
                            fromBMP = new FromBMPtoFile();
                            fromBMP.execute(path, f.toString());
                        }
                        else{
                            onAsyncStart();
                            fromFile = new FromFileToBMP();
                            fromFile.execute(PathAndName);
                        }

                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialogPassword=alertDialog.create();
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
    private String getPathAndName(String path){
        StringBuilder buf=new StringBuilder();
        for(int i=0;i<path.length()-4;i++)
            buf.append(path.charAt(i));
        return buf.toString();
    }

    private void Initializate(){
        f=new Flag((short)0);

        befor=(ImageView)findViewById(R.id.imageView);
        after=befor;
        tv=(TextView)findViewById(R.id.textView);
        tvOriginal=(TextView)findViewById(R.id.textOriginal);
        tvReceived=(TextView)findViewById(R.id.textReceived);
        pB=(ProgressBar)findViewById(R.id.progressBar);
        pB.setMax(100);
        bStar=(Button)findViewById(R.id.button2);
        bPath=(Button)findViewById(R.id.button);
        sw1=(Switch)findViewById(R.id.switch1);
        sw2=(Switch)findViewById(R.id.switch2);
        sw3=(Switch)findViewById(R.id.switch3);
        chronometer=(Chronometer)findViewById(R.id.chronometer2);


        type=new String("null");
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        intentFM = new Intent(this,FileManager.class);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    }
    private void SwitchInitializate(){
        sw1.setText("Fixed Length");
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                f.setLongCode(b);

            }
        });

        sw2.setText("GlobalBase");
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                f.setGlobalBase(b);
            }
        });

        sw3.setText("Password");
        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                f.setPassword(b);
            }
        });


        sw1.setChecked(true);
        sw2.setChecked(true);
        //sw3.setChecked(true);
    }
    private void SetClickListener(){
        bStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("BMP")||type.equals("bmp")||type.equals("jpg")||type.equals("jpeg")){
                    isFromBar=false;
                    if(f.isPassword())
                        alertDialogPassword.show();
                    else{
                        onAsyncStart();
                        fromBMP = new FromBMPtoFile();
                        fromBMP.execute(path, f.toString());
                    }
                }
                else if(type.equals("bar")){
                    isFromBar=true;
                    if(f.isPassword())
                        alertDialogPassword.show();
                    else {
                        onAsyncStart();
                        fromFile = new FromFileToBMP();
                        fromFile.execute(PathAndName);
                    }
                }

            }
        });

        bPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(intentFM, FILE_RESULT);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFullImage();
                /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_RESULT);*/
            }
        });
    }

    private void saveFullImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(photoPath);
        mOutputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void onAsyncStart(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        bStar.setClickable(false);
        bPath.setClickable(false);
        floatingActionButton.hide();
    }
    private void onAsyncFinish(){
        chronometer.stop();
        bStar.setClickable(true);
        bPath.setClickable(true);
        floatingActionButton.show();
    }
    @Override
    public void onClick(View view) {


    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT) {
            Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
            after.setImageBitmap(thumbnailBitmap);
        }
        if(requestCode==FILE_RESULT) {
            path = data.getStringExtra("path");
            after.setImageBitmap(BitmapFactory.decodeFile(path));
            name = getNameFile(path);
            type = getTypeFile(path);
            PathAndName = getPathAndName(path);
            tv.setText(name + '.' + type);

            File info=new File(path);
            tvOriginal.setText(String.valueOf(info.length()/1024)+" Kb");
        }
        if(requestCode==TAKE_PICTURE){
            path=photoPath;
            name = getNameFile(photoPath);
            type = getTypeFile(photoPath);
            PathAndName = getPathAndName(photoPath);
            tv.setText(name + '.' + type);

            File info=new File(photoPath);
            tvOriginal.setText(String.valueOf(info.length()/1024)+" Kb");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Preferences");
        mi.setIntent(new Intent(this, PrefActivity.class));
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();
        // Операции для выбранного пункта меню
        Intent intent=new Intent(this,PrefActivity.class);
        startActivity(intent);
        return true;
    }
    protected void onResume() {
       f.setAlignment(sp.getBoolean("Alignment",false));
       f.setGlobalBase(sp.getBoolean("GlobalBase",false));
       f.setDC(sp.getBoolean("DC",false));
       f.setLongCode(sp.getBoolean("LongCode",false));
       f.setEnlargement(sp.getBoolean("Enlargement",false));
       f.setOneFile(sp.getBoolean("OneFile",false));
        n=Integer.valueOf(sp.getString("Width","4"));
        m=Integer.valueOf(sp.getString("Hieght","4"));
        OPC.setMAXLONG(Integer.valueOf(sp.getString("MAXLONG","54")));


        super.onResume();
    }
}


