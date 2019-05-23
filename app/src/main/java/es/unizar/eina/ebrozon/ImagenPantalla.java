package es.unizar.eina.ebrozon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import es.unizar.eina.ebrozon.lib.Common;

public class ImagenPantalla extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_pantalla);

        // Recibe como atributo el id de la imagen
        String idImagen = getIntent().getStringExtra("Imagen");

        if (idImagen != null) {
            Common.establecerFotoServidor(getApplicationContext(), idImagen,
                    (ImageView) findViewById(R.id.ImagenPantallaImagen));
        }
    }
}
