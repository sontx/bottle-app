package it.slyce.messaging.utils.asyncTasks;

import android.content.Context;

import java.util.List;

import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.messageItem.MessageItem;
import it.slyce.messaging.message.messageItem.MessageRecyclerAdapter;
import it.slyce.messaging.utils.MessageUtils;
import it.slyce.messaging.utils.Refresher;

public class InsertMessagesTask extends AsyncTaskBase {
    private List<Message> mMessages;
    private List<MessageItem> mMessageItems;
    private MessageRecyclerAdapter mRecyclerAdapter;
    private Context context;
    private Refresher mRefresher;
    private int upTo;
    private OnTaskCompletedListener onTaskCompletedListener;

    public InsertMessagesTask(List<Message> messages, List<MessageItem> messageItems, MessageRecyclerAdapter mRecyclerAdapter, Context context, Refresher refresher, int upTo) {
        this.mMessages = messages;
        this.mRecyclerAdapter = mRecyclerAdapter;
        this.upTo = upTo;
        this.mRefresher = refresher;
        this.mMessageItems = messageItems;
        this.context = context;
    }

    public void setOnTaskCompletedListener(OnTaskCompletedListener onTaskCompletedListener) {
        this.onTaskCompletedListener = onTaskCompletedListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mRefresher.setIsRefreshing(true);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if (context == null)
            return null;

        synchronized (getLock()) {
            for (int i = upTo - 1; i >= 0; i--) {
                Message message = mMessages.get(i);
                mMessageItems.add(0, message.toMessageItem(context)); // this call is why we need the AsyncTask
            }
        }

        for (int i = 0; i < mMessageItems.size(); i++) {
            MessageUtils.markMessageItemAtIndexIfFirstOrLastFromSource(i, mMessageItems);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if (upTo >= 0 && upTo < mMessageItems.size()) {
            mRecyclerAdapter.notifyItemRangeInserted(0, upTo);
            mRecyclerAdapter.notifyItemChanged(upTo);
        } else {
            mRecyclerAdapter.notifyDataSetChanged();
        }

        mRefresher.setIsRefreshing(false);

        if (onTaskCompletedListener != null) {
            onTaskCompletedListener.onTaskCompleted();
            onTaskCompletedListener = null;
        }
    }
}
