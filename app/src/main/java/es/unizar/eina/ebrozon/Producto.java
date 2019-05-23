package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.Ventas;
import es.unizar.eina.ebrozon.lib.compra;

public class Producto extends AppCompatActivity {
    private Ventas productos;
    private Integer posVenta;

    private String un; // usuario
    private String vendedorUn; // vendedor
    private String precio = "";
    private String numProd = ""; // id producto

    Switch ProductoSeguir;
    private boolean siguiendo; // Siguiendo producto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        productos = new Ventas();

        // Recibe como atributo la posición de la venta
        posVenta = (Integer) getIntent().getSerializableExtra("Venta");

        final SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        un = sharedpreferences.getString(Common.un, null);

        // Barra de vendedor
        TextView vendedorUsuario = (TextView) findViewById(R.id.ProductoVendedorUsuario);
        vendedorUn = null;
        try {
            vendedorUn = productos.getUsuarioVenta(posVenta);
            vendedorUsuario.setText(vendedorUn);
        } catch (Exception ignored) { }

        ImageView vendedorImagen = (ImageView) findViewById(R.id.ProductoVendedorImagen);
        try {
            Common.establecerFotoUsuarioServidor(getApplicationContext(), vendedorUn, vendedorImagen);
        } catch (Exception ignored) { }

        ImageButton vendedorChat = (ImageButton) findViewById(R.id.ProductoBotonChat);
        if (vendedorUn.equals(un)) {
            vendedorChat.setVisibility(View.INVISIBLE);
            vendedorChat.setClickable(false);
        }
        else {
            vendedorChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Producto.this, Chat.class);
                    intent.putExtra("usuarioComunica", vendedorUn);
                    startActivity(intent);
                }
            });
        }

        ImageButton productoEditar = (ImageButton) findViewById(R.id.ProductoBotonEditar);
        if (vendedorUn.equals(un)) {
            productoEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: editar producto
                }
            });
        }
        else {
            productoEditar.setVisibility(View.INVISIBLE);
            productoEditar.setClickable(false);
        }

        ImageButton productoBorrar = (ImageButton) findViewById(R.id.ProductoBotonBorrar);
        if (vendedorUn.equals(un)) {
            productoBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: borrar producto
                }
            });
        }
        else {
            productoBorrar.setVisibility(View.INVISIBLE);
            productoBorrar.setClickable(false);
        }


        // TODO: Preparar para varias imagenes
        ImageView ProductoImagen = (ImageView) findViewById(R.id.ProductoImagen);
        Bitmap result = productos.getImagenResumen(posVenta);
        if (result != null) {
            ProductoImagen.setImageBitmap(result);
        }

        TextView ProductoNombre = (TextView) findViewById(R.id.ProductoNombre);
        try {
            ProductoNombre.setText(productos.getNombreVenta(posVenta));
        } catch (Exception ignored) { }

        TextView ProductoPrecio = (TextView) findViewById(R.id.ProductoPrecio);
        try {
            precio = productos.getPrecioVenta(posVenta);
            ProductoPrecio.setText(precio);
        } catch (Exception ignored) { }

        TextView ProductoCiudad = (TextView) findViewById(R.id.ProductoCiudad);
        try {
            ProductoCiudad.setText(productos.getCiudadVenta(posVenta));
        } catch (Exception ignored) { }

        TextView ProductoDescripcion = (TextView) findViewById(R.id.ProductoDescripcion);
        try {
            ProductoDescripcion.setText(productos.getDescripcionVenta(posVenta));
        } catch (Exception ignored) { }

        TextView ProductoCategoria = (TextView) findViewById(R.id.ProductoCategoria);
        try {
            ProductoCategoria.setText(productos.getCategoriaVenta(posVenta));
        } catch (Exception ignored) { }

        try {
            numProd = productos.getIdVenta(posVenta);

            // Switch seguir producto
            ProductoSeguir = (Switch) findViewById(R.id.ProductoSeguir);
            ProductoSeguir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (siguiendo != isChecked) {
                        siguiendo = isChecked;
                        seguirProducto();
                    }
                }
            });

            comprobarSiguiendoProducto();
        } catch (Exception ignored) { }


        Button oferta = findViewById(R.id.ProductoBotonOferta);
        if (vendedorUn.equals(un)) {
            oferta.setVisibility(View.INVISIBLE);
            oferta.setClickable(false);
        } else{
            oferta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //compra.ofertar( posVenta, Producto.this, precio,  sharedpreferences);
                }
            });
        }

        Button comprar = findViewById(R.id.ProductoBotonCompra);
        if (vendedorUn.equals(un)) {
            comprar.setVisibility(View.INVISIBLE);
            comprar.setClickable(false);
        } else{
            comprar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compra.ofertar( numProd, Producto.this,precio,  sharedpreferences);
                    productos.eliminarVenta(posVenta);
                    setResult(Common.RESULTADO_OK, new Intent());
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Common.RESULTADO_NOK, new Intent());
        finish();
    }

    private void comprobarSiguiendoProducto() {
        siguiendo = false;
        String url = Common.url + "/listarSeguimientosUsuario?un=" + un;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if (!response.equals("[]")) {
                            try {
                                JSONArray seguimiento = new JSONArray(response);
                                for (int i=0; i<seguimiento.length(); i++) {
                                    if (seguimiento.getJSONObject(i).getString("nventa").equals(numProd)) {
                                        siguiendo = true;
                                        break;
                                    }
                                }
                            } catch (Exception ignored) { }
                        }

                        ProductoSeguir.setChecked(siguiendo);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recibir la lista de productos");
                        ProductoSeguir.setChecked(siguiendo);
                    }
                }
        );
        queue.add(postRequest);
    }

    private void seguirProducto() {
        String url = Common.url;
        if (siguiendo) {
            url += "/seguirProducto";
        }
        else {
            url += "/dejarSeguirProducto";
        }
        url += "?un=" + un + "&nv=" + numProd;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
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
                        Log.d("Error.Response", "Error al recibir la lista de productos");
                        siguiendo = !siguiendo;
                        ProductoSeguir.setChecked(siguiendo);
                    }
                }
        );
        queue.add(postRequest);
    }
}
