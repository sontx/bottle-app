package com.blogspot.sontx.bottle.view.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.QRData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QRCodeActivity extends ActivityBase {

    public static final String QR_DATA = "qr-data";

    @BindView(R.id.qr_image_view)
    ImageView qrImageView;

    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);

        QRData qrData = (QRData) getIntent().getSerializableExtra(QR_DATA);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(qrData);
            bitmap = generateQRBitMap(json);
            qrImageView.setImageBitmap(bitmap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            showErrorMessage(e);
        }
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null)
            bitmap.recycle();
        super.onDestroy();
    }

    private Bitmap generateQRBitMap(final String content) {

        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            int color1 = ContextCompat.getColor(this, R.color.primary_dark);
            int color2 = ContextCompat.getColor(this, R.color.background_primary);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? color1 : color2);
                }
            }

            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
