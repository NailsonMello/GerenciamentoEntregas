package com.exemplo.nailson.safad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DadosEntrega extends AppCompatActivity {

    private TextView codigo, nfe, fantasia, endereco, vendedor, status, motorista, entregador, carro, placa, data, ocorrencia, iduser, horasaida, horaentrada;
    private String mChave_anuncio = null;
    private String mMotorista = null;
    private Button mudarMot;
    private ImageView emRota, clienteDevolveu, proximaNota, aguardandoCliente, entregaRealizada, naoDeuTempo;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseNome;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseNotification;
    String email;
    String password;
    Session session;


    String Send_User_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_entrega);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);

        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(DadosEntrega.this);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        Send_User_Id = mAuth.getCurrentUser().getUid();

        mDatabaseNotification = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("Notification");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("entrega");
        mChave_anuncio = getIntent().getExtras().getString("entrega_id");
        mMotorista = getIntent().getExtras().getString("nome");

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("entrega").child(mChave_anuncio);
        iduser = (TextView)findViewById(R.id.id_userr);
        codigo = (TextView)findViewById(R.id.entregacodigo);
        ocorrencia = (TextView)findViewById(R.id.entregaocorrencio);
        nfe = (TextView)findViewById(R.id.entreganfe);
        fantasia = (TextView)findViewById(R.id.entregafantasia);
        endereco = (TextView)findViewById(R.id.entregaend);
        vendedor = (TextView)findViewById(R.id.entregavendedor);
        status = (TextView)findViewById(R.id.entregastatus);
        motorista = (TextView)findViewById(R.id.entregamotorista);
        entregador = (TextView)findViewById(R.id.entregaentregador);
        data = (TextView)findViewById(R.id.entregadata);
        horaentrada = (TextView)findViewById(R.id.horaEntrada);
        horasaida = (TextView)findViewById(R.id.horaSaida);
        mudarMot = (Button) findViewById(R.id.btnMudar);

        if (mAuth.getCurrentUser().getUid().equals("0EZzQYHUZ0W8qgYi3WAsOY8v4bV2")){
            mudarMot.setVisibility(View.VISIBLE);
        }
        emRota = (ImageView)findViewById(R.id.emrota);
        clienteDevolveu = (ImageView)findViewById(R.id.clientedevolveu);
        proximaNota = (ImageView)findViewById(R.id.proximanota);
        aguardandoCliente = (ImageView)findViewById(R.id.aguardando);
        entregaRealizada = (ImageView)findViewById(R.id.entregafeita);
        naoDeuTempo = (ImageView)findViewById(R.id.naodeutempo);

        if (mCurrentUser == null){
            emRota.setEnabled(false);
            clienteDevolveu.setEnabled(false);
            proximaNota.setEnabled(false);
            aguardandoCliente.setEnabled(false);
            entregaRealizada.setEnabled(false);
            naoDeuTempo.setEnabled(false);
            ocorrencia.setEnabled(false);
            horaentrada.setEnabled(false);
            horasaida.setEnabled(false);
        }


        horaentrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoraEntrada();
            }
        });
        horasaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoraSaida();
            }
        });
        emRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmrRota();
            }
        });
        clienteDevolveu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClienteDevolveu();
            }
        });
        proximaNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProximaNota();
            }
        });
        aguardandoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AguardadoCliente();
            }
        });
        entregaRealizada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntregaRealizada();
            }
        });
        naoDeuTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaoDeuTempo();
            }
        });
        mudarMot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntregaAntiga();
            }
        });


        String id = mCurrentUser.getUid();
        mDatabaseNome = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario").child(id);

        MostraEntrega();
        email = "safadapp@gmail.com";
        password = "safad@1234";

    }

        private void MostraEntrega() {

        mDatabase.child(mChave_anuncio).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String codigoentrega = dataSnapshot.child("codigo").getValue().toString();
                String danfeentrega = dataSnapshot.child("danfe").getValue().toString();
                String fantasiaentrega = dataSnapshot.child("fantasia").getValue().toString();
                String enderecoentrega = dataSnapshot.child("bairro").getValue().toString();
                String vendedorentrega = dataSnapshot.child("vendedor").getValue().toString();
                String statusentrega = dataSnapshot.child("status").getValue().toString();
                String motoristaentrega = dataSnapshot.child("motorista").getValue().toString();
                String entregadorentrega = dataSnapshot.child("entregador").getValue().toString();
                String dataentrega = dataSnapshot.child("data").getValue().toString();
                // String idusuario = dataSnapshot.child("idUsuario").getValue().toString();
                String ocorrencias = dataSnapshot.child("ocorrencia").getValue().toString();
                String entrada = dataSnapshot.child("horaentrada").getValue().toString();
                String saida = dataSnapshot.child("horasaida").getValue().toString();

                String limpaNumero = onlyText(fantasiaentrega);
                String limpaTexto = onlyNumbers(codigoentrega);
                ActionBar act = getSupportActionBar();
                act.setTitle(fantasiaentrega);
                codigo.setText(limpaTexto);
                nfe.setText(danfeentrega);
                endereco.setText(enderecoentrega);
                fantasia.setText(limpaNumero);
                vendedor.setText(vendedorentrega);
                status.setText(statusentrega);
                motorista.setText(motoristaentrega);
                entregador.setText(entregadorentrega);
                data.setText(dataentrega);
                ocorrencia.setText(ocorrencias);
                // iduser.setText(idusuario);
                horaentrada.setText(entrada);
                horasaida.setText(saida);

                mDatabaseNome.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nome = dataSnapshot.child("nome").getValue().toString();
                        if (motorista.getText().toString().equals(nome)) {

                            emRota.setEnabled(true);
                            clienteDevolveu.setEnabled(true);
                            proximaNota.setEnabled(true);
                            aguardandoCliente.setEnabled(true);
                            entregaRealizada.setEnabled(true);
                            naoDeuTempo.setEnabled(true);
                            ocorrencia.setEnabled(true);
                            horaentrada.setEnabled(true);
                            horasaida.setEnabled(true);

                        }else {
                            emRota.setEnabled(false);
                            clienteDevolveu.setEnabled(false);
                            proximaNota.setEnabled(false);
                            aguardandoCliente.setEnabled(false);
                            entregaRealizada.setEnabled(false);
                            naoDeuTempo.setEnabled(false);
                            ocorrencia.setEnabled(false);
                            horaentrada.setEnabled(false);
                            horasaida.setEnabled(false);


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

        ocorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alerta = new AlertDialog.Builder(DadosEntrega.this);
                LayoutInflater inflater = getLayoutInflater();
                final View viewAlerta = inflater.inflate(R.layout.editar, null);
                alerta.setView(viewAlerta);

                final EditText editarDetalhes = (EditText) viewAlerta.findViewById(R.id.ocorrenciaEditar);

                alerta.setTitle("Editando detalhes...");
                alerta.setIcon(android.R.drawable.ic_menu_edit);
                alerta.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alerta.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String detalhes = editarDetalhes.getText().toString();
                        mDatabaseUser.child("ocorrencia").setValue(detalhes).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    if (editarDetalhes.getText().toString().equals("")){
                                        mDatabaseUser.child("ocorrencia").setValue("Sem detalhes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }

                                }
                            }
                        });
                    }

                });



                AlertDialog alertTermo = alerta.create();
                alertTermo.show();
            }
        });
    }


    //serve para limpar os numeros
    public static String onlyText(String str) {
        if (str != null) {
            return str.replaceAll("[^aAbBcCdDeEfFgGhHiIjJlLmMnNoOpPqQrRsStTuUvVxXzZyYwW.,;:/???????? ]", "");
        } else {
            return "";
        }
    }

    //serve para limpar os caracters
    public static String onlyNumbers(String str) {
        if (str != null) {
            return str.replaceAll("[^0123456789]", "");
        } else {
            return "";
        }
    }
    private void HoraSaida() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        String dataFormatada = sdf.format(hora);
        mDatabaseUser.child("horasaida").setValue(dataFormatada).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        });
    }

    private void EntregaAntiga() {
        sendEmail();
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sdf.format(date);

        String sta = "Entregue: "+ dateString+" por: "+  mMotorista;

        mDatabaseUser.child("motorista").setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    finish();
                }
            }
        });

    }

    private void sendEmail() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","465");

        try {
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

            if (session !=null){


                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(email));
                message.setSubject(nfe.getText().toString());
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("adm.ba@romadistribuidora.com.br"));
                message.setContent("Codigo cliente: "+codigo.getText().toString()+
                        "\nNfe: "+nfe.getText().toString()+
                        "\nCliente: "+fantasia.getText().toString()+
                        "\nVendedor: "+vendedor.getText().toString()+
                        "\nData: "+data.getText().toString()+
                        "\nStatus da entrega: "+status.getText().toString()+
                        "\nMotorista: "+entregador.getText().toString()+
                        "\nEntregador: "+motorista.getText().toString()+
                        "\nOcorrencia: "+ocorrencia.getText().toString(), "text/plain; charset=utf-8");
                Transport.send(message);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void HoraEntrada() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        String dataFormatada = sdf.format(hora);

        mDatabaseUser.child("horaentrada").setValue(dataFormatada).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        });
    }
    private void NaoDeuTempo() {
        String sta = "Não deu tempo";

        mDatabaseUser.child("status").setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    enviarNotification();
                }
            }
        });
    }

    private void EntregaRealizada() {
        String sta = "Entrega realizada";

        mDatabaseUser.child("status").setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    enviarNotification();
                }
            }
        });
    }

    private void AguardadoCliente() {
        String sta = "Aguardando Cliente";

        mDatabaseUser.child("status").setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    enviarNotification();
                }
            }
        });
    }

    private void ProximaNota() {
        String sta = "Proxima Entrega";

        mDatabaseUser.child("status").setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    enviarNotification();
                }
            }
        });
    }

    private void ClienteDevolveu() {

        String sta = "Cliente devolveu";

        mDatabaseUser.child("status").setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    enviarNotification();
                }
            }
        });
    }

    private void EmrRota() {

        String sta = "Em Rota";

        mDatabaseUser.child("status").setValue(sta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    enviarNotification();
                }
            }
        });
    }

    public void enviarNotification(){

        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("Id_User", Send_User_Id);
        notificationData.put("type", "request");
        notificationData.put("Id_entrega", mChave_anuncio);

        mDatabaseNotification.child(mChave_anuncio).push().setValue(notificationData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }
    @Override
    public void onBackPressed() {
        //Intent telaAnuncio = new Intent(DadosEntrega.this, MainActivity.class);
        //startActivity(telaAnuncio);
        finish();

    }
    @Override
    public boolean onSupportNavigateUp(){
        //startActivity(new Intent(DadosEntrega.this, MainActivity.class));
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editar, menu);
        menu.removeItem(R.id.editar_entrega);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }



}
