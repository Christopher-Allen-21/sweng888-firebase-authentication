package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemAdapter
        extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Item item);
    }

    private final List<Item> items = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public ItemAdapter(
            OnDeleteClickListener deleteClickListener
    ) {
        this.deleteClickListener = deleteClickListener;
    }

    public void setItems(List<Item> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_product,
                        parent,
                        false
                );

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ItemViewHolder holder,
            int position
    ) {
        Item item = items.get(position);

        holder.nameTextView.setText(item.getName());
        holder.descriptionTextView.setText(
                item.getDescription()
        );

        NumberFormat currencyFormat =
                NumberFormat.getCurrencyInstance(
                        Locale.US
                );

        holder.priceTextView.setText(
                currencyFormat.format(item.getPrice())
        );

        holder.deleteButton.setOnClickListener(
                view -> deleteClickListener.onDeleteClick(item)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final TextView descriptionTextView;
        private final TextView priceTextView;
        private final ImageButton deleteButton;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView =
                    itemView.findViewById(R.id.itemNameTextView);

            descriptionTextView =
                    itemView.findViewById(
                            R.id.itemDescriptionTextView
                    );

            priceTextView =
                    itemView.findViewById(
                            R.id.itemPriceTextView
                    );

            deleteButton =
                    itemView.findViewById(
                            R.id.deleteItemButton
                    );
        }
    }
}