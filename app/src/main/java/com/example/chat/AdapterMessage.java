package com.example.chat;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.databinding.ItemMessageBinding;
import com.example.chat.databinding.ItemMessageMeBinding;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.ViewHolder> {

    List<MessageModel> list;

    public AdapterMessage(List<MessageModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterMessage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
        else
            view = ItemMessageMeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getName().equals(HomeFragment.name) ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMessage.ViewHolder holder, int position) {
        if (list.get(position).getType().equals("image")) {
            byte[] bytes = Base64.decode(list.get(position).getContent(), Base64.DEFAULT);
            holder.image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            holder.image.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
        } else {

            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.message.setText(list.get(position).getContent());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        ImageView image;

        public ViewHolder(@NonNull View view) {
            super(view);
            message = view.findViewById(R.id.message);
            image = view.findViewById(R.id.img);
        }
    }
}
