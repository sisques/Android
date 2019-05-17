package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.Ventas;


public class PantallaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int ACT_FILTROS = 0;

    SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeLayout;

    private String un; // usuario
    private String cor; // correo
    private String pr; // provincia
    private String ci; // ciudad
    private String im; // imagen perfil

    private Boolean listarPorCiudad;
    private Boolean filtroUsar;
    private String filtroCi; // filtro ciudad
    private Boolean misProductos;

    private Ventas productos;
    private TextView menuNombre;
    private TextView menuCorreo;
    private ImageView menuImagen;
    private Button botonFiltros;
    private ListView listaProductosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productos = new Ventas();
        productos.setImagenDefault(BitmapFactory.decodeResource(getResources(),R.drawable.logo));
        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        listarPorCiudad = false; // Al principio se listan todos los productos
        filtroUsar = false;
        misProductos = false;

        // Filtros
        botonFiltros = findViewById(R.id.principal_filtros);
        botonFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PantallaPrincipal.this, Filtros.class);
                i.putExtra("listarPorCiudad", listarPorCiudad);
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
                            pr = usuario.getString("provincia");
                            ci = usuario.getString("ciudad");
                            if (ci.isEmpty()) {
                                ci = "...";
                            }
                            im = usuario.getString("urlArchivo");

                            Common.establecerFotoServidor(getApplicationContext(), im, menuImagen);

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Common.cor, cor);
                            editor.putString(Common.ci, ci);
                            editor.putString(Common.pr, pr);
                            editor.putString(Common.im, im);
                            editor.commit();
                        }
                        catch(Exception e) {
                            cor = sharedpreferences.getString(Common.cor, null);
                            pr = sharedpreferences.getString(Common.pr, null);
                            ci = sharedpreferences.getString(Common.ci, null);
                            im = sharedpreferences.getString(Common.im, null);
                        }

                        if (pr == null || pr.equals("")) {
                            pr = "Zaragoza";
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

    private void listarProductos() {
        if (misProductos)
            listarProductosUsuario(un);
        else
            listarProductosCiudad();
    }

    private void listarProductosCiudad() {
        String ciudad;
        if (filtroUsar && !filtroCi.equals("...")) {
            ciudad = filtroCi;
        }
        else {
            if (ci != null && !ci.equals("..."))
                ciudad = ci;
            else
                ciudad = pr;
        }

        Integer id = productos.getIdMax();

        if (listarPorCiudad)
            gestionarPeticionListar(Common.url + "/listarProductosCiudad?id=" + id + "&ci=" + ciudad);
        else
            gestionarPeticionListar(Common.url + "/listarPaginaPrincipal?id=" + id);
    }

    private void listarProductosUsuario(String usuario) {
        Integer id = productos.getIdMax();
        gestionarPeticionListar(Common.url + "/listarProductosUsuario?id=" + id +"&un=" + usuario);
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
                            }catch (Exception ignored) { }
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
            public boolean setViewValue(View view, Object data, String textRepresentation)
            { // Para el tratamiento de imágenes
                if((view instanceof ImageView) & (data instanceof String))
                {
                    Common.establecerFotoServidor(getApplicationContext(), (String) data, (ImageView) view);
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

    @Override
    public void onBackPressed() {
        // mantener en esta página
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }
    }

    public void logout(){
        SharedPreferences    sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(PantallaPrincipal.this, PreLogin.class));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_principal) {
            botonFiltros.setVisibility(View.VISIBLE);
            botonFiltros.setClickable(true);
            misProductos = false;
            resetPantalla();
        }
        else if (id == R.id.nav_perfil) {
            startActivity(new Intent(PantallaPrincipal.this, perfil_usuario.class));
        }
        else if (id == R.id.nav_en_venta) {
            botonFiltros.setVisibility(View.INVISIBLE);
            botonFiltros.setClickable(false);
            misProductos = true;
            resetPantalla();
        }
        else if (id == R.id.nav_siguiendo) {

        }
        else if (id == R.id.nav_mensajes) {

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
                filtroUsar = true;
                listarPorCiudad = data.getBooleanExtra("listarPorCiudad", listarPorCiudad);
                filtroCi = data.getData().toString();

                productos.clear();
                SimpleAdapter sa = (SimpleAdapter) listaProductosListView.getAdapter();
                sa.notifyDataSetChanged();
                listarProductos();
            }
            else if (resultCode == Common.RESULTADO_CANCELADO) {
                resetPantalla();
            }
        }
    }

    private void resetPantalla() {
        filtroUsar = false;
        listarPorCiudad = false;

        productos.clear();
        SimpleAdapter sa = (SimpleAdapter) listaProductosListView.getAdapter();
        sa.notifyDataSetChanged();
        recuperarUsuario();
        listarProductos();
    }
}
