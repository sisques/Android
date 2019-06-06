package es.unizar.eina.ebrozon.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import es.unizar.eina.ebrozon.R;
import es.unizar.eina.ebrozon.editar_perfil;
import es.unizar.eina.ebrozon.perfil_usuario;

import static android.support.v4.content.ContextCompat.startActivity;

public class AdaptadorOpinion extends BaseAdapter {

    // Adaptador para mostrar una lista de opiniones en el perfil de usuario.

    private static LayoutInflater inflater = null;

    private Context context;    // Contexto del perfil de usuario.
    private static JSONArray opinions = new JSONArray(); // Lista de opiniones del usuario.
    private boolean mine;       // Si es true -> son opiniones hechas por el propio usuario que utiliza la aplicación.

    // Constructor, se le pasa el contexto, la lista y si son suyas.
    public AdaptadorOpinion (Context context, JSONArray opinions, boolean mine) {
        this.context = context;
        this.opinions = opinions;
        this.mine = mine;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    // Asociar cada elemento de cada valoración.
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.content_opinion, null);

        final TextView author = view.findViewById(R.id.opinionUserName);
        final TextView content = view.findViewById(R.id.opinionText);
        ImageView star1 = view.findViewById(R.id.opinionStar1);
        ImageView star2 = view.findViewById(R.id.opinionStar2);
        ImageView star3 = view.findViewById(R.id.opinionStar3);
        ImageView star4 = view.findViewById(R.id.opinionStar4);
        ImageView star5 = view.findViewById(R.id.opinionStar5);

        try {
            JSONObject opinion = opinions.getJSONObject(i);
            Integer rating;
            // Si las valoraciones son hechas por otros usuarios, el usuario que se muestra es el
            // autor, si no se muestra el receptor de la valoración del usuario.
            if (mine) {
                author.setText(opinion.getString("receptor"));
            }
            else {
                author.setText(opinion.getString("emisor"));
            }
            content.setText(opinion.getString("contenido"));
            rating = opinion.getInt("estrellas");
            // Dibujar las estrellas correspondientes la valoración.
            switch (rating) {
                case 1:
                    star1.setImageResource(android.R.drawable.star_big_on);
                    break;
                case 2:
                    star1.setImageResource(android.R.drawable.star_big_on);
                    star2.setImageResource(android.R.drawable.star_big_on);
                    break;
                case 3:
                    star1.setImageResource(android.R.drawable.star_big_on);
                    star2.setImageResource(android.R.drawable.star_big_on);
                    star3.setImageResource(android.R.drawable.star_big_on);
                    break;
                case 4:
                    star1.setImageResource(android.R.drawable.star_big_on);
                    star2.setImageResource(android.R.drawable.star_big_on);
                    star3.setImageResource(android.R.drawable.star_big_on);
                    star4.setImageResource(android.R.drawable.star_big_on);
                    break;
                case 5:
                    star1.setImageResource(android.R.drawable.star_big_on);
                    star2.setImageResource(android.R.drawable.star_big_on);
                    star3.setImageResource(android.R.drawable.star_big_on);
                    star4.setImageResource(android.R.drawable.star_big_on);
                    star5.setImageResource(android.R.drawable.star_big_on);
                    break;
            }
        }
        catch(Exception e) {
            // ...
        }

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Va al perfil del usuario de la valoración al hacer click en su nombre.
                Intent intent = new Intent(context, perfil_usuario.class);
                intent.putExtra("username", author.getText());
                context.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return opinions.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return opinions.getJSONObject(i);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
