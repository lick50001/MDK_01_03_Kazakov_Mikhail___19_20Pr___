package com.example.camera_klimov.presentations;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.camera_klimov.ProductFragment;
import com.example.camera_klimov.ProductsFragment;
import com.example.camera_klimov.R;
import com.example.network.datas.product.ProductCreate;
import com.example.network.domains.callbacks.MyResponseCallback;
import com.example.network.domains.models.Product;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.example.uicomponents.text.TextBig;

import java.io.File;
import java.util.regex.Pattern;

public class ProductActivity extends AppCompatActivity {

    public static ProductActivity init;
    TextBig etName, etDescription, etExpedinture, etPrice;
    Spinner sCategory;
    BthBig btnCreate;
    View btnImageSelect;
    BottomSheetHepler bottomSheetHepler;
    String currentPhotoPath;
    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);

        init = this;

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etExpedinture = findViewById(R.id.etExpenditure);
        etPrice = findViewById(R.id.etPrice);
        btnCreate = findViewById(R.id.btnCreate);
        btnImageSelect = findViewById(R.id.btnImageSelect);
        sCategory = findViewById(R.id.sCategory);

        bottomSheetHepler = new BottomSheetHepler(this);

        etName.init("Название", "Введите название", "");
        etDescription.init("Описание", "Введите описание", "");
        etExpedinture.init("Расход", "Введите расход", "");
        etPrice.init("Стоимость", "Введите стоимость", "");

        btnCreate.init("Подтвердить", BthCustom.TypeButton.PRIMARY);
        btnCreate.setEnabled(false);

        etName.Text.setOnFocusChangeListener(LastFocus);
        etPrice.Text.setOnFocusChangeListener(LastFocus);
        etDescription.Text.setOnFocusChangeListener(LastFocus);
        etExpedinture.Text.setOnFocusChangeListener(LastFocus);

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
                    this,
                    MainActivity.TOKEN,
                    product,
                    imageURI,
                    new MyResponseCallback() {
                        @Override
                        public void onCompile(String result) {
                            Log.e("PRODUCT CREATE", result);
                            Toast.makeText(init, "Новый продукт создан!", Toast.LENGTH_SHORT).show();
                            ProductsFragment.init.ProductGetUser();
                            finish();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("PRODUCT CREATE", error);
                        }
                    }
            );
            RequestProductCreate.execute();
        });
    }

    View.OnFocusChangeListener LastFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            EditText editText = (EditText) view;

            if (hasFocus) {
                editText.setBackgroundResource(com.example.uicomponents.R.drawable.text_hover);
                View parent = (View) editText.getParent();
                TextView message = parent.findViewById(com.example.uicomponents.R.id.textViewMessage);
                if (message != null) message.setText(" ");
            } else {
                String value = editText.getText().toString().trim();
                if (value.isEmpty()) {
                    editText.setBackgroundResource(com.example.uicomponents.R.drawable.text_error);
                    View parent = (View) editText.getParent();
                    TextView message = parent.findViewById(com.example.uicomponents.R.id.textViewMessage);
                    if (message != null) message.setText("Поле не может быть пустым");
                } else if (value.matches("\\d+")) {
                    editText.setBackgroundResource(com.example.uicomponents.R.drawable.text_error);
                    View parent = (View) editText.getParent();
                    TextView message = parent.findViewById(com.example.uicomponents.R.id.textViewMessage);
                    if (message != null) message.setText("Не корректный ввод значений");
                } else {
                    editText.setBackgroundResource(com.example.uicomponents.R.drawable.text_default);
                    View parent = (View) editText.getParent();
                    TextView message = parent.findViewById(com.example.uicomponents.R.id.textViewMessage);
                    if (message != null) message.setText(" ");
                }
            }

            if (hasFocus) return;

            boolean state = true;
            if (etName.Text.getText().toString().isEmpty()) state = false;
            if (etDescription.Text.getText().toString().isEmpty()) state = false;
            if (etExpedinture.Text.getText().toString().isEmpty()) state = false;
            if (etPrice.Text.getText().toString().isEmpty()) state = false;

            boolean isCorrectPrice = Pattern.matches("\\d*", etPrice.Text.getText().toString());
            etPrice.OnError(!isCorrectPrice, "Поле принимает только цифры");
            if (!isCorrectPrice) state = false;

            btnCreate.setEnabled(state);
        }
    };

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
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );

            currentPhotoPath = PhotoFile.getAbsolutePath();

            imageURI = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    PhotoFile);

            PictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            PictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);

            startActivityForResult(PictureIntent, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        bottomSheetHepler.dialog.cancel();

        if (resultCode == RESULT_OK) {
            if (requestCode == 1)
                imageURI = data.getData();
            ((ImageView) btnImageSelect).setImageURI(imageURI);
        }
    }
}