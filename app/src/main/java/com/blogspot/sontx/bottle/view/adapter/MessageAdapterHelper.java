package com.blogspot.sontx.bottle.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.custom.AutoSizeImageView;
import com.blogspot.sontx.bottle.view.custom.RichVideoView;
import com.blogspot.sontx.bottle.view.fragment.OnMessageInteractionListener;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.slyce.messaging.utils.DateTimeUtils;

public final class MessageAdapterHelper {
    public static void bindMessageToVideoViewHolder(VideoViewHolder holder, MessageBase message, Resource resource) {
        String url = resource.absoluteUrl(message.getMediaUrl());
        holder.richVideoView.setVideoUrl(url);
    }

    public static void bindMessageToPhotoViewHolder(PhotoViewHolder holder, MessageBase message, Resource resource) {
        String url = resource.absoluteUrl(message.getMediaUrl());
        holder.autoSizeImageView.setImageUrl(url);
    }

    public static void bindMessageToTextViewHolder(MessageBase message, final TextViewHolder textViewHolder, Resource resource, final String currentUserId, final OnMessageInteractionListener listener) {
        PublicProfile owner = message.getOwner();

        textViewHolder.item = message;
        textViewHolder.displayNameView.setText(owner.getDisplayName());
        String url = resource.absoluteUrl(owner.getAvatarUrl());
        Picasso.with(textViewHolder.root.getContext()).load(url).into(textViewHolder.avatarView);
        textViewHolder.timestampView.setText(DateTimeUtils.getTimestamp(textViewHolder.root.getContext(), message.getTimestamp()));
        textViewHolder.textContentView.setText(message.getText());

        showCurrentUserMessageBar(message, textViewHolder, currentUserId);

        textViewHolder.directMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOnDirectMessageClick(listener, textViewHolder);
            }
        });
        textViewHolder.voteMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOnVoteMessageClick(listener, textViewHolder);
            }
        });
        textViewHolder.moreOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOnEditMessageClick(textViewHolder, currentUserId, listener);
            }
        });
        textViewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOnDeleteMessageClick(textViewHolder, listener);
            }
        });
    }

    public static void unbindMessageToTextViewHolder(TextViewHolder textViewHolder) {
        textViewHolder.directMessageView.setOnClickListener(null);
        textViewHolder.voteMessageView.setOnClickListener(null);
        textViewHolder.moreOptionView.setOnClickListener(null);
        textViewHolder.deleteView.setOnClickListener(null);
    }

    public static void fireOnDeleteMessageClick(final TextViewHolder textViewHolder, final OnMessageInteractionListener listener) {
        if (textViewHolder.item.getId() < 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(textViewHolder.itemView.getContext());
            builder.setMessage("Your message is posting to server, wait a few minutes and try again.");
            builder.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(textViewHolder.itemView.getContext());
        builder.setMessage("Delete this message, are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null)
                    listener.onDeleteMessageClick(textViewHolder.item);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static void fireOnEditMessageClick(final TextViewHolder textViewHolder, final String currentUserId, final OnMessageInteractionListener listener) {
        showEditMode(textViewHolder, currentUserId, true);

        textViewHolder.ignoreEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOnIgnoreChangeClick(textViewHolder, currentUserId);
            }
        });
        textViewHolder.applyEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOnApplyChangeClick(textViewHolder, currentUserId, listener);
            }
        });
    }

    public static void fireOnApplyChangeClick(TextViewHolder textViewHolder, String currentUserId, OnMessageInteractionListener listener) {
        String text = textViewHolder.editTextContentView.getText().toString();
        text = text.trim();

        if (text.length() == 0) {
            Toast.makeText(textViewHolder.itemView.getContext(), "Your message is empty!", Toast.LENGTH_SHORT).show();
        } else {
            fireOnIgnoreChangeClick(textViewHolder, currentUserId);
            textViewHolder.textContentView.setText(text);
            MessageBase messageBase = textViewHolder.item;
            messageBase.setText(text);
            listener.onUpdateMessageClick(messageBase);
        }
    }

    public static void fireOnIgnoreChangeClick(TextViewHolder textViewHolder, String currentUserId) {
        showEditMode(textViewHolder, currentUserId, false);
    }

    public static void fireOnVoteMessageClick(OnMessageInteractionListener listener, TextViewHolder textViewHolder) {
        if (null != listener)
            listener.onVoteMessageClick(textViewHolder.item);
    }

    public static void fireOnDirectMessageClick(OnMessageInteractionListener listener, TextViewHolder textViewHolder) {
        if (null != listener)
            listener.onDirectMessageClick(textViewHolder.item);
    }

    private static void showCurrentUserMessageBar(MessageBase message, TextViewHolder textViewHolder, String currentUserId) {
        if (message.getOwner().getId().equals(currentUserId)) {
            textViewHolder.voteMessageView.setVisibility(View.GONE);
            textViewHolder.directMessageView.setVisibility(View.GONE);
            textViewHolder.moreOptionView.setVisibility(View.VISIBLE);
            textViewHolder.deleteView.setVisibility(View.VISIBLE);
        } else {
            textViewHolder.voteMessageView.setVisibility(View.VISIBLE);
            textViewHolder.directMessageView.setVisibility(View.VISIBLE);
            textViewHolder.moreOptionView.setVisibility(View.GONE);
            textViewHolder.deleteView.setVisibility(View.GONE);
        }
    }

    private static void showEditMode(TextViewHolder textViewHolder, String currentUserId, boolean isEditMode) {
        if (isEditMode) {
            textViewHolder.voteMessageView.setVisibility(View.GONE);
            textViewHolder.directMessageView.setVisibility(View.GONE);
            textViewHolder.moreOptionView.setVisibility(View.GONE);
            textViewHolder.deleteView.setVisibility(View.GONE);
            textViewHolder.ignoreEditView.setVisibility(View.VISIBLE);
            textViewHolder.applyEditView.setVisibility(View.VISIBLE);

            textViewHolder.editTextContentView.setVisibility(View.VISIBLE);
            textViewHolder.textContentView.setVisibility(View.GONE);
            textViewHolder.editTextContentView.setText(textViewHolder.textContentView.getText());

            if (textViewHolder instanceof PhotoViewHolder) {
                PhotoViewHolder photoViewHolder = (PhotoViewHolder) textViewHolder;
                photoViewHolder.autoSizeImageView.setVisibility(View.GONE);
            }
        } else {
            textViewHolder.ignoreEditView.setVisibility(View.GONE);
            textViewHolder.applyEditView.setVisibility(View.GONE);

            textViewHolder.editTextContentView.setVisibility(View.GONE);
            textViewHolder.textContentView.setVisibility(View.VISIBLE);

            if (textViewHolder instanceof PhotoViewHolder) {
                PhotoViewHolder photoViewHolder = (PhotoViewHolder) textViewHolder;
                photoViewHolder.autoSizeImageView.setVisibility(View.VISIBLE);
            }

            InputMethodManager inputMethodManager = (InputMethodManager) textViewHolder.itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(textViewHolder.editTextContentView.getWindowToken(), 0);

            showCurrentUserMessageBar(textViewHolder.item, textViewHolder, currentUserId);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final CircleImageView avatarView;
        final TextView displayNameView;
        final TextView timestampView;
        final TextView textContentView;
        public final View directMessageView;
        public final View voteMessageView;
        public final View interactionView;
        public final View moreOptionView;
        public final View deleteView;
        final TextView editTextContentView;
        public final View ignoreEditView;
        public final View applyEditView;

        public MessageBase item;

        public TextViewHolder(View view) {
            super(view);
            root = view;
            displayNameView = ButterKnife.findById(view, R.id.display_name_view);
            timestampView = ButterKnife.findById(view, R.id.timestamp_view);
            avatarView = ButterKnife.findById(view, R.id.avatar_view);
            textContentView = ButterKnife.findById(view, R.id.text_content_view);
            directMessageView = ButterKnife.findById(view, R.id.direct_message_view);
            voteMessageView = ButterKnife.findById(view, R.id.vote_message_view);
            interactionView = ButterKnife.findById(view, R.id.interaction_layout);
            moreOptionView = ButterKnife.findById(view, R.id.more_option_message_view);
            editTextContentView = ButterKnife.findById(view, R.id.edit_text_content_view);
            deleteView = ButterKnife.findById(view, R.id.delete_message_view);
            ignoreEditView = ButterKnife.findById(view, R.id.ignore_change_view);
            applyEditView = ButterKnife.findById(view, R.id.apply_change_view);
        }
    }

    public static class PhotoViewHolder extends TextViewHolder {
        final AutoSizeImageView autoSizeImageView;

        public PhotoViewHolder(View view) {
            super(view);
            autoSizeImageView = ButterKnife.findById(view, R.id.image_view);
        }
    }

    public static class VideoViewHolder extends TextViewHolder {
        final RichVideoView richVideoView;

        VideoViewHolder(View view) {
            super(view);
            richVideoView = ButterKnife.findById(view, R.id.video_view);
        }
    }
}
