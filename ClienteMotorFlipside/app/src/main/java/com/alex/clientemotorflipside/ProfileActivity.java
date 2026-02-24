package com.alex.clientemotorflipside; // Asegúrate que sea tu paquete correcto

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView lblNombre, lblEmail, lblTelefono, lblRol;
    private Button btnCerrarSesion, btnVolver;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Vincular vistas
        lblNombre = findViewById(R.id.lblNombrePerfil);
        lblEmail = findViewById(R.id.lblEmailPerfil);
        lblTelefono = findViewById(R.id.lblTelefonoPerfil);
        lblRol = findViewById(R.id.lblRolPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnVolver = findViewById(R.id.btnVolverHome);

        cargarDatosUsuario();

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut(); // Esto borra la sesión del móvil

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            lblEmail.setText(user.getEmail());
            String uid = user.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            String telefono = documentSnapshot.getString("telefono");
                            String rol = documentSnapshot.getString("rol");

                            lblNombre.setText(nombre != null ? nombre : "Sin nombre");
                            lblTelefono.setText(telefono != null ? telefono : "Sin teléfono");
                            lblRol.setText(rol != null ? rol : "Usuario");

                        } else {
                            Log.d("PERFIL", "El usuario no tiene datos en Firestore");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error cargando perfil", Toast.LENGTH_SHORT).show();
                        Log.e("PERFIL", "Error: ", e);
                    });
        }
    }
}