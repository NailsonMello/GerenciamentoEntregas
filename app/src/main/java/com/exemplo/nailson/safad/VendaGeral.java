package com.exemplo.nailson.safad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;

public class VendaGeral extends AppCompatActivity {
    private String vendedor = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView metaVendedor;
    private TextView diasuteis;
    private TextView realizado;
    private TextView faltante;
    private TextView vendadia;
    private TextView nomeVendedor;
    private TextView texto;
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venda_geral);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0882f4")));
        getWindow().setStatusBarColor(Color.parseColor("#9a0882f4"));
        act.setTitle("Venda geral");
        DatabaseUtil.getDatabase();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("vendas");

        vendedor = getIntent().getExtras().getString("user_id");

        texto = (TextView)findViewById(R.id.texto);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        metaVendedor = (TextView)findViewById(R.id.metaVendedor);
        diasuteis = (TextView)findViewById(R.id.diasuteis);
        realizado = (TextView)findViewById(R.id.realizadoVendedor);
        faltante = (TextView)findViewById(R.id.vendafaltante);
        vendadia = (TextView)findViewById(R.id.vendadia);
        nomeVendedor = (TextView)findViewById(R.id.nomeVendedor);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child(vendedor).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String meta = dataSnapshot.child("meta").getValue().toString();
                String dia = dataSnapshot.child("diasuteis").getValue().toString();
                String real = dataSnapshot.child("venda_geral").getValue().toString();
                String falta = dataSnapshot.child("venda_faltante").getValue().toString();
                String vendad = dataSnapshot.child("venda_dia").getValue().toString();
                String nome = dataSnapshot.child("nome").getValue().toString();


                diasuteis.setText(dia);
                nomeVendedor.setText(nome);

                Double mtVendedor = Double.parseDouble(meta);
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                String formatado = nf.format (mtVendedor);
                metaVendedor.setText(formatado);
                Double realVendedor = Double.parseDouble(real);
                String formatado1 = nf.format (realVendedor);
                realizado.setText(formatado1);
                Double diaVendedor = Double.parseDouble(vendad);
                String formatado2 = nf.format (diaVendedor);
                vendadia.setText(formatado2);
                Double faltaVendedor = Double.parseDouble(falta);
                String formatado3 = nf.format (faltaVendedor);
                faltante.setText(formatado3);

                double result = ((realVendedor/mtVendedor)*100);
                progressBar.setProgress((int) Double.parseDouble(String.valueOf(result)));
                texto.setText((int) Double.parseDouble(String.valueOf(result)) + "%");
                if (result >= 100){
                  texto.setTextColor(R.color.colorPrimary);
                  progressBar.setProgressDrawable(getDrawable(R.drawable.custom_progressbar_verde));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(VendaGeral.this, Entrega.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent telaAnuncio = new Intent(VendaGeral.this, Entrega.class);
        startActivity(telaAnuncio);
        finish();
        return true;
    }

}
