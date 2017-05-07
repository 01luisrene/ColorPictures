package com.cerezalab.colorpictures;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    public static final int PETICION_FOTO = 1;
    public static final int PETICION_VIDEO = 2;
    public static final int PETICION_GALERIA_FOTOS = 3;
    public static final int PETICION_GALERIA_VIDEOS = 4;

    public static final int MEDIA_FOTO = 5;
    public static final int MEDIA_VIDEO = 6;
    private static final int MAX_DURATION = 30;

    private Uri mediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            //Manejar la información
            if(requestCode == PETICION_FOTO){
                //Ver la foto
                Intent intent  = new Intent(this, ImageActivity.class);
                intent.setData(mediaUri);
                startActivity(intent);
            }
            if(requestCode == PETICION_VIDEO){
                Intent intent = new Intent(Intent.ACTION_VIEW, mediaUri);
                intent.setDataAndType(mediaUri, "video/*");
                startActivity(intent);
            }
            if(requestCode == PETICION_GALERIA_FOTOS){
                Intent intent = new Intent(this, ImageActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
            }
            if(requestCode == PETICION_GALERIA_VIDEOS){
                Intent intent = new Intent(this, VideoActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
            }

        }else{

            Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show();

        }

    }

    public void tomarFoto(View view) {

        try {
            mediaUri = crearArchivoMedio(MEDIA_FOTO);
            
            if(mediaUri == null){
                Toast.makeText(this, "Problema con tu almacenamiento", Toast.LENGTH_SHORT).show();
            }else{

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                startActivityForResult(intent, PETICION_FOTO);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void tomarVideo(View view) {

        try {
            mediaUri = crearArchivoMedio(MEDIA_VIDEO);

            if(mediaUri == null){
                Toast.makeText(this, "Problema con tu almacenamiento", Toast.LENGTH_SHORT).show();
            }else{

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_DURATION);
                startActivityForResult(intent, PETICION_VIDEO);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void verGaleriaFotos(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("image/*");

        startActivityForResult(intent, PETICION_GALERIA_FOTOS);

    }

    public void verGaleriaVideos(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("video/*");

        startActivityForResult(intent, PETICION_GALERIA_VIDEOS);

    }

    private Uri crearArchivoMedio(int tipoMedio) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String nombreArchivo;
        File archivo;

        if(tipoMedio == MEDIA_FOTO){

            nombreArchivo = "IMG_" + timeStamp + "_";

            File directorioAlmacenamieto = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            archivo = File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamieto);

            Log.d("TAG", archivo.getAbsolutePath());

            MediaScannerConnection.scanFile(this, new String[] { archivo.getPath() }, new String[] { "image/jpeg","video/mp4" }, null);

            return Uri.fromFile(archivo);

        }else if(tipoMedio == MEDIA_VIDEO){

            nombreArchivo = "MOV_" + timeStamp + "_";

            File directorioAlmacenamieto = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

            archivo = File.createTempFile(nombreArchivo, ".mp4", directorioAlmacenamieto);

            Log.d("TAG", archivo.getAbsolutePath());

            MediaScannerConnection.scanFile(this, new String[] { archivo.getPath() }, new String[] { "image/jpeg","video/mp4" }, null);

            return Uri.fromFile(archivo);

        }else{
            return null;
        }

    }

    private boolean almacenamientoExternoDisponible(){

        String estado = Environment.getExternalStorageState();

        if(estado.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }

    }

}
