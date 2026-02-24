package com.alex.clientemotorflipside;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MisMotosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private MotosAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_motos);

        db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recyclerMotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);

        Query query = db.collection("motos")
                .whereEqualTo("id_cliente", uid);

        FirestoreRecyclerOptions<Moto> options = new FirestoreRecyclerOptions.Builder<Moto>()
                .setQuery(query, Moto.class)
                .build();

        adapter = new MotosAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((doc, position) -> {
            Moto moto = doc.toObject(Moto.class);
            String id = doc.getId();

            Intent intent = new Intent(MisMotosActivity.this, ActivityMoto.class);

            intent.putExtra("id_documento", id);
            intent.putExtra("marca", moto.getMarca());
            intent.putExtra("modelo", moto.getModelo());
            intent.putExtra("matricula", moto.getMatricula());
            intent.putExtra("color", moto.getColor());
            intent.putExtra("kms", String.valueOf(moto.getKms()));
            intent.putExtra("ano", moto.getAno());

            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}