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
import android.widget.ImageView;

import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.service.SimpleCallback;
import com.blogspot.sontx.bottle.presenter.WriteMessagePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.WriteMessagePresenter;
import com.blogspot.sontx.bottle.utils.DelayJobUtils;
import com.blogspot.sontx.bottle.utils.TempUtils;
import com.blogspot.sontx.bottle.view.custom.OnBackPressedListener;
import com.blogspot.sontx.bottle.view.custom.RichEmojiEditText;
import com.blogspot.sontx.bottle.view.fragment.PhotoPreviewFragment;
import com.blogspot.sontx.bottle.view.fragment.PreviewFragmentBase;
import com.blogspot.sontx.bottle.view.fragment.VideoPreviewFragment;
import com.blogspot.sontx.bottle.view.interfaces.WriteMessageView;
import com.mvc.imagepicker.ImagePicker;
import com.vanniktech.emoji.EmojiPopup;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class WriteMessageActivity extends ActivityBase
        implements OnBackPressedListener, WriteMessageView, PreviewFragmentBase.OnRemoveExtraListener {

    static final String MESSAGE_TEXT = "message-text";
    static final String MESSAGE_MEDIA = "message-media";

    @BindView(R.id.type_view)
    ImageView typeView;
    @BindView(R.id.message_text)
    RichEmojiEditText messageText;
    @BindView(R.id.preview_container)
    ViewGroup previewContainerView;
    @BindView(R.id.input_type_layout)
    ViewGroup fullInputTypeLayout;
    @BindView(R.id.input_type_layout1)
    ViewGroup miniInputTypeLayout;

    private WriteMessagePresenter writeMessagePresenter;
    private InputType inputType;
    private EmojiPopup emojiPopup;
    private boolean firstTime = true;
    private boolean minimized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_write_message);
        ButterKnife.bind(this);

        writeMessagePresenter = new WriteMessagePresenterImpl(this);

        setupToolbar();

        setupEmoji();

        adjustSoftKeyboard();
        setInputType(InputType.WORD);

        ImagePicker.setMinQuality(Integer.parseInt(System.getProperty(Constants.OPTIMIZE_IMAGE_MIN_WIDTH_KEY)),
                Integer.parseInt(System.getProperty(Constants.OPTIMIZE_IMAGE_MIN_HEIGHT_KEY)));

        registerEvents();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onStop() {
        super.onStop();
        emojiPopup.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (emojiPopup.isShowing())
            emojiPopup.dismiss();
        else
            super.onBackPressed();
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
            writeMessagePresenter.requestPostMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // photo
        final Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null) {
            String photoPath = TempUtils.saveBitmapAsync(bitmap, new SimpleCallback<File>() {
                @Override
                public void onCallback(File value) {
                    bitmap.recycle();
                    displayPhotoPreview(value.getAbsolutePath());
                }
            });
            writeMessagePresenter.setMediaAsPhoto(photoPath);
            return;
        }

        // video
        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            @SuppressWarnings("unchecked")
            List<String> paths = (List<String>) data.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH);
            String videoPath = paths.get(0);
            writeMessagePresenter.setMediaAsVideo(videoPath);
            displayVideoPreview(videoPath);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.photo_button, R.id.photo_button1})
    void onPhotoClick() {
        ImagePicker.pickImage(this, getResources().getString(R.string.photo_picker_title));
    }

    @OnClick({R.id.video_button, R.id.video_button1})
    void onVideoClick() {
        new VideoPicker.Builder(this)
                .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                .directory(VideoPicker.Directory.DEFAULT)
                .extension(VideoPicker.Extension.MP4)
                .enableDebuggingMode(true)
                .build();
    }

    @OnClick({R.id.recording_button, R.id.recording_button1})
    void onRecordingClick() {

    }

    @OnClick({R.id.drawing_button, R.id.drawing_button1})
    void onDrawingClick() {

    }

    @OnClick({R.id.link_button, R.id.link_button1})
    void onLinkClick() {

    }

    @OnClick({R.id.type_button, R.id.type_button1})
    void onInputTypeClick() {
        if (inputType == InputType.WORD)
            setInputType(InputType.EMOJI);
        else if (inputType == InputType.EMOJI)
            setInputType(InputType.WORD);
    }

    @OnTouch(R.id.message_text)
    boolean onMessageTextTouch() {
        fullInputTypeLayout.setVisibility(View.GONE);
        showPreviewLayoutIfNecessary(false);
        setInputType(InputType.WORD);
        return false;
    }

    @Override
    public void onRemovePreview() {
        writeMessagePresenter.removeMedia();
        showPreviewLayoutIfNecessary(false);
    }

    @Override
    public String getText() {
        return messageText.getText().toString();
    }

    @Override
    public void goBackWithSuccess(String text, String mediaPath) {
        Intent intent = getIntent();
        intent.putExtra(MESSAGE_TEXT, text);
        intent.putExtra(MESSAGE_MEDIA, mediaPath);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupEmoji() {
        View rootView = ButterKnife.findById(this, R.id.root_view);
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
                    showInputTypeAsMinimizeMode(true);
                } else {
                    showInputTypeAsMinimizeMode(false);
                }
            }
        });
    }

    private void setInputType(InputType inputType) {
        this.inputType = inputType;

        if (inputType == InputType.WORD) {
            typeView.setImageResource(R.drawable.ic_pets_black_24dp);
            emojiPopup.dismiss();
            showSoftKeyboard();
        } else if (inputType == InputType.EMOJI) {
            typeView.setImageResource(R.drawable.ic_keyboard_black_24dp);
            showSoftKeyboard();
            emojiPopup.toggle();
        }
    }

    private void showInputTypeAsMinimizeMode(final boolean minimized) {
        if (this.minimized == minimized)
            return;

        WriteMessageActivity.this.minimized = minimized;

        if (minimized) {
            fullInputTypeLayout.setVisibility(View.GONE);
            miniInputTypeLayout.setVisibility(View.VISIBLE);
            showPreviewLayoutIfNecessary(false);
        } else {
            miniInputTypeLayout.setVisibility(View.GONE);
            fullInputTypeLayout.setVisibility(View.VISIBLE);
            showPreviewLayoutIfNecessary(true);
        }

    }

    private void showPreviewLayoutIfNecessary(boolean show) {
        if (writeMessagePresenter.isContainsMedia())
            previewContainerView.setVisibility(show ? View.VISIBLE : View.GONE);
        else
            previewContainerView.setVisibility(View.GONE);
    }

    private void displayVideoPreview(final String videoPath) {
        showPreviewLayoutIfNecessary(true);
        VideoPreviewFragment videoPreviewFragment = VideoPreviewFragment.newInstance();
        replaceFragment(R.id.preview_container, videoPreviewFragment);
        videoPreviewFragment.setVideoPath(videoPath);
        if (firstTime) {
            firstTime = false;
            DelayJobUtils.delay(new Runnable() {
                @Override
                public void run() {
                    displayVideoPreview(videoPath);
                }
            }, 600);
        }
    }

    private void displayPhotoPreview(final String photoPath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPreviewLayoutIfNecessary(true);
                PhotoPreviewFragment photoPreviewFragment = PhotoPreviewFragment.newInstance(photoPath);
                replaceFragment(R.id.preview_container, photoPreviewFragment);
            }
        });
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
