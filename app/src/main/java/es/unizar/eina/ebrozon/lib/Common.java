package es.unizar.eina.ebrozon.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public final class Common {
    public static final String url ="https://protected-caverns-60859.herokuapp.com";

    public static final String MyPreferences = "MyPrefs";
    public static final String un = "un_key";
    public static final String cor = "cor_key";
    public static final String pass= "pass_key";
    public static final String tel = "tel_key";
    public static final String na = "na_key";
    public static final String lna = "lna_key";
    public static final String cp = "cp_key";
    public static final String ci = "ci_key";
    public static final String pr = "pr_key";
    public static final String lat = "lat_key";
    public static final String lon = "lon_key";
    public static final String im = "im_key";



    public static final int RESULTADO_NOK = -2;
    public static final int RESULTADO_OK = -1;
    public static final int RESULTADO_CANCELADO = 0;

    // Funciones de uso general
    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static void establecerFotoServidor(final Context context, final String id, final ImageView imagen) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String urlPetition = Common.url + "/loadArchivoTemp?id=" + id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace(" ","+");
                        Bitmap result = StringToBitMap(response);
                        if (result != null) {
                            imagen.setImageBitmap(result);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error con imagen de perfil");
                    }
                }
        );
        queue.add(postRequest);
    }

    public static void establecerFotoUsuarioServidor(Context context, String un, final ImageView imagen) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String urlPetition = Common.url + "/loadArchivoUsuario?un=" + un;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace(" ","+");
                        Bitmap result = StringToBitMap(response);
                        if (result != null) {
                            imagen.setImageBitmap(result);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error con imagen de perfil");
                    }
                }
        );
        queue.add(postRequest);
    }
}
