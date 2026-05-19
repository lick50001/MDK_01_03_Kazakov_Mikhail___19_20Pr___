package com.example.camera_klimov.presentations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.camera_klimov.NavigationMenu;
import com.example.camera_klimov.ProductsFragment;
import com.example.camera_klimov.R;
import com.example.camera_klimov.domains.callbacks.OnTabClickListner;
import com.example.camera_klimov.domains.managers.PermissionManager;
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

public class MainActivity extends AppCompatActivity {

    public static MainActivity main;
    public static String TOKEN = "90ea2be3-da90-4542-86e3-c870fbe3750f";
    View btnOpenAddProduct;
    public Fragment openFragment;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        main = this;
        context = this;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        NavigationMenu menu = new NavigationMenu(this, MenuItemSelect);
        ft.add(R.id.menu_navigation, menu);
        ft.commit();
    }

    OnTabClickListner MenuItemSelect = new OnTabClickListner() {
        @Override
        public void onTabClick(Integer position) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            if (openFragment != null) {
                ft.remove(openFragment);
            }

            if (position == -1) {
                openFragment = new ProductsFragment(context);
                ft.add(R.id.contentFrame, openFragment);
            } else if (position == 2) {
                openFragment = new ProductsFragment(context, MenuItemSelect);
                ft.add(R.id.contentFrame, openFragment);
            }

            ft.commit();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ProductsFragment productsFragment = (ProductsFragment) openFragment;

        productsFragment.onActivityResult(requestCode, resultCode, data);
    }
}