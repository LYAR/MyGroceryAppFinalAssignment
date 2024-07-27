package com.example.mygroceryapp;

import static com.example.mygroceryapp.MainActivity.listOfCarts;
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
import com.example.mygroceryapp.adapter.ItemsAdapter;
import com.example.mygroceryapp.models.CartModel;
import com.example.mygroceryapp.models.ItemsModel;
import com.example.mygroceryapp.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    ImageView ivBack;
    RecyclerView rvItems;
    TextView tvNoDataFound;
    AppCompatButton btnPlaceOrder;
    CartAdapter adapter;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    DatabaseReference databaseRefCarts;
    DatabaseReference databaseRefOrders;
    ArrayList<CartModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        ivBack = findViewById(R.id.ivBack);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        rvItems = findViewById(R.id.rvItems);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        databaseRefCarts = FirebaseDatabase.getInstance().getReference("Carts");
        databaseRefOrders = FirebaseDatabase.getInstance().getReference("Orders");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!list.isEmpty()){
                    progressDialog.show();
                    for (CartModel cartModel : list){

                        LocalDateTime currentDateTime = null;
                        String formattedDateTime = "";
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            currentDateTime = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            formattedDateTime = currentDateTime.format(formatter);
                        }

                        String orderId = databaseRefOrders.push().getKey();
                        OrderModel model = new OrderModel(orderId, mAuth.getCurrentUser().getUid(), cartModel.getName(), cartModel.getDesc(), cartModel.getPrice(),
                                cartModel.getQuantity(), cartModel.getImage(), formattedDateTime);
                        databaseRefOrders.child(orderId).setValue(model).addOnCompleteListener(task -> {
                            databaseRefCarts.child(cartModel.getCartId()).removeValue();
                        }).addOnFailureListener(e -> {

                        });
                    }
                    progressDialog.dismiss();
                    list.clear();
                    Toast.makeText(CartActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        list.clear();

        progressDialog.show();
        databaseRefCarts.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            CartModel model = ds.getValue(CartModel.class);
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
                        btnPlaceOrder.setVisibility(View.VISIBLE);
                        rvItems.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                        adapter = new CartAdapter(list, CartActivity.this);
                        rvItems.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        btnPlaceOrder.setVisibility(View.GONE);
                        rvItems.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                } else {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    btnPlaceOrder.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(CartActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}