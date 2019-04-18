package es.unizar.eina.ebrozon.lib;

//import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.unizar.eina.ebrozon.R;

public class Ventas {
    private List<HashMap<String, String>> ventas;
    private List<HashMap<String, String>> resumenes; // Información duplicada pero muestra más rápido

    private final String[] atributos =
            {"identificador", "usuario", "fechainicio", "fechaventa", "producto", "descripcion",
             "precio", "preciofinal", "comprador", "fechapago", "tienearchivo", "activa",
             "es_subasta", "ciudad", "user"};

    public Ventas() {
        ventas = new ArrayList<HashMap<String, String>>();
        resumenes = new ArrayList<HashMap<String, String>>();
    }

    public void anyadirVenta(String[] producto) {
        if (atributos.length != producto.length) System.err.println("WARNING VENTA");

        HashMap<String, String> venta = new HashMap<String, String>();
        HashMap<String, String> resumen = new HashMap<String, String>();

        for (int i=0; i<atributos.length; i++) {
            venta.put(atributos[i], producto[i]);
        }
        resumen.put(atributos[4], producto[4]);
        resumen.put(atributos[6], producto[6]);
        resumen.put(atributos[5], producto[5]);
        //TODO: Falta imagen
        resumen.put("imagen", Integer.toString(R.drawable.logo));

        ventas.add(venta);
        resumenes.add(resumen);
    }

    public List<HashMap<String, String>> getVentas() {
        return ventas;
    }

    public List<HashMap<String, String>> getResumenes() {
        return resumenes;
    }

    public String[] getResumenAtributos() {
        String[] resumenAtributos = new String[4];
        resumenAtributos[0] = atributos[4];
        resumenAtributos[1] = atributos[6];
        resumenAtributos[2] = atributos[5];
        //TODO: Falta imagen
        resumenAtributos[3] = "imagen";

        return resumenAtributos;
    }

    public String[] getAtributos() {
        return atributos;
    }
}
