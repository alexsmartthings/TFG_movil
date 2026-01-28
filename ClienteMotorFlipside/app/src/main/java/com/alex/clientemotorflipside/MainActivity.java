package com.alex.clientemotorflipside;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // 1. Declarar variables de la interfaz y Firebase
    private EditText txtEmail, txtPassword;
    private Button btnLogin;
    private TextView lblError, lblIrRegistro; // Añadido el enlace al registro
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Enlaza con tu diseño XML

        // 2. Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 3. Comprobar si ya hay una sesión iniciada (Opcional, pero muy útil)
        // Si el usuario ya entró ayer, no le pedimos login otra vez.
        if (mAuth.getCurrentUser() != null) {
            irAHome();
        }

        // 4. Vincular variables con los IDs del XML (activity_main.xml)
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        lblError = findViewById(R.id.lblError);
        lblIrRegistro = findViewById(R.id.lblIrRegistro); // El ID nuevo que pusimos

        // 5. Configurar el Click del botón LOGIN
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarLogin();
            }
        });

        // 6. Configurar el Click del texto "REGISTRARSE"
        lblIrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrimos la pantalla de registro
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void intentarLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        // Validaciones básicas: que no estén vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            lblError.setText("Por favor, rellena todos los campos");
            return;
        }

        // --- LÓGICA DE FIREBASE ---
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login Correcto
                            Toast.makeText(MainActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();

                            // Navegar a la pantalla principal
                            irAHome();

                        } else {
                            // Login Fallido
                            String errorMsg = "Error al iniciar sesión";
                            if (task.getException() != null) {
                                // Esto te da una pista de qué falló (clave mal, usuario no existe...)
                                errorMsg = task.getException().getMessage();
                            }
                            lblError.setText(errorMsg);
                        }
                    }
                });
    }

    private void irAHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        // Estas "Flags" sirven para borrar el historial.
        // Así, si el usuario da al botón "Atrás" desde el Home, se sale de la app en vez de volver al Login.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Cierra esta actividad actual
    }
}