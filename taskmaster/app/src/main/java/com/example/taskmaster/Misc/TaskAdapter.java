package com.example.taskmaster.Misc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<com.amplifyframework.datastore.generated.model.Task> taskItems;
    private final OnTaskClickListener listener;

    public TaskAdapter(List<Task> taskItems, OnTaskClickListener listener) {
        this.taskItems = taskItems;
        this.listener = listener;
    }



    public interface OnTaskClickListener {
        void onItemClicked(int position);

        void onDeleteItem(int position);

        void onUpdateItem(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task item = taskItems.get(position);
        holder.title.setText(item.getTitle());
        holder.body.setText(item.getDescription());
        holder.status.setText(item.getStatus());
    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView body;
        private final TextView status;
        private final TextView delete;

        ViewHolder(@NonNull View itemView, OnTaskClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.title_label);
            body = itemView.findViewById(R.id.body_label);
            status = itemView.findViewById(R.id.status_label);
            delete = itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteItem(getAdapterPosition());
                }
            });
        }
    }
}