package com.example.simplemusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.example.simplemusicplayer.adapter.MusicAdapter;
import com.example.simplemusicplayer.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding mainBinding;
    private MusicAdapter musicAdapter;
    private ArrayList<String> songList = new ArrayList<>();
    private final static String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath()+"/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        Log.e("Media path",MEDIA_PATH);
        mainBinding.recyclerViewSong.setLayoutManager(new LinearLayoutManager(this));
        checkPermission();


    }
    private void checkPermission(){
        //check permission is granted or not
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
            ,1);
        }
        else {
            loadAllAudioFiles();
        }
    }
    private void loadAllAudioFiles(){
        if(MEDIA_PATH != null){
            File allFiles = new File(MEDIA_PATH);
            File[] fileList = allFiles.listFiles();
            for (File file: fileList){
                Log.e("Media Path",file.toString()); //list all the file path
                if(file.isDirectory()){
                    scanDir(file);
                }
                else{
                    String path = file.getAbsolutePath();
                    if(path.endsWith(".mp3")){
                        songList.add(path);
                        musicAdapter.notifyDataSetChanged(); //has changes => update adapter
                    }
                }
            }
        }
        musicAdapter = new MusicAdapter(MainActivity.this,songList);
        mainBinding.recyclerViewSong.setAdapter(musicAdapter);

    }
    private void scanDir(File fileDir){
        if(fileDir != null){
            File[] fileList = fileDir.listFiles();
            try {
                for (File file : fileList) {
                    Log.e("Media Path", file.toString()); //list all the file path
                    if (file.isDirectory()) {
                        scanDir(file);
                    } else {
                        String path = file.getAbsolutePath();
                        if (path.endsWith(".mp3")) {
                            songList.add(path);
                        }
                    }
                }
            }catch (NullPointerException e ){ e.printStackTrace();}
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            loadAllAudioFiles();
        }
    }
}