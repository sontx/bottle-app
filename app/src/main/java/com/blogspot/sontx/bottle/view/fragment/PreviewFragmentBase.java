package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.view.View;

import com.blogspot.sontx.bottle.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class PreviewFragmentBase extends FragmentBase {
    private Unbinder unbinder;
    private OnRemoveExtraListener listener;

    protected void setupView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRemoveExtraListener) {
            listener = (OnRemoveExtraListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRemoveExtraListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @OnClick(R.id.remove_button)
    void onRemoveExtraClick() {
        if (listener != null)
            listener.onRemoveExtra();
    }

    public interface OnRemoveExtraListener {
        void onRemoveExtra();
    }
}
