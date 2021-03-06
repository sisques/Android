package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import android.content.SharedPreferences;

import es.unizar.eina.ebrozon.lib.Common;


public class InicioSesion extends AppCompatActivity {

    String url = Common.url + "/logear";

    SharedPreferences sharedpreferences;


    private Button iniciar;
    private Button olvidar;
    private EditText userName;
    private EditText password;
    private Boolean mostrandoPasswd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        userName = findViewById(R.id.userName );
        password = findViewById(R.id.Password_login);
        iniciar = findViewById(R.id.enviarMail );
        olvidar = findViewById(R.id.forgotPassword_login);
        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);

        userName.addTextChangedListener(loginTextWatcher);
        password.addTextChangedListener(loginTextWatcher);
    }

    /**
     * Se encarga de comprobar si hay algo escrito en los campos nombre de usuario y contraseña, para
     * desactivar el boton
     */
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String uname = userName.getText().toString().trim();
            String passwd = password.getText().toString().trim();
            iniciar.setEnabled(!uname.isEmpty() && !passwd.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * En caso de que el servidor responda "O", se añadirán los valores pertinentes a las
     * sharedPreferences para que estos sean persistentes.
     * @param estado respuesta del servidor
     * @param msg mensaje a mostar
     */
    private void gestionLogin (String  estado, String msg){
        if (estado.equals("O")){
            // Utilizando notación Documentacion/Peticiones back end.txt
            String un = userName.getText().toString().trim();
            String cor = ""; //TODO: Obtener perfil del servidor
            String pass = password.getText().toString().trim();
            String tel = "";
            String na = "";
            String lna = "";
            String cp = "";
            String ci = "";
            String pr = "";
            String lat = "41.673379";
            String lon = "-0.829330";
            String im = "";


            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Common.un, un);
            editor.putString(Common.cor, cor);
            editor.putString(Common.tel, tel);
            editor.putString(Common.pass, pass);
            editor.putString(Common.na, na);
            editor.putString(Common.lna, lna);
            editor.putString(Common.cp, cp);
            editor.putString(Common.ci, ci);
            editor.putString(Common.pr, pr);
            editor.putString(Common.lat, lat);
            editor.putString(Common.lon, lon);

            editor.putString(Common.im, im);
            editor.commit();



            startActivity(new Intent(InicioSesion.this, PantallaPrincipal.class));
            finish();
        }
        else if (estado.equals("E")){
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param un nombre de usuario
     * @param pass contraseña del usuario
     * @return devuelve la url a la que se enviara la petición http
     */
    public String parseParams(String un, String pass){
        String aux = url;
        un = un.replace(" ","%20");
        pass = pass.replace(" ", "%20");
        aux = aux+"?un="+un+"&pass="+pass;
        return aux;
    }


    /**
     * Envia la petición de inicio de sesión, donde uName es el nombre de usuario con el que se
     * iniciará la sesión y passwd la contraseña de este usuario. En caso de exito se invoca la función
     * gestionLogin.
     * @param uName
     * @param passwd
     */
    private void doPost(final String uName, final String passwd) {

    RequestQueue queue = Volley.newRequestQueue(this);
    String urlPetition = parseParams(uName, passwd);

    StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
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
                    iniciar.setEnabled(true);
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
                    iniciar.setEnabled(true);
                }
            }
    );
        queue.add(postRequest);
}

    /**
     * Muestra la contraseña en caso de que el usuario pulse el boton indicado
     * @param view
     */
    public void mostrar(View view){
        if (mostrandoPasswd){
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mostrandoPasswd = false;
        }
        else{
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mostrandoPasswd= true;
        }
    }


    /**
     * Envia la petición de inicio de sesión al servidor en caso de que el usuario pulse el boton
     * indicado
     * @param view
     */
    public void iniciarSesion(View view){


        String uname = userName.getText().toString().trim();
        String passwd = password.getText().toString().trim();

        iniciar.setEnabled(false);
        doPost(uname, passwd) ;



    }


    /**
     * Inicia la actividad de reseteo de contraseña en caso de que el usario pulse el boton indicado
     * @param view
     */
    public void aiMamaMeOlvideLaContra(View view){
        startActivity(new Intent(InicioSesion.this, aiMamaMeOlvideLaContra.class));



    }

}
