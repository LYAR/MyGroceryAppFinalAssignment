package com.example.mygroceryapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.mygroceryapp.ItemDetailsActivity;
import com.example.mygroceryapp.R;
import com.example.mygroceryapp.admin.AddProductsActivity;
import com.example.mygroceryapp.models.ItemsModel;

import java.util.List;
import java.util.Objects;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{
    List<ItemsModel> list;
    Context context;
    String from;

    public ItemsAdapter(List<ItemsModel> list, Context context, String from) {
        this.list = list;
        this.context = context;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel model = list.get(position);
        holder.tvName.setText("Name: "+model.getName());
        holder.tvPrice.setText("Price: ₪"+ model.getPrice());
        Glide.with(holder.ivImage).load(model.getImage()).into(holder.ivImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(from, "admin")){
                    Intent intent = new Intent(context, AddProductsActivity.class);
                    intent.putExtra("data", model);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ItemDetailsActivity.class);
                    intent.putExtra("from", "items");
                    intent.putExtra("data", model);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
