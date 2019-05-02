package es.unizar.eina.ebrozon;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import es.unizar.eina.ebrozon.lib.Ventas;

public class Producto extends AppCompatActivity {
    private Ventas productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        productos = new Ventas();

        Integer posVenta = (Integer) getIntent().getSerializableExtra("Venta");

        // Recibe como atributo la posición de la venta
        HashMap<String, Object> venta = productos.getVenta(posVenta);

        // TODO: Preparar para varias imagenes
        Bitmap imagen = Ventas.getImagen(productos.getResumen(posVenta));
        ImageView ProductoImagen = (ImageView) findViewById(R.id.ProductoImagen);
        ProductoImagen.setImageBitmap(imagen);

        TextView ProductoNombre = (TextView) findViewById(R.id.ProductoNombre);
        ProductoNombre.setText(Ventas.getNombre(venta));

        TextView ProductoPrecio = (TextView) findViewById(R.id.ProductoPrecio);
        ProductoPrecio.setText(Ventas.getPrecio(venta));

        TextView ProductoDescripcion = (TextView) findViewById(R.id.ProductoDescripcion);
        ProductoDescripcion.setText(Ventas.getDescricpion(venta));
    }
}
