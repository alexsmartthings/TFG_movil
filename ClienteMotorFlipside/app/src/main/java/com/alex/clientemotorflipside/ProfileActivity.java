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

        // 1. Inicializar
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Vincular vistas
        lblNombre = findViewById(R.id.lblNombrePerfil);
        lblEmail = findViewById(R.id.lblEmailPerfil);
        lblTelefono = findViewById(R.id.lblTelefonoPerfil);
        lblRol = findViewById(R.id.lblRolPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnVolver = findViewById(R.id.btnVolverHome);

        // 3. Cargar datos
        cargarDatosUsuario();

        // 4. Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut(); // Esto borra la sesión del móvil

            // Volvemos al Login y borramos el historial para no poder volver atrás
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 5. Botón Volver
        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Datos que ya tenemos gracias al Login (Auth)
            lblEmail.setText(user.getEmail());
            String uid = user.getUid();

            // Datos que faltan: Vamos a Firestore a por ellos
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // El documento existe, sacamos los campos
                            String nombre = documentSnapshot.getString("nombre");
                            String telefono = documentSnapshot.getString("telefono");
                            String rol = documentSnapshot.getString("rol");

                            // Actualizamos la pantalla
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