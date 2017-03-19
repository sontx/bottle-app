package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.ChatChannelInfo;
import com.blogspot.sontx.bottle.presenter.ChatChannelPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ChatChannelPresenter;
import com.blogspot.sontx.bottle.view.adapter.ListChatChannelRecyclerViewAdapter;
import com.blogspot.sontx.bottle.view.interfaces.ChatChannelView;

import co.dift.ui.SwipeToAction;

public class ListChatChannelFragment extends FragmentBase implements ChatChannelView {
    public static final String USER_ID = "user_id";

    private ChatChannelPresenter chatChannelPresenter;

    private SwipeToAction.SwipeListener<ChatChannelInfo> listener;
    private ListChatChannelRecyclerViewAdapter listChatChannelRecyclerViewAdapter;

    public ListChatChannelFragment() {
    }

    public static ListChatChannelFragment newInstance(String userId) {
        ListChatChannelFragment listChatChannelFragment = new ListChatChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        listChatChannelFragment.setArguments(bundle);
        return listChatChannelFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            String userId = bundle.getString(USER_ID);

            listChatChannelRecyclerViewAdapter = new ListChatChannelRecyclerViewAdapter();

            chatChannelPresenter = new ChatChannelPresenterImpl(this);
            chatChannelPresenter.setup(userId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatchannel_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(listChatChannelRecyclerViewAdapter);

            new SwipeToAction(recyclerView, listener);
        }
        return view;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SwipeToAction.SwipeListener) {
            listener = (SwipeToAction.SwipeListener<ChatChannelInfo>) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SwipeToAction.SwipeListener<ChatChannelDummy>");
        }
        chatChannelPresenter.fetchAvailableChatChannelsAsync();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void addNewChatChannel(ChatChannelInfo chatChannelInfo) {
        FragmentActivity activity = getActivity();
        listChatChannelRecyclerViewAdapter.add(chatChannelInfo);
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listChatChannelRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
