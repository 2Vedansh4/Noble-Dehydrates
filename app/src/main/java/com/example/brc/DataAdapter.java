package com.example.brc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {
    private List<DataEntry> dataEntries;

    public void setData(List<DataEntry> dataEntries) {
        this.dataEntries = dataEntries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataEntry entry = dataEntries.get(position);
        holder.currentTime.setText(entry.getCurrentTime());
        holder.intentData.setText(entry.getIntentData());
        holder.userEmail.setText(entry.getUserEmail());
        holder.userInput.setText(entry.getUserInput());
    }

    @Override
    public int getItemCount() {
        return dataEntries != null ? dataEntries.size() : 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        public TextView currentTime;
        public TextView intentData;
        public TextView userEmail;
        public TextView userInput;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            currentTime = itemView.findViewById(R.id.currentTime);
            intentData = itemView.findViewById(R.id.intentData);
            userEmail = itemView.findViewById(R.id.userEmail);
            userInput = itemView.findViewById(R.id.userInput);
        }
    }
}
