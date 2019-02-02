package com.exemplo.nailson.safad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.HashMap;

public class CadastroUsuario extends AppCompatActivity {

    private EditText edtNome, edtSobNome, edtEmail, edtSenha, edtTel;
    private Button cadastrar;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgessdialog;
    private DatabaseReference mDatabase;
    private final String TOPIC = "RomaBahia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Cadastrar Usuario");
        mProgessdialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        edtNome = (EditText) findViewById(R.id.nomeUsuario);
        edtSobNome = (EditText) findViewById(R.id.sobrenomeUsuario);
        edtEmail = (EditText) findViewById(R.id.emailUsuario);
        edtSenha = (EditText) findViewById(R.id.senhaUsuario);
        edtTel = (EditText) findViewById(R.id.telUsuario);
        cadastrar = (Button)findViewById(R.id.btnUserPF);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastrarUsuario();
            }
        });
    }

    private void CadastrarUsuario() {

        String nome = edtNome.getText().toString();
        String sobrenome = edtSobNome.getText().toString();
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();
        String telefone = edtTel.getText().toString();
        if (!TextUtils.isEmpty(nome) || !TextUtils.isEmpty(sobrenome)
                || !TextUtils.isEmpty(email) ||
                !TextUtils.isEmpty(senha) || !TextUtils.isEmpty(telefone)) {

            mProgessdialog.setTitle("Salvando Usuario...");
            mProgessdialog.setMessage("Aguarde enquanto criamos sua conta!");
            mProgessdialog.setCanceledOnTouchOutside(false);
            mProgessdialog.show();
            register_user(nome, sobrenome, email, senha, telefone);
        }
        if (edtSenha.getText().length() < 6) {
            Toast.makeText(this, "Sua senha precisa conter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
        }

    }
    private void register_user(final String nome, final String sobrenome, final String email, final String senha, final String telefone) {
                    if (!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(sobrenome)
                    && !TextUtils.isEmpty(email)
                    && !TextUtils.isEmpty(senha) && !TextUtils.isEmpty(telefone)) {

                mAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = current_user.getUid();


                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario").child(uid);

                                    String device_token = FirebaseInstanceId.getInstance().getToken();

                                    HashMap<String, String> userMap = new HashMap<>();
                                    userMap.put("nome", nome);
                                    userMap.put("sobrenome", sobrenome);
                                    userMap.put("telefone", telefone);
                                    userMap.put("email", email);
                                    userMap.put("imagem", "default");
                                    userMap.put("funcao", "default");
                                    userMap.put("online", "false");
                                    userMap.put("device_token", device_token);

                                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                mProgessdialog.dismiss();
                                                Intent telaInicial = new Intent(CadastroUsuario.this, MainActivity.class);
                                                telaInicial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(telaInicial);
                                                finish();
                                            }

                                        }
                                    });

                                } else {
                                    mProgessdialog.hide();
                                    Toast.makeText(CadastroUsuario.this, "" +
                                            "Falha ao cadastrar usuario. Por favor verifique o formulário e tente novamente", Toast.LENGTH_LONG).show();
                                }


                            }
                        });
            } else {
                mProgessdialog.hide();
                Toast.makeText(CadastroUsuario.this, "" +
                        "Falha ao cadastrar usuario. Todos os campos do formulário devem ser preenchidos...", Toast.LENGTH_LONG).show();

            }
        }
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(CadastroUsuario.this, MainActivity.class));
        finish();
        return true;
    }
}
