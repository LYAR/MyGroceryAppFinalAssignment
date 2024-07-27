package com.example.mygroceryapp;

import static com.example.mygroceryapp.MainActivity.listOfCarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mygroceryapp.models.CartModel;
import com.example.mygroceryapp.models.ItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ItemDetailsActivity extends AppCompatActivity {
    TextView tvName, tvDesc, tvPrice;
    ImageView ivImage;
    EditText etQuantity;
    ImageView ivBack;
    AppCompatButton btnAddToCart, btnDelete;
    String from = "";
    ItemsModel itemsModel;
    CartModel cartModel;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    DatabaseReference databaseRefCarts;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        ivBack = findViewById(R.id.ivBack);
        tvName = findViewById(R.id.tvName);
        tvDesc = findViewById(R.id.tvDesc);
        tvPrice = findViewById(R.id.tvPrice);
        etQuantity = findViewById(R.id.etQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnDelete = findViewById(R.id.btnDelete);
        ivImage = findViewById(R.id.ivImage);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        databaseRefCarts = FirebaseDatabase.getInstance().getReference("Carts");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent().getExtras() != null) {
            from = getIntent().getStringExtra("from");
            if (from.equals("cart")) {
                cartModel = (CartModel) getIntent().getSerializableExtra("data");
                tvName.setText("Item Name: " + cartModel.getName());
                tvDesc.setText("Item Desc: " + cartModel.getDesc());
                tvPrice.setText("Item Price: ₪" + cartModel.getPrice());
                etQuantity.setText(cartModel.getQuantity());
                etQuantity.setFocusable(false);
                btnAddToCart.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
                Glide.with(ivImage).load(cartModel.getImage()).into(ivImage);
            } else {
                itemsModel = (ItemsModel) getIntent().getSerializableExtra("data");
                tvName.setText("Item Name: " + itemsModel.getName());
                tvDesc.setText("Item Desc: " + itemsModel.getDesc());
                tvPrice.setText("Item Price: ₪" + itemsModel.getPrice());
                btnDelete.setVisibility(View.GONE);
                Glide.with(ivImage).load(itemsModel.getImage()).into(ivImage);
            }
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRefCarts.child(cartModel.getCartId()).removeValue();
                Toast.makeText(ItemDetailsActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = etQuantity.getText().toString();
                if (quantity.isEmpty()) {
                    Toast.makeText(ItemDetailsActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                String cartId = databaseRefCarts.push().getKey();
                CartModel model = new CartModel(cartId, mAuth.getCurrentUser().getUid(), itemsModel.getName(), itemsModel.getDesc(), itemsModel.getPrice(),
                        quantity, itemsModel.getImage());
                databaseRefCarts.child(cartId).setValue(model).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    Toast.makeText(ItemDetailsActivity.this, "Successfully Added to Cart", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ItemDetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

}