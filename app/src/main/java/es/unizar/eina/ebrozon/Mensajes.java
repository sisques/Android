package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.unizar.eina.ebrozon.lib.Common;

public class Mensajes extends AppCompatActivity {
    private ListView listaMensajesListView;
    private SwipeRefreshLayout swipeLayout;

    private List<HashMap<String, String>> mensajes;
    private final String[] atributos = {"usuarioComunica", "fecha", "contenido", "usuarioImagen"};

    private String un; // usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);

        listaMensajesListView = (ListView) findViewById(R.id.listaMensajes);
        mensajes = new ArrayList<HashMap<String, String>>();

        SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        un = sharedpreferences.getString(Common.un, null);

        // Refresh
        swipeLayout = findViewById(R.id.mensajesRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // Cada vez que se realiza el gesto para refrescar
                mensajes.clear();
                listarMensajes();
                swipeLayout.setRefreshing(false);
            }
        });

        listarMensajes();
    }

    private void listarMensajes() {
        String urlPetition = Common.url+"/listarChats?un="+un;
        gestionarPeticionListar(urlPetition);
    }

    private void gestionarPeticionListar(String urlPetition) {
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
                                anyadirMensajes(new JSONArray(response));
                                gestionarListarTrasPeticion();
                            }catch (Exception ignored) { }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recibir la lista de mensajes");
                    }
                }
        );
        queue.add(postRequest);
    }

    private void gestionarListarTrasPeticion() {
        int[] to = {R.id.MensajeResumenUsuario, R.id.MensajeResumenFecha,
                R.id.MensajeResumenMensaje, R.id.MensajeResumenImagen};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), this.mensajes, R.layout.content_mensaje_resumen, this.atributos, to);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation)
            { // Para el tratamiento de imágenes
                if((view instanceof ImageView) & (data instanceof String))
                {
                    Common.establecerFotoUsuarioServidor(getApplicationContext(), (String) data, (ImageView) view);
                    return true;
                }
                return false;
            }
        });

        listaMensajesListView.setAdapter(simpleAdapter);

        listaMensajesListView.setClickable(true);
        listaMensajesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(Mensajes.this, Chat.class);
                intent.putExtra(atributos[0], mensajes.get(position).get(atributos[0]));
                startActivity(intent);
            }
        });
    }

    private void anyadirMensajes(JSONArray JSONmensajes) {
        HashMap<String, String> mensaje;
        JSONObject JSONmensaje;
        String aux;

        for (int i=0; i<JSONmensajes.length(); i++) {
            try {
                JSONmensaje = JSONmensajes.getJSONObject(i);

                mensaje = new HashMap<String, String>();

                aux = JSONmensaje.get("emisor").toString();
                if (aux.equals(this.un)) {
                    aux = JSONmensaje.get("receptor").toString();
                }
                mensaje.put(atributos[0], aux);

                mensaje.put(atributos[1], JSONmensaje.get(atributos[1]).toString());
                mensaje.put(atributos[2], JSONmensaje.get(atributos[2]).toString());
                mensaje.put(atributos[3], aux);
                mensajes.add(mensaje);
            }
            catch (Exception ignored) { }
        }
    }
}
