package es.unizar.eina.ebrozon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.ResultIPC;
import es.unizar.eina.ebrozon.lib.Ventas;

public class SubirProd3_3 extends AppCompatActivity {


    Button siguiente;
    Button anterior;
    ImageButton motor;
    ImageButton tv;
    ImageButton compu;
    ImageButton pelis;
    ImageButton deporte;
    ImageButton hogar;
    ImageButton electro;
    ImageButton ropa;
    ImageButton bebe;
    ImageButton agri;
    ImageButton empleo;
    ImageButton otros;
    SharedPreferences sharedpreferences;
    String[] categorias =   {   "Motor y accesorios",
                                "Tv, audio, foto y vídeo",
                                "Informática y electrónica",
                                "Cine, libros y música",
                                "Deporte y ocio",
                                "Hogar y jardín",
                                "Electrodomésticos",
                                "Moda y accesorios",
                                "Niños y bebés",
                                "Industria y agricultura",
                                "Empleo y servicios",
                                "Otros"
                            };
    int categoria = 0;

    private Integer posVenta; // Posición de la venta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_prod3_3);

        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);

        siguiente = findViewById(R.id.PantallaSiguiente);
        anterior = findViewById(R.id.PantallaAnterior);

        motor = findViewById(R.id.Motor);
        tv = findViewById(R.id.Tv);
        compu = findViewById(R.id.Informatica);
        pelis = findViewById(R.id.Cine);
        deporte = findViewById(R.id.Deporte);
        hogar = findViewById(R.id.Hogar);
        electro = findViewById(R.id.Electrodomesticos);
        ropa = findViewById(R.id.Moda);
        bebe = findViewById(R.id.ninyos);
        agri = findViewById(R.id.Industria);
        empleo = findViewById(R.id.Empleo);
        otros = findViewById(R.id.Otros);

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

        motor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(1);
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(2);
            }
        });
        compu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(3);
            }
        });
        pelis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(4);
            }
        });
        deporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(5);
            }
        });
        hogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(6);
            }
        });
        electro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(7);
            }
        });
        ropa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(8);
            }
        });
        bebe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(9);
            }
        });
        agri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(10);
            }
        });
        empleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(11);
            }
        });
        otros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirCategoria(12);
            }
        });

        // Editar
        posVenta = getIntent().getIntExtra("posVenta", -1);
        try {
            if (posVenta != -1) {
                Ventas productos = new Ventas();

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
                Long fechaLimite = intentAnterior.getLongExtra("fechaLimite",0);
                String precioInicial = intentAnterior.getStringExtra("precioInicial");

                ProgressDialog dialog = ProgressDialog.show(SubirProd3_3.this, "",
                        "Subiendo...", true);


                String url = Common.url;
                if (productos.getEsSubastaVenta(posVenta).equals("0")) {
                    url += "/actualizarVenta?id=";
                } else {
                    url += "/actualizarSubasta?pin=" + precioInicial + "&end=" + fechaLimite + "&id=";
                }
                url += productos.getIdVenta(posVenta) + "&prod=" + producto + "&desc=" + descripcion
                        + "&pre=" + precio;

                JSONArray imagenes = productos.getIdImagenesVenta(posVenta);

                if (imagen1_bm != null) {
                    url += "&arc1=" + BitMapToString(imagen1_bm); // TODO: problema
                }
                if (imagen2_bm != null) {
                    url += "&arc2=" + BitMapToString(imagen2_bm);
                }
                if (imagen3_bm != null) {
                    url += "&arc3=" + BitMapToString(imagen3_bm);
                }
                if (imagen4_bm != null) {
                    url += "&arc4=" + BitMapToString(imagen4_bm);
                }

                RequestQueue queue = Volley.newRequestQueue(this);

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", "Error al recibir la lista de productos");
                            }
                        }
                );
                queue.add(postRequest);

                volverPrincipal(dialog);
            }
        }
        catch (Exception e) {
            pasoAnterior();
        }
    }

    private void elegirCategoria(int cat){
        if(categoria == cat){
            categoria = 0;
            resetearBotones();
            siguiente.setEnabled(false);
        } else{
            categoria = cat;
            bloquearRestoYDestacar(cat);
            siguiente.setEnabled(true);
        }


    }

    private void resetearBotones(){
        motor.setImageResource(R.drawable.motor);
        tv.setImageResource(R.drawable.tv);
        compu.setImageResource(R.drawable.compu);
        pelis.setImageResource(R.drawable.pelis);
        deporte.setImageResource(R.drawable.deporte);
        hogar.setImageResource(R.drawable.hogar);
        electro.setImageResource(R.drawable.electro);
        ropa.setImageResource(R.drawable.ropa);
        bebe.setImageResource(R.drawable.bebe);
        agri.setImageResource(R.drawable.agri);
        empleo.setImageResource(R.drawable.empleo);
        otros.setImageResource(R.drawable.otros);
    }

    private void bloquearRestoYDestacar(int cat){
        motor.setImageResource(R.drawable.motor_dis);
        tv.setImageResource(R.drawable.tv_dis);
        compu.setImageResource(R.drawable.compu_dis);
        pelis.setImageResource(R.drawable.pelis_dis);
        deporte.setImageResource(R.drawable.deporte_dis);
        hogar.setImageResource(R.drawable.hogar_dis);
        electro.setImageResource(R.drawable.electro_dis);
        ropa.setImageResource(R.drawable.ropa_dis);
        bebe.setImageResource(R.drawable.bebe_dis);
        agri.setImageResource(R.drawable.agri_dis);
        empleo.setImageResource(R.drawable.empleo_dis);
        otros.setImageResource(R.drawable.otros_dis);
        switch (cat) {
            case 1:
                motor.setImageResource(R.drawable.motor_sel);
                break;
            case 2:
                tv.setImageResource(R.drawable.tv_sel);
                break;
            case 3:
                compu.setImageResource(R.drawable.compu_sel);
                break;
            case 4:
                 pelis.setImageResource(R.drawable.pelis_sel);
                break;
            case 5:
                deporte.setImageResource(R.drawable.deporte_sel);
                break;
            case 6:
                hogar.setImageResource(R.drawable.hogar_sel);
                break;
            case 7:
                electro.setImageResource(R.drawable.electro_sel);
                break;
            case 8:
                ropa.setImageResource(R.drawable.ropa_sel);
                break;
            case 9:
                bebe.setImageResource(R.drawable.bebe_sel);
                break;
            case 10:
                agri.setImageResource(R.drawable.agri_sel);
                break;
            case 11:
                empleo.setImageResource(R.drawable.empleo_sel);
                break;
            case 12:
                otros.setImageResource(R.drawable.otros_sel);
                break;

        }
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
                precio, esSubasta, fechaLimite, precioInicial, categoria, dialog);


    }

    private void pasoAnterior() {
        setResult(Common.RESULTADO_NOK, new Intent());
        finish();
    }

    @Override
    public void onBackPressed() {
        pasoAnterior();
    }

    private void volverPrincipal(ProgressDialog dialog) {
        dialog.dismiss();
        setResult(Common.RESULTADO_OK, new Intent());
        finish();
    }

    private void gestionPeticion(String estado, String msg, ProgressDialog dialog) {
        switch(estado){
            case "O":
                volverPrincipal(dialog);
                break;
            case "E":
                siguiente.setEnabled(true);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                break;
            default:
                siguiente.setEnabled(true);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void subirProducto(final String producto, final String descripcion, final Bitmap imagen1_bm,
                               final Bitmap imagen2_bm, final Bitmap imagen3_bm, final Bitmap imagen4_bm,
                               final String precio, final Boolean esSubasta, final long fechaLimite,
                               final String precioInicial, final int categ, final ProgressDialog dialog) {

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
                        gestionPeticion(estado, msg, dialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        String msg = "Error desconocido";
                        if (error.getMessage() != null) {
                            msg = error.getMessage();
                        }
                        Log.d("Error.Response", msg);
                        Toast.makeText(SubirProd3_3.this, "Error al subir: " + msg, Toast.LENGTH_LONG).show();
                        volverPrincipal(dialog);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String uName = sharedpreferences.getString(Common.un, "usuario");
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
                params.put("cat", categorias[categ-1]);



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
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
    }
}





