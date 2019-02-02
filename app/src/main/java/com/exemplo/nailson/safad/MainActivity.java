package com.exemplo.nailson.safad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exemplo.nailson.safad.FloatingWidget.FloatingViewService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.exemplo.nailson.safad.R.id.nav_sair;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private CardView entrega, motorista, rotas, mapa;
    private DatabaseReference mDatabase, dref;
    private DatabaseReference mDataUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView nomeMenu, emailMenu, con;
    private CircleImageView imgmenu;
    private Button btn_anunciar, btn_testarCon;
    private FirebaseUser user;
    private MenuItem login, sair, cadastro, vendas;
    private ListView listView;
    private String mChave_anuncio = null;
    private LinearLayout scrollView;
    View view;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(MainActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        user = mAuth.getCurrentUser();
        item();
        VerificarConexao();
        preencherMenu();

        entrega = (CardView) findViewById(R.id.entrega);
        motorista = (CardView) findViewById(R.id.motorista);
        rotas = (CardView) findViewById(R.id.rotas);
        mapa = (CardView) findViewById(R.id.mapa);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");

        entrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUser == null){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("ATENÇÃO");
                    alerta.setIcon(android.R.drawable.ic_menu_info_details);
                    alerta.setMessage("Desculpa Você não tem permissão para entrar, contate o administrador");
                    alerta.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alerta.show();

                }else {
                    Intent main = new Intent(MainActivity.this, EntregaVendedores.class);
                    startActivity(main);
                    //finish();

                }
            }
        });
        rotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarMotorista();
            }

        });

        motorista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null){
                    Intent motorista = new Intent(MainActivity.this, LoginUsuario.class);
                   // finish();
                    startActivity(motorista);
                }else {
                    Intent motorista = new Intent(MainActivity.this, Motorista.class);
                    //finish();
                    startActivity(motorista);
                }
            }
        });

        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motrarLocal();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
    }


    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent login = new Intent(MainActivity.this, LoginUsuario.class);
            startActivity(login);
           // finish();
        }else {
            mDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue("false");

        }

    }

    private void motrarLocal() {

        Intent motr = new Intent(MainActivity.this, ListaUsuarios.class);
        startActivity(motr);
       // finish();
    }

    private void MostrarMotorista(){
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter<String> adapter;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Escolha um Entregador");
        alert.setIcon(R.drawable.motoristas);
        View row = getLayoutInflater().inflate(R.layout.lista_motoristas, null);
        final ListView li = (ListView)row.findViewById(R.id.testandooo);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        li.setAdapter(adapter);
        String entregador = "entregador";
        Query query;
        dref = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");
        query = dref.orderByChild("funcao").startAt(entregador).endAt(entregador + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String nome = dataSnapshot.child("nome").getValue(String.class);
                list.add(nome);
                adapter.notifyDataSetChanged();
                li.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        String noome = String.valueOf(parent.getItemAtPosition(position));
                        Intent motr = new Intent(MainActivity.this, Rotas.class);
                        motr.putExtra("nomemotorista", noome);
                        startActivity(motr);
                        //finish();
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        alert.setView(row);
        AlertDialog dialog = alert.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startService(new Intent(MainActivity.this, FloatingViewService.class));
                finish();
            } else if (Settings.canDrawOverlays(this)) {
                startService(new Intent(MainActivity.this, FloatingViewService.class));
                finish();
            } else {
                askPermission();
                Toast.makeText(this, "Você precisa de permissão de janela de alerta do sistema para fazer isso", Toast.LENGTH_SHORT).show();
            }
            finishAffinity();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finishAffinity();

        }


        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent login = new Intent(MainActivity.this, LoginUsuario.class);
            startActivity(login);
            //finish();
        } else if (id == R.id.nav_chat) {

            if (mAuth.getCurrentUser() == null) {
                Intent ite = new Intent(MainActivity.this, LoginUsuario.class);
                startActivity(ite);
                //finish();
            }else{
                Intent chat = new Intent(MainActivity.this, ListaConversa.class);
                startActivity(chat);
                //finish();
            }
        } else if (id == R.id.nav_minha_conta) {
            if (mAuth.getCurrentUser() == null) {
                Intent ite = new Intent(MainActivity.this, LoginUsuario.class);
                startActivity(ite);
               // finish();
            }else{
                Intent ite = new Intent(MainActivity.this, PerfilUsuario.class);
                ite.putExtra("idUser", mAuth.getCurrentUser().getUid());
                startActivity(ite);
                //finish();
            }
        } else if (id == R.id.nav_cadastrar_user) {

                Intent ite = new Intent(MainActivity.this, CadastroUsuario.class);
                startActivity(ite);
                //finish();

        } else if (id == R.id.nav_vendas) {

            Intent ite = new Intent(MainActivity.this, Entrega.class);
            startActivity(ite);
            //finish();




        } else if (id == nav_sair) {
            mAuth.signOut();
            if (mAuth.getCurrentUser() == null) {
                Intent it = new Intent(MainActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void VerificarConexao() {

        ConnectivityManager on = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = on.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            btn_testarCon.setVisibility(View.GONE);

        } else {

            btn_testarCon.setVisibility(View.VISIBLE);

        }
    }

    public void item(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View menu = navigationView.getHeaderView(0);
        emailMenu = (TextView)menu.findViewById(R.id.emailViewmenu);
        nomeMenu = (TextView)menu.findViewById(R.id.nomeViewmenu);

        imgmenu = (CircleImageView)menu.findViewById(R.id.imageViewmenu);
        Menu menuu = navigationView.getMenu();
        login = menuu.findItem(R.id.nav_login);
        sair = menuu.findItem(R.id.nav_sair);
        cadastro = menuu.findItem(R.id.nav_cadastrar_user);
        vendas = menuu.findItem(R.id.nav_vendas);
        btn_testarCon = (Button) findViewById(R.id.btn_testarCon);
        btn_testarCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificandoConexao(view);
            }
        });

    }

    public void preencherMenu(){

        mDataUser = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");

        if (mAuth.getCurrentUser() != null) {

            mDataUser.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String codi = dataSnapshot.getKey();
                    String NomeMenu = dataSnapshot.child("nome").getValue().toString();
                    String EmailMenu = dataSnapshot.child("email").getValue().toString();
                    String imgMenu = dataSnapshot.child("imagem").getValue().toString();
                    String func = dataSnapshot.child("funcao").getValue().toString();

                    emailMenu.setText(EmailMenu);
                    nomeMenu.setText(NomeMenu);

                    Picasso.with(MainActivity.this).load(imgMenu).placeholder(R.drawable.default_avatar).into(imgmenu);


                    if (func.toString().equals("admin")){
                        cadastro.setVisible(true);
                    }else {
                        cadastro.setVisible(false);
                    }

                    if (codi.toString().equals("0EZzQYHUZ0W8qgYi3WAsOY8v4bV2")){
                        vendas.setVisible(true);
                    }else {
                        vendas.setVisible(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            login.setVisible(false);
            sair.setVisible(true);
        }else {
            emailMenu.setText("noslianmmello@gmail.com");
            nomeMenu.setText("Roma Log");
            imgmenu.setImageResource(R.drawable.iconeroma);
            login.setVisible(true);
            sair.setVisible(false);
        }
    }
    public void verificandoConexao(View view){
        ConnectivityManager on = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = on.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Snackbar snackbar = Snackbar.make(view, "Conectado com Sucesso!!!",Snackbar.LENGTH_SHORT);
            snackbar.show();
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            btn_testarCon.setVisibility(View.GONE);
        } else {
            Snackbar snackbar = Snackbar.make(view, "Falha ao se conectar a internet",Snackbar.LENGTH_SHORT);
            snackbar.show();
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();


        }
    }

}
