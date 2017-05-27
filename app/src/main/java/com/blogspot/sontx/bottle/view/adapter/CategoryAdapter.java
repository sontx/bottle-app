package com.blogspot.sontx.bottle.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Category;

import java.util.List;

import butterknife.ButterKnife;
import lombok.Getter;

public class CategoryAdapter extends BaseAdapter {

    @Getter
    private List<Category> values;
    private int categoryId = -1;

    public CategoryAdapter(List<Category> items) {
        values = items;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Category category = values.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.item = category;

        viewHolder.titleView.setText(category.getName());
        viewHolder.descriptionView.setText(category.getDescription());
        viewHolder.onlineView.setVisibility(category.getId() == categoryId ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    private class ViewHolder {
        final View root;
        final View onlineView;
        final TextView titleView;
        final TextView descriptionView;

        Category item;

        ViewHolder(View view) {
            root = view;
            titleView = ButterKnife.findById(view, R.id.title_view);
            descriptionView = ButterKnife.findById(view, R.id.description_view);
            onlineView = ButterKnife.findById(view, R.id.online_view);
        }
    }
}
