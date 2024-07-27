package com.example.mygroceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygroceryapp.adapter.ItemsAdapter;
import com.example.mygroceryapp.admin.AdminMainActivity;
import com.example.mygroceryapp.models.ItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroceriesActivity extends AppCompatActivity {
    ImageView ivBack;
    RecyclerView rvItems;
    ItemsAdapter productsAdapter;
    TextView tvNoDataFound;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    DatabaseReference databaseRefProducts;
    ArrayList<ItemsModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        ivBack = findViewById(R.id.ivBack);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        rvItems = findViewById(R.id.rvItems);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        databaseRefProducts = FirebaseDatabase.getInstance().getReference("Products");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        list.clear();

        progressDialog.show();
        databaseRefProducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            ItemsModel model = ds.getValue(ItemsModel.class);
                            list.add(model);
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (list.size() > 0) {
                        tvNoDataFound.setVisibility(View.GONE);
                        rvItems.setVisibility(View.VISIBLE);
                        rvItems.setLayoutManager(new LinearLayoutManager(GroceriesActivity.this));
                        productsAdapter = new ItemsAdapter(list, GroceriesActivity.this, "user");
                        rvItems.setAdapter(productsAdapter);
                        productsAdapter.notifyDataSetChanged();
                    } else {
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        rvItems.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(GroceriesActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}