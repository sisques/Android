package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.adaptadorOfertasCompras;

import static java.lang.Thread.sleep;

public class ofertasYCompras extends AppCompatActivity {
    private ListView lista;
    private SwipeRefreshLayout swipeLayout;


    private String un;
    private String id;

    private final String ofertas =  "/listarOfertasRecibidas";  //?un=  nombre usuario
    private final String confirmarOferta =  "/aceptarOferta";  //?id=   id oferta
    private final String cancelarOferta =  "/rechazarOferta";  //?id=   id oferta

    private final String compras =  "/listarOfertasRecibidasAceptadasPendientes";   //?un=  nombre usuario
    private final String confirmarPago= "/confirmarPagoVenta";  //?id=  id venta
    private final String cancelarPago = "/cancelarPagoVenta";   //?id=  id venta


    private int ne;
    private String[][] datos = new String[50][9];
                                //[x][0] = nombre prod
                                //[x][1] = tipo
                                //[x][2] = importe
                                //[x][3] = usuario
                                //[x][4] = fecha
                                //[x][5] = yes
                                //[x][6] = no
                                //[x][7] = idVenta


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas_y_compras);
        swipeLayout = findViewById(R.id.ofertasComprasRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // Cada vez que se realiza el gesto para refrescar

                ne = 0;
                obtenerOfertas();
                swipeLayout.setRefreshing(false);
                 }
        });

        lista = (ListView) findViewById(R.id.listaOfertasCompras);
        SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        un = sharedpreferences.getString(Common.un, null);
        ne = 0;
        obtenerOfertas();


    }

    private void continuar(){
        lista.setAdapter(new adaptadorOfertasCompras(this, datos, ne, un));
    }






    private void obtenerOfertas() {
        String urlPetition = Common.url + ofertas + "?un=" + un;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if (!response.equals("[]")) {
                            try {
                                anyadirOfertas(new JSONArray(response));

                            }catch (Exception ignored) { }

                        }
                        obtenerCompras();
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

    private void obtenerCompras() {
        String urlPetition = Common.url + compras + "?un=" + un;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if (!response.equals("[]")) {
                            try {
                                anyadirCompras(new JSONArray(response));

                            }catch (Exception ignored) { }
                        }
                        continuar();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recibir la lista de compras");
                    }
                }
        );
        queue.add(postRequest);
    }

    private void anyadirOfertas(JSONArray JSONelementos) {
        JSONObject JSONelemento;

        for (int i=0; i<JSONelementos.length(); i++) {
            try {
                JSONelemento = JSONelementos.getJSONObject(i);

                datos[i][0] = JSONelemento.get("producto").toString();
                datos[i][1] = getResources().getString(R.string.tipo_oferta_recibida);
                datos[i][2] = JSONelemento.get("cantidad").toString();
                datos[i][3] = JSONelemento.get("usuario").toString();

                String aux = JSONelemento.get("fecha").toString();
                String anyo = aux.substring(0,4);
                String mes = aux.substring(5,7);
                String dia = aux.substring(8,10);
                String hora = String.valueOf(Integer.valueOf(aux.substring(11,13)) + 2);
                String minuto = aux.substring(14,16);
                String fecha = dia + "/" + mes + "/" + anyo + " " + hora + ":" +minuto;

                datos[i][4] = fecha;
                datos[i][5] = getResources().getString(R.string.boton_aceptar_oferta);
                datos[i][6] = getResources().getString(R.string.boton_rechazar_oferta);
                datos[i][7] = JSONelemento.get("identificador").toString();
                datos[i][8] = "oferta";

                ne++;


            }
            //catch (Exception ignored) { }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void anyadirCompras(JSONArray JSONelementos) {
        JSONObject JSONelemento;
        int neInit = ne;
        for (int i=0; i<JSONelementos.length(); i++) {
            try {
                JSONelemento = JSONelementos.getJSONObject(i);

                datos[neInit +i][0] = JSONelemento.get("producto").toString();
                datos[neInit +i][1] = getResources().getString(R.string.tipo_compra_pendiente);
                datos[neInit +i][2] = JSONelemento.get("cantidad").toString();
                datos[neInit +i][3] = JSONelemento.get("usuario").toString();

                String aux = JSONelemento.get("fecha").toString();
                String anyo = aux.substring(0,4);
                String mes = aux.substring(5,7);
                String dia = aux.substring(8,10);
                String hora = String.valueOf(Integer.valueOf(aux.substring(11,13)) + 2);
                String minuto = aux.substring(14,16);
                String fecha = dia + "/" + mes + "/" + anyo + " " + hora + ":" +minuto;

                datos[neInit +i][4] = fecha;
                datos[neInit +i][5] = getResources().getString(R.string.boton_aceptar_pago);
                datos[neInit +i][6] = getResources().getString(R.string.boton_rechazar_pago);
                datos[neInit +i][7] = JSONelemento.get("nventa").toString();
                datos[i][8] = "compra";

                ne++;


            }
            //catch (Exception ignored) { }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}



