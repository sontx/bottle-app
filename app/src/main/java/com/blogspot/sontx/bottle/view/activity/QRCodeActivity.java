package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.QRData;
import com.blogspot.sontx.bottle.model.service.ChildEventAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    public static final String ANOTHER_GUY = "another-guy";

    @BindView(R.id.qr_image_view)
    ImageView qrImageView;

    private Bitmap bitmap = null;
    private ChildEventHandler childEventHandler = null;
    private DatabaseReference qrcodesRef;
    private DatabaseReference qrcodeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);

        QRData qrData = (QRData) getIntent().getSerializableExtra(QR_DATA);

        String qrcodesKey = System.getProperty(Constants.FIREBASE_QRCODES_KEY);
        qrcodesRef = FirebaseDatabase.getInstance().getReference(qrcodesKey);
        qrcodeRef = qrcodesRef.push();
        qrData.setId(qrcodeRef.getKey());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(qrData);

            bitmap = generateQRBitMap(json);
            qrImageView.setImageBitmap(bitmap);

            qrcodeRef.setValue(qrData);

            qrcodesRef.addChildEventListener(childEventHandler = new ChildEventHandler());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            showErrorMessage(e);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null)
            bitmap.recycle();
        if (childEventHandler != null)
            qrcodesRef.removeEventListener(childEventHandler);
        qrcodeRef.removeValue();
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

    private class ChildEventHandler extends ChildEventAdapter {
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            QRData qrData = dataSnapshot.getValue(QRData.class);
            if (qrData.isRead()) {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
