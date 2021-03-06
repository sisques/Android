package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import es.unizar.eina.ebrozon.lib.Common;

public class Filtros extends AppCompatActivity {

    private String provincia; // Provincia utilizada; "" = todas las provincias
    private Integer orden; // Tipo de ordenación
    private String categoria; // Categoría utilizada; "" = todas las categorías
    private Integer tipoVenta; // Tipo de venta; -1 = todos los tipos
    private Double precioMinimo; // Precio mínimo; -1 = sin precio mínimo
    private Double precioMaximo; // Precio máximo; -1 = sin precio máximo
    private Double distMaxima; // Distancia máxima: -1.0 = sin distancia máxima

    private Spinner spinnerProvincia;
    private Spinner spinnerOrden;
    private Spinner spinnerCategoria;
    private Spinner spinnerTipo;
    private EditText textPrecioMinimo;
    private EditText textPrecioMaximo;
    private EditText textDistMaxima;
    private LinearLayout layoutDistancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        provincia = getIntent().getStringExtra("ProvinciaFiltros");
        orden = getIntent().getIntExtra("OrdenFiltros", 0);
        categoria = getIntent().getStringExtra("CategoriaFiltros");
        tipoVenta = getIntent().getIntExtra("TipoVentaFiltros", -1);
        precioMinimo = getIntent().getDoubleExtra("PrecioMinimoFiltros", -1.0);
        precioMaximo = getIntent().getDoubleExtra("PrecioMaximoFiltros", -1.0);
        distMaxima = getIntent().getDoubleExtra("DistMaximaFiltros", -1.0);

        // Botón aplicar
        Button botonAplicar = findViewById(R.id.FiltroBotonAplicar);
        botonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();

                if (textPrecioMinimo != null && !textPrecioMinimo.getText().toString().isEmpty()) {
                    precioMinimo = Double.parseDouble(textPrecioMinimo.getText().toString());
                }
                else {
                    precioMinimo = -1.0;
                }

                if (textPrecioMaximo != null && !textPrecioMaximo.getText().toString().isEmpty()) {
                    precioMaximo = Double.parseDouble(textPrecioMaximo.getText().toString());
                }
                else {
                    precioMaximo = -1.0;
                }

                if (textDistMaxima != null && !textDistMaxima.getText().toString().isEmpty()) {
                    distMaxima = Double.parseDouble(textDistMaxima.getText().toString());
                }
                else {
                    distMaxima = -1.0;
                }

                data.putExtra("ProvinciaFiltros", provincia);
                data.putExtra("OrdenFiltros", orden);
                data.putExtra("CategoriaFiltros", categoria);
                data.putExtra("TipoVentaFiltros", tipoVenta);
                data.putExtra("PrecioMinimoFiltros", precioMinimo);
                data.putExtra("PrecioMaximoFiltros", precioMaximo);
                data.putExtra("DistMaximaFiltros", distMaxima);

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
        final List<String> listaProvincia = Arrays.asList(getResources().getStringArray(R.array.ListaProvinciasFiltros));
        spinnerProvincia = findViewById(R.id.FiltroListaProvincias);
        spinnerProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                provincia = spinnerProvincia.getSelectedItem().toString();
                if (provincia.equals(listaProvincia.get(0))) {
                    provincia = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        if (provincia != null) {
            int i = listaProvincia.indexOf(provincia);
            if (!provincia.equals("") && i != -1) {
                spinnerProvincia.setSelection(i);
            }
        }

        // Listado de orden
        spinnerOrden = findViewById(R.id.FiltroListaOrdenacion);
        spinnerOrden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                orden = position;
                if (orden == 7) {
                    layoutDistancia.setVisibility(View.VISIBLE);
                }
                else {
                    layoutDistancia.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        if (orden >= 0) {
            spinnerOrden.setSelection(orden);
        }

        // Listado de categoria
        final List<String> listaCategoria = Arrays.asList(getResources().getStringArray(R.array.ListaCategoriasFiltros));
        spinnerCategoria = findViewById(R.id.FiltroListaCategoria);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                categoria = spinnerCategoria.getSelectedItem().toString();
                if (categoria.equals(listaCategoria.get(0))) {
                    categoria = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        if (categoria != null) {
            int i = listaCategoria.indexOf(categoria);
            if (!categoria.equals("") && i != -1) {
                spinnerCategoria.setSelection(i);
            }
        }

        // Listado de tipo
        spinnerTipo = findViewById(R.id.FiltroListaTipo);
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tipoVenta = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        if (tipoVenta >= -1) {
            spinnerTipo.setSelection(tipoVenta + 1);
        }

        // Precio mínimo
        textPrecioMinimo = findViewById(R.id.FiltroPrecioMinimo);
        if (precioMinimo >= 0.0) {
            textPrecioMinimo.setText(precioMinimo.toString());
        }

        // Precio máximo
        textPrecioMaximo = findViewById(R.id.FiltroPrecioMaximo);
        if (precioMaximo >= 0.0) {
            textPrecioMaximo.setText(precioMaximo.toString());
        }

        // Distancia máxima
        textDistMaxima = findViewById(R.id.FiltroDistanciaMaxima);
        if (distMaxima >= 0.0) {
            textDistMaxima.setText(distMaxima.toString());
        }

        // Layout distancia
        layoutDistancia = findViewById(R.id.FiltroDistanciaLayout);
        layoutDistancia.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(Common.RESULTADO_NOK, data);
        finish();
    }
}
