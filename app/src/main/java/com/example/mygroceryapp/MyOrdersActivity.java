package com.example.mygroceryapp;

import static com.example.mygroceryapp.MainActivity.listOfOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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

import com.example.mygroceryapp.adapter.CartAdapter;
import com.example.mygroceryapp.adapter.OrderAdapter;
import com.example.mygroceryapp.models.CartModel;
import com.example.mygroceryapp.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    ImageView ivBack;
    RecyclerView rvItems;
    OrderAdapter adapter;
    TextView tvNoDataFound;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    DatabaseReference databaseRefOrders;
    ArrayList<OrderModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        ivBack = findViewById(R.id.ivBack);
        rvItems = findViewById(R.id.rvItems);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        databaseRefOrders = FirebaseDatabase.getInstance().getReference("Orders");

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
        databaseRefOrders.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            OrderModel model = ds.getValue(OrderModel.class);
                            if (model.getUserId().equals(mAuth.getCurrentUser().getUid())){
                                list.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (list.size() > 0) {
                        tvNoDataFound.setVisibility(View.GONE);
                        rvItems.setVisibility(View.VISIBLE);
                        rvItems.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
                        adapter = new OrderAdapter(list, MyOrdersActivity.this);
                        rvItems.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
                Toast.makeText(MyOrdersActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}