package es.unizar.eina.ebrozon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.ResultIPC;

public class SubirProd3_3 extends AppCompatActivity {


    Button siguiente;
    Button anterior;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_prod3_3);

        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);

        siguiente = findViewById(R.id.PantallaSiguiente);
        anterior = findViewById(R.id.PantallaAnterior);

        siguiente.setEnabled(true);
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

    }

    private void siguientePaso() {

        siguiente.setEnabled(false);
        Intent intentAnterior = getIntent();
        String producto = intentAnterior.getStringExtra("nombreProducto");
        String descripcion = intentAnterior.getStringExtra("descripcionProducto");
        int sync = getIntent().getIntExtra("bigdata:synccode", -1);
        final List<Object> datos = ResultIPC.get().getLargeData(sync);


        Bitmap imagen1_bm = ( Bitmap )datos.get(0);
        Bitmap imagen2_bm = ( Bitmap )datos.get(1);
        Bitmap imagen3_bm = ( Bitmap )datos.get(2);
        Bitmap imagen4_bm = ( Bitmap )datos.get(3);

        String precio = intentAnterior.getStringExtra("precioProducto");
        Boolean esSubasta = intentAnterior.getBooleanExtra("esSubasta", false);
        Long fechaLimite = intentAnterior.getLongExtra("fechaLimite",0);
        String precioInicial = intentAnterior.getStringExtra("precioInicial");

        ProgressDialog dialog = ProgressDialog.show(SubirProd3_3.this, "",
                "Subiendo...", true);

        subirProducto(producto, descripcion, imagen1_bm, imagen2_bm, imagen3_bm, imagen4_bm,
                precio, esSubasta, fechaLimite, precioInicial);


    }

    private void pasoAnterior() {
        finish();
    }

    private void volverPrincipal() {
        Intent intent = new Intent(SubirProd3_3.this, PantallaPrincipal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    private void gestionPeticion(String estado, String msg) {
        if (estado.equals("O")) {

            volverPrincipal();
        } else if (estado.equals("E")) {
            siguiente.setEnabled(true);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        } else {
            siguiente.setEnabled(true);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    private void subirProducto(final String producto, final String descripcion, final Bitmap imagen1_bm,
                               final Bitmap imagen2_bm, final Bitmap imagen3_bm, final Bitmap imagen4_bm,
                               final String precio, final Boolean esSubasta, final long fechaLimite,
                               final String precioInicial) {

        RequestQueue queue = Volley.newRequestQueue(this);


        String urlPetition = Common.url;
        if (esSubasta) {
            urlPetition = urlPetition + "/publicarSubasta";
        } else {
            urlPetition = urlPetition + "/publicarVenta";
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        gestionPeticion(estado, msg);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(SubirProd3_3.this, "Error al subir: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                String uName = sharedpreferences.getString(Common.un, null);
                String foto1 = BitMapToString(imagen1_bm);
                String foto2 = BitMapToString(imagen2_bm);
                String foto3 = BitMapToString(imagen3_bm);
                String foto4 = BitMapToString(imagen4_bm);


                params.put("un", uName);
                params.put("prod", producto);
                params.put("desc", descripcion);
                params.put("pre", precio);

                params.put("arc1", foto1);
                params.put("arc2", foto2);
                params.put("arc3", foto3);
                params.put("arc4", foto4);

                if (esSubasta) {
                    params.put("pin", precioInicial);
                    params.put("end", String.valueOf(fechaLimite));
                }


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }


        };
        queue.add(postRequest);
    }


    public String BitMapToString(Bitmap bitmap){
        if(bitmap == null){
            return "";
        }
        else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        }
    }
}





