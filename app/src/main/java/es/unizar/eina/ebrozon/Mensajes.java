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

import static es.unizar.eina.ebrozon.lib.Common.StringToBitMap;

public class Mensajes extends AppCompatActivity {
    private final int ACT_CHAT = 0;

    private ListView listaMensajesListView;
    SimpleAdapter simpleAdapter;
    private SwipeRefreshLayout swipeLayout;

    private String un; // usuario

    private List<HashMap<String, Object>> mensajes;
    private final String[] atributos = {"usuarioComunica", "fecha", "contenido", "usuarioImagen"};

    private int numChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);

        listaMensajesListView = (ListView) findViewById(R.id.listaMensajes);
        mensajes = new ArrayList<HashMap<String, Object>>();

        SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        un = sharedpreferences.getString(Common.un, null);

        inicializarListView();

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

        mensajes.clear();
        listarMensajes();
    }

    private void inicializarListView() {
        int[] to = {R.id.MensajeResumenUsuario, R.id.MensajeResumenFecha,
                R.id.MensajeResumenMensaje, R.id.MensajeResumenImagen};

        simpleAdapter = new SimpleAdapter(getBaseContext(), this.mensajes, R.layout.content_mensaje_resumen, this.atributos, to);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation)
            { // Para el tratamiento de imágenes
                if ((view instanceof ImageView) && (data instanceof Bitmap)) {
                    ((ImageView) view).setImageBitmap((Bitmap) data);
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
                numChat = position;

                Intent intent = new Intent(Mensajes.this, Chat.class);
                intent.putExtra(atributos[0], (String) mensajes.get(position).get(atributos[0]));
                startActivityForResult(intent, ACT_CHAT);
            }
        });
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
                                simpleAdapter.notifyDataSetChanged();
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

    private void anyadirMensajes(JSONArray JSONmensajes) {
        HashMap<String, Object> mensaje;
        JSONObject JSONmensaje;
        String usuarioComunica, fecha;

        for (int i=0; i<JSONmensajes.length(); i++) {
            try {
                JSONmensaje = JSONmensajes.getJSONObject(i);

                mensaje = new HashMap<String, Object>();

                usuarioComunica = JSONmensaje.get("emisor").toString();
                if (usuarioComunica.equals(this.un)) {
                    usuarioComunica = JSONmensaje.get("receptor").toString();
                }
                mensaje.put(atributos[0], usuarioComunica);

                fecha = JSONmensaje.get(atributos[1]).toString();
                mensaje.put(atributos[1], fecha.substring(11, 16) + " " + fecha.substring(8, 10) + "-"
                        + fecha.substring(5, 7) + "-" + fecha.substring(2, 4));

                mensaje.put(atributos[2], JSONmensaje.get(atributos[2]).toString());

                mensaje.put(atributos[3], null);

                mensajes.add(mensaje);

                descargarImagenUsuario(usuarioComunica, i);
            }
            catch (Exception ignored) { }
        }
    }

    private void descargarImagenUsuario(final String usuarioComunica, final int numMensaje) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String urlPetition = Common.url + "/loadArchivoUsuario?un=" + usuarioComunica;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace(" ","+");

                        if (mensajes.size() > numMensaje) {
                            mensajes.get(numMensaje).put(atributos[3], StringToBitMap(response));
                            simpleAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error con imagen de perfil");
                    }
                }
        );
        queue.add(postRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_CHAT) {
            if (resultCode == Common.RESULTADO_OK) {
                String mensaje = data.getStringExtra("ChatUltimo");
                String fecha = data.getStringExtra("FechaUltimo");

                if (mensaje != null && !mensaje.equals("") && fecha != null && !fecha.equals("")) {
                    mensajes.get(numChat).put(atributos[1], fecha);
                    mensajes.get(numChat).put(atributos[2], mensaje);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
