package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import es.unizar.eina.ebrozon.lib.Common;


//decidir entre subasta y compra normal
// etiquetas

public class SubirProducto extends AppCompatActivity {

    String url = Common.url + "/publicarVenta";
    private Button subirProducto;
    private EditText nombreProducto;
    private EditText descripcionProducto;
    private EditText precioProducto;

    SharedPreferences sharedpreferences;

    private Boolean prodNameCheckLength = false;
    private Boolean prodDescCheckLength = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_producto);


        nombreProducto = findViewById(R.id.NombreProducto);

        descripcionProducto = findViewById(R.id.ProductoResumenDescripcion);
        precioProducto = findViewById(R.id.precioVenta);

        subirProducto = findViewById(R.id.SubirProducto);

        subirProducto.setEnabled(false);
        nombreProducto.addTextChangedListener(uploadProductTextWatcher);
        descripcionProducto.addTextChangedListener(uploadProductTextWatcher);
        precioProducto.addTextChangedListener(uploadProductTextWatcher);

        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);

    }

    private TextWatcher uploadProductTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String prod = nombreProducto.getText().toString().trim();
            String desc = descripcionProducto.getText().toString().trim();
            String precio = precioProducto.getText().toString().trim();
            subirProducto.setEnabled(!prod.isEmpty() && !desc.isEmpty() && !precio.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

            String prod = nombreProducto.getText().toString().trim();
            String desc = descripcionProducto.getText().toString().trim();

            prodNameCheckLength = (prod.length() >= 3 && prod.length() <= 100);;
            prodDescCheckLength = (desc.length() >= 10);

        }
    };



    private void gestionSubidaProducto (String  estado, String msg){
        if (estado.equals("O")){
            startActivity(new Intent(SubirProducto.this, PantallaPrincipal.class));
        }
        else if (estado.equals("E")){
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
    }


    public String parseParams(String prodName, String prodDesc, String prodPrice){
        String aux = url;

        //Urlificar los parametros
        prodName = Uri.encode(prodName);
        prodDesc =  Uri.encode(prodDesc);
        prodPrice =  Uri.encode(prodPrice);
        String uName = sharedpreferences.getString(Common.un, null);
        aux = aux+"?un="+uName+"&prod="+prodName+"&desc="+prodDesc+"&pre="+prodPrice;
        Log.d("URL", aux);
        return aux;
    }

    private void doPost(final String prodName, final String prodDesc, final String prodPrice) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String urlPetition = parseParams(prodName, prodDesc, prodPrice);

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
            String prec = precioProducto.getText().toString().trim();
            subirProducto.setEnabled(false);
            doPost(prod, desc, prec);
        }


    }


}
