package com.blogspot.sontx.bottle.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.view.custom.OnBackPressedListener;
import com.blogspot.sontx.bottle.view.custom.RichEmojiEditText;
import com.blogspot.sontx.bottle.view.custom.RichFloatingActionButton;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.one.EmojiOneProvider;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class WriteMessageActivity extends ActivityBase implements OnBackPressedListener {
    @BindView(R.id.input_type_button)
    ImageButton inputTypeButton;
    @BindView(R.id.message_text)
    RichEmojiEditText messageText;
    @BindView(R.id.root_view)
    ViewGroup rootView;
    @BindColor(R.color.background_card)
    int cardColor;
    @BindColor(R.color.colorAccent)
    int accentColor;

    private InputType inputType;
    private EmojiPopup emojiPopup;
    private MaterialSheetFab materialSheetFab;

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

        messageText.setOnBackPressedListener(this);
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

    @OnClick(R.id.fab_sheet_item_photo)
    void onPhotoClick() {

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

    private void setupEmoji() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(messageText);
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

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(messageText, InputMethodManager.SHOW_IMPLICIT);
    }

    private enum InputType {
        WORD,
        EMOJI
    }
}
