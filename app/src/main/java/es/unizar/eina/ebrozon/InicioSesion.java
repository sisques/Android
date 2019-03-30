package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class InicioSesion extends AppCompatActivity {


    String url = "";
    RequestQueue queue = Volley.newRequestQueue(this);
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

    private Boolean validate (String UserName, String password){

    }

    //Este método cambia a la actividad de pantalla principal.
    public void iniciarSesion(View view){
        if ( validate(userName.getText().toString(), password.getText().toString()) ) {
            startActivity(new Intent(InicioSesion.this, PantallaPrincipal.class));
        }
        else{

        }
    }

}
