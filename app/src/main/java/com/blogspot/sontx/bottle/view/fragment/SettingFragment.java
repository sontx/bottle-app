package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blogspot.sontx.bottle.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingFragment extends FragmentBase {
    private OnSettingFragmentInteractionListener listener;
    private Unbinder unbinder;
    @BindView(R.id.friend_id_view)
    EditText anotherGuyIdView;

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @OnClick(R.id.chat_button)
    void onChatClick() {
        if (listener != null) {
            String id = anotherGuyIdView.getText().toString();
            listener.chatClick(id);
        }
    }

    public interface OnSettingFragmentInteractionListener {
        void logoutClick();
        void chatClick(String anotherGuyId);
    }
}
