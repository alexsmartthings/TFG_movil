package com.alex.clientemotorflipside;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Importante para los logs
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtNombre, txtTelefono, txtEmail, txtPass;
    private Button btnRegistrar;
    private TextView lblVolver;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtNombre = findViewById(R.id.txtNombreReg);
        txtTelefono = findViewById(R.id.txtTelefonoReg);
        txtEmail = findViewById(R.id.txtEmailReg);
        txtPass = findViewById(R.id.txtPassReg);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        lblVolver = findViewById(R.id.lblVolverLogin);

        Log.d("MI_APP", "Pantalla de Registro iniciada");

        btnRegistrar.setOnClickListener(v -> {
            Log.d("MI_APP", "Botón Registrar pulsado");
            realizarRegistro();
        });

        lblVolver.setOnClickListener(v -> {
            Log.d("MI_APP", "Volviendo al login...");
            finish(); // cierra esta ventana y vuelve al login
        });
    }

    private void realizarRegistro() {
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();

        Log.d("MI_APP", "Datos introducidos: " + email + " | Nombre: " + nombre);

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {

            Log.w("MI_APP", "Error: Campos vacíos");
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Log.w("MI_APP", "Error: Contraseña muy corta");
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MI_APP", "Intentando crear usuario en Auth...");

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("MI_APP", "¡Usuario Auth creado con éxito!");

                        String uid = mAuth.getCurrentUser().getUid();
                        guardarDatosFirestore(uid, nombre, email, telefono);

                    } else {
                        String errorMsg = task.getException().getMessage();
                        Log.e("MI_APP", "Fallo en Auth: " + errorMsg);
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarDatosFirestore(String uid, String nombre, String email, String telefono) {
        Log.d("MI_APP", "Guardando datos adicionales en Firestore...");

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nombre", nombre);
        userMap.put("email", email);
        userMap.put("telefono", telefono);
        userMap.put("rol", "CLIENTE"); // Importante para tu lógica de negocio

        db.collection("users").document(uid).set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MI_APP", "¡Datos guardados en Firestore correctamente!");
                    Toast.makeText(RegisterActivity.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("MI_APP", "Error escribiendo en Firestore: " + e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Cuenta creada pero falló al guardar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}