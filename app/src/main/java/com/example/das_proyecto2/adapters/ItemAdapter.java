package com.example.das_proyecto2.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyecto2.items.Item;

import java.util.List;

public abstract class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;
    private Context context;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
}
