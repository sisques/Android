package es.unizar.eina.ebrozon.lib;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import es.unizar.eina.ebrozon.R;
import es.unizar.eina.ebrozon.ValorarUusuario;
import es.unizar.eina.ebrozon.perfil_usuario;


public class adaptadorOfertasCompras extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context contexto;
    private String [][] datos;
    int numElementos;
    String un;

    private final String confirmarOferta =  "/aceptarOferta";  //?id=   id oferta
    private final String cancelarOferta =  "/rechazarOferta";  //?id=   id oferta

    private final String confirmarPago= "/confirmarPagoVenta";  //?id=  id venta
    private final String cancelarPago = "/cancelarPagoVenta";   //?id=  id venta

    /**
     * Constructor de la clase
     * @param c Contexto sobre el que se ejecuta.
     * @param d Datos a mostrar.
     * @param ne Numero de elementos a mostrar.
     * @param u Nombre de usuario
     */
    public adaptadorOfertasCompras (Context c, String[][] d, int ne, String u){
        contexto = c;
        datos = d;
        numElementos = ne;
        inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
        un = u;
    }

    /**
     *  Se Encarga de poblar los elementos del adaptador con la informacion recibida atraves de la
     *  variable "datos".
     * @param i
     * @param convertView
     * @param parent
     * @return devuelve el adaptador recién creado para mostrarlo por pantalla
     */
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View vista = inflater.inflate(R.layout.content_ofertas_y_compras, null);

        TextView nombreProd =  vista.findViewById(R.id.OPNombrePRod);
        TextView tipo =  vista.findViewById(R.id.OPTipo);
        TextView importe =  vista.findViewById(R.id.OPImporte);
        TextView usuario =  vista.findViewById(R.id.OPUsuario);
        final String vendedorUn = datos[i][3];
        usuario.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( contexto, perfil_usuario.class);
                intent.putExtra("username", vendedorUn);
                contexto.startActivity(intent);
            }
        } );
        TextView fecha =  vista.findViewById(R.id.OPFecha);
        Button yes =  vista.findViewById(R.id.OPsi);
        Button no = vista.findViewById(R.id.OPno);

        nombreProd.setText(datos[i][0]);
        tipo.setText(datos[i][1]);
        importe.setText(datos[i][2]);
        usuario.setText(datos[i][3]);
        fecha.setText(datos[i][4]);
        yes.setText(datos[i][5]);
        no.setText(datos[i][6]);
        final String id = datos[i][7];
        final String a = datos[i][1];
        final String us = datos[i][3];

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.equals("Oferta")){
                    aceptarOferta(id);

                }else{
                    confirmarPagoVenta(id);
                }
                Intent intent = new Intent(contexto, ValorarUusuario.class);
                intent.putExtra("username",us);
                intent.putExtra("mode","opinion");
                contexto.startActivity(intent);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.equals("Oferta")){
                    rechazarOferta(id);

                }else{
                    cancelarPagoVenta(id);
                }
            }
        });
        return vista;
    }

    /**
     * Envía la petición al servidor para confirmar el pago de la venta con identificador id
     * @param id
     */
    private void confirmarPagoVenta(final String id) {

        RequestQueue queue = Volley.newRequestQueue(contexto);
        String urlPetition = Common.url + confirmarPago + "?id=" + id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        if(msg.equals("Ok")){
                            msg = "Se ha confirmado el pago";
                        }
                        gestionRespuesta(estado, msg);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        String e = "Error desconocido";
                        if (error != null){
                            e = error.getMessage();
                        }
                        Log.d("Error.Response", e);
                        String response = error.getMessage().replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        gestionRespuesta(estado, msg);
                    }
                }
        );
        queue.add(postRequest);
    }


    /**
     * Envía la petición al servidor para cancelar el pago de la venta con identificador id
     * @param id
     */
    private void cancelarPagoVenta(final String id) {

        RequestQueue queue = Volley.newRequestQueue(contexto);
        String urlPetition = Common.url + cancelarPago + "?id=" + id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        if(msg.equals("Ok")){
                            msg = "La venta se ha vuelto a abrir";
                        }
                        gestionRespuesta(estado, msg);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        String e = "Error desconocido";
                        if (error.getMessage() != null){
                            e = error.getMessage();
                        }
                        Log.d("Error.Response", e);
                        String response = e.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        gestionRespuesta(estado, msg);
                    }
                }
        );
        queue.add(postRequest);
    }

    /**
     * Envía la petición al servidor para aceptar el pago de la oferta con identificador id
     * @param id
     */
    private void aceptarOferta(final String id) {

        RequestQueue queue = Volley.newRequestQueue(contexto);
        String urlPetition = Common.url + confirmarOferta + "?id=" + id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        if(msg.equals("Ok")){
                            msg = "Oferta aceptada";
                        }
                        gestionRespuesta(estado, msg);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        String e = "Error desconocido";
                        if (error.getMessage() != null){
                            e = error.getMessage();
                        }
                        Log.d("Error.Response", e);
                        String response = e.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        gestionRespuesta(estado, msg);
                    }
                }
        );
        queue.add(postRequest);
    }

    /**
     * Envía la petición al servidor para rechazar el pago de la oferta con identificador id
     * @param id
     */
    private void rechazarOferta(final String id) {

        RequestQueue queue = Volley.newRequestQueue(contexto);
        String urlPetition = Common.url + cancelarOferta + "?id=" + id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        if(msg.equals("Ok")){
                            msg = "Oferta rechazada";
                        }
                        gestionRespuesta(estado, msg);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        String e = "Error desconocido";
                        if (error != null){
                            e = error.getMessage();
                        }
                        Log.d("Error.Response", e);
                        String response = e.replace("{", "").replace("}", "").replace("\"", "");
                        String estado = response.split(":")[0];
                        String msg = response.replace(estado + ":", "");
                        gestionRespuesta(estado, msg);
                    }
                }
        );
        queue.add(postRequest);
    }

    private void gestionRespuesta (String  estado, String msg){
        if (estado.equals("O")){
            Toast.makeText(contexto.getApplicationContext(),msg, Toast.LENGTH_LONG).show();

        }
        else if (estado.equals("E")){
            Toast.makeText(contexto.getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(contexto.getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public int getCount() {
        return numElementos;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}










