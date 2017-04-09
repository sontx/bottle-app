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
import com.blogspot.sontx.bottle.view.adapter.RoomRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class ListRoomFragment extends FragmentBase {

    private static final String ARG_COLUMN_COUNT = "column-count";
    @Setter
    private static List<Room> tempList;
    private int mColumnCount = 1;
    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;

    public ListRoomFragment() {
        if (tempList != null) {
            roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(tempList);
            tempList = null;
        } else {
            roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(new ArrayList<Room>());
        }
    }

    public static ListRoomFragment newInstance(int columnCount) {
        ListRoomFragment fragment = new ListRoomFragment();
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
            OnListRoomInteractionListener listener = (OnListRoomInteractionListener) context;
            roomRecyclerViewAdapter.setListener(listener);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListRoomInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        roomRecyclerViewAdapter.setListener(null);
    }

    public void clearRooms() {
        if (roomRecyclerViewAdapter != null) {
            roomRecyclerViewAdapter.getValues().clear();
            roomRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

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
