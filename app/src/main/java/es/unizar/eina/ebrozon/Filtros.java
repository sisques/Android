package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import es.unizar.eina.ebrozon.lib.Common;

public class Filtros extends AppCompatActivity {

    private Spinner spinnerCi; // spinner ciudad
    private String filtroCi; // filtro ciudad
    private Boolean filtroPorCiudad; // Mostrar por ciudad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        filtroPorCiudad = getIntent().getBooleanExtra("listarPorCiudad", false);

        // Switch mostrar todos los productos
        Switch mostrarTodos = findViewById(R.id.FiltroTodos);
        mostrarTodos.setChecked(!filtroPorCiudad);
        mostrarTodos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filtroPorCiudad = !isChecked;
            }
        });

        // Botón aplicar
        Button botonAplicar = findViewById(R.id.FiltroBotonAplicar);
        botonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.setData(Uri.parse(filtroCi));
                data.putExtra("listarPorCiudad", filtroPorCiudad);
                setResult(Common.RESULTADO_OK, data);
                finish();
            }
        });

        // Botón restablecer
        Button botonRestablecer = findViewById(R.id.FiltroBotonRestablecer);
        botonRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.setData(Uri.parse(filtroCi));
                setResult(Common.RESULTADO_CANCELADO, data);
                finish();
            }
        });

        // Listado de ciudades
        spinnerCi = findViewById(R.id.FiltroListaProvincias);
        spinnerCi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filtroCi = spinnerCi.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.setData(Uri.parse(filtroCi));
        setResult(Common.RESULTADO_NOK, data);
        finish();
    }
}
