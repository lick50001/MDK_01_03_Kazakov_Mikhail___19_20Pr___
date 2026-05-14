package com.example.camera_klimov.presentations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.camera_klimov.R;
import com.example.uicomponents.button.BthBig;
import com.example.uicomponents.button.BthCustom;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetHepler {
    public BottomSheetDialog dialog;

    public BottomSheetHepler(Context context) {
        dialog = new BottomSheetDialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.bs_select_image, null);

        BthBig btnGallery = view.findViewById(R.id.btnGallery);
        BthBig btnCamera = view.findViewById(R.id.btnCamera);

        btnGallery.init(
                "\uD83D\uDDBC\uFE0F Выбрать из галереи",
                BthCustom.TypeButton.SECONDARY
        );

        btnCamera.init(
                "\uD83D\uDCF8 Сфотографировать",
                BthCustom.TypeButton.SECONDARY
        );

        btnGallery.Btn.setOnClickListener(v ->{
            ProductActivity.init.OpenGallery();
        });

        btnCamera.Btn.setOnClickListener(v -> {
            ProductActivity.init.OpenCamera();
        });

        dialog.setContentView(view);
    }
}
