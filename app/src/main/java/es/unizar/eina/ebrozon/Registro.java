package es.unizar.eina.ebrozon;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import es.unizar.eina.ebrozon.credentials;


import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Registro extends AppCompatActivity {

    String url ="https://protected-caverns-60859.herokuapp.com/registrar";

    private Button registrar;

    private EditText userName;
    private EditText mail;
    private EditText password;
    private EditText confirmPassword;

    private Spinner city;

    private Boolean mostrandoPass1 = false;
    private Boolean mostrandoPass2 = false;

    private Boolean userNameCheck = false;
    private Boolean passwordCheck = false;
    private Boolean mailCheck = false;
    private Boolean confirmCheck = false;
    private Boolean cityCheck = false;

    private Boolean userNameEmpty = true;
    private Boolean passwordEmpty = true;
    private Boolean confirmEmpty = true;
    private Boolean mailEmpty = true;

    private TextView limitUserName;
    private TextView limitMail;
    private TextView limitPass;
    private TextView limitPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        registrar = findViewById(R.id.registrar);
        userName = findViewById(R.id.Username_register);
        mail = findViewById(R.id.Email_register);
        city = findViewById(R.id.city_register);
        password = findViewById(R.id.Password_register);
        confirmPassword = findViewById(R.id.confirmPassword_registger);
        limitUserName = findViewById(R.id.limUser);
        limitMail = findViewById(R.id.limMail);
        limitPass = findViewById(R.id.limContra1);
        limitPass2 = findViewById(R.id.limContra2);

        userName.addTextChangedListener(registerTextWatcher);
        mail.addTextChangedListener(registerTextWatcher);
        password.addTextChangedListener(registerTextWatcher);
        confirmPassword.addTextChangedListener(registerTextWatcher);

        city.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                cityCheck = !(city.getSelectedItem().toString().equals("..."));
                registrar.setEnabled(!userNameEmpty && !passwordEmpty && !mailEmpty && !confirmEmpty && cityCheck);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {

        private String uname;
        private String passwd;
        private String passwd2;
        private String email;
        private String newlimitUser;
        private String newLimitEmail;
        private String newLimitPasswd;
        private String newLimitPasswd2;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            uname = userName.getText().toString().trim();
            passwd = password.getText().toString().trim();
            passwd2 = confirmPassword.getText().toString().trim();
            email= mail.getText().toString().trim();

            Integer userchars = 30 - uname.length();
            if (userchars < 0) {
                limitUserName.setTextColor(Color.rgb(200,0,0));
            }
            else {
                limitUserName.setTextColor(Color.rgb(128,128,128));
            }
            newlimitUser = userchars.toString();

            Integer passchars = 100 - passwd.length();
            if (passchars < 0) {
                limitPass.setTextColor(Color.rgb(200,0,0));
            }
            else {
                limitPass.setTextColor(Color.rgb(128,128,128));
            }
            newLimitPasswd = passchars.toString();

            Integer pass2chars = 100 - passwd2.length();
            if (pass2chars < 0) {
                limitPass2.setTextColor(Color.rgb(200,0,0));
            }
            else {
                limitPass2.setTextColor(Color.rgb(128,128,128));
            }
            newLimitPasswd2 = pass2chars.toString();

            Integer mailchars = 100 - email.length();
            if (mailchars < 0) {
                limitMail.setTextColor(Color.rgb(200,0,0));
            }
            else {
                limitMail.setTextColor(Color.rgb(128,128,128));
            }
            newLimitEmail = mailchars.toString();

            limitUserName.setText(newlimitUser);
            limitMail.setText(newLimitEmail);
            limitPass.setText(newLimitPasswd);
            limitPass2.setText(newLimitPasswd2);

            userNameEmpty = uname.isEmpty();
            passwordEmpty = passwd.isEmpty();
            mailEmpty = email.isEmpty();
            confirmEmpty = passwd2.isEmpty();
        }

        @Override
        public void afterTextChanged(Editable s) {
            userNameCheck = (uname.length() <= 30 && uname.length() >= 3);
            mailCheck = (email.length() <= 100 && email.length() >= 3);
            passwordCheck = (passwd.length() <= 100 && passwd.length() >= 8);
            confirmCheck = passwd.equals(passwd2);

            registrar.setEnabled(!userNameEmpty && !passwordEmpty && !mailEmpty && !confirmEmpty && cityCheck);
        }
    };

    public void mostrar1_register(View view){
        if (mostrandoPass1){
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mostrandoPass1 = false;
        }
        else{
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mostrandoPass1 = true;
        }
    }

    public void mostrar2_register(View view){
        if (mostrandoPass2){
            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mostrandoPass2 = false;
        }
        else{
            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mostrandoPass2 = true;
        }
    }

    public void registrarCuenta(View view){
        if (!userNameCheck) {
            Toast.makeText(getApplicationContext(),"Nombre de usuario inválido.", Toast.LENGTH_LONG).show();
        }
        else if (!mailCheck) {
            Toast.makeText(getApplicationContext(),"Dirección de correo inválida.", Toast.LENGTH_LONG).show();
        }
        else if (!passwordCheck) {
            Toast.makeText(getApplicationContext(),"Contraseña inválida.", Toast.LENGTH_LONG).show();
        }
        else if (!confirmCheck) {
            Toast.makeText(getApplicationContext(),"Contraseñas no coinciden.", Toast.LENGTH_LONG).show();
        }
        else {
            String uname = userName.getText().toString().trim();
            String passwd = password.getText().toString().trim();
            String email= mail.getText().toString().trim();
            String prov = city.getSelectedItem().toString();

            registrar.setEnabled(false);
            doPost(uname,passwd,email,prov);
        }
    }
    private void gestionRegistro (String  estado, String msg){
        if (estado.equals("O")){
            startActivity(new Intent(Registro.this, PantallaPrincipal.class));
        }
        else if (estado.equals("E")){
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
        }
    }

    public String parseParams(String un, String pass, String nom, String ap, String corr, String prov){
        String aux = url;
        aux = aux+"?un="+un+"&pass="+pass+"&cor="+corr+"&na="+nom+"&lna="+ap+"&pr="+prov;
        return aux;
    }

    private void doPost(final String uName, final String passwd, final String email, final String provincia) {


        RequestQueue queue = Volley.newRequestQueue(this);
        String urlPetition = parseParams(uName, passwd,"Nombre","Apellido",email,provincia);

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
                        gestionRegistro(estado, msg);
                        registrar.setEnabled(true);
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
                        gestionRegistro(estado, msg);
                        registrar.setEnabled(true);
                    }
                }
        );
        queue.add(postRequest);
    }


}
