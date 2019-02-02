package com.exemplo.nailson.safad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class Rotas extends AppCompatActivity {
    private RecyclerView listaMotorista;
    private DatabaseReference mDatabase, mDatabaseMotoristas;
    private TextView sttatus, dataatual;
    private FrameLayout rootta;
    private ImageView imgRota;
    private String mMotorista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotas);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Entregas do dia");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(Rotas.this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("entrega");
        mDatabaseMotoristas = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("entrega");
        mMotorista = getIntent().getExtras().getString("nomemotorista");
        sttatus = (TextView)findViewById(R.id.statusrota);
        dataatual = (TextView) findViewById(R.id.dataatual);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sdf.format(date);
        dataatual.setText(dateString);
        listaMotorista = (RecyclerView) findViewById(R.id.lista_rotas);
        listaMotorista.setLayoutManager(new LinearLayoutManager(this));
        listaMotorista.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));




    }


    @Override
    protected void onStart() {
        super.onStart();
        Query query, mQuery;

        query = mDatabaseMotoristas.orderByChild("motorista").startAt(mMotorista).endAt(mMotorista + "\uf8ff");
        mQuery = mDatabase.orderByChild("data").startAt(dataatual.getText().toString()).endAt(dataatual + "\uf8ff");

            FirebaseRecyclerAdapter<Pessoa, Rotas.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, Rotas.AnuncioViewHolder>
                    (
                            Pessoa.class,
                            R.layout.tela_rotas,
                            Rotas.AnuncioViewHolder.class,
                            query


                    ) {

                @Override
                protected void populateViewHolder(final Rotas.AnuncioViewHolder viewHolder, final Pessoa model, int position) {

                    final String chave_entrega = getRef(position).getKey();
                    viewHolder.setFantasia(model.getFantasia());
                    viewHolder.setVendedor(model.getVendedor());
                    viewHolder.setData(model.getData());
                    viewHolder.setStatus(model.getStatus());

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (FirebaseAuth.getInstance().getCurrentUser() == null){

                                Intent telaEntrega = new Intent(Rotas.this, LoginUsuario.class);
                                startActivity(telaEntrega);
                            }else {
                                Intent telaEntrega = new Intent(Rotas.this, DadosEntrega.class);
                                telaEntrega.putExtra("entrega_id", chave_entrega);
                                telaEntrega.putExtra("nome", mMotorista);
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

        public AnuncioViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setFantasia(String fantasia){

            TextView nomeUser = (TextView)mView.findViewById(R.id.nomerota);
            nomeUser.setText(fantasia);

        }


        public void setVendedor(String vendedor) {

            TextView telUser = (TextView) mView.findViewById(R.id.telefonerota);
            telUser.setText(vendedor);


        }

        public void setData(String data){

            LinearLayout linearLayout =(LinearLayout)mView.findViewById(R.id.telarota);
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final String dateStringG = sdf.format(date);
            ImageView img = (ImageView)mView.findViewById(R.id.imgPrioridade);
            TextView dataUser = (TextView)mView.findViewById(R.id.dataEntrega);
            dataUser.setText(data);

            if (!dataUser.getText().toString().equals(dateStringG)){
                img.setVisibility(View.VISIBLE);
                linearLayout.setBackgroundResource(R.drawable.alerta_entrega);
            }else {
                img.setVisibility(View.GONE);
                linearLayout.setBackgroundResource(R.drawable.alerta_entrega_normal);
            }
        }


        public void setStatus(String status){
            ImageView img = (ImageView)mView.findViewById(R.id.imgPrioridade);
            LinearLayout linearLayout =(LinearLayout)mView.findViewById(R.id.telarota);
            TextView statuss = (TextView) mView.findViewById(R.id.statusrota);
            statuss.setText(status);
         FrameLayout   rootta = (FrameLayout)mView.findViewById(R.id.framerota);
         ImageView   imgRota = (ImageView)mView.findViewById(R.id.imagem_status);
            if (statuss.getText().toString().equals("Proxima Entrega")){
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
                linearLayout.setBackgroundResource(R.drawable.alerta_entrega_normal);
                img.setVisibility(View.GONE);
            }
            if (statuss.getText().toString().equals("Aguardando Cliente")){
                rootta.setBackgroundResource(R.drawable.aguardando);
                imgRota.setImageResource(R.drawable.aguardar);
            }
            if (statuss.getText().toString().equals("Não deu tempo")){
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
        Intent telaAnuncio = new Intent(Rotas.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(Rotas.this, MainActivity.class));
        finish();
        return true;
    }
}
