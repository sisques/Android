package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.content.SharedPreferences;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.Ventas;

import static es.unizar.eina.ebrozon.lib.Common.StringToBitMap;


public class PantallaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int ACT_FILTROS = 0;

    SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeLayout;

    private String un; // usuario
    private String cor; // correo
    private String im; // imagen perfil

    private Ventas productos; // Productos y resúmenes

    private Boolean misProductos; // Para ver productos en venta

    // Listar productos
    private String provincia; // Provincia utilizada; "" = todas las provincias
    private Integer orden; // Tipo de ordenación
    private String categoria; // Categoría utilizada; "" = todas las categorías
    private Integer tipoVenta; // Tipo de venta; -1 = todos los tipos
    private Integer precioMinimo; // Precio mínimo; -1 = sin precio mínimo
    private Integer precioMaximo; // Precio máximo; -1 = sin precio máximo

    // Buscar productos
    private Boolean buscar; // Opción de búsqueda
    private String busqueda; // Palabra a buscar

    private TextView menuNombre;
    private TextView menuCorreo;
    private ImageView menuImagen;
    private Button botonFiltros;
    private ListView listaProductosListView;
    private SearchView menuBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productos = new Ventas();
        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        misProductos = false;

        provincia = ""; // Al principio se listan todos los productos
        orden = 0;
        categoria = ""; // Al principio se listan todas las categorias
        tipoVenta = -1;
        precioMinimo = -1;
        precioMaximo = -1;

        buscar = false;
        busqueda = null;

        // Filtros
        botonFiltros = findViewById(R.id.principal_filtros);
        botonFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PantallaPrincipal.this, Filtros.class);
                i.putExtra("ProvinciaFiltros", provincia);
                i.putExtra("OrdenFiltros", orden);
                i.putExtra("CategoriaFiltros", categoria);
                i.putExtra("TipoVentaFiltros", tipoVenta);
                i.putExtra("PrecioMinimoFiltros", precioMinimo);
                i.putExtra("PrecioMaximoFiltros", precioMaximo);
                startActivityForResult(i, ACT_FILTROS);
            }
        });

        // Subir producto
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.principal_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PantallaPrincipal.this, SubirProd1_3.class));
            }
        });

        // Menu hamburguesa
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawerMenu);
        navigationView.setNavigationItemSelectedListener(this);

        // Parte de arriba del menu hamburguesa
        View menuArriba = navigationView.getHeaderView(0);
        menuNombre = (TextView) menuArriba.findViewById(R.id.menuNombre);
        menuCorreo = (TextView) menuArriba.findViewById(R.id.menuCorreo);
        menuImagen = (ImageView) menuArriba.findViewById(R.id.menuImagen);

        // Refresh
        swipeLayout = findViewById(R.id.listaProductosRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // Cada vez que se realiza el gesto para refrescar
                productos.clear();
                recuperarUsuario();
                listarProductos();
                swipeLayout.setRefreshing(false);
            }
        });

        // Barra de búsqueda
        menuBusqueda = findViewById(R.id.principal_busqueda);
        menuBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { // Cuando se le da a buscar
                if (s.length() > 1) {
                    busqueda = s;
                    buscar = true;
                    productos.clear();
                    listarProductos();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) { // Mientras se escribe
                return false;
            }
        });
        menuBusqueda.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() { // Cuando se le da a cerrar
                busqueda = null;
                buscar = false;
                productos.clear();
                listarProductos();
                return false;
            }
        });

        productos.clear();
        recuperarUsuario();
        listarProductos();
    }

    private void recuperarUsuario() {
        RequestQueue queue = Volley.newRequestQueue(this);

        un = sharedpreferences.getString(Common.un, null);

        String urlPetition = Common.url+"/recuperarUsuario?un="+un;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONObject usuario = new JSONObject(response);
                            cor = usuario.getString("correo");
                            im = usuario.getString("urlArchivo");

                            Common.establecerFotoServidor(getApplicationContext(), im, menuImagen);

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Common.cor, cor);
                            editor.putString(Common.im, im);
                            editor.commit();
                        }
                        catch(Exception e) {
                            cor = sharedpreferences.getString(Common.cor, null);
                            im = sharedpreferences.getString(Common.im, null);
                        }
                        menuNombre.setText(un);
                        menuCorreo.setText(cor);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recuperar usuario");
                    }
                }
        );
        queue.add(postRequest);
    }

    private void listarProductos() { // TODO: Añadir listado para más de 25 productos y más filtros
        if (misProductos) {
            listarProductosUsuario(un);
        }
        else {
            String url = Common.url + "/listarProductos?met=";

            switch (orden) {
                case 1 :
                    url += "Fecha asc";
                    break;
                case 2 :
                    url += "Precio asc";
                    break;
                case 3 :
                    url += "Precio des";
                    break;
                case 4 :
                    url += "Popularidad";
                    break;
                case 5 :
                    url += "Coincidencias";
                    break;
                case 6 :
                    url += "Valoraciones";
                    break;
                default :
                    url += "Fecha des";
                    break;
            }

            if (tipoVenta >= 0) {
                url += "&tp=" + tipoVenta;
            }

            if (!provincia.equals("")) {
                url += "&pr=" + provincia;
            }

            if (!categoria.equals("")) {
                url += "&cat=" + categoria;
            }

            if (buscar && busqueda != null && busqueda.length() > 1) {
                url += "&ets=" + busqueda;
            }

            if (productos.getIdUltimo() > 0) {
                url += "&id=" + productos.getIdUltimo();
            }

            gestionarPeticionListar(url);
        }
    }

    private void listarProductosUsuario(String usuario) {
        gestionarPeticionListar(Common.url + "/listarProductosUsuario?id=" +
                productos.getIdUltimo() + "&un=" + usuario);
    }

    private void gestionarPeticionListar(String urlPetition) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if (!response.equals("[]")) {
                            try {
                                productos.anyadirVentas(new JSONArray(response));
                                gestionarListarTrasPeticion();
                                descargarImagenesPrincipales();
                            } catch (Exception ignored) { }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recibir la lista de productos");
                    }
                }
        );
        queue.add(postRequest);
    }

    private void gestionarListarTrasPeticion() {
        String[] from = productos.getResumenAtributos();
        int[] to = {R.id.ProductoResumenTitulo, R.id.ProductoResumenPrecio,
                R.id.ProductoResumenDescripcion, R.id.ProductoResumenCiudad, R.id.ProductoResumenImagen};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), productos.getResumenes(), R.layout.content_producto_resumen, from, to);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) { // Para el tratamiento de imágenes
                if ((view instanceof ImageView) && (data instanceof Bitmap)) {
                    ((ImageView) view).setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });

        listaProductosListView = (ListView) findViewById(R.id.listaProductos);
        listaProductosListView.setAdapter(simpleAdapter);

        listaProductosListView.setClickable(true);
        listaProductosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(PantallaPrincipal.this, Producto.class);
                intent.putExtra("Venta", position);
                startActivity(intent);
            }
        });
    }

    private void descargarImagenesPrincipales() {
        String imagen;
        for (int i=0; i<productos.getTamanyo(); i++) {
            try {
                imagen = productos.getIdImagenesVenta(i).getString(0);
                descargarImagenPrincipal(imagen, i);
            }
            catch (Exception ignored) {}
        }
    }

    private void descargarImagenPrincipal(final String idImagen, final int numProducto) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String urlPetition = Common.url + "/loadArchivoTemp?id=" + idImagen;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace(" ","+");

                        if (productos.getTamanyo() > numProducto) {
                            productos.anyadirImagen(numProducto, StringToBitMap(response));
                            if (listaProductosListView != null) {
                                SimpleAdapter sa = (SimpleAdapter) listaProductosListView.getAdapter();
                                if (sa != null) {
                                    sa.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error con imagen de perfil");
                    }
                }
        );
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        // mantener en esta página
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void logout(){
        SharedPreferences    sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_principal) {
            botonFiltros.setVisibility(View.VISIBLE);
            botonFiltros.setClickable(true);
            menuBusqueda.setVisibility(View.VISIBLE);
            buscar = true;
            misProductos = false;
            resetPantalla();
        }
        else if (id == R.id.nav_siguiendo) {

        }
        else if (id == R.id.nav_en_venta) {
            botonFiltros.setVisibility(View.INVISIBLE);
            botonFiltros.setClickable(false);
            menuBusqueda.setVisibility(View.INVISIBLE);
            buscar = false;
            misProductos = true;
            resetPantalla();
        }
        else if (id == R.id.nav_mensajes) {
            startActivity(new Intent(PantallaPrincipal.this, Mensajes.class));
        }
        else if (id == R.id.nav_perfil) {
            startActivity(new Intent(PantallaPrincipal.this, perfil_usuario.class));
        }
        else if (id == R.id.nav_cerrar_sesion) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_FILTROS) {
            if (resultCode == Common.RESULTADO_OK) {

                provincia = data.getStringExtra("ProvinciaFiltros");
                if (provincia == null) {
                    provincia = "";
                }
                orden = data.getIntExtra("OrdenFiltros", 0);
                categoria = data.getStringExtra("CategoriaFiltros");
                if (categoria == null) {
                    categoria = "";
                }
                tipoVenta = data.getIntExtra("TipoVentaFiltros", -1);
                precioMinimo = data.getIntExtra("PrecioMinimoFiltros", -1);
                precioMaximo = data.getIntExtra("PrecioMaximoFiltros", -1);

                productos.clear();
                if (listaProductosListView != null) {
                    SimpleAdapter sa = (SimpleAdapter) listaProductosListView.getAdapter();
                    if (sa != null) {
                        sa.notifyDataSetChanged();
                    }
                }
                listarProductos();
            }
            else if (resultCode == Common.RESULTADO_CANCELADO) {
                provincia = "";
                orden = 0;
                categoria = "";
                tipoVenta = -1;
                precioMinimo = -1;
                precioMaximo = -1;

                resetPantalla();
            }
        }
    }

    private void resetPantalla() {
        productos.clear();
        if (listaProductosListView != null) {
            SimpleAdapter sa = (SimpleAdapter) listaProductosListView.getAdapter();
            if (sa != null) {
                sa.notifyDataSetChanged();
            }
        }
        recuperarUsuario();
        listarProductos();
    }
}
