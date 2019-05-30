package es.unizar.eina.ebrozon;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Boolean seguimientos; // true: Pantalla de seguimientos; false: Listado de productos normal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        productos = new Ventas();


        // Recibe como atributo la posición de la venta
        posVenta = getIntent().getIntExtra("Venta", -1);
        if (posVenta == -1) finish();
        seguimientos = getIntent().getBooleanExtra("Seguimientos", false);

        final SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        un = sharedpreferences.getString(Common.un, null);

        // Barra de vendedor
        TextView vendedorUsuario = (TextView) findViewById(R.id.ProductoVendedorUsuario);
        vendedorUn = null;
        try {
            vendedorUn = productos.getUsuarioVenta(posVenta);
            vendedorUsuario.setText(vendedorUn);
            vendedorUsuario.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Producto.this, perfil_usuario.class);
                    intent.putExtra("username", vendedorUn);
                    startActivity(intent);
                }
            } );
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
                    Intent intent = new Intent(Producto.this, SubirProd1_3.class);
                    intent.putExtra("posVenta", posVenta);
                    startActivity(intent);
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
                    borrarProducto();
                    setResult(Common.RESULTADO_OK, new Intent());
                    finish();
                }
            });
        }
        else {
            productoBorrar.setVisibility(View.INVISIBLE);
            productoBorrar.setClickable(false);
        }

        // Imágenes
        final ImageView[] ProductoImagenes = {
                (ImageView) findViewById(R.id.ProductoImagen),
                (ImageView) findViewById(R.id.ProductoImagen2),
                (ImageView) findViewById(R.id.ProductoImagen3),
                (ImageView) findViewById(R.id.ProductoImagen4)
        };

        Bitmap result = productos.getImagenResumen(posVenta);
        if (result != null) {
            ProductoImagenes[0].setImageBitmap(result);
        }

        ProductoImagenes[1].setVisibility(View.INVISIBLE);
        ProductoImagenes[2].setVisibility(View.INVISIBLE);
        ProductoImagenes[3].setVisibility(View.INVISIBLE);

        // Imágenes en grande
        try {
            final JSONArray imagenes = productos.getIdImagenesVenta(posVenta);
            for (int i=0; i<imagenes.length(); i++) {
                try {
                    if (i > 0) {
                        Common.establecerFotoServidor(getApplicationContext(), imagenes.getString(i),
                                ProductoImagenes[i]);
                        ProductoImagenes[i].setVisibility(View.VISIBLE);
                    }

                    final int finalI = i;
                    ProductoImagenes[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent intent = new Intent(Producto.this, ImagenPantalla.class);
                                intent.putExtra("Imagen", imagenes.getString(finalI));
                                startActivity(intent);
                            } catch (Exception ignored) { }
                        }
                    });
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }


        TextView ProductoNombre = (TextView) findViewById(R.id.ProductoNombre);
        String nombreProd="";
        try {
            nombreProd = productos.getNombreVentaLargo(posVenta);
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

        TextView ProductoTipo = (TextView) findViewById(R.id.ProductoTipo);
        try {
            if (productos.getEsSubastaVenta(posVenta).equals("0")) {
                ProductoTipo.setText("VENTA");
            }
            else {
                ProductoTipo.setText("SUBASTA");
            }
        } catch (Exception ignored) { }

        TextView ProductoDistancia = (TextView) findViewById(R.id.ProductoDistancia);
        try {
            if (productos.getDistanciaVenta(posVenta).equals("999999.0")) {
                ProductoDistancia.setVisibility(View.INVISIBLE);
            }
            else {
                ProductoDistancia.setText(productos.getDistanciaVenta(posVenta));
            }
        } catch (Exception ignored) { }


        try {
            numProd = productos.getIdVenta(posVenta);

            // Switch seguir producto
            ProductoSeguir = (Switch) findViewById(R.id.ProductoSeguir);
            if (vendedorUn.equals(un)) {
                ProductoSeguir.setVisibility(View.INVISIBLE);
                ProductoSeguir.setClickable(false);
            }
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
        String subasta = "0";
        String pinicial = "";
        String pactual = "";
        String finSubasta="";
        try{
            subasta = productos.getEsSubastaVenta(posVenta);
            pinicial = productos.getPrecioInicial(posVenta);
            pactual = productos.getPujaActual(posVenta);
            String aux = productos.getFechaFin(posVenta);
            String anyo = aux.substring(0,4);
            String mes = aux.substring(5,7);
            String dia = aux.substring(8,10);
            String hora = String.valueOf(Integer.valueOf(aux.substring(11,13)) + 1);
            String minuto = aux.substring(14,16);
            finSubasta = dia + "/" + mes + "/" + anyo + " " + hora + ":" +minuto;


        } catch(Exception ignored){}


        TextView productoFecha = findViewById(R.id.ProductoFecha);


        if(subasta.equals("0")){
            productoFecha.setVisibility(View.GONE);
        } else {
            productoFecha.setText(finSubasta);
            ProductoPrecio.setText(pactual);
            productoFecha.setTextSize(15);
            ProductoPrecio.setTextSize(15);
            ProductoCiudad.setTextSize(15);
            oferta.setText(R.string.puja);
        }


        if (vendedorUn.equals(un)) {
            oferta.setVisibility(View.INVISIBLE);
            oferta.setClickable(false);
        } else{
            final String finalSubasta = subasta;
            final String finalPinicial = pinicial;
            final String finalPactual = pactual;
            oferta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupProducto popupProducto = new PopupProducto(Producto.this, numProd, precio,
                            sharedpreferences, finalSubasta, finalPinicial, finalPactual);
                    popupProducto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupProducto.show();

                }
            });
        }

        Button comprar = findViewById(R.id.ProductoBotonCompra);
        if (vendedorUn.equals(un)) {
            comprar.setVisibility(View.INVISIBLE);
            comprar.setClickable(false);
        } else{
            final String finalNombreProd = nombreProd;
            comprar.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compra.ofertar( numProd, Producto.this,precio,  sharedpreferences);

                    productos.eliminarVenta(posVenta);


                    Intent intent = new Intent(Producto.this, Chat.class);
                    mensajeExterno("Hola, estoy interesado en tu producto " + finalNombreProd+".", un, vendedorUn);
                    intent.putExtra("usuarioComunica", vendedorUn);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (!siguiendo && seguimientos) {
            productos.eliminarVenta(posVenta);
            setResult(Common.RESULTADO_OK, new Intent());
        }
        else {
            setResult(Common.RESULTADO_NOK, new Intent());
        }
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

    private void borrarProducto() {
        String url = Common.url + "/desactivarVenta?id=" + numProd;
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

    private void mensajeExterno(String mensaje, String origen, String destino) {
        String urlPetition = Common.url + "/mandarMensaje?em=" + origen + "&re=" + destino
                + "&con=" + mensaje;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
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
                        Log.d("Error.Response", "Error al recibir la lista de ofertas");
                    }
                }
        );
        queue.add(postRequest);
    }





}
