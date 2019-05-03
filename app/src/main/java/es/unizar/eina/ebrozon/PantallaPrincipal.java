package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.util.Base64;

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

import org.json.JSONObject;

import java.util.ArrayList;

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

    private Ventas productos;
    private TextView menuNombre;
    private TextView menuCorreo;
    private ImageView menuImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productos = new Ventas();
        productos.setImagenDefault(BitmapFactory.decodeResource(getResources(),R.drawable.logo));
        sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        listarPorCiudad = false;
        filtroUsar = false;

        // Filtros
        Button botonFiltros = findViewById(R.id.principal_filtros);
        botonFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(PantallaPrincipal.this,
                        Filtros.class), ACT_FILTROS);
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
                listarProductosCiudad();
                swipeLayout.setRefreshing(false);
            }
        });

        recuperarUsuario();
        listarProductosCiudad();
    }

    private Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void bajarFotoServidor(String id, final ImageView imagen) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String urlPetition = Common.url + "/loadArchivoTemp?id=" + id;

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        response = response.replace(" ","+");
                        Bitmap result = StringToBitMap(response);
                        if (result != null) {
                            imagen.setImageBitmap(result);
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

                            bajarFotoServidor(im, menuImagen);

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

    private void gestionarRespuesta(String response) {
        if (!response.equals("[]")) {
            response = response.replace("[", "").replace("]", "");
            response = response.replace("{", "").replace("\"", "");

            String[] listaProductos = response.split("\\}");

            for (int i = 1; i < listaProductos.length; i++) {
                listaProductos[i] = listaProductos[i].substring(1);
            }

            String[] producto;

            // TODO: Añadir imagen al producto
            for (int i = 0; i < listaProductos.length; i++) {
                producto = listaProductos[i].split(",");
                for (int j = 0; j < producto.length; j++) {
                    producto[j] = producto[j].split(":", 2)[1];
                    if (producto[j].equals("null")) producto[j] = "";
                }
                productos.anyadirVenta(producto, null);
            }

            listarProductos();
        }
    }

    private void gestionarListar(String urlPetition) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        gestionarRespuesta(response);
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

    private void listarProductosCiudad() {
        String ciudad;
        if (filtroUsar) {
            ciudad = filtroCi;
        }
        else {
            if (ci != null && !ci.equals("..."))
                ciudad = ci;
            else
                ciudad = pr;
        }

        if (listarPorCiudad)
            gestionarListar(Common.url + "/listarProductosCiudad?ci=" + ciudad);
        else
            gestionarListar(Common.url + "/listarPaginaPrincipal");
    }

    private void listarProductosUsuario(String usuario) {
        gestionarListar(Common.url + "/listarProductosUsuario?un=" + usuario);
    }

    private void listarProductos() {
        String[] from = productos.getResumenAtributos();
        int[] to = {R.id.ProductoResumenTitulo, R.id.ProductoResumenPrecio,
                R.id.ProductoResumenDescripcion, R.id.ProductoResumenCiudad, R.id.ProductoResumenImagen};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), productos.getResumenes(), R.layout.content_producto_resumen, from, to);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,String textRepresentation)
            { // Para el tratamiento de imágenes
                if((view instanceof ImageView) & (data instanceof Bitmap))
                {
                    ImageView iv = (ImageView) view;
                    Bitmap bm = (Bitmap) data;
                    iv.setImageBitmap(bm);
                    return true;
                }
                return false;
            }
        });

        final ListView androidListView = (ListView) findViewById(R.id.listaProductos);
        androidListView.setAdapter(simpleAdapter);

        androidListView.setClickable(true);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        //mantener en esta paginna
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
            // Handle the camera action
        } else if (id == R.id.nav_ajustes) {

        } else if (id == R.id.nav_ayuda) {

        } else if (id == R.id.nav_en_venta) {

        } else if (id == R.id.nav_siguiendo) {

        } else if (id == R.id.nav_mensajes) {

        }else if (id == R.id.nav_cerrar_sesion) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_FILTROS) {
            if (resultCode == RESULT_OK) {
                listarPorCiudad = true;
                filtroCi = data.getData().toString();
                listarProductosCiudad();
            }
            else {
                listarPorCiudad = false;
            }
        }
    }
}
