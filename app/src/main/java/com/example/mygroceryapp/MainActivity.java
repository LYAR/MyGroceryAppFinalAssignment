package com.example.mygroceryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mygroceryapp.models.CartModel;
import com.example.mygroceryapp.models.ItemsModel;
import com.example.mygroceryapp.models.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AppCompatButton btnOrderGroceries, btnGoToCart, btnMyOrders, btnLogout;
    TextView tvLabel;
    public static ArrayList<CartModel> listOfCarts = new ArrayList<>();
    public static List<OrderModel> listOfOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        btnOrderGroceries = findViewById(R.id.btnOrderGroceries);
        btnGoToCart = findViewById(R.id.btnGoToCart);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        btnLogout = findViewById(R.id.btnLogout);
        tvLabel = findViewById(R.id.tvLabel);

        btnOrderGroceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GroceriesActivity.class));
            }
        });

        btnGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        btnMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyOrdersActivity.class));
            }
        });;

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

    }
}