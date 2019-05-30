package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import es.unizar.eina.ebrozon.lib.Common;

public class CambiarContra extends AppCompatActivity {

    String urlChangePasswd = "https://protected-caverns-60859.herokuapp.com/cambiarContrasena";

    SharedPreferences sharedpreferences;
    private String currentUser;
    private String currentPasswd;

    private EditText oldpasswd;
    private EditText newpasswd;
    private EditText confirmpasswd;

    private boolean seeingOld = false;
    private boolean seeingNew = false;
    private boolean seeingConfirm = false;

    private boolean checkOld = false;
    private boolean checkNew = false;
    private boolean checkConfirm = false;

    private Button editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contra);

        oldpasswd = findViewById(R.id.passwordOldText);
        newpasswd = findViewById(R.id.passwordNewText);
        confirmpasswd = findViewById(R.id.passwordConfirmText);
        editPassword = findViewById(R.id.passwordSend);

        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        currentUser = sharedpreferences.getString(Common.un, null);
        currentPasswd = sharedpreferences.getString(Common.pass, null);

        editPassword.setEnabled(false);
        oldpasswd.addTextChangedListener(passWatcher);
        newpasswd.addTextChangedListener(passWatcher);
        confirmpasswd.addTextChangedListener(passWatcher);
    }

    private TextWatcher passWatcher = new TextWatcher() {

        private String oldText;
        private String newText;
        private String confirmText;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            oldText = oldpasswd.getText().toString();
            newText = newpasswd.getText().toString();
            confirmText = confirmpasswd.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkOld = oldText.equals(currentPasswd);
            checkNew = (newText.length() <= 100) && (newText.length() >= 8) && newText.matches("[a-zA-Z0-9_]+");
            checkConfirm = confirmText.equals(newText);
            editPassword.setEnabled(checkOld && checkConfirm && checkNew);
        }
    };

    public void verContraActual(View view) {
        if (seeingOld) {
            oldpasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            seeingOld = false;
        }
        else {
            oldpasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            seeingOld = true;
        }
    }

    public void verContraNueva(View view) {
        if (seeingNew) {
            newpasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            seeingNew = false;
        }
        else {
            newpasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            seeingNew = true;
        }
    }

    public void verContraConfirmar(View view) {
        if (seeingConfirm) {
            confirmpasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            seeingConfirm = false;
        }
        else {
            confirmpasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            seeingConfirm = true;
        }
    }

    public void cambiarcontra(View view) {
        editPassword.setEnabled(false);
        final String password = newpasswd.getText().toString();
        final String oldpassword = oldpasswd.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlChangePasswd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(CambiarContra.this, "Contraseña cambiada.", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Common.pass, password);
                        editor.commit();
                        editPassword.setEnabled(true);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(CambiarContra.this, "Se ha producido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("un",currentUser);
                params.put("oldpass",oldpassword);
                params.put("newpass",password);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
