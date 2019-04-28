package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class perfil_usuario extends AppCompatActivity {

    String url ="https://protected-caverns-60859.herokuapp.com/recuperarUsuario";

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    SharedPreferences sharedpreferences;

    private TextView username;
    private TextView user_fullname;
    private TextView user_email;
    private TextView user_province;
    private TextView user_city;

    private double user_rating;

    private ImageButton editar;

    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        username = findViewById(R.id.profileUsername);
        user_fullname = findViewById(R.id.profileFullName);
        user_email = findViewById(R.id.profileEmail);
        user_province = findViewById(R.id.profileProvince);
        user_city = findViewById(R.id.profileCity);
        editar = findViewById(R.id.profileEditButton);

        star1 = findViewById(R.id.profileStar1);
        star2 = findViewById(R.id.profileStar2);
        star3 = findViewById(R.id.profileStar3);
        star4 = findViewById(R.id.profileStar4);
        star5 = findViewById(R.id.profileStar5);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        recuperarUsuario();
    }

    private void recuperarUsuario() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String currentUser = sharedpreferences.getString(Name, null);
        String urlPetition = url+"?un="+currentUser;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
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
                                user_city.setText("...");
                            }
                            else {
                                user_city.setText(usuario.getString("ciudad"));
                            }
                            user_rating = usuario.getDouble("estrellas");
                            dibujarEstrellas(user_rating);
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
        queue.add(postRequest);
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
}
