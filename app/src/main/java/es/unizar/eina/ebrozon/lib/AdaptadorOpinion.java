package es.unizar.eina.ebrozon.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import es.unizar.eina.ebrozon.R;

public class AdaptadorOpinion extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private Context context;
    private static JSONArray opinions = new JSONArray();

    public AdaptadorOpinion (Context context, JSONArray opinions) {
        this.context = context;
        this.opinions = opinions;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.content_opinion, null);

        TextView author = view.findViewById(R.id.opinionUserName);
        TextView content = view.findViewById(R.id.opinionText);
        ImageView star1 = view.findViewById(R.id.opinionStar1);
        ImageView star2 = view.findViewById(R.id.opinionStar2);
        ImageView star3 = view.findViewById(R.id.opinionStar3);
        ImageView star4 = view.findViewById(R.id.opinionStar4);
        ImageView star5 = view.findViewById(R.id.opinionStar5);

        try {
            JSONObject opinion = opinions.getJSONObject(i);
            Integer rating;
            author.setText(opinion.getString("em"));
            content.setText(opinion.getString("con"));
            rating = opinion.getInt("es");
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
                // Abrir el perfil del usuario con el contexto necesario
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
