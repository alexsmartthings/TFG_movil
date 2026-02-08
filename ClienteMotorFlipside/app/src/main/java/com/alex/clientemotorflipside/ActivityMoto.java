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

    private EditText txtMarca, txtModelo, txtMatricula, txtAno, txtColor, txtKms;
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
        txtColor = findViewById(R.id.txtColorMoto);
        txtKms = findViewById(R.id.txtKmsMoto);
        btnGuardar = findViewById(R.id.btnGuardarMoto);
        btnCancelar = findViewById(R.id.btnCancelarMoto);

        // HEMOS BORRADO TODA LA LÓGICA DE CARGAR DATOS

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

        Map<String, Object> moto = new HashMap<>();
        moto.put("marca", marca);
        moto.put("modelo", modelo);
        moto.put("matricula", matricula);
        moto.put("ano", ano);
        moto.put("color", color);
        moto.put("kms", kms);
        moto.put("tipo", "CLIENTE");
        moto.put("id_cliente", mAuth.getCurrentUser().getUid());
        moto.put("fecha_registro", FieldValue.serverTimestamp());

        // SOLO GUARDAR (ADD)
        db.collection("motos").add(moto)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Moto guardada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}