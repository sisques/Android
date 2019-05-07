package es.unizar.eina.ebrozon;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.Ventas;

public class Producto extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        Ventas productos = new Ventas();

        // Recibe como atributo la posición de la venta
        Integer posVenta = (Integer) getIntent().getSerializableExtra("Venta");

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

        TextView ProductoDescripcion = (TextView) findViewById(R.id.ProductoDescripcion);
        try {
            ProductoDescripcion.setText(productos.getDescripcionVenta(posVenta));
        } catch (Exception ignored) { }
    }
}
