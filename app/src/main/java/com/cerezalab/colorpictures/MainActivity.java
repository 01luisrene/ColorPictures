package com.cerezalab.colorpictures;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
    private static final int CAMERA_WRITE_PERMISSION = 11;
    private static final int VIDEO_WRITE_PERMISSION = 12;

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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //Habilitar permisos para la version de API 23 a mas
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //solicitar permiso
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mostrarExplicacion(PETICION_FOTO);
                }else{

                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_WRITE_PERMISSION);
                }


            }else{
                //no solicitar permiso
                crearMedio(PETICION_FOTO);
            }

        }else{
            crearMedio(PETICION_FOTO);
        }

    }

    private void mostrarExplicacion(final int tipoPeticion) {

        new AlertDialog.Builder(this)
                .setTitle("Necesito tu permiso")
                .setMessage("Necesito permiso para poder guardar tus fotos y videos.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //pedir permiso
                        if(tipoPeticion == PETICION_FOTO){
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_WRITE_PERMISSION);
                        }else if(tipoPeticion == PETICION_VIDEO){
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, VIDEO_WRITE_PERMISSION);
                        }else{
                            throw new IllegalArgumentException();
                        }
                    }
                })
                .setNegativeButton("Nop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Desplegar mensaje de lamentación
                        Toast.makeText(MainActivity.this, "ouch :(", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if(requestCode == CAMERA_WRITE_PERMISSION){

                crearMedio(PETICION_FOTO);

            }

            if(requestCode == VIDEO_WRITE_PERMISSION){

                crearMedio(PETICION_VIDEO);

            }

        }

    }

    public void tomarVideo(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //Habilitar permisos para la version de API 23 a mas
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //solicitar permiso
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mostrarExplicacion(PETICION_VIDEO);
                }else{

                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, VIDEO_WRITE_PERMISSION);
                }


            }else{
                //no solicitar permiso
                crearMedio(PETICION_VIDEO);
            }

        }else{
            crearMedio(PETICION_VIDEO);
        }

    }
    private void crearMedio(int tipoPeticion){

        try {
            if(tipoPeticion == PETICION_FOTO){
                mediaUri = crearArchivoMedio(MEDIA_FOTO);
            }else if(tipoPeticion == PETICION_VIDEO){
                mediaUri = crearArchivoMedio(MEDIA_VIDEO);
            }else{
                throw new IllegalArgumentException();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mediaUri == null){
            Toast.makeText(this, "Hubo un problema", Toast.LENGTH_SHORT).show();
        }else{
            iniciarCamara(mediaUri, tipoPeticion);
        }

    }
    private void iniciarCamara(Uri mediaUri, int tipoPeticion) {

        Intent intent;

        if(tipoPeticion == PETICION_VIDEO){

            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_DURATION);
            startActivityForResult(intent, tipoPeticion);

        }else if(tipoPeticion == PETICION_FOTO){

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            startActivityForResult(intent, tipoPeticion);

        }else{
            try {
                throw new IllegalAccessException();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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
        if(!almacenamientoExternoDisponible())
            return null;


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
