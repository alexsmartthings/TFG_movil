package com.alex.clientemotorflipside;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnPerfil = findViewById(R.id.btnIrPerfil);
        Button btnCita = findViewById(R.id.btnPedirCita);
        Button btnAddMoto = findViewById(R.id.btnRegistrarMoto);
        Button btnReparaciones = findViewById(R.id.btnVerReparaciones);
        Button btnMisMotos = findViewById(R.id.btnMisMotos);
        Button btnLogout = findViewById(R.id.btnCerrarSesion);

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });


        btnCita.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SolicitudCitaActivity.class);
            startActivity(intent);
        });

        btnAddMoto.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ActivityMoto.class));
        });

        btnReparaciones.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ListaReparacionesActivity.class));
        });
        if (btnMisMotos != null) {
            btnMisMotos.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, MisMotosActivity.class));
            });
        }
        btnLogout.setOnClickListener(v -> {
            // 1. Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut();

            // 2. Volver a la pantalla de inicio (MainActivity / Login)
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);

            // 3. ¡IMPORTANTE! Borrar el historial de pantallas
            // Esto evita que el usuario pueda volver atrás pulsando el botón físico
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}