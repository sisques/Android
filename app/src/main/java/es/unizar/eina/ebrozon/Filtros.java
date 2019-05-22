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

import java.util.Arrays;
import java.util.List;

import es.unizar.eina.ebrozon.lib.Common;

public class Filtros extends AppCompatActivity {

    private String provincia; // provincia elegida, "" = todas
    private Spinner spinnerPr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        provincia = getIntent().getStringExtra("ProvinciaFiltros");

        // Botón aplicar
        Button botonAplicar = findViewById(R.id.FiltroBotonAplicar);
        botonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.setData(Uri.parse(provincia));
                //data.putExtra("listarPorCiudad", filtroPorCiudad);
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
                setResult(Common.RESULTADO_CANCELADO, data);
                finish();
            }
        });

        // Listado de provincias
        final List<String> listaPr = Arrays.asList(getResources().getStringArray(R.array.ListaProvinciasFiltros));

        spinnerPr = findViewById(R.id.FiltroListaProvincias);
        spinnerPr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                provincia = spinnerPr.getSelectedItem().toString();
                if (provincia.equals(listaPr.get(0))) {
                    provincia = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        int i = listaPr.indexOf(provincia);
        if (!provincia.equals("") && i != -1) {
            spinnerPr.setSelection(i);
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        //data.setData(Uri.parse(filtroCi));
        setResult(Common.RESULTADO_NOK, data);
        finish();
    }
}
