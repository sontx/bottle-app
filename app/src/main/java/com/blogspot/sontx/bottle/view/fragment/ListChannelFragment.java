package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.QRData;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.ListChannelPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ListChannelPresenter;
import com.blogspot.sontx.bottle.view.activity.QRCodeActivity;
import com.blogspot.sontx.bottle.view.adapter.ChannelRecyclerViewAdapter;
import com.blogspot.sontx.bottle.view.interfaces.ListChannelView;

import java.util.ArrayList;
import java.util.List;

public class ListChannelFragment extends FragmentBase implements ListChannelView {
    private ListChannelPresenter listChannelPresenter;
    private ChannelRecyclerViewAdapter channelRecyclerViewAdapter;
    private OnChannelInteractionListener listener;

    public ListChannelFragment() {
    }

    public static ListChannelFragment newInstance() {
        return new ListChannelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(new ArrayList<Channel>(), listener);

        listChannelPresenter = new ListChannelPresenterImpl(this);
        listChannelPresenter.updateChannelsIfNecessary();

        listChannelPresenter.registerEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(channelRecyclerViewAdapter);
            registerForContextMenu(recyclerView);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChannelInteractionListener) {
            listener = (OnChannelInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnChannelInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listChannelPresenter.unregisterEvents();
        listener = null;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final Channel selectedChannel = channelRecyclerViewAdapter.getSelectedChannel();
        if (selectedChannel != null) {
            String title = item.getTitle().toString();
            if (item.getItemId() == ChannelRecyclerViewAdapter.ITEM_DELETE_CONVERSATION) {
                promptDeleteChannel(selectedChannel, title);
            } else if (item.getItemId() == ChannelRecyclerViewAdapter.ITEM_GENERATE_QRCODE) {
                promptGenerateQRCode(selectedChannel, title);
            } else if (item.getItemId() == ChannelRecyclerViewAdapter.ITEM_SCAN_QRCODE) {

            }
        }
        return super.onContextItemSelected(item);
    }

    private void promptGenerateQRCode(final Channel selectedChannel, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage("Write a message");
        final EditText editText = new EditText(getActivity());
        editText.setHint("Or leave it blank");
        builder.setView(editText);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                QRData qrData = new QRData();
                qrData.setAnotherGuyId(selectedChannel.getAnotherGuy().getId());
                qrData.setCurrentUserId(selectedChannel.getCurrentUser().getId());
                qrData.setMessage(editText.getText().toString());
                Intent intent = new Intent(getActivity(), QRCodeActivity.class);
                intent.putExtra(QRCodeActivity.QR_DATA, qrData);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void promptDeleteChannel(final Channel selectedChannel, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage("Delete conversation with " + selectedChannel.getAnotherGuy().getPublicProfile().getDisplayName());
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listChannelPresenter.deleteChannelAsync(selectedChannel);
                channelRecyclerViewAdapter.getValues().remove(selectedChannel);
                channelRecyclerViewAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void clearChannels() {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.getValues().clear();
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showChannel(Channel channel) {
        if (channelRecyclerViewAdapter != null) {
            List<Channel> values = channelRecyclerViewAdapter.getValues();
            synchronized (this) {
                for (int i = 0; i < values.size(); i++) {
                    Channel value = values.get(i);
                    if (value.getId().equalsIgnoreCase(channel.getId())) {
                        values.set(i, channel);
                        channelRecyclerViewAdapter.notifyItemChanged(i);
                        return;
                    }
                }
                values.add(0, channel);
                channelRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void showChannels(List<Channel> channels) {
        if (channelRecyclerViewAdapter != null) {
            synchronized (this) {
                channelRecyclerViewAdapter.getValues().addAll(channels);
                channelRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void removeChannel(String channelId) {
        if (channelRecyclerViewAdapter != null) {
            synchronized (this) {
                List<Channel> values = channelRecyclerViewAdapter.getValues();
                for (int i = 0; i < values.size(); i++) {
                    Channel value = values.get(i);
                    if (value.getId().equals(channelId)) {
                        values.remove(i);
                        channelRecyclerViewAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }
        }
    }

    public interface OnChannelInteractionListener {
        void onListChannelInteraction(Channel channel);
    }
}
