package es.unizar.eina.ebrozon.lib;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Ventas {
    // Son static para mantener la misma lista sin duplicar
    private static List<HashMap<String, Object>> ventas
            = new ArrayList<HashMap<String, Object>>();
    private static List<HashMap<String, Object>> resumenes
            = new ArrayList<HashMap<String, Object>>(); // Información duplicada pero muestra más rápido

    private static ArrayList<Bitmap> imagenDefault = null;

    private final static String[] atributos =
            {"identificador", "usuario", "fechainicio", "fechaventa", "producto", "descripcion",
             "precio", "preciofinal", "comprador", "fechapago", "tienearchivo", "activa",
             "es_subasta", "ciudad", "user", "imagenes"};

    public void clear() {
        ventas.clear();
        resumenes.clear();
    }

    public void anyadirVenta(String[] producto, ArrayList<Bitmap> imagenes) {
        if (atributos.length != producto.length) System.err.println("WARNING VENTA");

        HashMap<String, Object> venta = new HashMap<String, Object>();
        HashMap<String, Object> resumen = new HashMap<String, Object>();

        for (int i=0; i<producto.length; i++) {
            venta.put(atributos[i], producto[i]);
        }
        if (imagenes != null && imagenes.size() > 0)
            venta.put(atributos[atributos.length-1], imagenes);
        else
            venta.put(atributos[atributos.length-1], imagenDefault);

        resumen.put(atributos[4], producto[4]);
        resumen.put(atributos[6], producto[6] + " €");
        resumen.put(atributos[5], producto[5]);
        if (imagenes != null && imagenes.size() > 0)
            resumen.put("imagen", imagenes.get(0));
        else
            resumen.put("imagen", imagenDefault.get(0));

        ventas.add(venta);
        resumenes.add(resumen);
    }

    public void setImagenDefault(Bitmap imagen) {
        imagenDefault = new ArrayList<Bitmap>();
        imagenDefault.add(imagen);
    }

    public List<HashMap<String, Object>> getVentas() {
        return ventas;
    }

    public List<HashMap<String, Object>> getResumenes() {
        return resumenes;
    }

    public String[] getResumenAtributos() {
        String[] resumenAtributos = new String[4];
        resumenAtributos[0] = atributos[4];
        resumenAtributos[1] = atributos[6];
        resumenAtributos[2] = atributos[5];
        resumenAtributos[3] = "imagen";

        return resumenAtributos;
    }

    public String[] getAtributos() {
        return atributos;
    }

    public HashMap<String, Object> getVenta(int index) {
        return ventas.get(index);
    }

    public static String getId(HashMap<String, Object> v) {
        return (String) v.get(atributos[0]);
    }

    public static String getNombre(HashMap<String, Object> v) {
        return (String) v.get(atributos[4]);
    }

    public static String getPrecio(HashMap<String, Object> v) {
        return (String) v.get(atributos[6]) + " €";
    }

    public static String getDescricpion(HashMap<String, Object> v) {
        return (String) v.get(atributos[5]);
    }

    public static Bitmap getImagen(HashMap<String, Object> v) {
        return (Bitmap) v.get("imagen");
    }

    public static ArrayList<Bitmap> getImagenes(HashMap<String, Object> v) {
        return (ArrayList<Bitmap>) v.get(atributos[atributos.length-1]);
    }
}
