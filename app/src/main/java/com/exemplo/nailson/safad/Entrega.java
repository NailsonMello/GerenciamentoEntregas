package com.exemplo.nailson.safad;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class Entrega extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private RecyclerView listavendedores;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseEntrega, mDatabaseUsuarios;
    private TextView local;
    Geocoder geocoder;
    List<Address> addresses;
    private GoogleApiClient googleApiClient;

    Double latitude = -13.0010265;
    Double longitude = -38.5138832;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0882f4")));
        getWindow().setStatusBarColor(Color.parseColor("#9a0882f4"));
        act.setTitle("Venda geral");
        DatabaseUtil.getDatabase();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseEntrega = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("vendas");
        mDatabaseUsuarios = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");

        listavendedores = (RecyclerView) findViewById(R.id.lista_vendedores);
        final LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setReverseLayout(true);
        ln.setStackFromEnd(true);
        listavendedores.setLayoutManager(ln);
        local = (TextView) findViewById(R.id.local);
        listavendedores.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mDatabaseUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Tentando conexão com o Google API. Se a tentativa for bem sucessidade, o método onConnected() será chamado, senão, o método onConnectionFailed() será chamado.
        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        pararConexaoComGoogleApi();
    }
    public void pararConexaoComGoogleApi() {
        //Verificando se está conectado para então cancelar a conexão!
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            // aqui você captura lat e lgn caso o localização seja diferente de nul
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);

                String address = addresses.get(0).getAddressLine(0);
                String area = addresses.get(0).getLocality();
                String city = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();

                String fullAddress = address;

                local.setText(fullAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // caso contrario ele chama seu método
            showSettingsAlert();
        }
            }
    public boolean getLocalization(Context context) {
        int REQUEST_PERMISSION_LOCALIZATION = 221;
        boolean res = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

                res = false;
                ActivityCompat.requestPermissions((Activity) context, new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCALIZATION);

            }
        }
        return res;
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void showSettingsAlert(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Entrega.this);

        // Titulo do dialogo
        alertDialog.setTitle("GPS");

        // Mensagem do dialogo
        alertDialog.setMessage("GPS não está habilitado. Deseja configurar?");

        // botao ajustar configuracao
        alertDialog.setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // botao cancelar
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Query query;

        query = mDatabaseEntrega.orderByChild("porcentagem");
        FirebaseRecyclerAdapter<Pessoa, Entrega.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, Entrega.AnuncioViewHolder>
                (
                        Pessoa.class,
                        R.layout.tela_vendas,
                        Entrega.AnuncioViewHolder.class,
                        query

                ) {

            @Override
            protected void populateViewHolder(final Entrega.AnuncioViewHolder viewHolder, final Pessoa model, int position) {

                final String usuario = getRef(position).getKey();
                viewHolder.setNome(model.getNome());
                viewHolder.setPorcentagem(model.getPorcentagem());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAuth.getCurrentUser() == null) {
                            Intent motr = new Intent(Entrega.this, LoginUsuario.class);
                            startActivity(motr);
                            finish();
                        } else {
                            Intent motr = new Intent(Entrega.this, VendaGeral.class);
                            motr.putExtra("user_id", usuario);
                            startActivity(motr);
                            finish();
                        }
                    }
                });

            }

        };
        listavendedores.setAdapter(firebaseRecyclerAdapter);

    }


    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ProgressBar progressBar, progressBarver;


        public AnuncioViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNome(String nome) {

            TextView nomeUser = (TextView) mView.findViewById(R.id.nome);
            nomeUser.setText(nome);

        }


        @SuppressLint("ResourceAsColor")
        public void setPorcentagem(Double porcentagem) {

            TextView telUser = (TextView) mView.findViewById(R.id.texto);
            progressBar = (ProgressBar)mView.findViewById(R.id.progress);
            progressBarver = (ProgressBar)mView.findViewById(R.id.progressver);
            Double mtVendedor = Double.parseDouble(String.valueOf(porcentagem));
            progressBar.setProgress((int) Double.parseDouble(String.valueOf(mtVendedor)));
            progressBarver.setProgress((int) Double.parseDouble(String.valueOf(mtVendedor)));
            telUser.setText((int) Double.parseDouble(String.valueOf(mtVendedor)) + "%");

            if (mtVendedor >= 100){

                telUser.setTextColor(R.color.colorPrimary);

                //progressBar.setProgressDrawable(g(R.drawable.custom_progressbar_verde));
                progressBar.setVisibility(View.GONE);
                progressBarver.setVisibility(View.VISIBLE);


            }

        }


}


    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(Entrega.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(Entrega.this, MainActivity.class));
        finish();
        return true;
    }


}
