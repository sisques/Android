package es.unizar.eina.ebrozon;
/*
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.unizar.eina.ebrozon.lib.Common;


public class SubirSubasta extends AppCompatActivity {

    String url = Common.url + "/publicarSubasta";
    private Button subirProducto;
    private EditText nombreProducto;
    private EditText descripcionProducto;
    private EditText precioInicialProducto;
    private EditText precioVentaProducto;
    private TextView fechaLimite;
    private TextView horaLimite;

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Password= "passwordKey";
    SharedPreferences sharedpreferences;

    private Boolean prodNameCheckLength = false;
    private Boolean prodDescCheckLength = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_subasta);


        nombreProducto = findViewById(R.id.NombreProducto);
        descripcionProducto = findViewById(R.id.Descripcion);
        precioInicialProducto = findViewById(R.id.precioInicial);
        precioVentaProducto = findViewById(R.id.precioVenta);
        fechaLimite = findViewById(R.id.fechaFinSubasta);
        horaLimite = findViewById(R.id.horaFinSubasta);
        subirProducto = findViewById(R.id.SubirProducto);



        horaLimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SubirSubasta.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String hora = String.valueOf(hourOfDay);
                        String mins = String.valueOf(minute);
                        if( hora.length() == 1 ){
                            hora = '0'+hora;
                        }
                        if( mins.length() == 1 ){
                            mins = '0'+mins;
                        }


                        horaLimite.setText(hora+":"+mins);
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });






        fechaLimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SubirSubasta.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
                fechaLimite.setText(date);
            }
        };





        subirProducto.setEnabled(false);
        nombreProducto.addTextChangedListener(uploadProductTextWatcher);
        descripcionProducto.addTextChangedListener(uploadProductTextWatcher);
        precioInicialProducto.addTextChangedListener(uploadProductTextWatcher);
        precioVentaProducto.addTextChangedListener(uploadProductTextWatcher);
        fechaLimite.addTextChangedListener(uploadProductTextWatcher);
        horaLimite.addTextChangedListener(uploadProductTextWatcher);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    private TextWatcher uploadProductTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String prod = nombreProducto.getText().toString().trim();
            String desc = descripcionProducto.getText().toString().trim();
            String precioInicial = precioInicialProducto.getText().toString().trim();
            String precioVenta = precioVentaProducto.getText().toString().trim();
            String fecha = fechaLimite.getText().toString().trim();
            String hora = horaLimite.getText().toString().trim();
            subirProducto.setEnabled(
                                    !prod.isEmpty() &&
                                    !desc.isEmpty() &&
                                    !precioInicial.isEmpty() &&
                                    !precioVenta.isEmpty() &&
                                    !fecha.isEmpty() &&
                                    !hora.isEmpty()
                                    );
        }

        @Override
        public void afterTextChanged(Editable s) {

            String prod = nombreProducto.getText().toString().trim();
            String desc = descripcionProducto.getText().toString().trim();

            prodNameCheckLength = (prod.length() >= 3 && prod.length() <= 100);
            prodDescCheckLength = (desc.length() >= 10);

        }
    };



    private void gestionSubidaProducto (String  estado, String msg){
        if (estado.equals("O")){
            startActivity(new Intent(SubirSubasta.this, PantallaPrincipal.class));
        }
        else if (estado.equals("E")){
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
    }


    public String parseParams(String prodName, String prodDesc, String prodInitialPrice, String prodSellPrice, String prodLimitDate, String prodLimitHour){
        String aux = url;

        //Urlificar los parametros
        prodName = Uri.encode(prodName);
        prodDesc =  Uri.encode(prodDesc);
        prodInitialPrice =  Uri.encode(prodInitialPrice);
        prodSellPrice = Uri.encode(prodSellPrice);

        String fecha =  prodLimitDate.substring(6,10)   +
                        prodLimitDate.substring(3, 5)   +
                        prodLimitDate.substring(0, 2)   +
                        prodLimitHour.substring(0, 2)   +
                        prodLimitHour.substring(3, 5)   ;

        fecha =  Uri.encode(fecha);

        String uName = sharedpreferences.getString(Common.un, null);
        aux = aux+"?un="+uName+"&prod="+prodName+"&desc="+prodDesc+"&pre="+prodSellPrice+"&pin="+prodInitialPrice+
        "&end="+fecha;
        Log.d("URL", aux);
        return aux;
    }

        private void doPost(final String prodName, final String prodDesc, final String prodInitialPrice,
                            final String prodSellPrice, final String prodLimitDate, final String prodLimitHour) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String urlPetition = parseParams(prodName, prodDesc, prodInitialPrice, prodSellPrice, prodLimitDate, prodLimitHour);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{","").replace("}","").replace("\"","");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado+":","");
                        gestionSubidaProducto(estado, msg);
                        subirProducto.setEnabled(true);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        String response = error.getMessage().replace("{","").replace("}","").replace("\"","");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado+":","");
                        gestionSubidaProducto(estado, msg);
                        subirProducto.setEnabled(true);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void subirProducto(View view){

        if(!prodNameCheckLength){
            Toast.makeText(getApplicationContext(),"El nombre del producto debe tener entre 3 y 100 caracteres", Toast.LENGTH_LONG).show();
        }
        else if(!prodDescCheckLength){
            Toast.makeText(getApplicationContext(),"La descripción del producto debe tener como mínimo 10 caracteres", Toast.LENGTH_LONG).show();
        }
        else {
            String prod = nombreProducto.getText().toString().trim();
            String desc = descripcionProducto.getText().toString().trim();
            String precioInicial = precioInicialProducto.getText().toString().trim();
            String precioVenta = precioVentaProducto.getText().toString().trim();

            String fecha = fechaLimite.getText().toString().trim();
            String hora = horaLimite.getText().toString().trim();
            //ARREGLAR PETICION

            if(!checkDate(fecha, hora)){
               Toast.makeText(getApplicationContext(),"La fecha limite no puede ser anterior a la fecha actual", Toast.LENGTH_LONG).show();
            }

            subirProducto.setEnabled(false);
            doPost(prod, desc, precioInicial, precioVenta, fecha, hora);
        }


    }


    private boolean checkDate(String fecha, String hora) {

        int dia_input = Integer.parseInt(  fecha.substring(0,2) );
        int mes_input = Integer.parseInt(  fecha.substring(3,5) );
        int anyo_input = Integer.parseInt(  fecha.substring(6,10) );
        int hora_input = Integer.parseInt(  hora.substring(0, 2) );
        int min_input = Integer.parseInt(  hora.substring(3, 5) );

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        String dateString = format.format( new Date()   );
        int dia_hoy = Integer.parseInt(  dateString.substring(0,2) );
        int mes_hoy = Integer.parseInt(  dateString.substring(3,5) );
        int anyo_hoy = Integer.parseInt(  dateString.substring(6,10) );

        format = new SimpleDateFormat("HH:mm");

        dateString = format.format( new Date()   );
        int hora_hoy = Integer.parseInt(  dateString.substring(0, 2) );
        int min_hoy = Integer.parseInt(  dateString.substring(3, 5) );

        if (anyo_input >= anyo_hoy  && mes_input >= mes_hoy && dia_input >= dia_hoy && hora_input >= hora_hoy && min_input >= min_hoy){
            return true;
        }


        return  false;
    }


}
*/