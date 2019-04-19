package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import es.unizar.eina.ebrozon.lib.Common;

public class PreLogin extends AppCompatActivity {

    private Button iniciar;
    private Button registrar;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);

        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);

        String uName = sharedpreferences.getString(Common.un, null);
        String passwd = sharedpreferences.getString(Common.pass, null);

        if( uName != null && passwd != null && !uName.isEmpty() &&  !passwd.isEmpty() ) {
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
