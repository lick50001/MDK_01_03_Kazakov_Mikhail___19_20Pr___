package com.example.camera_klimov.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.camera_klimov.R;
import com.example.camera_klimov.domains.PermissionManager;
import com.example.network.datas.product.ProductByUser;
import com.example.network.domains.callbacks.MyResponseCallback;
import com.example.network.domains.models.Product;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity main;
    public static String TOKEN = "830003a4-ae0d-4ede-9ceb-5f43bdebfaa8";
    View btnOpenAddProduct;
    LinearLayout llContent;
    List<Product> Products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        PermissionManager.GetPermission(this, this);

        btnOpenAddProduct = findViewById(R.id.btnOpenAddProduct);
        llContent = findViewById(R.id.llContent);

        btnOpenAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductActivity.class);
            startActivity(intent);
        });

        ProductGetUser();
    }

    public void ProductGetUser() {
        ProductByUser RequestProductGetUser = new ProductByUser(TOKEN, new MyResponseCallback() {
            @Override
            public void onCompile(String result) {
                Log.d("PRODUCT GET USER", result);

                Products = new GsonBuilder().create().fromJson(
                        result,
                        new TypeToken<ArrayList<Product>>(){}.getType()
                );

                CreateElement();
            }

            @Override
            public void onError(String error) {
                Log.e("PRODUCT GET USER", error);
            }
        });
        RequestProductGetUser.execute();
    }

    public void CreateElement() {
        for (Product product : Products) {
            View itemProduct = LayoutInflater.from(this).inflate(R.layout.product, llContent, false);

            BthBig btnBig = itemProduct.findViewById(R.id.btnOpenProduct);
            TextView tvName = itemProduct.findViewById(R.id.tvName);
            TextView tvPrice = itemProduct.findViewById(R.id.tvPrice);

            btnBig.init("Открыть", BthCustom.TypeButton.PRIMARY);

            tvName.setText(product.name);

            tvPrice.setText(product.price + " Р");

            llContent.addView(itemProduct);
        }
    }
}