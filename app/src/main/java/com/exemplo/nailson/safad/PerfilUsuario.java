package com.exemplo.nailson.safad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

public class PerfilUsuario extends AppCompatActivity {

    private TextView nome, sobrenome, email, telefoneUser;
    private ImageView imgperfil, carregarFoto;
    private FrameLayout frameLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mPerfilDatabase, mDatabaseUser;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private Uri resultUri;
    private StorageReference mStorage;

    private static final int IMAGEM_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        final ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);

        DatabaseUtil.getDatabase();
        String keyAnuncio = getIntent().getStringExtra("idUser");
        final String emailUserr = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();
        mPerfilDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario").child(keyAnuncio);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario");

        mProgress = new ProgressDialog(this);
        final String userId = mAuth.getCurrentUser().getUid();
        imgperfil = (ImageView)findViewById(R.id.imagemPerfil);
        nome = (TextView)findViewById(R.id.nomePerfil);
        email = (TextView)findViewById(R.id.emailperfil);
        sobrenome = (TextView)findViewById(R.id.sobrePerfil);
        telefoneUser = (TextView)findViewById(R.id.telPerfil);
        frameLayout = (FrameLayout)findViewById(R.id.frameUser);
        carregarFoto = (ImageView)findViewById(R.id.editarFoto);

        mPerfilDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nomePerfil = dataSnapshot.child("nome").getValue().toString();
                String sobrePerfil = dataSnapshot.child("sobrenome").getValue().toString();
                String telefonePerfil = dataSnapshot.child("telefone").getValue().toString();
                String imagemPerfil = dataSnapshot.child("imagem").getValue().toString();
                String emailPerfil = dataSnapshot.child("email").getValue().toString();
                act.setTitle(nomePerfil+" "+sobrePerfil);
                nome.setText(nomePerfil);
                sobrenome.setText(sobrePerfil);
                email.setText(emailPerfil);
                telefoneUser.setText(telefonePerfil);
                Picasso.with(PerfilUsuario.this).load(imagemPerfil).placeholder(R.drawable.default_avatar).into(imgperfil);

                mDatabaseUser.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       String nomeUser = dataSnapshot.child("nome").getValue().toString();

                       if (nome.getText().toString().equals(nomeUser)){
                           frameLayout.setVisibility(View.VISIBLE);
                           carregarFoto.setVisibility(View.VISIBLE);
                       }
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

        carregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CarregarIMG = new Intent();
                CarregarIMG.setType("image/*");
                CarregarIMG.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser( CarregarIMG, "SELECIONAR IMAGEM"), IMAGEM_REQUEST);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGEM_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                editarPerfil(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void editarPerfil(Uri resultUri) {

        final String user_id = mAuth.getCurrentUser().getUid();
        if (resultUri != null){

            mProgress.setTitle("Editando perfil");
            mProgress.setMessage("Aguarde, sua foto esta sendo carregada...");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            StorageReference filepath = mStorage.child("usuario").child(user_id+"img");
            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){
                        @SuppressWarnings("VisibleForTests")   final String download_url = task.getResult().getDownloadUrl().toString();

                        Map update_hashMap = new HashMap();
                        update_hashMap.put("imagem", download_url);


                        mDatabase.child(user_id).updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    mProgress.dismiss();


                                    Toast.makeText(PerfilUsuario.this, "Imagem enviada com sucesso", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(PerfilUsuario.this, "Voc? n?o carregou nenhuma imagem", Toast.LENGTH_LONG).show();
                                }

                            }
                        });


                    } else {

                        Toast.makeText(PerfilUsuario.this, "Erro ao carregar imagem", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();

                    }


                }
            });





        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(PerfilUsuario.this, MainActivity.class));
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PerfilUsuario.this, MainActivity.class));
        finish();

    }

}

