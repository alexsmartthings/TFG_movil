package com.alex.clientemotorflipside;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ActivityMoto extends AppCompatActivity {

    private TextView lblTitulo;
    private EditText txtMarca, txtModelo, txtMatricula, txtAno, txtColor, txtKms;
    private Button btnGuardar, btnCancelar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Variable para saber si estamos editando (tendrá texto) o creando (será null)
    private String motoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 1. VINCULAR VISTAS (Usando tus IDs correctos)
        lblTitulo = findViewById(R.id.lblTitulo);
        txtMarca = findViewById(R.id.txtMarcaMoto);
        txtModelo = findViewById(R.id.txtModeloMoto);
        txtMatricula = findViewById(R.id.txtMatriculaMoto);
        txtAno = findViewById(R.id.txtAnoMoto);
        txtColor = findViewById(R.id.txtColorMoto);
        txtKms = findViewById(R.id.txtKmsMoto);
        btnGuardar = findViewById(R.id.btnGuardarMoto);
        btnCancelar = findViewById(R.id.btnCancelarMoto);

        // 2. VERIFICAR SI VENIMOS A EDITAR
        if (getIntent().hasExtra("id_documento")) {
            motoId = getIntent().getStringExtra("id_documento");

            // Rellenar campos con los datos que pasamos desde la lista
            txtMarca.setText(getIntent().getStringExtra("marca"));
            txtModelo.setText(getIntent().getStringExtra("modelo"));
            txtMatricula.setText(getIntent().getStringExtra("matricula"));
            txtAno.setText(getIntent().getStringExtra("ano"));
            txtColor.setText(getIntent().getStringExtra("color"));
            txtKms.setText(getIntent().getStringExtra("kms"));

            btnGuardar.setText("ACTUALIZAR MOTO"); // Cambio visual
            lblTitulo.setText("EDITAR MOTO");
        }

        btnGuardar.setOnClickListener(v -> guardarMoto());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarMoto() {
        String marca = txtMarca.getText().toString().trim();
        String modelo = txtModelo.getText().toString().trim();
        String matricula = txtMatricula.getText().toString().trim().toUpperCase();
        String ano = txtAno.getText().toString().trim();
        String color = txtColor.getText().toString().trim();
        String kmsStr = txtKms.getText().toString().trim();

        if (TextUtils.isEmpty(marca) || TextUtils.isEmpty(modelo) || TextUtils.isEmpty(matricula)) {
            Toast.makeText(this, "Rellena los datos principales", Toast.LENGTH_SHORT).show();
            return;
        }

        int kms = 0;
        try { if (!kmsStr.isEmpty()) kms = Integer.parseInt(kmsStr); } catch (Exception e) {}

        // Preparamos el mapa de datos
        Map<String, Object> moto = new HashMap<>();
        moto.put("marca", marca);
        moto.put("modelo", modelo);
        moto.put("matricula", matricula);
        moto.put("ano", ano); // Se guarda como String (correcto)
        moto.put("color", color);
        moto.put("kms", kms);

        // Estos campos solo los ponemos si es nueva o si queremos asegurarnos que no se pierden
        moto.put("id_cliente", mAuth.getCurrentUser().getUid());

        // DECISIÓN: ¿CREAR O ACTUALIZAR?
        if (motoId != null) {
            // --- MODO EDICIÓN ---
            db.collection("motos").document(motoId)
                    .update(moto)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Moto actualizada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
        } else {
            // --- MODO CREACIÓN ---
            moto.put("fecha_registro", FieldValue.serverTimestamp()); // Solo al crear

            db.collection("motos").add(moto)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Moto guardada", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}