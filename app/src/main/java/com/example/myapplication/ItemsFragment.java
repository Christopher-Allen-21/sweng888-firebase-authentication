package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ItemsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextInputEditText nameEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText priceEditText;
    private MaterialButton addButton;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    private ItemAdapter itemAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ListenerRegistration itemsListener;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_items,
                container,
                false
        );

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.itemsRecyclerView);
        nameEditText = view.findViewById(R.id.itemNameEditText);
        descriptionEditText =
                view.findViewById(R.id.itemDescriptionEditText);
        priceEditText = view.findViewById(R.id.itemPriceEditText);
        addButton = view.findViewById(R.id.addItemButton);
        progressBar = view.findViewById(R.id.itemsProgressBar);
        emptyTextView = view.findViewById(R.id.emptyItemsTextView);

        configureRecyclerView();

        addButton.setOnClickListener(
                clickedView -> insertItem()
        );

        queryItems();

        return view;
    }

    private void configureRecyclerView() {
        itemAdapter = new ItemAdapter(this::confirmDeleteItem);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemAdapter);
    }

    private void queryItems() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            showMessage("You must be logged in");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        itemsListener = firestore.collection("items")
                .whereEqualTo("ownerUid", user.getUid())
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .addSnapshotListener(
                        (snapshot, exception) -> {
                            progressBar.setVisibility(View.GONE);

                            if (exception != null) {
                                showMessage(
                                        "Unable to load products: "
                                                + exception.getMessage()
                                );
                                return;
                            }

                            List<Item> items = new ArrayList<>();

                            if (snapshot != null) {
                                for (DocumentSnapshot document
                                        : snapshot.getDocuments()) {

                                    Item item =
                                            document.toObject(Item.class);

                                    if (item != null) {
                                        item.setId(document.getId());
                                        items.add(item);
                                    }
                                }
                            }

                            itemAdapter.setItems(items);

                            emptyTextView.setVisibility(
                                    items.isEmpty()
                                            ? View.VISIBLE
                                            : View.GONE
                            );
                        }
                );
    }

    private void insertItem() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            showMessage("You must be logged in");
            return;
        }

        String name = getText(nameEditText);
        String description = getText(descriptionEditText);
        String priceText = getText(priceEditText);

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Product name is required");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError(
                    "Description is required"
            );
            descriptionEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceText)) {
            priceEditText.setError("Price is required");
            priceEditText.requestFocus();
            return;
        }

        double price;

        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException exception) {
            priceEditText.setError("Enter a valid price");
            priceEditText.requestFocus();
            return;
        }

        if (price < 0) {
            priceEditText.setError(
                    "Price cannot be negative"
            );
            priceEditText.requestFocus();
            return;
        }

        Item item = new Item(
                name,
                description,
                price,
                user.getUid(),
                System.currentTimeMillis()
        );

        setInsertLoading(true);

        firestore.collection("items")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    setInsertLoading(false);
                    clearForm();

                    showMessage("Product added");
                })
                .addOnFailureListener(exception -> {
                    setInsertLoading(false);

                    showMessage(
                            "Unable to add product: "
                                    + exception.getMessage()
                    );
                });
    }

    private void confirmDeleteItem(Item item) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Product")
                .setMessage(
                        "Delete \"" + item.getName() + "\"?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteItem(item)
                )
                .show();
    }

    private void deleteItem(Item item) {
        if (item.getId() == null
                || item.getId().trim().isEmpty()) {
            showMessage("Invalid product identifier");
            return;
        }

        firestore.collection("items")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(
                        unused -> showMessage("Product deleted")
                )
                .addOnFailureListener(exception ->
                        showMessage(
                                "Unable to delete product: "
                                        + exception.getMessage()
                        )
                );
    }

    private void clearForm() {
        nameEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
        nameEditText.requestFocus();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null
                ? ""
                : editText.getText().toString().trim();
    }

    private void setInsertLoading(boolean loading) {
        addButton.setEnabled(!loading);
        addButton.setText(
                loading ? "Adding..." : "Add Product"
        );
    }

    private void showMessage(String message) {
        if (isAdded()) {
            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (itemsListener != null) {
            itemsListener.remove();
            itemsListener = null;
        }
    }
}