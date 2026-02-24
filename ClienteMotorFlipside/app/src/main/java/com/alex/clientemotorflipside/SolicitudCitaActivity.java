package com.alex.clientemotorflipside;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolicitudCitaActivity extends AppCompatActivity {

    private Spinner spinnerMotos;
    private EditText txtDireccion, txtAveria;
    private Button btnEnviar, btnCancelar;

    private List<String> listaNombresMotos = new ArrayList<>();
    private List<String> listaIdsMotos = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_cita);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        spinnerMotos = findViewById(R.id.spinnerMotos);
        txtDireccion = findViewById(R.id.txtDireccionCita);
        txtAveria = findViewById(R.id.txtAveria);
        btnEnviar = findViewById(R.id.btnEnviarSolicitud);
        btnCancelar = findViewById(R.id.btnCancelarCita);

        cargarMisMotos();
        cargarMiDireccion();

        btnEnviar.setOnClickListener(v -> enviarSolicitud());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarMisMotos() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("motos")
                .whereEqualTo("id_cliente", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaNombresMotos.clear();
                    listaIdsMotos.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String marca = doc.getString("marca");
                        String modelo = doc.getString("modelo");

                        listaNombresMotos.add(marca + " " + modelo);
                        listaIdsMotos.add(doc.getId());
                    }

                    if (listaNombresMotos.isEmpty()) {
                        listaNombresMotos.add("No tienes motos registradas");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_dropdown_item, listaNombresMotos);
                    spinnerMotos.setAdapter(adapter);
                });
    }

    private void cargarMiDireccion() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String dir = doc.getString("direccion");
                        if (dir != null) {
                            txtDireccion.setText(dir);
                        }
                    }
                });
    }

    private void enviarSolicitud() {
        String direccion = txtDireccion.getText().toString().trim();
        String averia = txtAveria.getText().toString().trim();
        int posicionMoto = spinnerMotos.getSelectedItemPosition();

        if (TextUtils.isEmpty(direccion) || TextUtils.isEmpty(averia)) {
            Toast.makeText(this, "Por favor, rellena todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaIdsMotos.isEmpty()) {
            Toast.makeText(this, "Necesitas tener una moto para pedir cita", Toast.LENGTH_LONG).show();
            return;
        }

        String idMotoSeleccionada = listaIdsMotos.get(posicionMoto);
        String nombreMoto = listaNombresMotos.get(posicionMoto);

        Map<String, Object> cita = new HashMap<>();
        cita.put("id_cliente", mAuth.getCurrentUser().getUid());
        cita.put("email_cliente", mAuth.getCurrentUser().getEmail());
        cita.put("id_moto", idMotoSeleccionada);
        cita.put("moto_resumen", nombreMoto);
        cita.put("direccion_recogida", direccion);
        cita.put("descripcion_averia", averia);
        cita.put("estado", "PENDIENTE");
        cita.put("fecha_solicitud", FieldValue.serverTimestamp());

        db.collection("citas").add(cita)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "¡Solicitud Enviada! Pasaremos a buscarla.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}