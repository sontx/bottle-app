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

    private int mColumnCount = 1;
    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;
    private RoomPresenter roomPresenter;
    private OnListRoomInteractionListener listener;

    public ListRoomFragment() {
    }

    public static ListRoomFragment newInstance(int columnCount, int categoryId) {
        ListRoomFragment fragment = new ListRoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            int categoryId = getArguments().getInt(ARG_CATEGORY_ID);

            roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(new ArrayList<Room>(), listener);

            roomPresenter = new RoomPresenterImpl(this);
            roomPresenter.getRoomsAsync(categoryId);
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

    public interface OnListRoomInteractionListener {
        void onListRoomInteraction(Room item);
    }
}
