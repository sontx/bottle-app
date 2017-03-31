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

    private ChannelRecyclerViewAdapter channelRecyclerViewAdapter;

    public ChannelFragment() {
        channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(new ArrayList<Channel>());
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
            String currentUserId = getArguments().getString(ARG_CURRENT_USER_ID);
            channelRecyclerViewAdapter.setCurrentUserId(currentUserId);
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
            recyclerView.setAdapter(channelRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChannelInteractionListener) {
            OnChannelInteractionListener listener = (OnChannelInteractionListener) context;
            channelRecyclerViewAdapter.setListener(listener);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChannelInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        channelRecyclerViewAdapter.setListener(null);
    }

    public void clearChannels() {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.getValues().clear();
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void showChannel(Channel channel) {
        if (channelRecyclerViewAdapter != null) {
            List<Channel> values = channelRecyclerViewAdapter.getValues();
            for (int i = 0; i < values.size(); i++) {
                Channel value = values.get(i);
                if (value.getId().equalsIgnoreCase(channel.getId())) {
                    values.set(i, channel);
                    channelRecyclerViewAdapter.notifyDataSetChanged();
                    //channelRecyclerViewAdapter.notifyItemChanged(i);
                    return;
                }
            }
            values.add(0, channel);
            channelRecyclerViewAdapter.notifyDataSetChanged();
            //channelRecyclerViewAdapter.notifyItemInserted(0);
        }
    }

    public interface OnChannelInteractionListener {
        void onChannelInteraction(Channel channel);
    }
}
