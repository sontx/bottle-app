package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.presenter.SettingPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.SettingPresenter;
import com.blogspot.sontx.bottle.view.interfaces.SettingView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingFragment extends FragmentBase implements SettingView, OnFragmentVisibleChangedListener {
    private OnSettingFragmentInteractionListener listener;
    private Unbinder unbinder;
    private SettingPresenter settingPresenter;

    @BindView(R.id.avatar_view)
    ImageView avatarView;
    @BindView(R.id.title_view)
    TextView displayNameView;
    @BindView(R.id.detail_view)
    TextView statusView;

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingPresenter = new SettingPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingFragmentInteractionListener) {
            listener = (OnSettingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSettingFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.logout_button)
    void onLogoutClick() {
        if (listener != null)
            listener.logoutClick();
    }

    @Override
    public void showDisplayName(String displayName) {
        displayNameView.setText(displayName);
    }

    @Override
    public void showAvatar(String avatarUrl) {
        Picasso.with(avatarView.getContext()).load(avatarUrl).into(avatarView);
    }

    @Override
    public void showStatus(String text) {
        statusView.setText(text);
    }

    @Override
    public void onFragmentVisibleChanged(boolean isVisible) {
        if (isVisible)
            settingPresenter.updateUI();
    }

    public interface OnSettingFragmentInteractionListener {
        void logoutClick();
    }
}
