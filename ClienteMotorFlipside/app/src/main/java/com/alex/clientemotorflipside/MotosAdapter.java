package com.alex.clientemotorflipside;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MotosAdapter extends FirestoreRecyclerAdapter<Moto, MotosAdapter.MotoViewHolder> {

    private OnItemClickListener listener;

    public MotosAdapter(@NonNull FirestoreRecyclerOptions<Moto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MotoViewHolder holder, int position, @NonNull Moto model) {
        holder.lblMarcaModelo.setText(model.getMarca() + " " + model.getModelo());
        holder.lblMatricula.setText(model.getMatricula());
        holder.lblInfoExtra.setText(model.getAno() + " • " + model.getKms() + " km");

        // Cuando hacemos clic en una tarjeta...
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                // Enviamos el documento (para sacar el ID) y la posición
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        });
    }

    @NonNull
    @Override
    public MotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_moto, parent, false);
        return new MotoViewHolder(v);
    }

    static class MotoViewHolder extends RecyclerView.ViewHolder {
        TextView lblMarcaModelo, lblMatricula, lblInfoExtra;

        public MotoViewHolder(@NonNull View itemView) {
            super(itemView);
            lblMarcaModelo = itemView.findViewById(R.id.lblMarcaModelo);
            lblMatricula = itemView.findViewById(R.id.lblMatricula);
            lblInfoExtra = itemView.findViewById(R.id.lblInfoExtra);
        }
    }

    // --- INTERFAZ PARA EL CLIC ---
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}