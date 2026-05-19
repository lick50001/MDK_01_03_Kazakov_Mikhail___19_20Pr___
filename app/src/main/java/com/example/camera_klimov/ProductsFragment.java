package com.example.camera_klimov;

import static com.example.camera_klimov.presentations.MainActivity.TOKEN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera_klimov.domains.callbacks.OnTabClickListner;
import com.example.camera_klimov.domains.managers.PermissionManager;
import com.example.camera_klimov.presentations.MainActivity;
import com.example.camera_klimov.presentations.ProductActivity;
import com.example.network.datas.product.ProductByUser;
import com.example.network.datas.product.ProductDelete;
import com.example.network.domains.callbacks.MyResponseCallback;
import com.example.network.domains.models.Product;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    View btnOpenAddProduct;
    LinearLayout llContent;
    List<Product> Products;
    Context context;
    OnTabClickListner listner;

    public ProductsFragment(Context context, OnTabClickListner listner) {
        this.context = context;
        this.listner = listner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        PermissionManager.GetPermission(context, MainActivity.main);

        btnOpenAddProduct = view.findViewById(R.id.btnOpenAddProduct);
        llContent = view.findViewById(R.id.llContent);

        btnOpenAddProduct.setOnClickListener(v -> {
            listner.onTabClick(-1);
        });

        ProductGetUser();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ProductGetUser();
    }

    public void ProductGetUser() {
        ProductByUser RequestProductGetUser = new ProductByUser(
                TOKEN,
                new MyResponseCallback() {
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
        llContent.removeAllViews();

        for (Product product : Products) {
            View itemProduct = LayoutInflater.from(context).inflate(R.layout.product, llContent, false);

            BthBig btnBig = itemProduct.findViewById(R.id.btnOpenProduct);
            ImageView btnDelete = itemProduct.findViewById(R.id.btnDeleteProduct);
            TextView tvName = itemProduct.findViewById(R.id.tvName);
            TextView tvPrice = itemProduct.findViewById(R.id.tvPrice);

            btnBig.init("Открыть", BthCustom.TypeButton.PRIMARY);

            btnBig.Btn.setTextSize(16);

            tvName.setText(product.name);
            tvPrice.setText(product.price + " Р");

            registerForContextMenu(itemProduct);

            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> {
                    ProductDelete RequestProductDelete = new ProductDelete(
                            TOKEN,
                            product.id,
                            new MyResponseCallback() {
                                @Override
                                public void onCompile(String result) {
                                    Log.e("PRODUCT DELETE", result);
                                    Toast.makeText(context, "Продукт удалён!", Toast.LENGTH_SHORT).show();
                                    llContent.removeAllViews();
                                    ProductGetUser();
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e("PRODUCT DELETE", error);
                                }
                            }
                    );
                    RequestProductDelete.execute();
                });
            }

            llContent.addView(itemProduct);
        }
    }
}