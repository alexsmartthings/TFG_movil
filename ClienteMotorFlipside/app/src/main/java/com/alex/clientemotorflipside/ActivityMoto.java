package com.alex.clientemotorflipside;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityMoto extends AppCompatActivity {

    private EditText txtMarca, txtModelo, txtMatricula, txtAno;
    private Button btnGuardar, btnCancelar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        txtMarca = findViewById(R.id.txtMarcaMoto);
        txtModelo = findViewById(R.id.txtModeloMoto);
        txtMatricula = findViewById(R.id.txtMatriculaMoto);
        txtAno = findViewById(R.id.txtAnoMoto);
        btnGuardar = findViewById(R.id.btnGuardarMoto);
        btnCancelar = findViewById(R.id.btnCancelarMoto);

        btnGuardar.setOnClickListener(v -> guardarMoto());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarMoto() {
        String marca = txtMarca.getText().toString().trim();
        String modelo = txtModelo.getText().toString().trim();
        String matricula = txtMatricula.getText().toString().trim().toUpperCase(); // Matrículas en mayúsculas quedan mejor
        String ano = txtAno.getText().toString().trim();

        if (TextUtils.isEmpty(marca) || TextUtils.isEmpty(modelo) || TextUtils.isEmpty(matricula)) {
            Toast.makeText(this, "Marca, Modelo y Matrícula son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // PREPARAMOS LOS DATOS
        Map<String, Object> moto = new HashMap<>();
        moto.put("marca", marca);
        moto.put("modelo", modelo);
        moto.put("matricula", matricula);
        moto.put("ano", ano); // Puedes convertirlo a int si prefieres: Integer.parseInt(ano)

        // --- CAMPOS CLAVE ---
        moto.put("tipo", "CLIENTE"); // Esto diferencia tu moto de las de venta
        moto.put("id_cliente", mAuth.getCurrentUser().getUid()); // La vinculamos contigo
        moto.put("fecha_registro", FieldValue.serverTimestamp());

        // GUARDAR EN FIRESTORE
        db.collection("motos").add(moto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Moto registrada en tu garaje!", Toast.LENGTH_LONG).show();
                    finish(); // Volvemos atrás
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}