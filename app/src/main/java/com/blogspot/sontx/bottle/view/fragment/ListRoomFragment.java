package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.presenter.RoomPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.RoomPresenter;
import com.blogspot.sontx.bottle.view.adapter.RoomRecyclerViewAdapter;
import com.blogspot.sontx.bottle.view.interfaces.ListRoomView;

import java.util.ArrayList;
import java.util.List;

public class ListRoomFragment extends FragmentBase implements ListRoomView {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_CATEGORY_ID = "category-id";
    private static final String ARG_ROOM_ID = "room-id";

    private int mColumnCount = 1;
    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;
    private RoomPresenter roomPresenter;
    private OnListRoomInteractionListener listener;

    public ListRoomFragment() {
    }

    public static ListRoomFragment newInstanceWithRoomId(int columnCount, int roomId) {
        ListRoomFragment fragment = new ListRoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_ROOM_ID, roomId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListRoomFragment newInstanceWithCategoryId(int columnCount, int categoryId, int roomId) {
        ListRoomFragment fragment = new ListRoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_CATEGORY_ID, categoryId);
        args.putInt(ARG_ROOM_ID, roomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT);

            roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(new ArrayList<Room>(), listener);

            roomPresenter = new RoomPresenterImpl(this);

            int roomId = -1;
            if (arguments.containsKey(ARG_ROOM_ID))
                roomId = arguments.getInt(ARG_ROOM_ID);

            if (arguments.containsKey(ARG_CATEGORY_ID)) {
                int categoryId = arguments.getInt(ARG_CATEGORY_ID);
                roomPresenter.getRoomsAsync(categoryId, true);
            } else {
                roomPresenter.getRoomsHaveSameCategoryAsync(roomId, true);
            }

            roomRecyclerViewAdapter.setRoomId(roomId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            int avatarSize = view.getResources().getDimensionPixelSize(R.dimen.avatar_size);
            roomRecyclerViewAdapter.setAvatarSize(avatarSize);
            recyclerView.setAdapter(roomRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListRoomInteractionListener) {
            listener = (OnListRoomInteractionListener) context;
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
    public void showRooms(List<Room> rooms) {
        if (roomRecyclerViewAdapter != null) {
            roomRecyclerViewAdapter.getValues().addAll(rooms);
            roomRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showCategory(Category category) {
        if (listener != null)
            listener.onCategoryAvailable(category);
    }

    public interface OnListRoomInteractionListener {
        void onListRoomInteraction(Room item);

        void onCategoryAvailable(Category category);
    }
}
