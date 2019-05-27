package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Timer;
import java.util.TimerTask;

import es.unizar.eina.ebrozon.lib.Common;

public class Chat extends AppCompatActivity {
    private ListView listaChatListView;
    SimpleAdapter simpleAdapter;
    private SwipeRefreshLayout swipeLayout;

    private List<HashMap<String, Object>> chat;
    private final String[] atributos = {"contenido", "emisor"};
    private Integer idMax; // id del último mensaje

    private String un; // usuario
    private String usuarioComunica; // usuario con el que se comunica

    private String fechaUltimo;

    Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Recibe como atributo el usuario con el que se comunica
        usuarioComunica = (String) getIntent().getSerializableExtra("usuarioComunica");

        fechaUltimo = "";

        listaChatListView = (ListView) findViewById(R.id.listaChat);
        chat = new ArrayList<HashMap<String, Object>>();
        idMax = 0;

        SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        un = sharedpreferences.getString(Common.un, null);

        inicializarListView();

        // Barra de arriba (Usuario con el que se comunica)
        ImageView fotoUsuarioComunica = (ImageView) findViewById(R.id.chatImagen);
        Common.establecerFotoUsuarioServidor(getApplicationContext(), usuarioComunica, fotoUsuarioComunica);

        TextView nombreUsuarioComunica = (TextView) findViewById(R.id.chatUsuario);
        nombreUsuarioComunica.setText(usuarioComunica);

        // Enviar mensajes
        final EditText mensaje = (EditText) findViewById(R.id.chatEscribir);
        final ImageButton enviar = (ImageButton) findViewById(R.id.chatEnviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje(mensaje.getText().toString());
                mensaje.getText().clear();
            }
        });

        // Timer, para recargar el chat cada segundo
        mTimer = new Timer();

        // Refresh
        swipeLayout = findViewById(R.id.chatRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // Cada vez que se realiza el gesto para refrescar
                mTimer.cancel();
                mTimer.purge();

                chat.clear();
                listarChat(true, false, true);

                swipeLayout.setRefreshing(false);
            }
        });

        listarChat(true, true, true);
    }

    private void inicializarListView() {
        int[] to = {R.id.ChatMensaje, R.id.ChatMensaje};

        simpleAdapter = new SimpleAdapter(getBaseContext(), this.chat, R.layout.content_chat_mensaje, this.atributos, to);
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

    @Override
    public void onBackPressed() {
        mTimer.cancel();
        mTimer.purge();

        Intent data = new Intent();

        if (chat.size() > 0) {
            data.putExtra("ChatUltimo", (String) chat.get(chat.size() - 1).get(atributos[0]));
            data.putExtra("FechaUltimo", fechaUltimo);
            setResult(Common.RESULTADO_OK, data);
        }
        else {
            setResult(Common.RESULTADO_NOK, data);
        }

        finish();
    }

    private void enviarMensaje(String mensaje) {
        if (!mensaje.equals("")) {
            mTimer.cancel();
            mTimer.purge();

            String urlPetition = Common.url + "/mandarMensaje?em=" + un + "&re=" + usuarioComunica
                    + "&con=" + mensaje;

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);

                            if (idMax == 0) {
                                listarChat(true, true, true);
                            } else {
                                listarChat(false, true, true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", "Error al recibir la lista de chat");

                            mTimer = new Timer();
                            mTimer.schedule((new TimerTask() {
                                public void run() {
                                    listarChat(false, false, false);
                                } }), 0, 500);
                        }
                    }
            );
            queue.add(postRequest);
        }
    }

    // Si all, lista todos los mensajes, si no, añade a la lista los mensajes desde idMax
    // Si lastMsg, se posiciona al final del chat, si no, donde estaba
    // Si initTimer, inicia el timer
    private void listarChat(Boolean all, Boolean lastMsg, Boolean initTimer) {
        if (all) {
            String urlPetition = Common.url + "/cargarChat?em=" + un + "&re=" + usuarioComunica;
            gestionarPeticionListar(urlPetition, lastMsg, initTimer);
        }
        else {
            if (idMax > 0) {
                String urlPetition = Common.url + "/recibirMensaje?em=" + un + "&re=" + usuarioComunica
                        + "&lm=" + idMax;
                gestionarPeticionListar(urlPetition, lastMsg, initTimer);
            }
        }
    }

    private void gestionarPeticionListar(String urlPetition, final Boolean lastMsg, final Boolean initTimer) {
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
                                simpleAdapter.notifyDataSetChanged();
                                if (lastMsg) {
                                    listaChatListView.setSelection(simpleAdapter.getCount() - 1);
                                }
                            }catch (Exception ignored) { }
                        }

                        if (initTimer) {
                            mTimer = new Timer();
                            mTimer.schedule((new TimerTask() {
                                public void run() {
                                    listarChat(false, false, false);
                                } }), 0, 500);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recibir la lista de chat");

                        if (initTimer) {
                            mTimer = new Timer();
                            mTimer.schedule((new TimerTask() {
                                public void run() {
                                    listarChat(false, false, false);
                                } }), 0, 500);
                        }
                    }
                }
        );
        queue.add(postRequest);
    }

    private void anyadirChat(JSONArray JSONmensajes) {
        HashMap<String, Object> mensaje;
        JSONObject JSONmensaje = null;

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

        if (JSONmensaje != null) {
            try {
                idMax = JSONmensaje.getInt("identificador");

                String aux = JSONmensaje.getString("fecha");
                fechaUltimo = aux.substring(11, 16) + " " + aux.substring(8, 10) + "-"
                        + aux.substring(5, 7) + "-" + aux.substring(2, 4);
            }
            catch (Exception ignored) { }
        }
    }
}
