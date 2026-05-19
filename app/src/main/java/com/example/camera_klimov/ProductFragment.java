package com.example.camera_klimov;

import static com.example.camera_klimov.presentations.ProductActivity.init;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera_klimov.presentations.BottomSheetHepler;
import com.example.camera_klimov.presentations.MainActivity;
import com.example.camera_klimov.presentations.ProductActivity;
import com.example.network.datas.product.ProductCreate;
import com.example.network.domains.callbacks.MyResponseCallback;
import com.example.network.domains.models.Product;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.example.uicomponents.text.TextBig;

import java.io.File;
import java.util.regex.Pattern;

public class ProductFragment extends Fragment {

    TextBig etName, etDescription, etExpedinture, etPrice;
    Spinner sCategory;
    BthBig btnCreate;
    View btnImageSelect;
    public BottomSheetHepler bottomSheetHepler;
    String currentPhotoPath;
    public Uri imageURI;
    public Context context;
    public ProductFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etExpedinture = view.findViewById(R.id.etExpenditure);
        etPrice = view.findViewById(R.id.etPrice);
        btnCreate = view.findViewById(R.id.btnCreate);
        btnImageSelect = view.findViewById(R.id.btnImageSelect);
        sCategory = view.findViewById(R.id.sCategory);

        bottomSheetHepler = new BottomSheetHepler(context);

        etName.init("Название", "Введите название", "");
        etDescription.init("Описание", "Введите описание", "");
        etExpedinture.init("Расход", "Введите расход", "");
        etPrice.init("Стоимость", "Введите стоимость", "");

        btnCreate.init("Подтвердить", BthCustom.TypeButton.PRIMARY);
        btnCreate.setEnabled(false);

        btnImageSelect.setOnClickListener(v -> {
            bottomSheetHepler.dialog.show();
        });

        btnCreate.Btn.setOnClickListener(v -> {
            Product product = new Product (
                    etName.Text.getText().toString(),
                    etDescription.Text.getText().toString(),
                    sCategory.getSelectedItemPosition(),
                    etExpedinture.Text.getText().toString(),
                    Integer.parseInt(etPrice.Text.getText().toString())
            );

            ProductCreate RequestProductCreate = new ProductCreate(
                    context,
                    MainActivity.TOKEN,
                    product,
                    imageURI,
                    new MyResponseCallback() {
                        @Override
                        public void onCompile(String result) {
                            Log.e("PRODUCT CREATE", result);
                            Toast.makeText(init, "Новый продукт создан!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("PRODUCT CREATE", error);
                        }
                    }
            );
            RequestProductCreate.execute();
        });
        return view;
    }

    public void OpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), 1);
    }

    public void OpenCamera() {
        try {
            Intent PictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File PhotoFile = File.createTempFile(
                    "MY_PHOTO_CADR",
                    ".jpg",
                    MainActivity.main.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );

            currentPhotoPath = PhotoFile.getAbsolutePath();

            imageURI = FileProvider.getUriForFile(
                    context,
                    MainActivity.main.getPackageName() + ".provider",
                    PhotoFile);

            PictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            PictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);

            startActivityForResult(PictureIntent, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        bottomSheetHepler.dialog.cancel();

        if (resultCode == MainActivity.RESULT_OK) {
            if (requestCode == 1)
                imageURI = data.getData();
            ((ImageView) btnImageSelect).setImageURI(imageURI);
        }
    }
}