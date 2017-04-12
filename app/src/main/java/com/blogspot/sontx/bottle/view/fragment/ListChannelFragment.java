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
import com.blogspot.sontx.bottle.presenter.ListChannelPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ListChannelPresenter;
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
            listener = (OnChannelInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnChannelInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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

    @Override
    public void showChannels(List<Channel> channels) {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.getValues().addAll(channels);
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public interface OnChannelInteractionListener {
        void onListChannelInteraction(Channel channel);
    }
}