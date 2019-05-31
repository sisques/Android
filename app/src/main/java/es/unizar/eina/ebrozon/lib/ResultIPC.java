package es.unizar.eina.ebrozon.lib;

import java.util.List;

public class ResultIPC {
    /**
     * Clase utilizada para pasar datos de gran tamaño entre Intents, principalmente usada para
     * pasar las imágenes de la primera etapa de subida de producto a la ultima.
     */
    private static ResultIPC instance;

    public synchronized static ResultIPC get() {
        if (instance == null) {
            instance = new ResultIPC ();
        }
        return instance;
    }

    private int sync = 0;

    private List<Object> largeData;
    public int setLargeData(List<Object> largeData) {
        this.largeData = largeData;
        return ++sync;
    }

    public List<Object> getLargeData(int request) {
        return (request == sync) ? largeData : null;
    }
}
