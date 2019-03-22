package es.unizar.eina.ebrozon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PreLogin extends AppCompatActivity {

    private Button iniciar;
    private Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);

        iniciar = findViewById(R.id.iniciar);
        registrar = findViewById(R.id.registrar);
    }


    //Este método cambia a la actividad de inicio de sesión.
    public void iniciarSesion(View view){
        Toast.makeText(this, "Inicio de sesión", Toast.LENGTH_LONG).show();

    }
    //Este método cambia a la actividad de registro.
    public void registrarCuenta(View view){
        Toast.makeText(this, "Registro de cuenta", Toast.LENGTH_LONG).show();
    }
}
