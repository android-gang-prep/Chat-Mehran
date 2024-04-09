package com.example.chat;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.example.chat.databinding.ItemBinding;
import com.example.chat.databinding.ItemLiveBottomBinding;
import com.example.chat.databinding.ItemLiveUpBinding;
import com.example.chat.repo.LoadImage;
import com.example.chat.repo.Repository;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AdapterLive extends RecyclerView.Adapter<AdapterLive.ViewHolder> {

    List<LiveModel> list;

    OnClickItem<LiveModel> onClickItem;

    public AdapterLive(List<LiveModel> list, OnClickItem<LiveModel> onClickItem) {
        this.list = list;
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public AdapterLive.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new ViewHolder(ItemLiveUpBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        else
            return new ViewHolder(ItemLiveBottomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2) == 0 ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLive.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> onClickItem.onClick(list.get(position)));
        if (list.get(position).getLive_stream_url() == null || list.get(position).getLive_stream_url().isEmpty())
            holder.cardView.setStrokeColor(Color.parseColor("#7a8194"));
        else
            holder.cardView.setStrokeColor(Color.parseColor("#b347ea"));

        if (holder.bitmap == null)
            LoadImage.getImage(holder.imageView.getContext()).getBitmap(list.get(position).getProfile_image(), new CallBackResponseBitmap() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    holder.bitmap = bitmap;
                    holder.imageView.post(() -> holder.imageView.setImageBitmap(holder.bitmap));
                }

                @Override
                public void onFail(Throwable throwable) {

                }

                @Override
                public void onNetworkFail(Throwable throwable) {

                }
            });
        else
            holder.imageView.setImageBitmap(holder.bitmap);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaterialCardView cardView;
        Bitmap bitmap;

        public ViewHolder(@NonNull ViewBinding binding) {
            super(binding.getRoot());
            if (binding instanceof ItemLiveUpBinding) {
                imageView = ((ItemLiveUpBinding) binding).img;
                cardView = ((ItemLiveUpBinding) binding).card;
            } else {
                imageView = ((ItemLiveBottomBinding) binding).img;
                cardView = ((ItemLiveBottomBinding) binding).card;
            }


        }
    }
}
