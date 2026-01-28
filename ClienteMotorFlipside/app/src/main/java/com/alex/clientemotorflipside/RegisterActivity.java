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

    // Variables de la interfaz
    private EditText txtNombre, txtTelefono, txtEmail, txtPass;
    private Button btnRegistrar;
    private TextView lblVolver;

    // Variables de Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Asegúrate de que tu XML se llame así

        // 1. INICIALIZAR FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. VINCULAR CON EL DISEÑO (XML)
        // Revisa que estos IDs coincidan con tu activity_register.xml
        txtNombre = findViewById(R.id.txtNombreReg);
        txtTelefono = findViewById(R.id.txtTelefonoReg);
        txtEmail = findViewById(R.id.txtEmailReg);
        txtPass = findViewById(R.id.txtPassReg);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        lblVolver = findViewById(R.id.lblVolverLogin);

        // Debug: Aviso de que la pantalla cargó bien
        Log.d("MI_APP", "Pantalla de Registro iniciada");

        // 3. EVENTOS DE CLIC
        btnRegistrar.setOnClickListener(v -> {
            Log.d("MI_APP", "Botón Registrar pulsado");
            realizarRegistro();
        });

        lblVolver.setOnClickListener(v -> {
            Log.d("MI_APP", "Volviendo al login...");
            finish(); // Simplemente cierra esta ventana y vuelve a la anterior (Login)
        });
    }

    private void realizarRegistro() {
        // Obtenemos el texto limpio (sin espacios al final)
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();

        Log.d("MI_APP", "Datos introducidos: " + email + " | Nombre: " + nombre);

        // --- VALIDACIONES ---

        // 1. Campos vacíos
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {

            Log.w("MI_APP", "Error: Campos vacíos");
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Contraseña corta (Firebase pide mínimo 6)
        if (pass.length() < 6) {
            Log.w("MI_APP", "Error: Contraseña muy corta");
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- CREAR USUARIO EN AUTH ---
        Log.d("MI_APP", "Intentando crear usuario en Auth...");

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("MI_APP", "¡Usuario Auth creado con éxito!");

                        // Obtenemos el UID único que Firebase le ha asignado
                        String uid = mAuth.getCurrentUser().getUid();

                        // Ahora guardamos el resto de datos en la base de datos
                        guardarDatosFirestore(uid, nombre, email, telefono);

                    } else {
                        // Fallo al crear (ej: email ya existe, sin internet, etc.)
                        String errorMsg = task.getException().getMessage();
                        Log.e("MI_APP", "Fallo en Auth: " + errorMsg);
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarDatosFirestore(String uid, String nombre, String email, String telefono) {
        Log.d("MI_APP", "Guardando datos adicionales en Firestore...");

        // Creamos el diccionario de datos
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nombre", nombre);
        userMap.put("email", email);
        userMap.put("telefono", telefono);
        userMap.put("rol", "CLIENTE"); // Importante para tu lógica de negocio

        // --- GUARDAR EN COLECCIÓN "users" ---
        // Usamos .document(uid) para que el ID del documento sea igual al UID del Auth
        db.collection("users").document(uid).set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MI_APP", "¡Datos guardados en Firestore correctamente!");
                    Toast.makeText(RegisterActivity.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();

                    // Navegar a la pantalla principal (Home)
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    // Estas flags borran el historial para que no puedas volver al registro con 'atrás'
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("MI_APP", "Error escribiendo en Firestore: " + e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Cuenta creada pero falló al guardar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Opcional: Podrías dejar pasar al usuario o borrar la cuenta creada en Auth si falla esto.
                });
    }
}