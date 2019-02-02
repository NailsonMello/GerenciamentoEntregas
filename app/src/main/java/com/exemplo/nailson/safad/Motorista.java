package com.exemplo.nailson.safad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Motorista extends AppCompatActivity {

    private RecyclerView listaMotorista;
    private DatabaseReference mDatabase, mDatabaseNotification ;
    private FirebaseAuth mAuth;
    String Send_User_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorista);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Entregadores");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(Motorista.this);
        mAuth = FirebaseAuth.getInstance();
        Send_User_Id = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");
        mDatabaseNotification = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("notifications");



        listaMotorista = (RecyclerView) findViewById(R.id.lista_motoristas);
        listaMotorista.setLayoutManager(new LinearLayoutManager(this));
        listaMotorista.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));


    }

    @Override
    protected void onStart() {
        super.onStart();

        String entregador = "entregador";
        Query query;

        query = mDatabase.orderByChild("funcao").startAt(entregador).endAt(entregador + "\uf8ff");

        FirebaseRecyclerAdapter<Pessoa, AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, AnuncioViewHolder>
                (
                        Pessoa.class,
                        R.layout.tela_motoristas,
                        AnuncioViewHolder.class,
                        query

                ){

            @Override
            protected void populateViewHolder(AnuncioViewHolder viewHolder, final Pessoa model, int position) {



                final String usuario = getRef(position).getKey();
                viewHolder.setNome(model.getNome());
                viewHolder.setTelefone(model.getTelefone());
                viewHolder.setImagem(getApplicationContext(), model.getImagem());

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
        public void setNome(String nome){

            TextView nomeUser = (TextView)mView.findViewById(R.id.nome);
            nomeUser.setText(nome);

        }


        public void setImagem(Context context, String imagem){

            CircleImageView imagem_anuncio = (CircleImageView)mView.findViewById(R.id.imagem_anuncio);
            Picasso.with(context).load(imagem).placeholder(R.drawable.iconeroma).into(imagem_anuncio);

        }

        public void setTelefone(String telefone) {

            TextView telUser = (TextView) mView.findViewById(R.id.telefone);
            telUser.setText(telefone);

        }

    }



    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(Motorista.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(Motorista.this, MainActivity.class));
        finish();
        return true;
    }

}
