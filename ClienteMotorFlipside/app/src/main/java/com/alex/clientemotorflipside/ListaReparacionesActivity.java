package com.alex.clientemotorflipside;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ListaReparacionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReparacionesAdapter adapter;
    private List<DocumentSnapshot> listaCitas = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reparaciones);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerReparaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adaptador vacío
        adapter = new ReparacionesAdapter(listaCitas);
        recyclerView.setAdapter(adapter);

        cargarMisReparaciones();
    }

    private void cargarMisReparaciones() {
        String uid = mAuth.getCurrentUser().getUid();

        // Consulta: Citas de ESTE cliente, ordenadas por fecha (más recientes primero)
        db.collection("citas")
                .whereEqualTo("id_cliente", uid)
                // .orderBy("fecha_solicitud", Query.Direction.DESCENDING) // OJO: Requiere crear índice en Firebase
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCitas.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        listaCitas.addAll(queryDocumentSnapshots.getDocuments());
                    } else {
                        Toast.makeText(this, "No tienes reparaciones activas", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged(); // Refresca la lista visualmente
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}