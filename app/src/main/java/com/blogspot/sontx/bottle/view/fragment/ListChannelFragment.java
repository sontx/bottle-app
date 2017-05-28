package com.blogspot.sontx.bottle.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.Constants;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.PublicProfile;
import com.blogspot.sontx.bottle.model.bean.QRData;
import com.blogspot.sontx.bottle.model.bean.chat.Channel;
import com.blogspot.sontx.bottle.presenter.ListChannelPresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.ListChannelPresenter;
import com.blogspot.sontx.bottle.system.Resource;
import com.blogspot.sontx.bottle.view.activity.QRCodeActivity;
import com.blogspot.sontx.bottle.view.adapter.ChannelRecyclerViewAdapter;
import com.blogspot.sontx.bottle.view.interfaces.ListChannelView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ListChannelFragment extends FragmentBase implements ListChannelView {
    private static final int REQUEST_QRCODE = 1;

    private ListChannelPresenter listChannelPresenter;
    private ChannelRecyclerViewAdapter channelRecyclerViewAdapter;
    private OnChannelInteractionListener listener;
    private Channel selectedChannel;
    private DatabaseReference qrcodesRef;
    private DatabaseReference qrcodeRef;

    public ListChannelFragment() {
    }

    public static ListChannelFragment newInstance() {
        return new ListChannelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(new ArrayList<Channel>(), listener);

        listChannelPresenter = new ListChannelPresenterImpl(this);
        listChannelPresenter.updateChannelsIfNecessary();

        listChannelPresenter.registerEvents();


        String qrcodesKey = System.getProperty(Constants.FIREBASE_QRCODES_KEY);
        qrcodesRef = FirebaseDatabase.getInstance().getReference(qrcodesKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(channelRecyclerViewAdapter);
            registerForContextMenu(recyclerView);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChannelInteractionListener) {
            listener = (OnChannelInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnChannelInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listChannelPresenter.unregisterEvents();
        listener = null;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        selectedChannel = channelRecyclerViewAdapter.getSelectedChannel();
        if (selectedChannel != null) {
            String title = item.getTitle().toString();
            if (item.getItemId() == ChannelRecyclerViewAdapter.ITEM_DELETE_CONVERSATION) {
                promptDeleteChannel(selectedChannel, title);
            } else if (item.getItemId() == ChannelRecyclerViewAdapter.ITEM_GENERATE_QRCODE) {
                promptGenerateQRCode(selectedChannel, title);
            } else if (item.getItemId() == ChannelRecyclerViewAdapter.ITEM_SCAN_QRCODE) {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            showScanQRResult(data, result);
        } else if (requestCode == REQUEST_QRCODE && resultCode == Activity.RESULT_OK) {
            showQRCodeResult(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showScanQRResult(final Intent data, IntentResult result) {
        if (result.getContents() == null)
            return;

        final String qrcodeId = result.getContents();

        qrcodeRef = qrcodesRef.child(qrcodeId);
        qrcodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                if (dataSnapshot.getValue() == null) {
                    Log.d(TAG, "QRData: isn't available");
                    qrcodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null)
                                showScanQRResult(dataSnapshot, qrcodeRef);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    return;
                }
                showScanQRResult(dataSnapshot, qrcodeRef);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showErrorMessage(databaseError.toException());
            }
        });
    }

    private void showScanQRResult(DataSnapshot dataSnapshot, final DatabaseReference qrcodeRef) {
        final QRData qrData = dataSnapshot.getValue(QRData.class);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (qrData.getChannelId().equals(selectedChannel.getId())) {
                    qrData.setRead(true);
                    qrcodeRef.setValue(qrData);
                    final PublicProfile anotherGuy = selectedChannel.getAnotherGuy().getPublicProfile();
                    showQRCodeDialog(anotherGuy, qrData);
                } else {
                    showQRCodeMismatch();
                }
            }
        });
    }

    private void showQRCodeMismatch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Be careful!");
        builder.setMessage("This guy is a stranger. You should run far away from them.");
        builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showQRCodeResult(Intent data) {
        PublicProfile anotherGuy = (PublicProfile) data.getSerializableExtra(QRCodeActivity.ANOTHER_GUY);
        QRData qrData = (QRData) data.getSerializableExtra(QRCodeActivity.QR_DATA);
        showQRCodeDialog(anotherGuy, qrData);
    }

    private void showQRCodeDialog(PublicProfile anotherGuy, QRData qrData) {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Your friend is here!");

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_qrcode_result, null);
        ImageView avatarView = ButterKnife.findById(view, R.id.avatar_view);
        TextView titleView = ButterKnife.findById(view, R.id.title_view);
        TextView detailView = ButterKnife.findById(view, R.id.detail_view);

        Resource resource = App.getInstance().getBottleContext().getResource();
        Picasso.with(getActivity()).load(resource.absoluteUrl(anotherGuy.getAvatarUrl())).into(avatarView);
        titleView.setText(anotherGuy.getDisplayName());
        detailView.setText(qrData.getMessage());

        builder.setView(view);

        builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void promptGenerateQRCode(final Channel selectedChannel, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage("Write a message");
        final EditText editText = new EditText(getActivity());
        editText.setHint("Or leave it blank");
        builder.setView(editText);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                QRData qrData = new QRData();
                qrData.setChannelId(selectedChannel.getId());
                qrData.setAnotherGuyId(selectedChannel.getAnotherGuy().getId());
                qrData.setCurrentUserId(selectedChannel.getCurrentUser().getId());
                qrData.setMessage(editText.getText().toString());
                Intent intent = new Intent(getActivity(), QRCodeActivity.class);
                intent.putExtra(QRCodeActivity.QR_DATA, qrData);
                intent.putExtra(QRCodeActivity.ANOTHER_GUY, selectedChannel.getAnotherGuy().getPublicProfile());
                startActivityForResult(intent, REQUEST_QRCODE);
            }
        });
        builder.show();
    }

    private void promptDeleteChannel(final Channel selectedChannel, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage("Delete conversation with " + selectedChannel.getAnotherGuy().getPublicProfile().getDisplayName());
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listChannelPresenter.deleteChannelAsync(selectedChannel);
                channelRecyclerViewAdapter.getValues().remove(selectedChannel);
                channelRecyclerViewAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void clearChannels() {
        if (channelRecyclerViewAdapter != null) {
            channelRecyclerViewAdapter.getValues().clear();
            channelRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showChannel(Channel channel) {
        if (channelRecyclerViewAdapter != null) {
            List<Channel> values = channelRecyclerViewAdapter.getValues();
            synchronized (this) {
                for (int i = 0; i < values.size(); i++) {
                    Channel value = values.get(i);
                    if (value.getId().equalsIgnoreCase(channel.getId())) {
                        values.set(i, channel);
                        channelRecyclerViewAdapter.notifyItemChanged(i);
                        return;
                    }
                }
                values.add(0, channel);
                channelRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void showChannels(List<Channel> channels) {
        if (channelRecyclerViewAdapter != null) {
            synchronized (this) {
                channelRecyclerViewAdapter.getValues().addAll(channels);
                channelRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void removeChannel(String channelId) {
        if (channelRecyclerViewAdapter != null) {
            synchronized (this) {
                List<Channel> values = channelRecyclerViewAdapter.getValues();
                for (int i = 0; i < values.size(); i++) {
                    Channel value = values.get(i);
                    if (value.getId().equals(channelId)) {
                        values.remove(i);
                        channelRecyclerViewAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }
        }
    }

    public interface OnChannelInteractionListener {
        void onListChannelInteraction(Channel channel);
    }
}
