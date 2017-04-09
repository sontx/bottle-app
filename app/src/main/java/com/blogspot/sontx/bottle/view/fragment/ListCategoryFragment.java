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
import com.blogspot.sontx.bottle.presenter.CategoryPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.CategoryPresenter;
import com.blogspot.sontx.bottle.view.adapter.CategoryRecyclerViewAdapter;
import com.blogspot.sontx.bottle.view.interfaces.ListCategoryView;

import java.util.ArrayList;
import java.util.List;

public class ListCategoryFragment extends FragmentBase implements ListCategoryView {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListCategoryInteractionListener listener;
    private CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;
    private CategoryPresenter categoryPresenter;

    public ListCategoryFragment() {
    }

    public static ListCategoryFragment newInstance(int columnCount) {
        ListCategoryFragment fragment = new ListCategoryFragment();
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

        categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(new ArrayList<Category>(), listener);

        categoryPresenter = new CategoryPresenterImpl(this);
        categoryPresenter.getCategoriesAsync();
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
            listener = (OnListCategoryInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListCategoryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
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
