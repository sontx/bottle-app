package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.presenter.RoomMessagePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomMessagePresenter;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.view.adapter.RoomMessageRecyclerViewAdapter;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomMessageView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ListRoomMessageFragment extends FragmentBase implements ListRoomMessageView, OnTabSelectListener {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private RoomMessageRecyclerViewAdapter roomMessageRecyclerViewAdapter;
    private RoomMessagePresenter roomMessagePresenter;
    private OnListRoomMessageInteractionListener listener;
    private BottomBar bottomBar;

    public ListRoomMessageFragment() {
    }

    public static ListRoomMessageFragment newInstance(int columnCount) {
        ListRoomMessageFragment fragment = new ListRoomMessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);

            roomMessageRecyclerViewAdapter = new RoomMessageRecyclerViewAdapter(new ArrayList<RoomMessage>(), listener);

            BottleContext bottleContext = App.getInstance().getBottleContext();
            if (bottleContext.isLogged()) {
                int currentRoomId = bottleContext.getCurrentUserSetting().getCurrentRoomId();
                roomMessagePresenter = new RoomMessagePresenterImpl(this);
                roomMessagePresenter.setCurrentRoomId(currentRoomId);
                roomMessagePresenter.getMoreRoomMessagesAsync();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_message_list, container, false);

        Context context = view.getContext();

        bottomBar = ButterKnife.findById(view, R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(this);

        RecyclerView recyclerView = ButterKnife.findById(view, R.id.list);
        if (mColumnCount <= 1)
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

        recyclerView.setAdapter(roomMessageRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bottomBar.removeOnTabSelectListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListRoomMessageInteractionListener) {
            listener = (OnListRoomMessageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListRoomInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void showRoomMessages(List<RoomMessage> rooms) {
        if (roomMessageRecyclerViewAdapter != null) {
            roomMessageRecyclerViewAdapter.getValues().addAll(rooms);
            roomMessageRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        if (tabId == R.id.tab_jump) {
            if (listener != null)
                listener.onJumpToAnotherRoomInteraction();
        } else if (tabId == R.id.tab_new) {
            if (listener != null)
                listener.onNewRoomMessageInteraction();
        }
    }

    public interface OnListRoomMessageInteractionListener {
        void onListRoomMessageInteraction(RoomMessage item);

        void onJumpToAnotherRoomInteraction();

        void onNewRoomMessageInteraction();
    }
}
