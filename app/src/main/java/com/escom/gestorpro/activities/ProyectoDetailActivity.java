package com.escom.gestorpro.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.escom.gestorpro.R;
import com.escom.gestorpro.providers.AuthProvider;
import com.escom.gestorpro.providers.ProyectoProvider;
import com.escom.gestorpro.providers.TareaProvider;
import com.escom.gestorpro.providers.UserProvider;
import com.escom.gestorpro.utils.RelativeTime;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProyectoDetailActivity extends AppCompatActivity {
    String mExtraProyectoId;
    String mIdUser = "";
    String url = "";
    int total_tareas = 0;

    ProyectoProvider mProyectoProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    TareaProvider mTareaProvider;

    TextView textViewTitulo;
    TextView textViewCodigo;
    TextView textViewFechaInicio;
    TextView textViewFechaFin;
    TextView textViewUsuario;
    TextView textViewCel;
    TextView textViewTareas;
    TextView textViewAvance;
    CircleImageView circleImageViewProfile;
    ImageView mImageViewBack;
    Button btVerPerfil;
    Button btnEliminar;
    Button btnCompletado;
    Button btnCuestionario;
    Toolbar toolbar;
    LinearLayout mLinearLayoutEditProyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyecto_detail);

        mProyectoProvider = new ProyectoProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mTareaProvider = new TareaProvider();

        textViewTitulo = findViewById(R.id.textViewTituloProyecto);
        textViewCodigo = findViewById(R.id.textViewCodigoProyecto);
        textViewFechaInicio = findViewById(R.id.textViewRelativeTimeInicio);
        textViewFechaFin = findViewById(R.id.textViewRelativeTimeFinal);
        textViewUsuario = findViewById(R.id.textViewUsuarioPD);
        textViewCel = findViewById(R.id.textViewCelPD);
        textViewTareas = findViewById(R.id.textViewTareas);
        textViewAvance = findViewById(R.id.textViewAvanceProyecto);
        circleImageViewProfile = findViewById(R.id.circleImageProyectoDetail);
        mImageViewBack = findViewById(R.id.imageViewBack);
        btVerPerfil = findViewById(R.id.btnVerPerfil);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnCompletado = findViewById(R.id.btnProyectoCompleto);
        btnCuestionario = findViewById(R.id.btnCuestionario);
        toolbar = findViewById(R.id.toolbar);
        mLinearLayoutEditProyecto = findViewById(R.id.linearLayoutEditProyecto);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mExtraProyectoId = getIntent().getStringExtra("id");

        btVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(mExtraProyectoId, mAuthProvider.getUid());
            }
        });

        btnCompletado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completado(mExtraProyectoId);
            }
        });

        btnCuestionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains("rol")){
                    String rol = documentSnapshot.getString("rol");
                    if (rol.equals("Líder de proyecto")){
                        mLinearLayoutEditProyecto.setVisibility(View.VISIBLE);
                    }
                    else{
                        mLinearLayoutEditProyecto.setVisibility(View.GONE);
                    }
                }
            }
        });

        mLinearLayoutEditProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditTarea();
            }
        });

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkProyectoCompletado();
        checkRol();
        getProyecto();
        getNumberTareas();
        getAvance();
        getCuestionario();

    }

    private void getCuestionario() {
        mProyectoProvider.getProyectoById(mExtraProyectoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("completo")){
                        int completo = documentSnapshot.getLong("completo").intValue();
                        if (completo == 1){
                            url = documentSnapshot.getString("cuestionario");
                            btnCuestionario.setVisibility(View.VISIBLE);

                        }
                        else{
                            btnCuestionario.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void goToEditTarea() {
        Intent intent = new Intent(ProyectoDetailActivity.this, EditProyectoActivity.class);
        intent.putExtra("idProyecto", mExtraProyectoId);
        startActivity(intent);
    }

    private void checkRol() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains("rol")){
                    String rol = documentSnapshot.getString("rol");
                    if (rol.equals("Líder de proyecto")){
                        btnCompletado.setVisibility(View.VISIBLE);
                    }
                    else{
                        btnCompletado.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void checkProyectoCompletado() {
        mProyectoProvider.getProyectoById(mExtraProyectoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("completo")) {
                        int avance = documentSnapshot.getLong("completo").intValue();
                        if (avance == 0) {
                            btnCompletado.setText("Marcar proyecto como completado");
                        }
                        else{
                            btnCompletado.setText("Marcar proyecto como incompleto");
                        }
                    }
                }
            }
        });
    }

    private void completado(String mExtraProyectoId) {
        final int[] value = new int[1];
        mProyectoProvider.getProyectoById(mExtraProyectoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("completo")){
                        int avance = documentSnapshot.getLong("completo").intValue();
                        if (avance == 0){
                            //Tarea marcada como incompleta
                            value[0] = 1;
                        }
                        else{
                            //Tarea marcada como completa
                            value[0] = 0;
                        }
                        mProyectoProvider.updateAvance(mExtraProyectoId, value[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if (value[0] == 1){
                                        btnCompletado.setText("Marcar proyecto como incompleto");
                                    }
                                    else{
                                        btnCompletado.setText("Marcar proyecto como completado");
                                    }
                                    Toast.makeText(ProyectoDetailActivity.this, "Proyecto actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void getAvance() {
        mTareaProvider.getTareaCompletoByProyecto(mExtraProyectoId, 1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    int tareas_completadas = queryDocumentSnapshots.size();
                    if (total_tareas != 0){
                        double avance = (tareas_completadas*100.00)/total_tareas;
                        textViewAvance.setText(new DecimalFormat("0.00").format(avance) + "% de avance");
                    }
                    else{
                        textViewAvance.setText("No hay tareas para calcular avance");
                    }
                }
            }
        });

    }

    private void getNumberTareas() {
        mTareaProvider.getTareasTotalByProyecto(mExtraProyectoId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    total_tareas = queryDocumentSnapshots.size();
                    if (total_tareas == 1){
                        textViewTareas.setText("1 Tarea asignada");
                    }
                    else{
                        textViewTareas.setText(total_tareas + " Tareas asignadas");
                    }
                }

            }
        });
    }

    private void getProyecto() {
        mProyectoProvider.getProyectoById(mExtraProyectoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    if (documentSnapshot.contains("fecha_inicio")) {
                        long timestamp_inicio = documentSnapshot.getLong("fecha_inicio");
                        Date date1 = new Date(timestamp_inicio);
                        String fecha_inicio = formatter.format(date1);
                        textViewFechaInicio.setText("Fecha inicio: " + fecha_inicio);
                    }
                    if (documentSnapshot.contains("fecha_fin")) {
                        long timestamp_fin = documentSnapshot.getLong("fecha_fin");
                        Date date2 = new Date(timestamp_fin);
                        String fecha_fin = formatter.format(date2);
                        textViewFechaFin.setText("Fecha fin: " + fecha_fin);
                    }
                    if (documentSnapshot.contains("equipo")){
                        ArrayList<String> list = (ArrayList<String>) documentSnapshot.get("equipo");
                        String liderId = list.get(0);
                        getUserInfo(liderId);
                    }
                    if (documentSnapshot.contains("nombre")){
                        String titulo = documentSnapshot.getString("nombre");
                        textViewTitulo.setText(titulo.toUpperCase());
                    }
                    if (documentSnapshot.contains("codigo")){
                        String codigo = documentSnapshot.getString("codigo");
                        textViewCodigo.setText("Código para unirse: " + codigo);
                    }
                }
            }
        });
    }

    private void getUserInfo(String idUser) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("id")) {
                        mIdUser = documentSnapshot.getString("id");
                    }
                    if (documentSnapshot.contains("usuario")) {
                        String usuario = documentSnapshot.getString("usuario");
                        textViewUsuario.setText(usuario);
                    }
                    if (documentSnapshot.contains("celular")) {
                        String phone = documentSnapshot.getString("celular");
                        textViewCel.setText(phone);
                    }
                    if (documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        Picasso.with(ProyectoDetailActivity.this).load(imageProfile).into(circleImageViewProfile);
                    }
                }
            }
        });
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains("rol")){
                    String rol = documentSnapshot.getString("rol");
                    if (rol.equals("Líder de proyecto")){
                        btnEliminar.setText("Eliminar proyecto");
                    }
                    else{
                        btnEliminar.setText("Abandonar proyecto");
                    }
                }
            }
        });
    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")){
            Intent intent = new Intent(ProyectoDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("usuario", mIdUser);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "El id del usuario aún no se carga", Toast.LENGTH_LONG).show();
        }

    }

    private void showConfirmDelete(String idProyecto, String idUsuario) {
        new AlertDialog.Builder(ProyectoDetailActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar proyecto")
                .setMessage("¿Estas seguro de realizar esta acción?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProyecto(idProyecto, idUsuario);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void deleteProyecto(String idProyecto, String idUsuario) {
        mProyectoProvider.getProyectoById(mExtraProyectoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains("equipo")){
                    ArrayList<String> list = (ArrayList<String>) documentSnapshot.get("equipo");
                    String liderId = list.get(0);
                    if (liderId.equals(idUsuario)){
                        mProyectoProvider.deleteProyecto(idProyecto);
                        Toast.makeText(ProyectoDetailActivity.this, "Se eliminó el proyecto", Toast.LENGTH_LONG).show();
                    }
                    else{
                        mProyectoProvider.deleteUsuarioFromProyecto(idProyecto, idUsuario);
                        Toast.makeText(ProyectoDetailActivity.this, "Abandonaste el proyecto", Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            }
        });
    }
}