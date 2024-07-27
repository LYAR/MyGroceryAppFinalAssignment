package com.example.mygroceryapp.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mygroceryapp.R;
import com.example.mygroceryapp.models.ItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddProductsActivity extends AppCompatActivity {
    TextView tvHeading;
    ImageView ivBack, ivProduct;
    EditText etName, etPrice, etDescription;
    String name, price, description, imageUri = "";
    AppCompatButton btnAdd;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    DatabaseReference databaseRefProducts;
    StorageReference storageReference;
    ItemsModel previousModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        ivBack = findViewById(R.id.ivBack);
        tvHeading = findViewById(R.id.tvHeading);
        ivProduct = findViewById(R.id.ivProduct);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        btnAdd = findViewById(R.id.btnAdd);

        if (getIntent().getExtras() != null){
            previousModel = (ItemsModel) getIntent().getSerializableExtra("data");
            if (previousModel != null){
                tvHeading.setText("Product Details");
                btnAdd.setText("Delete");
                etName.setText(previousModel.getName());
                etPrice.setText(previousModel.getPrice());
                etDescription.setText(previousModel.getDesc());
                etName.setFocusable(false);
                etPrice.setFocusable(false);
                etDescription.setFocusable(false);
                ivProduct.setEnabled(false);
                Glide.with(ivProduct).load(previousModel.getImage()).into(ivProduct);
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        databaseRefProducts = FirebaseDatabase.getInstance().getReference("Products");
        storageReference = FirebaseStorage.getInstance().getReference("ProductPictures");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Machine Image"), 123);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousModel != null){
                    databaseRefProducts.child(previousModel.getId()).removeValue();
                    showMessage("Deleted Successfully");
                    finish();
                }else{
                    if (isValidated()){
                        addProduct();
                    }
                }
            }
        });

    }

    private void addProduct() {
        progressDialog.show();
        Uri uriImage = Uri.parse(imageUri);
        StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());

        imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUri1 = uri.toString();
            String productId = databaseRefProducts.push().getKey();
            ItemsModel model = new ItemsModel(productId, name, description, price,downloadUri1);
            databaseRefProducts.child(productId).setValue(model).addOnSuccessListener(unused -> {
                showMessage("Added Successfully");
                progressDialog.dismiss();
                finish();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            });

        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        });
    }

    private Boolean isValidated() {
        name = etName.getText().toString().trim();
        price = etPrice.getText().toString().trim();
        description = etDescription.getText().toString().trim();

        if (imageUri.isEmpty()) {
            showMessage("Please choose product picture");
            return false;
        }

        if (name.isEmpty()) {
            showMessage("Please enter name");
            return false;
        }

        if (price.isEmpty()) {
            showMessage("Please enter price");
            return false;
        }

        if (description.isEmpty()) {
            showMessage("Please enter description");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData().toString();
            ivProduct.setImageURI(data.getData());
        }
    }

}