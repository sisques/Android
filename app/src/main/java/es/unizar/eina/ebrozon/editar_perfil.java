package es.unizar.eina.ebrozon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.util.Calendar;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import es.unizar.eina.ebrozon.lib.Common;

public class editar_perfil extends AppCompatActivity {

    String urlPetition = "https://protected-caverns-60859.herokuapp.com/actualizarUsuario";

    SharedPreferences sharedpreferences;

    private String[] cities;
    private String[] provinces;

    private ImageButton uploadPicture;
    private int GALLERY = 5;
    private int CAMERA = 1;
    private String IMAGE_DIRECTORY = "/";
    private Bitmap newPic;

    private AutoCompleteTextView user_province;
    private AutoCompleteTextView user_city;
    private EditText user_fullname;

    private Button confirm;
    private Button changePassword;
    private Button updateLocation;

    private String currentUser;
    // Anteriores
    private String name;
    private String province;
    private String city;

    @Override
    public void onBackPressed() {
        if (!(
                newPic == null &&
                (name.equals(user_fullname.getText().toString().trim())) &&
                (province.equals(user_province.getText().toString().trim())) &&
                (city.equals(user_city.getText().toString().trim()))
            )) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle("¿Descartar cambios?");
            String[] pictureDialogItems = {
                    "Sí",
                    "No"};
            pictureDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    startActivity(new Intent(editar_perfil.this, perfil_usuario.class));
                                    finish();
                                    break;
                                case 1:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
            pictureDialog.show();
        }
        else {
            startActivity(new Intent(editar_perfil.this, perfil_usuario.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        currentUser = sharedpreferences.getString(Common.un, null);

        confirm = findViewById(R.id.editConfirm);
        confirm.setEnabled(false);

        user_fullname = findViewById(R.id.editName);
        user_province = findViewById(R.id.editProvince);
        provinces = getResources().getStringArray(R.array.ListaProvincias);
        ArrayAdapter<String> adapterProvince = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,provinces);
        user_province.setAdapter(adapterProvince);
        user_city = findViewById(R.id.editCity);
        cities = getResources().getStringArray(R.array.ListaProvincias);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,cities);
        user_city.setAdapter(adapterCity);

        user_province.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

        });

        user_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

        });

        Intent intentAnterior = getIntent();
        name = intentAnterior.getStringExtra("user_fullname");
        province = intentAnterior.getStringExtra("user_province");
        city = intentAnterior.getStringExtra("user_city");
        user_fullname.setText(name);
        user_province.setText(province);
        if (!city.equals("Sin especificar")) {
            user_city.setText(city);
        }
        String id = intentAnterior.getStringExtra("user_idPic");
        bajarFotoServidor(id);

        changePassword = findViewById(R.id.editPassword);
        updateLocation = findViewById(R.id.editUpdateLocation);
        uploadPicture = findViewById(R.id.editPic);


        user_fullname.addTextChangedListener(registerTextWatcher);
        user_province.addTextChangedListener(registerTextWatcher);
        user_city.addTextChangedListener(registerTextWatcher);
        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        confirm.setEnabled(true);
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selecciona una opción");
        String[] pictureDialogItems = {
                "Elegir foto de la galería",
                "Tomar foto"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        confirm.setEnabled(false);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    newPic = bitmap;
                    //Toast.makeText(editar_perfil.this, "Imagen guardada", Toast.LENGTH_SHORT).show();
                    uploadPicture.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(editar_perfil.this, "Se ha producido un error", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            uploadPicture.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            newPic = thumbnail;
            //Toast.makeText(editar_perfil.this, "Imagen guardada", Toast.LENGTH_SHORT).show();
        }
        confirm.setEnabled(true);
    }

    public String saveImage(Bitmap myBitmap) {
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
                f = new File(wallpaperDirectory, "fotoperfil-"+ Calendar.getInstance().getTime()+".jpg");
            }
            else {
                f = new File(wallpaperDirectory, "fotoperfil.jpg");
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

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        //bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void actualizarUsuario(final Bitmap foto, final String fullname, final String province, final String city) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(editar_perfil.this, "Cambios aplicados", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(editar_perfil.this, perfil_usuario.class));
                        updateLocation.setEnabled(true);
                        confirm.setEnabled(true);
                        changePassword.setEnabled(true);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(editar_perfil.this, "Se ha producido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        Boolean actualizarFoto = (foto != null);
                        params.put("un",currentUser);
                        params.put("na",fullname.split(" ")[0]);
                        params.put("lna",fullname.split(" ")[1]);
                        params.put("pr",province);
                        params.put("ci",city);
                        if (actualizarFoto) {
                            String cadenaFoto = BitMapToString(foto);
                            params.put("im",cadenaFoto);
                        }
                        return params;
                    }
        };
        queue.add(postRequest);
    }

    private void bajarFotoServidor(String id) {
        confirm.setEnabled(false);
        RequestQueue queue = Volley.newRequestQueue(this);
        String urlPetition = "http://protected-caverns-60859.herokuapp.com/loadArchivoTemp?id="+id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace(" ","+");
                        int longitud = response.length();
                        Bitmap result = StringToBitMap(response);
                        uploadPicture.setImageBitmap(result);
                        confirm.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(editar_perfil.this, "Error al descargar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(postRequest);
    }

    public static boolean useSet(String[] arr, String targetValue) {
        Set<String> set = new HashSet<String>(Arrays.asList(arr));
        return set.contains(targetValue);
    }

    public void subirCambios(View view) {
        confirm.setEnabled(false);
        updateLocation.setEnabled(false);
        changePassword.setEnabled(false);
        String n = user_fullname.getText().toString();
        String p = user_province.getText().toString();
        String c = user_city.getText().toString();

        Boolean checkName = n.matches("\\p{L}+ \\p{L}+");
        Boolean checkNameLength = n.length() >= 3 && n.length() <= 75;

        if (!checkName) {
            Toast.makeText(getApplicationContext(),"El nombre completo tiene que seguir el patrón Nombre Apellido.", Toast.LENGTH_LONG).show();
        }
        else if (!checkNameLength) {
            Toast.makeText(getApplicationContext(),"El nombre completo tiene que tener entre 3 y 75 caracteres.", Toast.LENGTH_LONG).show();
        }
        else if (!useSet(provinces,p)) {
            Toast.makeText(getApplicationContext(),"Selecciona una provincia de la lista.", Toast.LENGTH_LONG).show();
        }
        else if (c.length() > 0 && !(useSet(cities,c))) {
            Toast.makeText(getApplicationContext(),"Selecciona una ciudad de la lista.", Toast.LENGTH_LONG).show();
        }
        else {
            if (!(
                    newPic == null &&
                            (name.equals(user_fullname.getText().toString().trim())) &&
                            (province.equals(user_province.getText().toString().trim())) &&
                            (city.equals(user_city.getText().toString().trim()))
            )) {
                actualizarUsuario(newPic, user_fullname.getText().toString().trim(), user_province.getText().toString().trim(), user_city.getText().toString().trim());
            }
        }
    }

    public void cambiarContraseña(View view) {
        updateLocation.setEnabled(false);
        changePassword.setEnabled(false);
        confirm.setEnabled(false);
        startActivity(new Intent(editar_perfil.this, CambiarContra.class));
        updateLocation.setEnabled(true);
        changePassword.setEnabled(true);
        confirm.setEnabled(true);
    }

    private void peticionVentas (final String lat, final String lon) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(editar_perfil.this, "Localización actualizada.", Toast.LENGTH_SHORT).show();
                        updateLocation.setEnabled(true);
                        confirm.setEnabled(true);
                        changePassword.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(editar_perfil.this, "Se ha producido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("un",currentUser);
                params.put("lat",lat);
                params.put("long",lon);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void actualizarVentas(View view) {
        changePassword.setEnabled(false);
        updateLocation.setEnabled(false);
        confirm.setEnabled(false);
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Se actualizará la localización de todos tus productos.");
        String[] pictureDialogItems = {
                "Actualizar",
                "Cancelar"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
                                String lat = sharedpreferences.getString(Common.lat, null);
                                String lon = sharedpreferences.getString(Common.lon, null);
                                peticionVentas(lat,lon);
                                dialog.dismiss();
                                break;
                            case 1:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        pictureDialog.show();
        changePassword.setEnabled(true);
        updateLocation.setEnabled(true);
        confirm.setEnabled(true);
    }
}