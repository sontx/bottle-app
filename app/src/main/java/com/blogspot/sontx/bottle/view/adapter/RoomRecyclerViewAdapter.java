package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Room;
import com.blogspot.sontx.bottle.view.fragment.ListRoomFragment;

import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;

public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.ViewHolder> {

    private final ListRoomFragment.OnListRoomInteractionListener listener;
    @Getter
    private List<Room> values;

    public RoomRecyclerViewAdapter(List<Room> items, ListRoomFragment.OnListRoomInteractionListener listener) {
        values = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = values.get(position);
        holder.titleView.setText(values.get(position).getName());
        holder.descriptionView.setText(values.get(position).getDescription());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListRoomInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View root;
        final CircleImageView imageView;
        final TextView titleView;
        final TextView descriptionView;

        Room item;

        ViewHolder(View view) {
            super(view);
            root = view;
            titleView = ButterKnife.findById(view, R.id.title_view);
            descriptionView = ButterKnife.findById(view, R.id.description_view);
            imageView = ButterKnife.findById(view, R.id.image_view);
        }
    }
}
