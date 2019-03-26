package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class InicioSesion extends AppCompatActivity {

    private Button iniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        iniciar = findViewById(R.id.iniciarSesion);
    }

    //Este método cambia a la actividad de pantalla principal.
    //TODO: Configurar el inicio de sesión antes de saltar a la pantalla principal
    public void iniciarSesion(View view){
        startActivity(new Intent(InicioSesion.this, PantallaPrincipal.class));
    }

}
