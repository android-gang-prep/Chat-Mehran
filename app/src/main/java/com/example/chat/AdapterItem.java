package com.example.chat;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.databinding.ItemBinding;
import com.example.chat.repo.Repository;

import java.util.List;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.ViewHolder> {

    List<ItemModel> list;

    public AdapterItem(List<ItemModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBinding binding = ItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterItem.ViewHolder holder, int position) {
        holder.binding.title.setText(list.get(position).getName());
        if (holder.bitmap==null)
        Repository.getRepository().getBitmap(list.get(position).getImage(), new CallBackResponseBitmap() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                holder.bitmap = bitmap;
                holder.binding.imageView.post(() -> holder.binding.imageView.setImageBitmap(holder.bitmap));
            }

            @Override
            public void onFail(Throwable throwable) {

            }

            @Override
            public void onNetworkFail(Throwable throwable) {

            }
        });
        else
          holder.binding.imageView.setImageBitmap(holder.bitmap);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBinding binding;
        Bitmap bitmap;

        public ViewHolder(@NonNull ItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
