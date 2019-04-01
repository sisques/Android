package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class InicioSesion extends AppCompatActivity {

    String url ="https://pruebaapp.free.beeceptor.com";

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

    private void gestionLogin (String  estado, String msg){
        if (estado.equals("O")){
            startActivity(new Intent(InicioSesion.this, PantallaPrincipal.class));
        }
        else if (estado.equals("E")){
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }

    }



    private void doPost(final String uName, final String passwd) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{","").replace("}","").replace("\"","");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado+":","");
                        gestionLogin(estado, msg);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        String response = error.getMessage().replace("{","").replace("}","").replace("\"","");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado+":","");
                        gestionLogin(estado, msg);
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
        String uname = userName.getText().toString();
        String passwd = password.getText().toString();
        if(true){//TODO
             }

        doPost(uname, passwd) ;


    }

}
