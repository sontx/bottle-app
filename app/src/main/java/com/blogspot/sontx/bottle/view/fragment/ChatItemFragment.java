package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.ChatChannel;
import com.blogspot.sontx.bottle.view.adapter.ChatItemRecyclerViewAdapter;

import co.dift.ui.SwipeToAction;

public class ChatItemFragment extends Fragment {

    private SwipeToAction.SwipeListener<ChatChannel> listener;

    public ChatItemFragment() {
    }

    public static ChatItemFragment newInstance() {
        return new ChatItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(new ChatItemRecyclerViewAdapter(ChatChannel.getDummyList()));

            new SwipeToAction(recyclerView, listener);
        }
        return view;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SwipeToAction.SwipeListener) {
            listener = (SwipeToAction.SwipeListener<ChatChannel>) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SwipeToAction.SwipeListener<ChatChannel>");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
