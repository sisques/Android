package es.unizar.eina.ebrozon;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.unizar.eina.ebrozon.lib.ResultIPC;


public class SubirProd1_3 extends AppCompatActivity {

    ImageButton foto1;
    ImageButton foto2;
    ImageButton foto3;
    ImageButton foto4;
    EditText nombreProducto;
    EditText descripcionProducto;
    Button siguiente;
    Button anterior;

    Bitmap imagen1_bm;
    Bitmap imagen2_bm;
    Bitmap imagen3_bm;
    Bitmap imagen4_bm;

    Bitmap placeholder;

    Boolean prodNameCheckLength = false;
    Boolean prodDescCheckLength = false;

    int GALERIA_1 = 1;
    int GALERIA_2 = 2;
    int GALERIA_3 = 3;
    int GALERIA_4 = 4;
    int CAMARA_1 = 5;
    int CAMARA_2 = 6;
    int CAMARA_3 = 7;
    int CAMARA_4 = 8;

    String IMAGE_DIRECTORY = "/Ebrozon_app";
    List<Object> datos = new ArrayList<Object>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_prod1_3);

        foto1 =  findViewById(R.id.archivo1);
        foto2 =  findViewById(R.id.archivo2);
        foto3 =  findViewById(R.id.archivo3);
        foto4 =  findViewById(R.id.archivo4);
        nombreProducto =  findViewById(R.id.NombreProducto);
        descripcionProducto = findViewById(R.id.DescripcionProducto);
        siguiente = findViewById(R.id.PantallaSiguiente);
        anterior = findViewById(R.id.PantallaAnterior);

        placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.subir_archivo);

        siguiente.setEnabled(false);
        anterior.setEnabled(true);

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                siguientePaso();
            }
        });

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasoAnterior();
            }
        });

        foto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen1();
            }
        });

        foto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen2();
            }
        });

        foto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen3();
            }
        });

        foto4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen4();
            }
        });



        nombreProducto.addTextChangedListener(subirProducto1_3TextWatcher);
        descripcionProducto.addTextChangedListener(subirProducto1_3TextWatcher);

    }

    private TextWatcher subirProducto1_3TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String prod = nombreProducto.getText().toString().trim();
            String desc = descripcionProducto.getText().toString().trim();

            siguiente.setEnabled(!prod.isEmpty() && !desc.isEmpty() && imagen1_bm != null );
        }

        @Override
        public void afterTextChanged(Editable s) {

            String prod = nombreProducto.getText().toString().trim();
            String desc = descripcionProducto.getText().toString().trim();

            prodNameCheckLength = (prod.length() >= 3 && prod.length() <= 100);;
            prodDescCheckLength = (desc.length() >= 10);

        }
    };

    private void siguientePaso(){
        if(!prodNameCheckLength){
            Toast.makeText(getApplicationContext(),"El nombre del producto debe tener entre 3 y 100 caracteres", Toast.LENGTH_LONG).show();
        }
        else if(!prodDescCheckLength){
            Toast.makeText(getApplicationContext(),"La descripción del producto debe tener como mínimo 10 caracteres", Toast.LENGTH_LONG).show();
        }
        else {
            String producto = nombreProducto.getText().toString().trim();
            String descripcion = descripcionProducto.getText().toString().trim();
            Intent intent = new Intent(SubirProd1_3.this, SubirProd2_3.class);
            intent.putExtra("nombreProducto", producto);
            intent.putExtra("descripcionProducto", descripcion);


            datos.add( imagen1_bm );
            datos.add( imagen2_bm );
            datos.add( imagen3_bm );
            datos.add( imagen4_bm );
            int sync = ResultIPC.get().setLargeData(datos);
            intent.putExtra("bigdata:synccode", sync);
            startActivity(intent);
        }
    }

    private void pasoAnterior(){
        finish();
    }

    private void subirImagen1(){
        mostrarDialogo(1);
    }

    private void subirImagen2(){
        mostrarDialogo(2);
    }

    private void subirImagen3(){
        mostrarDialogo(3);
    }

    private void subirImagen4(){
        mostrarDialogo(4);
    }

    private void mostrarDialogo(final int origen){
        AlertDialog.Builder dialogoFuente = new AlertDialog.Builder(this);
        dialogoFuente.setTitle("Seleccione una acción");
        String[] pictureDialogItems = {
                "Abrir galeria",
                "Abrir camara",
                "Eliminar foto",
                "Atrás"};
        dialogoFuente.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                abrirGaleria(origen);
                                break;
                            case 1:
                                abrirCamara(origen);
                                break;
                            case 2:
                                eliminarFoto(origen);
                                break;
                            case 3:
                                break;
                        }
                    }
                });
        dialogoFuente.show();
    }

    private void abrirGaleria(int origen) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        switch (origen) {
            case 1:
                origen = GALERIA_1;
                break;
            case 2:
                origen = GALERIA_2;
                break;
            case 3:
                origen = GALERIA_3;
                break;
            case 4:
                origen = GALERIA_4;
                break;
        }
        startActivityForResult(galleryIntent, origen);
    }

    private void abrirCamara(int origen) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        switch (origen) {
            case 1:
                origen = CAMARA_1;
                break;
            case 2:
                origen = CAMARA_2;
                break;
            case 3:
                origen = CAMARA_3;
                break;
            case 4:
                origen = CAMARA_4;
                break;
        }

        startActivityForResult(intent, origen);
    }

    private void eliminarFoto(int origen){
        switch (origen) {
            case 1:
                imagen1_bm = null;
                foto1.setImageBitmap(placeholder);
                break;
            case 2:
                imagen2_bm = null;
                foto2.setImageBitmap(placeholder);
                break;
            case 3:
                imagen3_bm = null;
                foto3.setImageBitmap(placeholder);
                break;
            case 4:
                imagen4_bm = null;
                foto4.setImageBitmap(placeholder);
                break;
        }

        String prod = nombreProducto.getText().toString().trim();
        String desc = descripcionProducto.getText().toString().trim();
        siguiente.setEnabled(!prod.isEmpty() && !desc.isEmpty() && imagen1_bm != null );

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALERIA_1 || requestCode == GALERIA_2 || requestCode == GALERIA_3 || requestCode == GALERIA_4) {
            if (data != null) {


                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);


                    int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );

                    bitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    switch (requestCode) {
                        case 1:

                            imagen1_bm = bitmap;
                            foto1.setImageBitmap(imagen1_bm);
                            break;
                        case 2:

                            imagen2_bm = bitmap;
                            foto2.setImageBitmap(imagen2_bm);
                            break;
                        case 3:

                            imagen3_bm = bitmap;
                            foto3.setImageBitmap(imagen3_bm);
                            break;
                        case 4:

                            imagen4_bm = bitmap;
                            foto4.setImageBitmap(imagen4_bm);
                            break;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SubirProd1_3.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMARA_1 || requestCode == CAMARA_2 || requestCode == CAMARA_3 || requestCode == CAMARA_4) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            guardarFoto(thumbnail);
            int nh = (int) ( thumbnail.getHeight() * (512.0 / thumbnail.getWidth()) );
            thumbnail = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);

            switch (requestCode) {
                case 5:
                    imagen1_bm = thumbnail;
                    foto1.setImageBitmap(imagen1_bm);
                    foto1.setScaleX(3);
                    foto1.setScaleY(3);
                    break;
                case 6:
                    imagen2_bm = thumbnail;
                    foto2.setImageBitmap(imagen2_bm);
                    break;
                case 7:
                    imagen3_bm = thumbnail;
                    foto3.setImageBitmap(imagen3_bm);
                    break;
                case 8:
                    imagen4_bm = thumbnail;
                    foto4.setImageBitmap(imagen4_bm);
                    break;
            }

         }
        String prod = nombreProducto.getText().toString().trim();
        String desc = descripcionProducto.getText().toString().trim();
        siguiente.setEnabled(!prod.isEmpty() && !desc.isEmpty() && imagen1_bm != null );
    }


    public String guardarFoto(Bitmap myBitmap) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File wallpaperDirectory = new File(
                    Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
            // have the object build the directory structure, if needed.
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            try {
                File f;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    f = new File(wallpaperDirectory, "producto-"+ Calendar.getInstance().getTime()+".jpg");
                }
                else {
                    f = new File(wallpaperDirectory, "producto.jpg");
                }
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                MediaScannerConnection.scanFile(this,
                        new String[]{f.getPath()},
                        new String[]{"image/jpeg"}, null);
                fo.close();
                Log.d("TAG", "Imagen guardada::--->" + f.getAbsolutePath());

                return f.getAbsolutePath();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return "";
        }



}
