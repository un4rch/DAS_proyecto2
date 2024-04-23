package com.example.das_proyecto2.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.das_proyecto2.R;
import com.example.das_proyecto2.items.Item;
import com.bumptech.glide.Glide;
import com.example.das_proyecto2.services.LikeService;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;
    private Context context;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemList.get(position);
        //Glide.with(context).load(item.getImageUrl()).into(holder.itemImage);
        Glide.with(context)
                .load("http://34.136.205.220:8000/get_img/"+item.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background) // Displays while the image loads
                .error(R.drawable.ic_launcher_background) // Displays if the image fails to load
                .into(holder.itemImage);
        Log.d("ItemAdapter", item.getImageUrl());
        holder.itemText.setText("Likes: " + item.getLikes());

        holder.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event, such as updating likes or opening an item detail.
                int likes = item.getLikes() + 1;
                item.setLikes(likes);
                holder.itemText.setText("Likes: " + likes);
                // Consulta a mysql y FCM
                new LikeService().execute(item.getImageUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemText;
        Button itemButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemText = itemView.findViewById(R.id.item_text);
            itemButton = itemView.findViewById(R.id.item_button);
        }
    }
}
