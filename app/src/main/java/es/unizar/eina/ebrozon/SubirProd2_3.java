package es.unizar.eina.ebrozon;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    Boolean fechaSet = false;
    Boolean horaSet = false;

    Button siguiente;
    Button anterior;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_prod2_3);


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

        siguiente.setEnabled(false);
        anterior.setEnabled(true);

        Calendar cal = Calendar.getInstance();

        fecha.setText( "INTRODUCE UNA FECHA PULSANDO AQUI" );

        hora.setText( "INTRODUCE UNA HORA PULSANDO AQUI" );

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasoAnterior();
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pasoSiguiente();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        esSubasta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cambiar(isChecked);
            }
        });

        precioProducto.addTextChangedListener(uploadProductTextWatcher);
        precioInicial.addTextChangedListener(uploadProductTextWatcher);


        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SubirProd2_3.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String dia = String.valueOf(day);
                String mes = String.valueOf(month);
                String anyo = String.valueOf(year);
                if( dia.length() == 1 ){
                    dia = '0'+dia;
                }
                if( mes.length() == 1 ){
                    mes = '0'+mes;
                }
                while( anyo.length() != 4){
                    anyo = '0'+anyo;
                }
                String date = dia + "/" + mes + "/" + anyo;
                fechaSet = true;
                fecha.setText(date);
                String p = precioProducto.getText().toString().trim();
                String i = precioInicial.getText().toString().trim();
                siguiente.setEnabled( !p.isEmpty() && !i.isEmpty() && fechaSet && horaSet);
            }
        };


        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SubirProd2_3.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String h = String.valueOf(hourOfDay);
                        String m = String.valueOf(minute);
                        if( h.length() == 1 ){
                            h = '0'+h;
                        }
                        if( m.length() == 1 ){
                            m = '0'+m;
                        }

                        horaSet = true;

                        hora.setText(h+":"+m);
                        String p = precioProducto.getText().toString().trim();
                        String i = precioInicial.getText().toString().trim();
                        siguiente.setEnabled( !p.isEmpty() && !i.isEmpty() && fechaSet && horaSet);
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });





    }

    private TextWatcher uploadProductTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(subasta){
                String p = precioProducto.getText().toString().trim();
                String i = precioInicial.getText().toString().trim();
                siguiente.setEnabled( !p.isEmpty() && !i.isEmpty() && fechaSet && horaSet);
            }
            else{
                String p = precioProducto.getText().toString().trim();
                siguiente.setEnabled( !p.isEmpty() );
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void pasoSiguiente() throws  ParseException{

        boolean fechaOk = true;

        String venta = precioProducto.getText().toString().trim();
        String inicial = null;
        Date fechaLimite = null;

        if (subasta){
            inicial = precioInicial.getText().toString().trim();
            String fechaCompleta = fecha.getText().toString().trim() + " " + hora.getText().toString().trim();
            SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            fechaLimite = formatter1.parse(fechaCompleta);
            if ( fechaLimite.before(new Date())){
                Toast.makeText(getApplicationContext(),"La fecha limite debe ser posterior al dia de hoy", Toast.LENGTH_LONG).show();
                fechaOk = false;
            }
        }
        if(fechaOk) {



            Intent intentAnterior = getIntent();
            String producto = intentAnterior.getStringExtra("nombreProducto");
            String descripcion = intentAnterior.getStringExtra("descripcionProducto");
            int sync  =  intentAnterior.getIntExtra("bigdata:synccode",-1);


            Intent intent = new Intent(SubirProd2_3.this, SubirProd3_3.class);
            intent.putExtra("nombreProducto", producto);
            intent.putExtra("descripcionProducto", descripcion);
            intent.putExtra("bigdata:synccode", sync);
            intent.putExtra("precioProducto", venta);
            intent.putExtra("esSubasta", subasta);
            intent.putExtra("precioInicial", inicial);
            long f = -1;
            if (fechaLimite != null){ f = fechaLimite.getTime();}

            intent.putExtra("fechaLimite", f);

            startActivity(intent);
        }

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


            String p = precioProducto.getText().toString().trim();
            String i = precioInicial.getText().toString().trim();
            siguiente.setEnabled( !p.isEmpty() && !i.isEmpty() && fechaSet && horaSet);


        }
        else{
            precioInicialProd.setVisibility(View.INVISIBLE);
            precioInicial.setVisibility(View.INVISIBLE);
            simbolo€.setVisibility(View.INVISIBLE);
            fechaLimiteSubasta.setVisibility(View.INVISIBLE);
            fecha.setVisibility(View.INVISIBLE);
            horaLimiteSubasta.setVisibility(View.INVISIBLE);
            hora.setVisibility(View.INVISIBLE);

            String p = precioProducto.getText().toString().trim();
            siguiente.setEnabled( !p.isEmpty() );
        }
        subasta = isChecked;


    }


}
