package com.emre.parselivequeryjava;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContentInfo;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {

    private Context context;
    public List<ParseObject> list;

    public MessagesAdapter(Context context, List<ParseObject> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ParseObject object = list.get(position);
        holder.message.setText((String) object.get("message"));

        holder.delete.setOnClickListener(view -> {
            object.deleteInBackground();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(ParseObject t) {
        this.list.add(t);
        notifyItemInserted(list.size() - 1);
    }

    public void removeItem(ParseObject object) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getObjectId().equals(object.getObjectId())){
                list.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, list.size());
                return;
            }
        }
    }
    public void updateItem(ParseObject object) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getObjectId().equals(object.getObjectId())){
                list.set(i,object);
                notifyDataSetChanged();
                return;
            }
        }
    }
}

class MessagesViewHolder extends RecyclerView.ViewHolder {

    TextView message;

    ImageView delete;

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.message);
        delete = itemView.findViewById(R.id.delete);
    }
}