package com.blogspot.sontx.bottle.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.RoomMessage;
import com.blogspot.sontx.bottle.presenter.RoomMessageChangePresenterImpl;
import com.blogspot.sontx.bottle.presenter.RoomMessagePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomMessageChangePresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomMessagePresenter;
import com.blogspot.sontx.bottle.system.BottleContext;
import com.blogspot.sontx.bottle.view.activity.ListRoomActivity;
import com.blogspot.sontx.bottle.view.activity.WriteMessageActivity;
import com.blogspot.sontx.bottle.view.adapter.RoomMessageRecyclerViewAdapter;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomMessageView;
import com.blogspot.sontx.bottle.view.interfaces.RoomMessageChangeView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.TabSelectionInterceptor;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ListRoomMessageFragment extends FragmentBase implements
        ListRoomMessageView,
        TabSelectionInterceptor,
        RoomMessageChangeView {

    private static final int REQUEST_CODE_NEW_ROOM_MESSAGE = 1;
    private static final int REQUEST_CODE_SELECT_ROOM = 2;
    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private RoomMessageRecyclerViewAdapter roomMessageRecyclerViewAdapter;
    private RoomMessagePresenter roomMessagePresenter;
    private RoomMessageChangePresenter roomMessageChangePresenter;
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

    public void removeMessage(MessageBase messageBase) {
        roomMessagePresenter.deleteMessageAsync(messageBase);
        if (roomMessageRecyclerViewAdapter != null) {
            synchronized (this) {
                List<RoomMessage> values = roomMessageRecyclerViewAdapter.getValues();
                for (int i = 0; i < values.size(); i++) {
                    RoomMessage value = values.get(i);
                    if (value == messageBase) {
                        values.remove(i);
                        roomMessageRecyclerViewAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }
        }
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
                roomMessagePresenter.selectRoom(currentRoomId);

                roomMessageChangePresenter = new RoomMessageChangePresenterImpl(this);
                roomMessageChangePresenter.selectRoom(currentRoomId);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_message_list, container, false);

        Context context = view.getContext();

        bottomBar = ButterKnife.findById(view, R.id.bottom_bar);
        bottomBar.setTabSelectionInterceptor(this);

        RecyclerView recyclerView = ButterKnife.findById(view, R.id.list);
        if (mColumnCount <= 1)
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

        recyclerView.setAdapter(roomMessageRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        roomMessageChangePresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        roomMessageChangePresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bottomBar.removeOverrideTabSelectionListener();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_ROOM_MESSAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String text = data.getStringExtra(WriteMessageActivity.MESSAGE_TEXT);
                String mediaPath = data.getStringExtra(WriteMessageActivity.MESSAGE_MEDIA);
                String type = data.getStringExtra(WriteMessageActivity.MESSAGE_TYPE);

                roomMessagePresenter.postRoomMessageAsync(text, mediaPath, type);
            }
            return;
        }

        if (requestCode == REQUEST_CODE_SELECT_ROOM) {
            if (resultCode == Activity.RESULT_OK) {
                int roomId = data.getIntExtra(ListRoomActivity.ROOM_ID, -1);
                roomMessagePresenter.selectRoom(roomId);
                roomMessageChangePresenter.selectRoom(roomId);
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showRoomMessages(List<RoomMessage> rooms) {
        if (roomMessageRecyclerViewAdapter != null) {
            roomMessageRecyclerViewAdapter.getValues().addAll(rooms);
            roomMessageRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public synchronized void addRoomMessage(final RoomMessage roomMessage) {
        if (roomMessage.getId() != MessageBase.UNDEFINED_ID) {
            // ignore current user message
            String ownerId = roomMessage.getOwner().getId();
            synchronized (this) {
                List<RoomMessage> values = roomMessageRecyclerViewAdapter.getValues();
                for (RoomMessage value : values) {
                    if (value.getOwner().getId().equals(ownerId))
                        return;
                }
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<RoomMessage> values = roomMessageRecyclerViewAdapter.getValues();
                synchronized (ListRoomMessageFragment.this) {
                    values.add(0, roomMessage);
                    roomMessageRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public synchronized void updateRoomMessage(RoomMessage message) {
        List<RoomMessage> values = roomMessageRecyclerViewAdapter.getValues();
        for (int i = 0; i < values.size(); i++) {
            RoomMessage value = values.get(i);
            if (value.getId() == message.getId()) {
                values.set(i, message);
                final int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomMessageRecyclerViewAdapter.notifyItemChanged(finalI);
                    }
                });
                break;
            }
        }
    }

    @Override
    public void removeRoomMessage(int messageId) {
        synchronized (this) {
            final List<RoomMessage> values = roomMessageRecyclerViewAdapter.getValues();
            for (int i = 0; i < values.size(); i++) {
                RoomMessage value = values.get(i);
                if (value.getId() == messageId) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            values.remove(finalI);
                            roomMessageRecyclerViewAdapter.notifyItemRemoved(finalI);
                        }
                    });
                    break;
                }
            }

        }
    }

    @Override
    public void updateRoomMessage(final RoomMessage roomMessage, final RoomMessage originRoomMessage) {
        List<RoomMessage> values = roomMessageRecyclerViewAdapter.getValues();
        for (int i = 0; i < values.size(); i++) {
            RoomMessage value = values.get(i);
            if (value == originRoomMessage) {
                values.set(i, roomMessage);
                final int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomMessageRecyclerViewAdapter.notifyItemChanged(finalI);
                    }
                });
                break;
            }
        }
    }

    @Override
    public void showListRoomsByCategoryId(int categoryId) {
        Intent intent = new Intent(getContext(), ListRoomActivity.class);
        intent.putExtra(ListRoomActivity.CATEGORY_ID, categoryId);
        startActivityForResult(intent, REQUEST_CODE_SELECT_ROOM);
    }

    @Override
    public void showListRoomsByRoomId(int roomId) {
        Intent intent = new Intent(getContext(), ListRoomActivity.class);
        intent.putExtra(ListRoomActivity.ROOM_ID, roomId);
        startActivityForResult(intent, REQUEST_CODE_SELECT_ROOM);
    }

    @Override
    public void clearRoomMessages() {
        if (roomMessageRecyclerViewAdapter != null) {
            roomMessageRecyclerViewAdapter.getValues().clear();
            roomMessageRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean shouldInterceptTabSelection(@IdRes int oldTabId, @IdRes int newTabId) {
        if (newTabId == R.id.tab_jump) {
            roomMessagePresenter.jumpToListRooms();
        } else if (newTabId == R.id.tab_new) {
            startActivityForResult(new Intent(getContext(), WriteMessageActivity.class), REQUEST_CODE_NEW_ROOM_MESSAGE);
        }
        return false;
    }

    public interface OnListRoomMessageInteractionListener extends OnMessageInteractionListener {
        void onListRoomMessageInteraction(RoomMessage item);
    }
}
