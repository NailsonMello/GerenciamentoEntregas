package com.exemplo.nailson.safad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.exemplo.nailson.safad.ChatColection.MessageAdapter;
import com.exemplo.nailson.safad.ChatColection.Messages;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mUsuario;
    private DatabaseReference mDatabaseUsuario;
    private DatabaseReference mDatabaseNotificationChat;
    private DatabaseReference mEuUsuario;
    private TextView NomeChat, voltarTela;
    private CircleImageView ImagemChat;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ImageButton mChatSendBtn;
    private FirebaseUser mUser;
    private EditText mChatMensagemView;
    private RecyclerView mMensagemLista;
    private final List<Messages> mensagemLista = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private FirebaseStorage mStorage;

    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ActionBar act = getSupportActionBar();
        act.hide();
        DatabaseUtil.getDatabase();
        mDatabaseUsuario = FirebaseDatabase.getInstance().getReference().child("SAFAD");
        mDatabaseNotificationChat = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("notificationChat");
        mEuUsuario = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        NomeChat = (TextView)findViewById(R.id.nomeChat);
        ImagemChat = (CircleImageView)findViewById(R.id.imgChatUser);

        mChatSendBtn = (ImageButton)findViewById(R.id.chat_enviar_btn);
        mChatMensagemView = (EditText)findViewById(R.id.chat_msg_view);
        voltarTela = (TextView)findViewById(R.id.btVoltar);

        mAdapter = new MessageAdapter(mensagemLista);
        mMensagemLista = (RecyclerView)findViewById(R.id.mensagenLista);

        mLinearLayout = new LinearLayoutManager(this);
        mMensagemLista.setHasFixedSize(true);
        mMensagemLista.setLayoutManager(mLinearLayout);
        mMensagemLista.setAdapter(mAdapter);


        mUsuario = getIntent().getStringExtra("user_id");
        mDatabaseUsuario.child("usuario").child(mUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String nomeChat = dataSnapshot.child("nome").getValue().toString();
                final String imgChat = (String) dataSnapshot.child("imagem").getValue();
                final  String email = dataSnapshot.child("email").getValue().toString();
                final String key = dataSnapshot.getKey();
                NomeChat.setText(nomeChat);
                Picasso.with(ChatActivity.this).load(imgChat).placeholder(R.drawable.default_avatar).into(ImagemChat);

                ImagemChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent perfil = new Intent(ChatActivity.this, PerfilUsuario.class);
                        perfil.putExtra("idUser", key);
                        perfil.putExtra("email", email);
                        startActivity(perfil);
                    }
                });

                String euUsuario = mUser.getUid();
                mEuUsuario.child("usuario").child(euUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String EuNome = dataSnapshot.child("nome").getValue(String.class);
                        final String EuImg = dataSnapshot.child("imagem").getValue(String.class);

                        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                enviarMensagem();
                                final String userId = mUser.getUid();

                                mDatabaseUsuario.child("Chat").child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(!dataSnapshot.hasChild(mUsuario)){

                                            Map chatAddMap = new HashMap();
                                            chatAddMap.put("seen", false);
                                            chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                                            chatAddMap.put("nome", nomeChat);
                                            chatAddMap.put("imagem", imgChat);

                                            Map chatEuMap = new HashMap();
                                            chatEuMap.put("seen", false);
                                            chatEuMap.put("timestamp", ServerValue.TIMESTAMP);
                                            chatEuMap.put("nome", EuNome);
                                            chatEuMap.put("imagem", EuImg);

                                            Map chatUserMap = new HashMap();
                                            chatUserMap.put("Chat/" + userId + "/" + mUsuario, chatAddMap);
                                            chatUserMap.put("Chat/" + mUsuario + "/" + userId, chatEuMap);

                                            mDatabaseUsuario.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                    if(databaseError != null){

                                                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                                    }

                                                }
                                            });

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        loadMensagem();






        voltarTela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voltar = new Intent(ChatActivity.this, ListaConversa.class);
                startActivity(voltar);
                finish();
            }
        });


    }


    private void loadMensagem() {
        String userId = mUser.getUid();
        mDatabaseUsuario.child("mensagem").child(userId).child(mUsuario).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                mensagemLista.add(message);
                mAdapter.notifyDataSetChanged();
                mMensagemLista.scrollToPosition(mensagemLista.size()- 1);

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


    }

    private void enviarMensagem() {

        String UserChat = mUser.getUid();
        String mensagem = mChatMensagemView.getText().toString();
        if (!TextUtils.isEmpty(mensagem)){

            String current_user_ref = "mensagem/" + UserChat + "/" + mUsuario;
            String chat_user_ref = "mensagem/" + mUsuario + "/" + UserChat;

            DatabaseReference user_mensagem_push = mDatabaseUsuario.child("mensagem")
                    .child(UserChat).child(mUsuario).push();
            String push_id = user_mensagem_push.getKey();

            Map mensagemMap = new HashMap();
            mensagemMap.put("message", mensagem);
            mensagemMap.put("seen", false);
            mensagemMap.put("type", "text");
            mensagemMap.put("time", ServerValue.TIMESTAMP);
            mensagemMap.put("from", UserChat);



            Map mensagemUserMap = new HashMap();
            mensagemUserMap.put(current_user_ref + "/" + push_id, mensagemMap);
            mensagemUserMap.put(chat_user_ref + "/" + push_id, mensagemMap);

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("Id_User_Msg", UserChat);
            notificationData.put("type", "request");
            notificationData.put("message", mensagem);

            mDatabaseNotificationChat.child(mUsuario).push().setValue(notificationData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

            mChatMensagemView.setText("");

            mDatabaseUsuario.updateChildren(mensagemUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if (databaseError != null){
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });

        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatActivity.this, MainActivity.class));
        finish();

    }

}
