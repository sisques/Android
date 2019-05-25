package es.unizar.eina.ebrozon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import es.unizar.eina.ebrozon.R;
import es.unizar.eina.ebrozon.lib.compra;

public class PopupProducto extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public EditText importe;

    String idVenta;
    String precio;
    SharedPreferences sp;
    String esSubasta;
    String precioInicial;
    String pujaActual;

    public PopupProducto(Activity a, String iV, String p, SharedPreferences sharedP, String subasta, String pinicial, String pactual) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        idVenta = iV;
        precio = p;
        sp = sharedP;
        esSubasta = subasta;
        precioInicial = pinicial;
        pujaActual = pactual;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_popup);
            importe = (EditText) findViewById(R.id.importe);
            yes = (Button) findViewById(R.id.ofrecerCantidad);
            no = (Button) findViewById(R.id.cancelar);
            yes.setOnClickListener(this);
            yes.setEnabled(false);
            no.setOnClickListener(this);
            importe.setHint("El vendedor pide " + precio);
            importe.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String imp = importe.getText().toString().trim();
                    Boolean correcto = true;
                    if(esSubasta.equals("1")) {correcto = Float.valueOf(pujaActual) < Float.valueOf(imp); }
                    yes.setEnabled(!imp.isEmpty() && correcto);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            if (esSubasta.equals("1")){
                importe.setHint("La puja máxima actual es " + pujaActual);
                yes.setText(R.string.pujaOfrecida);
            }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ofrecerCantidad:
                String oferta = importe.getText().toString();
                if (esSubasta.equals("0")) {
                    compra.ofertar(idVenta, this.c, oferta, sp);
                } else {
                    compra.pujar(idVenta, this.c, oferta, sp);
                }
                //c.finish();
                break;
            case R.id.cancelar:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}