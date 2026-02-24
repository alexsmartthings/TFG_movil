package com.alex.clientemotorflipside;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReparacionesAdapter extends RecyclerView.Adapter<ReparacionesAdapter.ViewHolder> {

    private List<DocumentSnapshot> listaCitas;

    public ReparacionesAdapter(List<DocumentSnapshot> listaCitas) {
        this.listaCitas = listaCitas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reparacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = listaCitas.get(position);

        String moto = doc.getString("moto_resumen");
        String averia = doc.getString("descripcion_averia");
        String estado = doc.getString("estado");
        Timestamp fecha = doc.getTimestamp("fecha_solicitud");

        holder.txtMoto.setText(moto != null ? moto : "Moto sin nombre");
        holder.txtEstado.setText("ESTADO: " + (estado != null ? estado : "DESCONOCIDO"));
        holder.txtAveria.setText(averia);

        if (fecha != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.txtFecha.setText(sdf.format(fecha.toDate()));
        }

        String colorHex = "#95a5a6"; // Gris por defecto
        if ("PENDIENTE".equals(estado)) colorHex = "#f1c40f"; // Amarillo
        else if ("EN_RECOGIDA".equals(estado)) colorHex = "#3498db"; // Azul
        else if ("EN_TALLER".equals(estado)) colorHex = "#e67e22"; // Naranja
        else if ("FINALIZADA".equals(estado)) colorHex = "#2ecc71"; // Verde

        holder.viewColor.setBackgroundColor(Color.parseColor(colorHex));
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMoto, txtEstado, txtAveria, txtFecha;
        View viewColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMoto = itemView.findViewById(R.id.lblMotoModelo);
            txtEstado = itemView.findViewById(R.id.lblEstadoTexto);
            txtAveria = itemView.findViewById(R.id.lblAveriaResumen);
            txtFecha = itemView.findViewById(R.id.lblFecha);
            viewColor = itemView.findViewById(R.id.viewColorEstado);
        }
    }
}