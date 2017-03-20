package com.blogspot.sontx.bottle.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.WriteMessagePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.WriteMessagePresenter;
import com.blogspot.sontx.bottle.utils.DelayJobUtils;
import com.blogspot.sontx.bottle.view.custom.OnBackPressedListener;
import com.blogspot.sontx.bottle.view.custom.RichEmojiEditText;
import com.blogspot.sontx.bottle.view.custom.RichFloatingActionButton;
import com.blogspot.sontx.bottle.view.fragment.PhotoPreviewFragment;
import com.blogspot.sontx.bottle.view.fragment.PreviewFragmentBase;
import com.blogspot.sontx.bottle.view.interfaces.WriteMessageView;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.mvc.imagepicker.ImagePicker;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.one.EmojiOneProvider;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class WriteMessageActivity extends ActivityBase implements OnBackPressedListener, WriteMessageView, PreviewFragmentBase.OnRemoveExtraListener {
    @BindView(R.id.input_type_button)
    ImageButton inputTypeButton;
    @BindView(R.id.message_text)
    RichEmojiEditText messageText;
    @BindView(R.id.root_view)
    ViewGroup rootView;
    @BindView(R.id.extra_layout)
    ViewGroup rootExtraView;
    @BindColor(R.color.background_card)
    int cardColor;
    @BindColor(R.color.accent)
    int accentColor;

    private InputType inputType;
    private EmojiPopup emojiPopup;
    private MaterialSheetFab materialSheetFab;
    private WriteMessagePresenter writeMessagePresenter;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new EmojiOneProvider());

        setContentView(R.layout.activity_write_message);
        ButterKnife.bind(this);

        setupToolbar();

        setupFloatingActionButton();

        setupEmoji();

        adjustSoftKeyboard();
        setInputType(InputType.WORD);

        ImagePicker.setMinQuality(Integer.parseInt(System.getProperty(Constants.OPTIMIZE_IMAGE_MIN_WIDTH_KEY)),
                Integer.parseInt(System.getProperty(Constants.OPTIMIZE_IMAGE_MIN_HEIGH_KEY)));

        registerEvents();

        writeMessagePresenter = new WriteMessagePresenterImpl(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        emojiPopup.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else if (emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onBackPressed(View view) {
        if (emojiPopup.isShowing()) {
            setInputType(InputType.WORD);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        if (selectedItem == R.id.menu_item_post) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null) {
            writeMessagePresenter.setExtraAsPhoto(bitmap);
            displayImagePreview(bitmap);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.fab_sheet_item_photo)
    void onPhotoClick() {
        materialSheetFab.hideSheet();
        ImagePicker.pickImage(this, getResources().getString(R.string.photo_picker_title));
    }

    @OnClick(R.id.fab_sheet_item_video)
    void onVideoClick() {

    }

    @OnClick(R.id.fab_sheet_item_recording)
    void onRecordingClick() {

    }

    @OnClick(R.id.fab_sheet_item_drawing)
    void onDrawingClick() {

    }

    @OnClick(R.id.fab_sheet_item_link)
    void onLinkClick() {

    }

    @OnClick(R.id.input_type_button)
    void onInputTypeClick() {
        if (inputType == InputType.WORD)
            setInputType(InputType.EMOJI);
        else if (inputType == InputType.EMOJI)
            setInputType(InputType.WORD);
    }

    @OnTouch(R.id.message_text)
    boolean onMessageTextTouch() {
        setInputType(InputType.WORD);
        return false;
    }

    @Override
    public void onRemoveExtra() {
        writeMessagePresenter.removeExtra();
        showExtraLayout(false);
    }

    private void setupEmoji() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(messageText);
    }

    private void registerEvents() {
        messageText.setOnBackPressedListener(this);

        messageText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                messageText.getWindowVisibleDisplayFrame(rect);
                int screenHeight = messageText.getRootView().getHeight();

                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    showExtraLayout(false);
                } else {
                    if (writeMessagePresenter.isContainsExtra()) {
                        DelayJobUtils.delay(new Runnable() {
                            @Override
                            public void run() {
                                showExtraLayout(true);
                            }
                        }, 600);
                    }
                }
            }
        });
    }

    private void setupFloatingActionButton() {
        RichFloatingActionButton floatingActionButton = ButterKnife.findById(this, R.id.fab);
        View sheetView = ButterKnife.findById(this, R.id.fab_sheet);
        View overlayView = ButterKnife.findById(this, R.id.overlay);
        materialSheetFab = new MaterialSheetFab<>(floatingActionButton, sheetView, overlayView, cardColor, accentColor);
    }

    private void setInputType(InputType inputType) {
        this.inputType = inputType;
        if (inputType == InputType.WORD) {
            inputTypeButton.setImageResource(R.drawable.ic_pets_black_24dp);
            emojiPopup.dismiss();
            showSoftKeyboard();
        } else if (inputType == InputType.EMOJI) {
            inputTypeButton.setImageResource(R.drawable.ic_keyboard_black_24dp);
            showSoftKeyboard();
            emojiPopup.toggle();
        }
    }

    private void showExtraLayout(boolean show) {
        rootExtraView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void displayImagePreview(final Bitmap bitmap) {
        showExtraLayout(true);
        PhotoPreviewFragment photoPreviewFragment = PhotoPreviewFragment.newInstance();
        replaceFragment(R.id.extra_layout, photoPreviewFragment);
        photoPreviewFragment.setBitmap(bitmap);
        if (firstTime) {
            firstTime = false;
            DelayJobUtils.delay(new Runnable() {
                @Override
                public void run() {
                    displayImagePreview(bitmap);
                }
            }, 600);
        }
    }

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(messageText, InputMethodManager.SHOW_IMPLICIT);
    }

    private enum InputType {
        WORD,
        EMOJI
    }
}
