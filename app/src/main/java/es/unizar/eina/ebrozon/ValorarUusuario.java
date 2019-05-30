package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.unizar.eina.ebrozon.lib.Common;

public class ValorarUusuario extends AppCompatActivity {

    private String urlValoracion = "https://protected-caverns-60859.herokuapp.com/mandarOpinion";
    private String urlReport = "https://protected-caverns-60859.herokuapp.com/mandarReport";

    SharedPreferences sharedpreferences;
    String currentUser;
    String opinionUser;
    String modo;

    private Button send;
    private Button dismiss;

    private Spinner reportList;

    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    private EditText content;
    private TextView contentLimit;
    private TextView title;
    private TextView titleActivity;

    private Integer rating = 0;

    private Boolean opinionCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valorar_uusuario);

        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        currentUser = sharedpreferences.getString(Common.un, null);

        send = findViewById(R.id.makeOpinionConfirm);
        dismiss = findViewById(R.id.makeOpinionDismiss);
        content = findViewById(R.id.makeOpinionText);
        star1 = findViewById(R.id.makeOpinionStar1);
        star2 = findViewById(R.id.makeOpinionStar2);
        star3 = findViewById(R.id.makeOpinionStar3);
        star4 = findViewById(R.id.makeOpinionStar4);
        star5 = findViewById(R.id.makeOpinionStar5);
        contentLimit = findViewById(R.id.makeOpinionLimit);
        titleActivity = findViewById(R.id.makeOpinionTitle);
        title = findViewById(R.id.makeOpinionTitle2);
        reportList = findViewById(R.id.makeOpinionReportList);

        Intent intentAnterior = getIntent();
        opinionUser = intentAnterior.getStringExtra("username");
        modo = intentAnterior.getStringExtra("mode");
        if (modo.equals("report")) {
            title.setText("Selecciona un motivo y escribe una explicación de los problemas encontrados con "+opinionUser+".");
            star1.setEnabled(false);
            star1.setVisibility(View.GONE);
            star2.setEnabled(false);
            star2.setVisibility(View.GONE);
            star3.setEnabled(false);
            star3.setVisibility(View.GONE);
            star4.setEnabled(false);
            star4.setVisibility(View.GONE);
            star5.setEnabled(false);
            star5.setVisibility(View.GONE);
            dismiss.setText("Cancelar");
            titleActivity.setText("Informar sobre un usuario");
        }
        else {
            String actual = title.getText().toString();
            title.setText(actual+" "+opinionUser+".");
            reportList.setVisibility(View.GONE);
            reportList.setEnabled(false);
        }

        content.addTextChangedListener(textListener);
        send.setEnabled(false);

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 1;
                dibujarEstrellas(rating);
                String contentText = content.getText().toString();
                opinionCheck = (!contentText.isEmpty() && rating > 0 && !(contentText.length() > 500));
                send.setEnabled(opinionCheck);
            }
        });

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 1;
                dibujarEstrellas(rating);
                String contentText = content.getText().toString();
                opinionCheck = (!contentText.isEmpty() && rating > 0 && !(contentText.length() > 500));
                send.setEnabled(opinionCheck);
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 2;
                dibujarEstrellas(rating);
                String contentText = content.getText().toString();
                opinionCheck = (!contentText.isEmpty() && rating > 0 && !(contentText.length() > 500));
                send.setEnabled(opinionCheck);
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 3;
                dibujarEstrellas(rating);
                String contentText = content.getText().toString();
                opinionCheck = (!contentText.isEmpty() && rating > 0 && !(contentText.length() > 500));
                send.setEnabled(opinionCheck);
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 4;
                dibujarEstrellas(rating);
                String contentText = content.getText().toString();
                opinionCheck = (!contentText.isEmpty() && rating > 0 && !(contentText.length() > 500));
                send.setEnabled(opinionCheck);
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 5;
                dibujarEstrellas(rating);
                String contentText = content.getText().toString();
                opinionCheck = (!contentText.isEmpty() && rating > 0 && !(contentText.length() > 500));
                send.setEnabled(opinionCheck);
            }
        });
    }

    private TextWatcher textListener = new TextWatcher() {

        private String contentText;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            contentText = content.getText().toString();
            Integer sizeContent = contentText.length();
            if (500 - sizeContent < 0) {
                contentLimit.setTextColor(Color.rgb(200,0,0));
            }
            else {
                contentLimit.setTextColor(Color.rgb(128,128,128));
            }
            Integer newLimit = 500 - sizeContent;
            contentLimit.setText(newLimit.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            contentText = content.getText().toString();
            if (modo.equals("report")) {
                opinionCheck = (!contentText.isEmpty() && !(contentText.length() > 500));
            }
            else {
                opinionCheck = (!contentText.isEmpty() && rating > 0 && !(contentText.length() > 500));
            }
            send.setEnabled(opinionCheck);
        }
    };

    private void peticionReporte() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String opinionText = content.getText().toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlReport,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(ValorarUusuario.this, "Informe enviado.", Toast.LENGTH_LONG).show();
                        send.setEnabled(true);
                        dismiss.setEnabled(true);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", error.getMessage());
                        Toast.makeText(ValorarUusuario.this, "Ha habido un problema al enviar el informe.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("em",currentUser);
                params.put("re",opinionUser);
                params.put("con",opinionText);
                params.put("mov",reportList.getSelectedItem().toString());
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void peticionValoracion() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String opinionText = content.getText().toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlValoracion,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(ValorarUusuario.this, "Valoración enviada.", Toast.LENGTH_LONG).show();
                        send.setEnabled(true);
                        dismiss.setEnabled(true);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", error.getMessage());
                        Toast.makeText(ValorarUusuario.this, "Ha habido un problema al enviar la valoración.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("em",currentUser);
                params.put("re",opinionUser);
                params.put("con",opinionText);
                params.put("es",rating.toString());
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void enviarValoracion(View view) {
        send.setEnabled(false);
        dismiss.setEnabled(false);
        if (modo.equals("report")) {
            peticionReporte();
        }
        else {
            peticionValoracion();
        }
    }

    public void omitirValoracion(View view) {
        finish();
    }

    private void dibujarEstrellas(Integer stars) {
        switch (stars) {
            case 1:
                star1.setImageResource(android.R.drawable.star_big_on);
                star2.setImageResource(android.R.drawable.star_big_off);
                star3.setImageResource(android.R.drawable.star_big_off);
                star4.setImageResource(android.R.drawable.star_big_off);
                star5.setImageResource(android.R.drawable.star_big_off);
                break;
            case 2:
                star1.setImageResource(android.R.drawable.star_big_on);
                star2.setImageResource(android.R.drawable.star_big_on);
                star3.setImageResource(android.R.drawable.star_big_off);
                star4.setImageResource(android.R.drawable.star_big_off);
                star5.setImageResource(android.R.drawable.star_big_off);
                break;
            case 3:
                star1.setImageResource(android.R.drawable.star_big_on);
                star2.setImageResource(android.R.drawable.star_big_on);
                star3.setImageResource(android.R.drawable.star_big_on);
                star4.setImageResource(android.R.drawable.star_big_off);
                star5.setImageResource(android.R.drawable.star_big_off);
                break;
            case 4:
                star1.setImageResource(android.R.drawable.star_big_on);
                star2.setImageResource(android.R.drawable.star_big_on);
                star3.setImageResource(android.R.drawable.star_big_on);
                star4.setImageResource(android.R.drawable.star_big_on);
                star5.setImageResource(android.R.drawable.star_big_off);
                break;
            case 5:
                star1.setImageResource(android.R.drawable.star_big_on);
                star2.setImageResource(android.R.drawable.star_big_on);
                star3.setImageResource(android.R.drawable.star_big_on);
                star4.setImageResource(android.R.drawable.star_big_on);
                star5.setImageResource(android.R.drawable.star_big_on);
                break;

        }
    }
}
