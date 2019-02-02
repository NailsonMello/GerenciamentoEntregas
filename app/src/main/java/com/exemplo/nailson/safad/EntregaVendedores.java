package com.exemplo.nailson.safad;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;


public class EntregaVendedores extends AppCompatActivity {

    private RecyclerView listaMotorista;
    private DatabaseReference mDatabase, mDatabaseMotoristas;
    private TextView sttatus, dataatual;
    private FrameLayout rootta;
    private ImageView imgRota, imgSearch;
    private String mMotorista;
    private FirebaseAuth mAuth;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_vendedores);

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Entregas realizadas");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(EntregaVendedores.this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");
        mDatabaseMotoristas = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("entrega");
        //mMotorista = getIntent().getExtras().getString("nomemotorista");
        sttatus = (TextView)findViewById(R.id.statusrota);
        dataatual = (TextView) findViewById(R.id.dataatual);

        listaMotorista = (RecyclerView) findViewById(R.id.lista_rotas);
        final LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setReverseLayout(true);
        ln.setStackFromEnd(true);
        listaMotorista.setLayoutManager(ln);

        dataatual.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EntregaVendedores.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });



        String user_id = mAuth.getCurrentUser().getUid();

        mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String NomeVendedor = dataSnapshot.child("nome").getValue(String.class);
                final String FuncaoVendedor = dataSnapshot.child("funcao").getValue(String.class);
                entregasVendedor(NomeVendedor,  FuncaoVendedor);

                        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month + 1;
                                String date = (String.format("%02d/%02d/%d", day,+month,year));
                                dataatual.setText(date);
                                if (dataatual.getText().toString().equals("clique p/ inserir data")){
                                    entregasVendedor(NomeVendedor,  FuncaoVendedor);
                                }else {
                                    entregasDatas(NomeVendedor, FuncaoVendedor, date);
                                }
                            }
                        };

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void entregasDatas(final String vendedores, final String funcaovendedor, final String dataent) {
        Query query ;
         query = mDatabaseMotoristas.orderByChild("data").startAt(dataent).endAt(dataent + "\uf8ff");

        FirebaseRecyclerAdapter<Pessoa, EntregaVendedores.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, EntregaVendedores.AnuncioViewHolder>
                (
                        Pessoa.class,
                        R.layout.tela_rotas,
                        EntregaVendedores.AnuncioViewHolder.class,
                        query


                ) {

            @Override
            protected void populateViewHolder(final EntregaVendedores.AnuncioViewHolder viewHolder, final Pessoa model, final int position) {



                if (vendedores.toString().equals(model.getVendedor()) || funcaovendedor.toString().equals("admin")) {
                    final String chave_entrega = getRef(position).getKey();
                    viewHolder.setFantasia(model.getFantasia());
                    viewHolder.setVendedor(model.getVendedor());
                    viewHolder.setData(model.getData());
                    viewHolder.setStatus(model.getStatus());
                    viewHolder.setLayout();
                }else {
                    viewHolder.setLayoutInvisivel();
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (FirebaseAuth.getInstance().getCurrentUser() == null){

                            Intent telaEntrega = new Intent(EntregaVendedores.this, LoginUsuario.class);
                            startActivity(telaEntrega);
                        }else {
                            final String chave_entrega = getRef(position).getKey();
                            Intent telaEntrega = new Intent(EntregaVendedores.this, DadosEntrega.class);
                            telaEntrega.putExtra("entrega_id", chave_entrega);
                            // telaEntrega.putExtra("nome", mMotorista);
                            startActivity(telaEntrega);
                            //finish();
                        }
                    }
                });


            }

        };
        listaMotorista.setAdapter(firebaseRecyclerAdapter);


    }


    public void entregasVendedor(final String vendedores, final String funcaovendedor){


        Query query;

            query = mDatabaseMotoristas;

        FirebaseRecyclerAdapter<Pessoa, EntregaVendedores.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, EntregaVendedores.AnuncioViewHolder>
                (
                        Pessoa.class,
                        R.layout.tela_rotas,
                        EntregaVendedores.AnuncioViewHolder.class,
                        query


                ) {

            @Override
            protected void populateViewHolder(final EntregaVendedores.AnuncioViewHolder viewHolder, final Pessoa model, final int position) {



                if (vendedores.toString().equals(model.getVendedor()) || funcaovendedor.toString().equals("admin")) {
                    final String chave_entrega = getRef(position).getKey();
                    viewHolder.setFantasia(model.getFantasia());
                    viewHolder.setVendedor(model.getVendedor());
                    viewHolder.setData(model.getData());
                    viewHolder.setStatus(model.getStatus());
                    viewHolder.setLayout();
                }else {
                    viewHolder.setLayoutInvisivel();
                }


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (FirebaseAuth.getInstance().getCurrentUser() == null){

                            Intent telaEntrega = new Intent(EntregaVendedores.this, LoginUsuario.class);
                            startActivity(telaEntrega);
                        }else {
                            final String chave_entrega = getRef(position).getKey();
                            Intent telaEntrega = new Intent(EntregaVendedores.this, DadosEntrega.class);
                            telaEntrega.putExtra("entrega_id", chave_entrega);
                           // telaEntrega.putExtra("nome", mMotorista);
                            startActivity(telaEntrega);
                            //finish();
                        }
                    }
                });


            }

        };
        listaMotorista.setAdapter(firebaseRecyclerAdapter);

    }

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Context context;

        public AnuncioViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setFantasia(String fantasia){

            TextView nomeUser = (TextView)mView.findViewById(R.id.nomerota);
            nomeUser.setText(fantasia);

        }

        public void setData(String data){
            TextView dataUser = (TextView)mView.findViewById(R.id.dataEntrega);
            dataUser.setText(data);
        }
        public void setLayout(){
            LinearLayout cardView = (LinearLayout)mView.findViewById(R.id.cardRota);
            final LinearLayout linearLayout =(LinearLayout)mView.findViewById(R.id.telarota);
            linearLayout.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);

        }
        public void setLayoutInvisivel(){
            LinearLayout cardView = (LinearLayout)mView.findViewById(R.id.cardRota);
            final LinearLayout linearLayout =(LinearLayout)mView.findViewById(R.id.telarota);
            linearLayout.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }
        public void setVendedor(String vendedor) {

            final TextView telUser = (TextView) mView.findViewById(R.id.telefonerota);
            telUser.setText(vendedor);

        }
        public void setStatus(String status){
            TextView statuss = (TextView) mView.findViewById(R.id.statusrota);
            statuss.setText(status);
            FrameLayout   rootta = (FrameLayout)mView.findViewById(R.id.framerota);
            ImageView   imgRota = (ImageView)mView.findViewById(R.id.imagem_status);
            if (statuss.getText().toString().equals("Pr?xima Entrega")){
                rootta.setBackgroundResource(R.drawable.proxima);
                imgRota.setImageResource(R.drawable.proxrota);
            }
            if (statuss.getText().toString().equals("Em Rota")){
                rootta.setBackgroundResource(R.drawable.emrota);
                imgRota.setImageResource(R.drawable.carrota);
            }
            if (statuss.getText().toString().equals("Entrega realizada")){
                rootta.setBackgroundResource(R.drawable.realizada);
                imgRota.setImageResource(R.drawable.feita);
            }
            if (statuss.getText().toString().equals("Aguardando Cliente")){
                rootta.setBackgroundResource(R.drawable.aguardando);
                imgRota.setImageResource(R.drawable.aguardar);
            }
            if (statuss.getText().toString().equals("N?o deu tempo")){
                rootta.setBackgroundResource(R.drawable.tempo);
                imgRota.setImageResource(R.drawable.naodeutempo);
            }
            if (statuss.getText().toString().equals("Cliente devolveu")){
                rootta.setBackgroundResource(R.drawable.devolveu);
                imgRota.setImageResource(R.drawable.devolvelnota);
            }

        }

    }

    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(EntregaVendedores.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(EntregaVendedores.this, MainActivity.class));
        finish();
        return true;
    }
}

