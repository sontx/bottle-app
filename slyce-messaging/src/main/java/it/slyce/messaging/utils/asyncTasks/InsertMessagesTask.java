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
    private final List<Message> insertMessages;
    private MessageRecyclerAdapter mRecyclerAdapter;
    private Context context;
    private Refresher mRefresher;
    private OnTaskCompletedListener onTaskCompletedListener;

    public InsertMessagesTask(List<Message> mMessages, List<Message> insertMessages, List<MessageItem> messageItems, MessageRecyclerAdapter mRecyclerAdapter, Context context, Refresher refresher) {
        this.mMessages = mMessages;
        this.insertMessages = insertMessages;
        this.mRecyclerAdapter = mRecyclerAdapter;
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

        int addedItemCount = 0;

        synchronized (getLock()) {
            int upTo = insertMessages.size();

            for (int i = upTo - 1, from = 0; i >= 0; i--) {
                Message message = insertMessages.get(i);

                // check if message already exists
                boolean exist = false;
                int maxCheck = Math.min(upTo + from, mMessages.size());
                for (int j = from; j < maxCheck; j++) {
                    Message existMessage = mMessages.get(j);
                    String existMessageId = existMessage.getId();
                    if (existMessageId != null && existMessageId.equals(message.getId())) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    mMessages.add(0, message);
                    mMessageItems.add(0, message.toMessageItem(context)); // this call is why we need the AsyncTask
                    from++;
                    addedItemCount++;
                }
            }
        }

        for (int i = 0; i < mMessageItems.size(); i++) {
            MessageUtils.markMessageItemAtIndexIfFirstOrLastFromSource(i, mMessageItems);
        }

        return addedItemCount;
    }

    @Override
    protected void onPostExecute(Object o) {
        int upTo = (int) o;

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
