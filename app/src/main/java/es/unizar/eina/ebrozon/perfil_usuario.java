package es.unizar.eina.ebrozon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

import es.unizar.eina.ebrozon.lib.AdaptadorOpinion;
import es.unizar.eina.ebrozon.lib.Common;

public class perfil_usuario extends AppCompatActivity {

    // Actividad del perfil del usuario que está usando la aplicación o de otro usuario.

    String urlRecuperarUsuario ="https://protected-caverns-60859.herokuapp.com/recuperarUsuario";
    String urlNumeroVentasUusuario ="https://protected-caverns-60859.herokuapp.com/numeroVentasUsuario";
    String urlNumeroComprasUsuario ="https://protected-caverns-60859.herokuapp.com/numeroComprasUsuario";
    String urlLoadArchivoTemp ="http://protected-caverns-60859.herokuapp.com/loadArchivoTemp";
    String urlListaOpinionesRecibidas ="http://protected-caverns-60859.herokuapp.com/listarOpinionesRecibidas";
    String urlListaOpinionesHechas ="http://protected-caverns-60859.herokuapp.com/listarOpinionesHechas";
    String user_idPic = "";

    SharedPreferences sharedpreferences;
    String currentUser;         // El ususario del perfil de la actividad.
    String appUser;             // El propio usuario que está utilizando la app.

    private TextView username;      // Nombre de usuario.
    private TextView user_fullname; // Nombre completo del usuario.
    private TextView user_email;    // Correo del usuario.
    private TextView user_province; // Provincia del usuario.
    private TextView user_city;     // Ciudad del usuario.
    private TextView ventas;        // Número de ventas hechas por el usuario.
    private TextView compras;       // Número de compras hechas por el usuario.

    private double user_rating;     // Puntuación del usuario.

    private ImageButton editar;     // Botón que lleva a la actividad de editar perfil.
    private Button verValoracionesUsuarios; // Botón que muestra las valoraciones de los usuarios.
    private Button verMisValoraciones;      // Botón que muestras las valoraciones hechas.
    private Button chat;                    // Botón para hablar con el usuario del perfil.
    private Button informar;                // Botón para reportar al usuario del perfil.

    // Estrellas del usuario del perfil.
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    private ImageView foto; // Foto de perfil del usuario.

    // Lista de valoraciones.
    private ListView opinionsList;
    private JSONArray jsonUsersOpinions = new JSONArray();
    private JSONArray jsonMyOpinions = new JSONArray();

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        // Asociar cada elemento de la vista.
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
        chat = findViewById(R.id.profileChat);
        informar = findViewById(R.id.profileReport);

        star1 = findViewById(R.id.profileStar1);
        star2 = findViewById(R.id.profileStar2);
        star3 = findViewById(R.id.profileStar3);
        star4 = findViewById(R.id.profileStar4);
        star5 = findViewById(R.id.profileStar5);

        foto = findViewById(R.id.profilePic);

        // Si se ha especificado un nombre en el intent, carga el perfil de otro usuario.
        Intent intentAnterior = getIntent();
        String name = intentAnterior.getStringExtra("username");
        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        appUser = sharedpreferences.getString(Common.un, null);
        if (!(name == null || appUser.equals(name))) {
            currentUser = name;
            editar.setVisibility(View.GONE);
            TextView titulo = findViewById(R.id.profileTitle);
            titulo.setText("Perfil");
            verMisValoraciones.setText("VALORACIONES HECHAS");
        }
        else {
            currentUser = appUser;
            chat.setEnabled(false);
            informar.setEnabled(false);
            chat.setVisibility(View.GONE);
            informar.setVisibility(View.GONE);
        }
        recuperarUsuario();

        // Carga las opiniones de otros usuarios.
        opinionsList = findViewById(R.id.profileOpinionsList);
        getOpinions(false);
    }

    // Recupera toda la información necesaria del usuario, haciendo 3 peticiones al servidor.
    private void recuperarUsuario() {
        RequestQueue queue = Volley.newRequestQueue(this);
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

    // Dibuja las estrellas según la puntuación del usuario.
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

    // Peitición al servidor para obtener las opiniones de los usuarios o las hechas.
    void getOpinions (final boolean myOpinions) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String urlPetition;
        if (myOpinions) {
            urlPetition = urlListaOpinionesHechas+"?un="+currentUser;
        }
        else {
            urlPetition = urlListaOpinionesRecibidas+"?un="+currentUser;
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                            try {
                                if (myOpinions) {
                                    jsonMyOpinions = new JSONArray(response);
                                    if (jsonMyOpinions.length() != 0) {
                                        opinionsList.setAdapter(new AdaptadorOpinion(perfil_usuario.this, jsonMyOpinions, true));
                                    }
                                }
                                else {
                                    jsonUsersOpinions = new JSONArray(response);
                                    if (jsonUsersOpinions.length() != 0) {
                                        opinionsList.setAdapter(new AdaptadorOpinion(perfil_usuario.this, jsonUsersOpinions, false));
                                    }
                                }
                                verValoracionesUsuarios.setEnabled(true);
                                verMisValoraciones.setEnabled(true);
                            }
                            catch (Exception e) {
                                // ...
                            }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response",error.getMessage());
                    }
                }
        );
        queue.add(postRequest);
    }

    // Llama a la actividad de editar perfil, pasando por intent todos los parámetros del perfil.
    public void editarPerfil(View view) {
        Intent intent = new Intent(perfil_usuario.this, editar_perfil.class);
        intent.putExtra("user_fullname", user_fullname.getText());
        intent.putExtra("user_province", user_province.getText());
        intent.putExtra("user_city", user_city.getText());
        intent.putExtra("user_idPic", user_idPic);
        startActivity(intent);
        finish();
    }

    // Carga las valoraciones de los usuarios en el perfil.
    public void mostrarValoracionesUsuarios(View view) {
        verValoracionesUsuarios.setEnabled(false);
        verMisValoraciones.setEnabled(false);
        verValoracionesUsuarios.setTextColor(Color.parseColor("#225896"));
        verMisValoraciones.setTextColor(Color.parseColor("#2A6EBC"));
        getOpinions(false);
    }

    // Carga las valoraciones hechas por el usuario del perfil.
    public void mostrarMisValoraciones(View view) {
        verValoracionesUsuarios.setEnabled(false);
        verMisValoraciones.setEnabled(false);
        verValoracionesUsuarios.setTextColor(Color.parseColor("#2A6EBC"));
        verMisValoraciones.setTextColor(Color.parseColor("#225896"));
        getOpinions(true);
    }

    // Llama a la actividad de chat con el usuario del perfil.
    public void iniciarChat(View view) {
        chat.setEnabled(false);
        Intent intent = new Intent(perfil_usuario.this, Chat.class);
        intent.putExtra("usuarioComunica", currentUser);
        startActivity(intent);
        chat.setEnabled(true);
        finish();
    }

    // Llama a la actividad de valorar (modo report) al usuario del perfil.
    public void reportar(View view) {
        Intent intent = new Intent(perfil_usuario.this, ValorarUusuario.class);
        intent.putExtra("username", currentUser);
        intent.putExtra("mode","report");
        startActivity(intent);
    }

    // Convierte un string en base64 a una imágen.
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

    // Recupera un string en base64 mediante una petición al servidor.
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
