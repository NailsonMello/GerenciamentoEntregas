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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaUsuarios extends AppCompatActivity {

    private RecyclerView listaMotorista;
    private DatabaseReference mDatabase, mDatabaseNotification ;
    private FirebaseAuth mAuth;
    String Send_User_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Usuarios");
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(ListaUsuarios.this);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");

        listaMotorista = (RecyclerView) findViewById(R.id.lista_motoristas);
        listaMotorista.setLayoutManager(new LinearLayoutManager(this));
        listaMotorista.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
           mDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");
        }
        FirebaseRecyclerAdapter<Pessoa, ListaUsuarios.AnuncioViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, ListaUsuarios.AnuncioViewHolder>
                (
                        Pessoa.class,
                        R.layout.tela_motoristas,
                        ListaUsuarios.AnuncioViewHolder.class,
                        mDatabase

                ){

            @Override
            protected void populateViewHolder(final ListaUsuarios.AnuncioViewHolder viewHolder, final Pessoa model, int position) {

                final String usuario = getRef(position).getKey();
                viewHolder.setNome(model.getNome());
                viewHolder.setTelefone(model.getTelefone());
                viewHolder.setImagem(getApplicationContext(), model.getImagem());
                mDatabase.child(usuario).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAuth.getCurrentUser() == null){
                            Intent motr = new Intent(ListaUsuarios.this, LoginUsuario.class);
                            startActivity(motr);
                            finish();
                        }else {
                            Intent motr = new Intent(ListaUsuarios.this, ChatActivity.class);
                            motr.putExtra("user_id", usuario);
                            startActivity(motr);
                            finish();
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

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setBackgroundResource(R.drawable.online);

            } else {

                userOnlineView.setBackgroundResource(R.drawable.offline);

            }

        }

    }


    @Override
    public void onBackPressed() {
        Intent telaAnuncio = new Intent(ListaUsuarios.this, MainActivity.class);
        startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(ListaUsuarios.this, MainActivity.class));
        finish();
        return true;
    }

}

