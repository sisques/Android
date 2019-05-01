package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class SubirProd2_3 extends AppCompatActivity {

    TextView precioInicialProd;
    TextView fechaLimiteSubasta;
    TextView fecha;
    TextView horaLimiteSubasta;
    TextView hora;
    TextView simbolo€;

    EditText precioProducto;
    EditText precioInicial;

    Switch esSubasta;
    Boolean subasta = false;

    Button siguiente;
    Button anterior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_prod2_3);

        //Intent intent = getIntent();
        //String message = intent.getStringExtra("message");

        precioInicialProd = findViewById(R.id.PrecioInicial_text_view);
        fechaLimiteSubasta = findViewById(R.id.FechaLimite_text_view);
        fecha = findViewById(R.id.FechaLimite);
        horaLimiteSubasta = findViewById(R.id.HoraLimite_text_view);
        hora = findViewById(R.id.HoraLimite);
        simbolo€ = findViewById(R.id.Simbolo_euro);

        precioProducto = findViewById(R.id.PrecioProducto);
        precioInicial = findViewById(R.id.PrecioInicial);

        esSubasta = findViewById(R.id.esSubasta);

        siguiente = findViewById(R.id.PantallaSiguiente);
        anterior = findViewById(R.id.PantallaAnterior);

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasoAnterior();
            }
        });

        esSubasta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cambiar(isChecked);
            }
        });




    }

    private void pasoAnterior(){
        finish();
    }

    private void cambiar (Boolean isChecked){
        if (isChecked){
            precioInicialProd.setVisibility(View.VISIBLE);
            precioInicial.setVisibility(View.VISIBLE);
            simbolo€.setVisibility(View.VISIBLE);
            fechaLimiteSubasta.setVisibility(View.VISIBLE);
            fecha.setVisibility(View.VISIBLE);
            horaLimiteSubasta.setVisibility(View.VISIBLE);
            hora.setVisibility(View.VISIBLE);

        }
        else{
            precioInicialProd.setVisibility(View.INVISIBLE);
            precioInicial.setVisibility(View.INVISIBLE);
            simbolo€.setVisibility(View.INVISIBLE);
            fechaLimiteSubasta.setVisibility(View.INVISIBLE);
            fecha.setVisibility(View.INVISIBLE);
            horaLimiteSubasta.setVisibility(View.INVISIBLE);
            hora.setVisibility(View.INVISIBLE);
        }
        subasta = isChecked;
    }
}
