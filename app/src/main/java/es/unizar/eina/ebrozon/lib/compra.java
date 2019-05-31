package es.unizar.eina.ebrozon.lib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public  class compra {



    private static void gestionPeticion(String estado, String msg, Context context) {
        switch(estado){
            case "O":
                ((Activity)(context)).finish();
                break;
            case "E":
                Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Envia una petición de oferta al servidor
     * @param idProducto identificador del producto sobre el que se realiza la oferta
     * @param context
     * @param precio precio de la oferta
     * @param sharedpreferences
     */
    public static void  ofertar(final String idProducto, final Context context,final String precio,  final SharedPreferences sharedpreferences) {

        RequestQueue queue = Volley.newRequestQueue(context);


        String urlPetition = Common.url + "/hacerOferta";

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        gestionPeticion(estado, msg, context);
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
                        Toast.makeText(context, "Error al subir: " + msg, Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String uName = sharedpreferences.getString(Common.un, "usuario");


                params.put("un", uName);
                params.put("nv",  idProducto);

                params.put("can", precio);




                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }


        };
        queue.add(postRequest);
    }


    public static void  pujar(final String idProducto, final Context context,final String precio,  final SharedPreferences sharedpreferences) {

        RequestQueue queue = Volley.newRequestQueue(context);


        String urlPetition = Common.url + "/realizarPuja";

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        gestionPeticion(estado, msg, context);
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
                        Toast.makeText(context, "Error al subir: " + msg, Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                String uName = sharedpreferences.getString(Common.un, "usuario");


                params.put("un", uName);
                params.put("id",  idProducto);

                params.put("ct", precio);




                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }


        };
        queue.add(postRequest);
    }


}
