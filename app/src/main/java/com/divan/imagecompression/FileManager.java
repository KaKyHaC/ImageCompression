package com.divan.imagecompression;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class FileManager extends ListActivity  {



    private List<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = new File("/");
    //when application started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        //browse to root directory
        browseTo(new File("/sdcard/ImageCompresion"));
    }

    //browse to parent directory
    private void upOneLevel(){
        if(this.currentDirectory.getParent() != null) {
            this.browseTo(this.currentDirectory.getParentFile());
        }
    }

    //browse to file or directory
    private void browseTo(final File aDirectory){
        //if we want to browse directory
        if (aDirectory.isDirectory()){
            //fill list with files from this directory
            this.currentDirectory = aDirectory;
            fill(aDirectory.listFiles());

            //set titleManager text
            TextView titleManager = (TextView) findViewById(R.id.titleManager);
            titleManager.setText(aDirectory.getAbsolutePath());
        } else {
            //if we want to open file, show this dialog:
            //listener when YES button clicked
            OnClickListener okButtonListener = new OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent();
                    intent.putExtra("path", aDirectory.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            };
            //listener when NO button clicked
            OnClickListener cancelButtonListener = new OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    //do nothing
                    //or add something you want
                }
            };

            //create dialog
            new AlertDialog.Builder(this)
                    .setTitle("Подтверждение") //title
                    .setMessage("Хотите открыть файл "+ aDirectory.getName() + "?") //message
                    .setPositiveButton("Да", okButtonListener) //positive button
                    .setNegativeButton("Нет", cancelButtonListener) //negative button
                    .show(); //show dialog

        }

    }
    //fill list
    private void fill(File[] files) {
        //clear list
        this.directoryEntries.clear();

        if (this.currentDirectory.getParent() != null)
            this.directoryEntries.add("..");

        //add only BMP file into list
        for (File file : files) {
            String path=file.getAbsolutePath();
            StringBuilder type=new StringBuilder();
            for(int i=path.length()-3;i<path.length();i++)
                type.append(path.charAt(i));

            if(type.toString().equals("BMP")||type.toString().equals("bar")||type.toString().equals("bmp")||path.charAt(path.length()-4)!='.')
              this.directoryEntries.add(file.getAbsolutePath());
            else if(type.toString().equals("jpg")||type.toString().equals("jpeg"))
                this.directoryEntries.add(file.getAbsolutePath());

        }

        //create array adapter to show everything
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, this.directoryEntries);
        this.setListAdapter(directoryList);
    }
    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected file name
        int selectionRowID = position;
        String selectedFileString = this.directoryEntries.get(selectionRowID);

        //if we select ".." then go upper
        if(selectedFileString.equals("..")){
            this.upOneLevel();
        } else {
            //browse to clicked file or directory using browseTo()
            File clickedFile = null;
            clickedFile = new File(selectedFileString);
            if (clickedFile != null)
                this.browseTo(clickedFile);
        }
    }

}
