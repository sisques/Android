package es.unizar.eina.ebrozon.lib;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Ventas {
    // Son static para mantener la misma lista sin duplicar
    private static List<JSONObject> ventas = new ArrayList<JSONObject>();
    private static List<HashMap<String, Object>> resumenes
            = new ArrayList<HashMap<String, Object>>(); // Información duplicada pero muestra más rápido
    private static Integer idMax = 0; // id máxima entre todas las ventas

    private Context contextPantalla = null;

    private final static String[] atributos =
            {"identificador", "usuario", "fechainicio", "fechaventa", "producto", "descripcion",
             "precio", "preciofinal", "comprador", "fechapago", "tienearchivo", "activa",
             "es_subasta", "ciudad", "provincia", "user", "archivos","categoria"};

    public void clear() {
        ventas.clear();
        resumenes.clear();
        idMax = 0;
    }

    public void anyadirVentas(JSONArray productos) {
        HashMap<String, Object> resumen;
        JSONObject producto;
        String aux;
        ImagenFinal imagen;
        Integer id;

        for (int i=0; i<productos.length(); i++) {
            try {
                producto = productos.getJSONObject(i);
                ventas.add(producto);

                id = producto.getInt(atributos[0]);
                if (id > idMax)
                    idMax = id;

                resumen = new HashMap<String, Object>();
                resumen.put(atributos[4], producto.get(atributos[4]).toString());
                resumen.put(atributos[6], producto.get(atributos[6]).toString());
                resumen.put(atributos[5], producto.get(atributos[5]).toString());

                aux = producto.get(atributos[13]).toString();
                if (!aux.equals(""))
                    resumen.put(atributos[13], aux);
                else
                    resumen.put(atributos[13], producto.get(atributos[14]).toString());

                aux = producto.getJSONArray(atributos[16]).get(0).toString();
                imagen = new ImagenFinal();
                resumen.put("imagen", imagen);
                if (!aux.equals("")) {
                    Common.obtenerFotoServidor(this.contextPantalla, aux, (ImagenFinal) resumen.get("imagen"));
                }
                else {
                    Common.obtenerFotoServidor(this.contextPantalla, "", (ImagenFinal) resumen.get("imagen"));
                }
                resumenes.add(resumen);
            }
            catch (Exception ignored) { }
        }
    }

    public void setContext(Context c) {
        this.contextPantalla = c;
    }

    public Integer getIdMax() {
        return idMax;
    }

    public List<HashMap<String, Object>> getResumenes() {
        return resumenes;
    }

    public String[] getResumenAtributos() {
        String[] resumenAtributos = new String[5];
        resumenAtributos[0] = atributos[4];
        resumenAtributos[1] = atributos[6];
        resumenAtributos[2] = atributos[5];
        resumenAtributos[3] = atributos[13];
        resumenAtributos[4] = "imagen";

        return resumenAtributos;
    }

    public String[] getAtributos() {
        return atributos;
    }

    public JSONObject getVenta(int index) {
        return ventas.get(index);
    }

    public HashMap<String, Object> getResumen(int index) {
        return resumenes.get(index);
    }

    public String getIdVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[0]).toString();
    }

    public String getUsuarioVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[1]).toString();
    }

    public String getFechainicioVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[2]).toString();
    }

    public String getFechaventaVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[3]).toString();
    }

    public String getNombreVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[4]).toString();
    }

    public String getDescripcionVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[5]).toString();
    }

    public String getPrecioVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[6]).toString();
    }

    public String getPreciofinalVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[7]).toString();
    }

    public String getCompradorVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[8]).toString();
    }

    public String getFechapagoVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[9]).toString();
    }

    public String getTienearchivoVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[10]).toString();
    }

    public String getActivaVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[11]).toString();
    }

    public String getEsSubastaVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[12]).toString();
    }

    public String getCiudadVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[13]).toString();
    }

    public String getProvinciaVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[14]).toString();
    }

    public String getUserVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[15]).toString();
    }

    public String getImagenVenta(int index) throws JSONException {
        return ventas.get(index).getJSONArray(atributos[16]).get(0).toString();
    }

    public ImagenFinal getImagenResumen(int index) {
        return (ImagenFinal) resumenes.get(index).get("imagen");
    }

    public String getCategoriaVenta(int index) throws JSONException {
        return ventas.get(index).get(atributos[17]).toString();
    }
}
