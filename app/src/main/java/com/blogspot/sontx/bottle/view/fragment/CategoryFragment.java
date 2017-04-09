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
import com.blogspot.sontx.bottle.view.adapter.CategoryRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class CategoryFragment extends FragmentBase {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    @Setter
    private List<Category> tempList;
    private CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;

    public CategoryFragment() {
        if (tempList != null) {
            categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(tempList);
            tempList = null;
        } else {
            categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(new ArrayList<Category>());
        }
    }

    public static CategoryFragment newInstance(int columnCount) {
        CategoryFragment fragment = new CategoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(categoryRecyclerViewAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListCategoryInteractionListener) {
            OnListCategoryInteractionListener listener = (OnListCategoryInteractionListener) context;
            categoryRecyclerViewAdapter.setListener(listener);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListCategoryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        categoryRecyclerViewAdapter.setListener(null);
    }

    public void clearCategories() {
        if (categoryRecyclerViewAdapter != null) {
            categoryRecyclerViewAdapter.getValues().clear();
            categoryRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void showCategories(List<Category> categories) {
        if (categoryRecyclerViewAdapter != null) {
            categoryRecyclerViewAdapter.getValues().addAll(categories);
            categoryRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public interface OnListCategoryInteractionListener {
        void onListCategoryInteraction(Category item);
    }
}
