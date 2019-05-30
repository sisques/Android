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

import es.unizar.eina.ebrozon.Chat;
import es.unizar.eina.ebrozon.Producto;
import es.unizar.eina.ebrozon.R;
import es.unizar.eina.ebrozon.perfil_usuario;

public class adaptadorOfertasComprasEnviadas extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context contexto;
    String [][] datos;
    int numElementos;
    String un;


    private final String cancelarOferta =  "/retirarOferta";  //?id=   id oferta



    public adaptadorOfertasComprasEnviadas(Context c, String[][] d, int ne, String u){
        contexto = c;
        datos = d;
        numElementos = ne;
        inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
        un = u;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View vista = inflater.inflate(R.layout.content_ofertas_y_compras, null);

        TextView nombreProd = (TextView) vista.findViewById(R.id.OPNombrePRod);
        TextView tipo = (TextView) vista.findViewById(R.id.OPTipo);
        TextView importe = (TextView) vista.findViewById(R.id.OPImporte);
        TextView usuario = (TextView) vista.findViewById(R.id.OPUsuario);
        TextView fecha = (TextView) vista.findViewById(R.id.OPFecha);
        Button yes = (Button) vista.findViewById(R.id.OPsi);
        Button no = (Button) vista.findViewById(R.id.OPno);

        nombreProd.setText(datos[i][0]);
        tipo.setText(datos[i][1]);
        importe.setText(datos[i][2]);
        usuario.setText(datos[i][3]);
        final String vendedorUn = datos[i][3];
        usuario.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( contexto, perfil_usuario.class);
                intent.putExtra("username", vendedorUn);
                contexto.startActivity(intent);
            }
        } );


        fecha.setText(datos[i][4]);
        yes.setText(datos[i][5]);
        no.setText(datos[i][6]);
        final String id = datos[i][7];
        final String a = datos[i][1];
        final String usr = datos[i][3];
        final String msg = "Hola, estoy interesado en tu producto " + datos[i][0] +".";

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexto, Chat.class);
                iniciarChat(msg,un,usr);
                intent.putExtra("usuarioComunica", vendedorUn);
                contexto.startActivity(intent);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rechazarOferta(id, a.equals("Oferta"));

            }
        });
        return vista;
    }


    private void iniciarChat(String mensaje, String origen, String destino) {
        String urlPetition = Common.url + "/mandarMensaje?em=" + origen + "&re=" + destino
                + "&con=" + mensaje;
        RequestQueue queue = Volley.newRequestQueue(contexto);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recibir la lista de ofertas");
                    }
                }
        );
        queue.add(postRequest);
    }




    private void rechazarOferta(final String id, final boolean oferta) {

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
                            if(oferta){
                                msg = "La peticion de oferta se ha rechazado";
                            } else {
                                msg = "La peticion de compra se ha eliminado";

                            }
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
                        String response =  e.replace("{", "").replace("}", "").replace("\"", "");
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










