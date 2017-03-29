package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.view.adapter.ChannelRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChannelFragment extends FragmentBase {
    private final static String ARG_CURRENT_USER_ID = "current_user_id";

    private OnChannelInteractionListener listener;
    private String currentUserId;
    private ChannelRecyclerViewAdapter channelRecyclerViewAdapter;

    public ChannelFragment() {
    }

    public static ChannelFragment newInstance(String currentUserId) {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_USER_ID, currentUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentUserId = getArguments().getString(ARG_CURRENT_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(new ArrayList<Channel>(), currentUserId, listener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChannelInteractionListener) {
            listener = (OnChannelInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChannelInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void clearChannels() {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.getValues().clear();
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }

    }

    public void addChannels(final List<Channel> channels) {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.getValues().addAll(channels);
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void updateChannel(Channel channel) {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void addChannel(final Channel channel) {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.getValues().add(channel);
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public interface OnChannelInteractionListener {
        void onChannelInteraction(Channel channel);
    }
}
