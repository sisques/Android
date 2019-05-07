package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class perfil_usuario extends AppCompatActivity {

    String urlRecuperarUsuario ="https://protected-caverns-60859.herokuapp.com/recuperarUsuario";
    String urlNumeroVentasUusuario ="https://protected-caverns-60859.herokuapp.com/numeroVentasUsuario";
    String urlNumeroComprasUsuario ="https://protected-caverns-60859.herokuapp.com/numeroComprasUsuario";
    String urlLoadArchivoTemp ="http://protected-caverns-60859.herokuapp.com/loadArchivoTemp";
    String user_idPic = "";

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    SharedPreferences sharedpreferences;

    private TextView username;
    private TextView user_fullname;
    private TextView user_email;
    private TextView user_province;
    private TextView user_city;
    private TextView ventas;
    private TextView compras;

    private double user_rating;

    private ImageButton editar;
    private Button verValoracionesUsuarios;
    private Button verMisValoraciones;

    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    private ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        username = findViewById(R.id.profileUsername);
        user_fullname = findViewById(R.id.profileFullName);
        user_email = findViewById(R.id.profileEmail);
        user_province = findViewById(R.id.profileProvince);
        user_city = findViewById(R.id.profileCity);
        ventas = findViewById(R.id.profileVentas);
        compras = findViewById(R.id.profileCompras);

        editar = findViewById(R.id.profileEditButton);
        editar.setEnabled(false);
        verValoracionesUsuarios = findViewById(R.id.profileOpinionButton1);
        verMisValoraciones = findViewById(R.id.profileOpinionButton2);
        verValoracionesUsuarios.setPaintFlags(verValoracionesUsuarios.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        verMisValoraciones.setPaintFlags(verMisValoraciones.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        star1 = findViewById(R.id.profileStar1);
        star2 = findViewById(R.id.profileStar2);
        star3 = findViewById(R.id.profileStar3);
        star4 = findViewById(R.id.profileStar4);
        star5 = findViewById(R.id.profileStar5);

        foto = findViewById(R.id.profilePic);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        recuperarUsuario();
        recuperarValoracionesUsuarios();
    }

    private void recuperarUsuario() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String currentUser = sharedpreferences.getString(Name, null);
        String urlPetition1 = urlRecuperarUsuario+"?un="+currentUser;
        String urlPetition2 = urlNumeroComprasUsuario+"?un="+currentUser;
        String urlPetition3 = urlNumeroVentasUusuario+"?un="+currentUser;

        StringRequest postRequest1 = new StringRequest(Request.Method.POST, urlPetition1,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONObject usuario = new JSONObject(response);
                            username.setText(usuario.getString("nombreusuario"));
                            user_fullname.setText(usuario.getString("nombre")+" "+usuario.getString("apellidos"));
                            user_email.setText(usuario.getString("correo"));
                            user_province.setText(usuario.getString("provincia"));
                            if (usuario.getString("ciudad").isEmpty()) {
                                user_city.setText("Sin especificar");
                            }
                            else {
                                user_city.setText(usuario.getString("ciudad"));
                            }
                            user_rating = usuario.getDouble("estrellas");
                            dibujarEstrellas(user_rating);
                            user_idPic = usuario.getString("archivo");
                            bajarFotoUsuario(user_idPic);
                            editar.setEnabled(true);
                        }
                        catch(Exception e) {
                            // ...
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );
        queue.add(postRequest1);

        StringRequest postRequest2 = new StringRequest(Request.Method.POST, urlPetition2,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        compras.setText(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );
        queue.add(postRequest2);

        StringRequest postRequest3 = new StringRequest(Request.Method.POST, urlPetition3,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        ventas.setText(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );
        queue.add(postRequest3);


    }

    private void dibujarEstrellas(double rating) {
        if (rating == 0) {}
        else if (rating >=1 && rating < 1.5) {
            // dibujar 1 estrella
            star1.setImageResource(android.R.drawable.star_big_on);
        }
        else if (rating >=1.5 && rating < 2) {
            // dibujar 1 estrella y media
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_on);
        }
        else if (rating >=2 && rating < 2.5) {
            // dibujar 2 estrellas
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_big_on);
        }
        else if (rating >=2.5 && rating < 3) {
            // dibujar 2 estrellas y media
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_big_on);
            star3.setImageResource(android.R.drawable.star_on);
        }
        else if (rating >=3 && rating < 3.5) {
            // dibujar 3 estrellas
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_big_on);
            star3.setImageResource(android.R.drawable.star_big_on);
        }
        else if (rating >=3.5 && rating < 4) {
            // dibujar 3 estrellas y media
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_big_on);
            star3.setImageResource(android.R.drawable.star_big_on);
            star4.setImageResource(android.R.drawable.star_on);
        }
        else if (rating >=4 && rating < 4.5) {
            // dibujar 4 estrellas
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_big_on);
            star3.setImageResource(android.R.drawable.star_big_on);
            star4.setImageResource(android.R.drawable.star_big_on);
        }
        else if (rating >=4.5 && rating < 5) {
            // dibujar 4 estrellas y media
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_big_on);
            star3.setImageResource(android.R.drawable.star_big_on);
            star4.setImageResource(android.R.drawable.star_big_on);
            star5.setImageResource(android.R.drawable.star_on);
        }
        else if (rating == 5) {
            // dibujar 5 estrellas
            star1.setImageResource(android.R.drawable.star_big_on);
            star2.setImageResource(android.R.drawable.star_big_on);
            star3.setImageResource(android.R.drawable.star_big_on);
            star4.setImageResource(android.R.drawable.star_big_on);
            star5.setImageResource(android.R.drawable.star_big_on);
        }
    }

    private void recuperarValoracionesUsuarios() {
        // Falta
    }

    private void recuperarMisValoraciones() {
        // Falta
    }

    public void editarPerfil(View view) {
        Intent intent = new Intent(perfil_usuario.this, editar_perfil.class);
        intent.putExtra("user_fullname", user_fullname.getText());
        intent.putExtra("user_province", user_province.getText());
        intent.putExtra("user_city", user_city.getText());
        intent.putExtra("user_idPic", user_idPic);
        startActivity(intent);
    }

    public void mostrarValoracionesUsuarios(View view) {
        verValoracionesUsuarios.setEnabled(false);
        recuperarValoracionesUsuarios();
        verValoracionesUsuarios.setEnabled(true);
    }

    public void mostrarMisValoraciones(View view) {
        verMisValoraciones.setEnabled(false);
        recuperarMisValoraciones();
        verMisValoraciones.setEnabled(true);
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

    private void bajarFotoUsuario(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String urlPetition = urlLoadArchivoTemp + "?id=" + id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace(" ", "+");
                        int longitud = response.length();
                        Bitmap result = StringToBitMap(response);
                        foto.setImageBitmap(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(perfil_usuario.this, "Error al descargar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(postRequest);
    }
}
