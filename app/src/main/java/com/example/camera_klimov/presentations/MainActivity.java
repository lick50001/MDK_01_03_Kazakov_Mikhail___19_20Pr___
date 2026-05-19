package com.example.camera_klimov.presentations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.camera_klimov.ProductFragment;
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
    public static String TOKEN = "a69506ed-e862-4659-bff9-99eb136e578d";
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
                openFragment = new ProductFragment(context);
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
        if (openFragment != null) {
            openFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(1, 101, Menu.NONE, "Изменить");
        menu.add(2, 102, Menu.NONE, "Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 1)
            Toast.makeText(context, "Изменение элемента", Toast.LENGTH_SHORT).show();
        else if (item.getGroupId() == 2)
            Toast.makeText(context, "Удаление элемента", Toast.LENGTH_SHORT).show();

        return true;
    }
}