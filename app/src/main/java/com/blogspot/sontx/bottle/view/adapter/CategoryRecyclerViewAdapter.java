package com.blogspot.sontx.bottle.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;
import com.blogspot.sontx.bottle.view.fragment.CategoryFragment;

import java.util.List;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    @Getter
    private List<Category> values;
    @Setter
    private CategoryFragment.OnListCategoryInteractionListener listener;

    public CategoryRecyclerViewAdapter(List<Category> items) {
        values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
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
                    listener.onListCategoryInteraction(holder.item);
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

        Category item;

        ViewHolder(View view) {
            super(view);
            root = view;
            titleView = ButterKnife.findById(view, R.id.title_view);
            descriptionView = ButterKnife.findById(view, R.id.description_view);
            imageView = ButterKnife.findById(view, R.id.image_view);
        }
    }
}
