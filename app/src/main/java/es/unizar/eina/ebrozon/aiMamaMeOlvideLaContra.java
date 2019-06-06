package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import es.unizar.eina.ebrozon.lib.Common;

/**
 * Clase que implementa la actividad para reestablecer la contraseña de una cuenta.
 */
public class aiMamaMeOlvideLaContra extends AppCompatActivity {

    private Button enviar;
    private EditText uname;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ai_mama_me_olvide_la_contra );
        enviar = findViewById(R.id.enviarMail);
        uname = findViewById(R.id.nombreUsuario );

        uname.addTextChangedListener(loginTextWatcher);
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            userName = uname.getText().toString().trim();
            enviar.setEnabled(!userName.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * Función que realiza lo petición correspondiente para que el servidor reinicie la contraseña
     * del usuario especificado en el cuadro de texto. Tras enviar ese mail, se notifica al usuario
     * que debe mirar el correo electrónico proporcionado durante el registro para iniciar sesión
     * @param view
     */
    public void enviarMail(View view){
        RequestQueue queue = Volley.newRequestQueue(this);
        userName = uname.getText().toString().trim();
        String urlPetition = Common.url + "/recuperarContrasena?un=" + userName;

        StringRequest postRequest = new StringRequest( Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{","").replace("}",
                                "").replace("\"","");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado+":","");
                        gestionCntr(estado, msg);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        String response = error.getMessage().replace("{","")
                                .replace("}","").replace("\"","");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado+":","");
                        gestionCntr(estado, msg);
                    }
                }
        );
        queue.add(postRequest);
    }

    /**
     *
     * @param estado Respuesta del servidor.
     * @param msg    Mensaje que se va a mostrar por pantalla.
     */
    private void gestionCntr (String  estado, String msg){
        if (estado.equals("O")){
            Toast.makeText(getApplicationContext(),"Correo enviado.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(aiMamaMeOlvideLaContra.this, InicioSesion.class));
            finish();
        }
        else if (estado.equals("E")){
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
    }
}
