package it.slyce.messaging;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.ZoomStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.slyce.messaging.listeners.LoadMoreMessagesListener;
import it.slyce.messaging.listeners.UserClicksAvatarPictureListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.MediaMessage;
import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;
import it.slyce.messaging.message.messageItem.MessageItem;
import it.slyce.messaging.message.messageItem.MessageRecyclerAdapter;
import it.slyce.messaging.utils.CustomSettings;
import it.slyce.messaging.utils.DateTimeUtils;
import it.slyce.messaging.utils.Refresher;
import it.slyce.messaging.utils.ScrollUtils;
import it.slyce.messaging.utils.asyncTasks.AddNewMessageTask;
import it.slyce.messaging.utils.asyncTasks.InsertMessagesTask;
import it.slyce.messaging.utils.asyncTasks.OnTaskCompletedListener;
import it.slyce.messaging.utils.asyncTasks.UpdateMessageTask;
import it.slyce.messaging.view.ViewUtils;
import it.slyce.messaging.view.text.FontEditText;

/**
 * Created by John C. Hunchar on 1/12/16.
 * Edited by noem
 */
public class SlyceMessagingFragment extends Fragment implements OnClickListener {
    private EditText mEntryField;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Message> mMessages;
    private List<MessageItem> mMessageItems;
    private MessageRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private View rootView;

    private LoadMoreMessagesListener loadMoreMessagesListener;
    private UserSendsMessageListener listener;
    private CustomSettings customSettings;
    private Refresher mRefresher;

    private String defaultAvatarUrl;
    private String defaultDisplayName;
    private String defaultUserId;
    private int startHereWhenUpdate;
    private long recentUpdatedTime;
    private File file;
    private Uri outputFileUri;
    private boolean isLoadInFirstTime = true;
    private volatile boolean enableScrollToLoadMore = true;
    private ImageView mSendButton;
    private ImageView mEmojiButton;
    private ImageView mSnapButton;

    public FontEditText getEntryField() {
        return (FontEditText) mEntryField;
    }

    public void setEmojiButtonToggleState(boolean isToggle) {
        if (isToggle) {
            mEmojiButton.setColorFilter(Color.TRANSPARENT);
        } else {
            mEmojiButton.setColorFilter(customSettings.buttonTintColor);
        }
    }

    public void setPictureButtonVisible(final boolean bool) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSnapButton.setVisibility(bool ? View.VISIBLE : View.GONE);
                }
            });
    }

    public void setLoadMoreMessagesListener(LoadMoreMessagesListener loadMoreMessagesListener) {
        this.loadMoreMessagesListener = loadMoreMessagesListener;
    }

    public void setUserClicksAvatarPictureListener(UserClicksAvatarPictureListener userClicksAvatarPictureListener) {
        this.customSettings.userClicksAvatarPictureListener = userClicksAvatarPictureListener;
    }

    public void setDefaultAvatarUrl(String defaultAvatarUrl) {
        this.defaultAvatarUrl = defaultAvatarUrl;
    }

    public void setDefaultDisplayName(String defaultDisplayName) {
        this.defaultDisplayName = defaultDisplayName;
    }

    public void setDefaultUserId(String defaultUserId) {
        this.defaultUserId = defaultUserId;
    }

    public void setStyle(int style) {
        TypedArray ta = getActivity().obtainStyledAttributes(style, R.styleable.SlyceMessagingTheme);
        this.customSettings.backgroundColor = ta.getColor(R.styleable.SlyceMessagingTheme_backgroundColor, Color.GRAY);
        rootView.setBackgroundColor(this.customSettings.backgroundColor); // the background color
        this.customSettings.timestampColor = ta.getColor(R.styleable.SlyceMessagingTheme_timestampTextColor, Color.BLACK);
        this.customSettings.stateColor = ta.getColor(R.styleable.SlyceMessagingTheme_stateTextColor, Color.BLACK);
        this.customSettings.externalBubbleTextColor = ta.getColor(R.styleable.SlyceMessagingTheme_externalBubbleTextColor, Color.WHITE);
        this.customSettings.externalBubbleBackgroundColor = ta.getColor(R.styleable.SlyceMessagingTheme_externalBubbleBackground, Color.WHITE);
        this.customSettings.localBubbleBackgroundColor = ta.getColor(R.styleable.SlyceMessagingTheme_localBubbleBackground, Color.WHITE);
        this.customSettings.localBubbleTextColor = ta.getColor(R.styleable.SlyceMessagingTheme_localBubbleTextColor, Color.WHITE);
        this.customSettings.snackbarBackground = ta.getColor(R.styleable.SlyceMessagingTheme_snackbarBackground, Color.WHITE);
        this.customSettings.snackbarButtonColor = ta.getColor(R.styleable.SlyceMessagingTheme_snackbarButtonColor, Color.WHITE);
        this.customSettings.snackbarTitleColor = ta.getColor(R.styleable.SlyceMessagingTheme_snackbarTitleColor, Color.WHITE);
        this.customSettings.buttonTintColor = ta.getColor(R.styleable.SlyceMessagingTheme_buttonTintColor, Color.GRAY);

        applyStyle(customSettings);

        ta.recycle();
    }

    private void applyStyle(CustomSettings customSettings) {
        mSendButton.setColorFilter(customSettings.buttonTintColor);
        mSnapButton.setColorFilter(customSettings.buttonTintColor);
    }

    public void addNewMessages(List<Message> messages) {
        enableScrollToLoadMore = false;

        mMessages.addAll(messages);
        AddNewMessageTask task = new AddNewMessageTask(messages, mMessageItems, mRecyclerAdapter, mRecyclerView, getActivity().getApplicationContext(), customSettings);
        task.setOnTaskCompletedListener(new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                scrollToBottom();
                ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
                scheduleTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        enableScrollToLoadMore = true;
                    }
                }, 500, TimeUnit.MILLISECONDS);

            }
        });
        task.execute();
    }

    public void addNewMessage(Message message) {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        addNewMessages(messages);
    }

    public void setOnSendMessageListener(UserSendsMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.fragment_slyce_messaging, null);
        this.rootView = v;
        this.customSettings = new CustomSettings();

        // Setup views
        mEntryField = (EditText) rootView.findViewById(R.id.slyce_messaging_edit_text_entry_field);
        mSendButton = (ImageView) rootView.findViewById(R.id.slyce_messaging_image_view_send);
        mEmojiButton = (ImageView) rootView.findViewById(R.id.slyce_messaging_image_view_emoji);
        mSnapButton = (ImageView) rootView.findViewById(R.id.slyce_messaging_image_view_snap);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.slyce_messaging_recycler_view);

        // Add interfaces
        mSendButton.setOnClickListener(this);
        mEmojiButton.setOnClickListener(this);
        mSnapButton.setOnClickListener(this);

        // Init variables for recycler view
        mMessages = new ArrayList<>();
        mMessageItems = new ArrayList<>();
        mRecyclerAdapter = new MessageRecyclerAdapter(mMessageItems, customSettings);
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return !mRefresher.isRefreshing();
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        };
        //mLinearLayoutManager.setStackFromEnd(true);

        // Setup recycler view
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return mRefresher.isRefreshing();
                    }
                }
        );

        //startUpdateTimestampsThread();
        startHereWhenUpdate = 0;
        recentUpdatedTime = 0;
        mRefresher = new Refresher(false);
        setStyle(R.style.MyTheme);

        setupScrollToLoadMore();

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 232);

        return rootView;
    }

    private void startUpdateTimestampsThread() {
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (int i = startHereWhenUpdate; i < mMessages.size() && i < mMessageItems.size(); i++) {
                    try {
                        MessageItem messageItem = mMessageItems.get(i);
                        Message message = messageItem.getMessage();
                        if (DateTimeUtils.dateNeedsUpdated(getActivity(), message.getDate(), messageItem.getDate())) {
                            messageItem.updateDate(getActivity(), message.getDate());
                            updateTimestampAtValue(i);
                        } else if (i == startHereWhenUpdate) {
                            i++;
                        }
                    } catch (RuntimeException exception) {
                        Log.d("debug", exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            }
        }, 0, 62, TimeUnit.SECONDS);
    }

    private void setupScrollToLoadMore() {
        if (Build.VERSION.SDK_INT >= 23) {
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    int firstVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

                    if (enableScrollToLoadMore && firstVisibleItem == 0 && loadMoreMessagesListener != null) {
                        loadMoreMessagesListener.requestLoadMoreMessages();
                    }
                }
            });
        } else {
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

                    if (enableScrollToLoadMore && firstVisibleItem == 0 && loadMoreMessagesListener != null) {
                        loadMoreMessagesListener.requestLoadMoreMessages();
                    }
                }
            });

            if (Build.VERSION.SDK_INT <= 21) {
                if (loadMoreMessagesListener != null)
                    loadMoreMessagesListener.requestLoadMoreMessages();
            }
        }
    }

    public void updateMessage(Message message, boolean refresh) {
        if (getActivity() != null && message != null)
            new UpdateMessageTask(message, mMessageItems, mRecyclerAdapter, mRefresher, refresh).execute();
    }

    public void loadMoreMessages(List<Message> messages) {
        replaceMessages(mMessages, messages);
    }

    private void replaceMessages(List<Message> mMessages, List<Message> insertMessages) {
        if (getActivity() != null) {
            InsertMessagesTask task = new InsertMessagesTask(mMessages, insertMessages, mMessageItems, mRecyclerAdapter, getActivity().getApplicationContext(), mRefresher);
            task.setOnTaskCompletedListener(new OnTaskCompletedListener() {
                @Override
                public void onTaskCompleted() {
                    if (isLoadInFirstTime) {
                        isLoadInFirstTime = false;
                        scrollToBottom();
                    }
                }
            });
            task.execute();
        }
    }

    private void scrollToBottom() {
        mRecyclerView.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);
    }

    private void updateTimestampAtValue(final int i) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerAdapter.notifyItemChanged(i);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.slyce_messaging_image_view_send) {
            sendUserTextMessage();
        } else if (v.getId() == R.id.slyce_messaging_image_view_emoji) {
            listener.onShowEmojiKeyboard(mEntryField);
        } else if (v.getId() == R.id.slyce_messaging_image_view_snap) {
            mEntryField.setText("");
            final File mediaStorageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            final File root = new File(mediaStorageDir, "SlyceMessaging");
            root.mkdirs();
            final String fname = "img_" + System.currentTimeMillis() + ".jpg";
            file = new File(root, fname);
            outputFileUri = Uri.fromFile(file);
            Intent takePhotoIntent = new CameraActivity.IntentBuilder(getActivity().getApplicationContext())
                    .skipConfirm()
                    .to(this.file)
                    .zoomStyle(ZoomStyle.SEEKBAR)
                    .updateMediaStore()
                    .build();
            Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhotoIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(pickPhotoIntent, "Take a photo or select one from your device");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
            try {
                startActivityForResult(chooserIntent, 1);
            } catch (RuntimeException exception) {
                Log.d("debug", exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 232 || (data == null && this.file.exists())) {
            return;
        }
        try {
            if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera && data != null) { // if there is no picture
                    return;
                }
                if (isCamera || data == null || data.getData() == null) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
                MediaMessage message = new MediaMessage();
                message.setUrl(selectedImageUri.toString());
                message.setDate(System.currentTimeMillis());
                message.setDisplayName(this.defaultDisplayName);
                message.setSource(MessageSource.LOCAL_USER);
                message.setAvatarUrl(this.defaultAvatarUrl);
                message.setUserId(this.defaultUserId);
                addNewMessage(message);
                ScrollUtils.scrollToBottomAfterDelay(mRecyclerView, mRecyclerAdapter);
                if (listener != null)
                    listener.onUserSendsMediaMessage(selectedImageUri, message.getTempId());
            }
        } catch (RuntimeException exception) {
            Log.d("debug", exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void sendUserTextMessage() {
        String text = ViewUtils.getStringFromEditText(mEntryField);
        if (TextUtils.isEmpty(text))
            return;
        mEntryField.setText("");

        // Build messageData object
        TextMessage message = new TextMessage();
        message.setDate(System.currentTimeMillis());
        message.setAvatarUrl(defaultAvatarUrl);
        message.setSource(MessageSource.LOCAL_USER);
        message.setDisplayName(defaultDisplayName);
        message.setText(text);
        message.setUserId(defaultUserId);
        addNewMessage(message);

        ScrollUtils.scrollToBottomAfterDelay(mRecyclerView, mRecyclerAdapter);
        if (listener != null)
            listener.onUserSendsTextMessage(message.getText(), message.getTempId());
    }
}
