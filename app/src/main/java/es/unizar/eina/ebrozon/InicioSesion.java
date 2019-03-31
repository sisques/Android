package es.unizar.eina.ebrozon;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class InicioSesion extends AppCompatActivity {

    String url = "http://httpbin.org/post";
    //TUTORIAL https://www.itsalif.info/content/android-volley-tutorial-http-get-post-put
    private Button iniciar;
    private Button olvidar;
    private EditText userName;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);


        userName = findViewById(R.id.UserName_login);
        password = findViewById(R.id.Password_login);
        iniciar = findViewById(R.id.LogIn);
        olvidar = findViewById(R.id.forgotPassword_login);
    }

    private void validate (String userName, String password){
        doPost(userName, password);
    }
    private void loginCorrecto(){ startActivity(new Intent(InicioSesion.this, PantallaPrincipal.class)); }
    private void loginIncorrecto(){}


    private void doPost(final String uName, final String passwd) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        loginCorrecto();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        loginIncorrecto();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String> params = new HashMap<String, String>();
                params.put("un", uName );
                params.put("pass", passwd );

                return params;
            }
        };
        queue.add(postRequest);
    }



    //Este método cambia a la actividad de pantalla principal.
    public void iniciarSesion(View view){
        validate(userName.getText().toString(), password.getText().toString()) ;


    }

}
