package es.unizar.eina.ebrozon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import es.unizar.eina.ebrozon.lib.Ventas;

public class Producto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        HashMap<String, String> venta = (HashMap<String, String>)
                getIntent().getSerializableExtra("Venta");

        ImageView ProductoImagen = (ImageView) findViewById(R.id.ProductoImagen);
        //Set imagen

        TextView ProductoNombre = (TextView) findViewById(R.id.ProductoNombre);
        ProductoNombre.setText(Ventas.getNombre(venta));

        TextView ProductoPrecio = (TextView) findViewById(R.id.ProductoPrecio);
        ProductoPrecio.setText(Ventas.getPrecio(venta));

        TextView ProductoDescripcion = (TextView) findViewById(R.id.ProductoDescripcion);
        ProductoDescripcion.setText(Ventas.getDescricpion(venta));
    }
}
