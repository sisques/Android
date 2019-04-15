package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import es.unizar.eina.ebrozon.credentials;

public class PreLogin extends AppCompatActivity {

    private Button iniciar;
    private Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);
        Boolean logeado =   credentials.uName != null &&
                            !credentials.uName.isEmpty() &&
                            credentials.passwd != null &&
                            !credentials.passwd.isEmpty();
        if( logeado ) {
            startActivity(new Intent(PreLogin.this, PantallaPrincipal.class));
        }

        iniciar = findViewById(R.id.LogIn);
        registrar = findViewById(R.id.registrar);

    }


    //Este método cambia a la actividad de inicio de sesión.
    public void iniciarSesion(View view){
        startActivity(new Intent(PreLogin.this, InicioSesion.class));
        //Toast.makeText(this, "Inicio de sesión", Toast.LENGTH_LONG).show();
    }

    //Este método cambia a la actividad de registro.
    public void registrarCuenta(View view){
        startActivity(new Intent(PreLogin.this, Registro.class));
        //Toast.makeText(this, "Registro de cuenta", Toast.LENGTH_LONG).show();
    }
}
