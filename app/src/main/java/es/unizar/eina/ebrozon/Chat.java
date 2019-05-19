package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

public class Chat extends AppCompatActivity {
    private ListView listaChatListView;
    private SwipeRefreshLayout swipeLayout;

    private List<HashMap<String, Object>> chat;
    private final String[] atributos = {"contenido", "emisor"};

    private String un; // usuario
    private String usuarioComunica; // usuario con el que se comunica

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Recibe como atributo el usuario con el que se comunica
        usuarioComunica = (String) getIntent().getSerializableExtra("usuarioComunica");

        listaChatListView = (ListView) findViewById(R.id.listaChat);
        chat = new ArrayList<HashMap<String, Object>>();

        SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        un = sharedpreferences.getString(Common.un, null);

        // Refresh
        swipeLayout = findViewById(R.id.chatRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // Cada vez que se realiza el gesto para refrescar
                chat.clear();
                listarChat();
                swipeLayout.setRefreshing(false);
            }
        });

        listarChat();
    }

    private void listarChat() {
        String urlPetition = Common.url + "/cargarChat?em=" + un + "&re=" + usuarioComunica;
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
                                anyadirChat(new JSONArray(response));
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
                        Log.d("Error.Response", "Error al recibir la lista de chat");
                    }
                }
        );
        queue.add(postRequest);
    }

    private void gestionarListarTrasPeticion() {
        int[] to = {R.id.ChatMensaje, R.id.ChatMensaje};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), this.chat, R.layout.content_chat_mensaje, this.atributos, to);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation)
            { // Para el tratamiento de imágenes
                if(data instanceof Boolean)
                {
                    if ((Boolean) data) {
                        ((TextView) view).setGravity(Gravity.END);
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                        p.setMargins(200, 10, 10, 10);
                        view.requestLayout();
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_shape_enviado));
                    }
                    else {
                        ((TextView) view).setGravity(Gravity.START);
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                        p.setMargins(10, 10, 200, 10);
                        view.requestLayout();
                        view.setBackground(getResources().getDrawable(R.drawable.rounded_shape_recibido));
                    }
                    return true;
                }
                return false;
            }
        });

        listaChatListView.setAdapter(simpleAdapter);
    }

    private void anyadirChat(JSONArray JSONmensajes) {
        HashMap<String, Object> mensaje;
        JSONObject JSONmensaje;

        for (int i=0; i<JSONmensajes.length(); i++) {
            try {
                JSONmensaje = JSONmensajes.getJSONObject(i);

                mensaje = new HashMap<String, Object>();
                mensaje.put(atributos[0], JSONmensaje.get(atributos[0]).toString());

                if (JSONmensaje.get(atributos[1]).toString().equals(this.un)) {
                    mensaje.put(atributos[1], Boolean.TRUE);
                }
                else {
                    mensaje.put(atributos[1], Boolean.FALSE);
                }

                chat.add(mensaje);
            }
            catch (Exception ignored) { }
        }
    }
}
