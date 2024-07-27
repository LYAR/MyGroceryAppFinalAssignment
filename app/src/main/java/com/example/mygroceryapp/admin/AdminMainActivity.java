package com.example.mygroceryapp.admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygroceryapp.R;
import com.example.mygroceryapp.adapter.ItemsAdapter;
import com.example.mygroceryapp.authentication.LoginActivity;
import com.example.mygroceryapp.models.ItemsModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {
    ImageView ivLogout;
    TextView tvNoDataFound;
    RecyclerView rvProducts;
    FloatingActionButton ivAdd;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    DatabaseReference databaseRefProducts;
    ItemsAdapter productsAdapter;
    ArrayList<ItemsModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        ivLogout = findViewById(R.id.ivLogout);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        rvProducts = findViewById(R.id.rvProducts);
        ivAdd = findViewById(R.id.ivAdd);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        databaseRefProducts = FirebaseDatabase.getInstance().getReference("Products");

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(AdminMainActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AddProductsActivity.class));
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
                        rvProducts.setVisibility(View.VISIBLE);
                        rvProducts.setLayoutManager(new LinearLayoutManager(AdminMainActivity.this));
                        productsAdapter = new ItemsAdapter(list, AdminMainActivity.this, "admin");
                        rvProducts.setAdapter(productsAdapter);
                        productsAdapter.notifyDataSetChanged();
                    } else {
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        rvProducts.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    rvProducts.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AdminMainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}