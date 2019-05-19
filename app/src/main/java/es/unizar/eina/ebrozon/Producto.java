package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.Ventas;

public class Producto extends AppCompatActivity {
    private String un; // usuario
    private String vendedorUn; // vendedor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        Ventas productos = new Ventas();

        // Recibe como atributo la posición de la venta
        Integer posVenta = (Integer) getIntent().getSerializableExtra("Venta");

        SharedPreferences sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
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
        //Bitmap imagen = productos.getImagenResumen(posVenta);
        ImageView ProductoImagen = (ImageView) findViewById(R.id.ProductoImagen);
        //ProductoImagen.setImageBitmap(imagen);
        Common.establecerFotoServidor(getApplicationContext(), productos.getImagenResumen(posVenta),
                ProductoImagen);

        TextView ProductoNombre = (TextView) findViewById(R.id.ProductoNombre);
        try {
            ProductoNombre.setText(productos.getNombreVenta(posVenta));
        } catch (Exception ignored) { }

        TextView ProductoPrecio = (TextView) findViewById(R.id.ProductoPrecio);
        try {
            ProductoPrecio.setText(productos.getPrecioVenta(posVenta));
        } catch (Exception ignored) { }

        TextView ProductoCiudad = (TextView) findViewById(R.id.ProductoCiudad);
        try {
            ProductoCiudad.setText(productos.getCiudadVenta(posVenta));
        } catch (Exception ignored) { }

        TextView ProductoDescripcion = (TextView) findViewById(R.id.ProductoDescripcion);
        try {
            ProductoDescripcion.setText(productos.getDescripcionVenta(posVenta));
        } catch (Exception ignored) { }
    }
}
