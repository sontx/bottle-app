package it.slyce.messaging.utils.asyncTasks;

import java.util.List;

import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.messageItem.MessageItem;
import it.slyce.messaging.message.messageItem.MessageRecyclerAdapter;
import it.slyce.messaging.utils.Refresher;

public class UpdateMessageTask extends AsyncTaskBase<Void, Void, Integer> {
    private List<MessageItem> mMessageItems;
    private Message message;
    private MessageRecyclerAdapter mRecyclerAdapter;
    private Refresher mRefresher;

    public UpdateMessageTask(Message message, List<MessageItem> messageItems, MessageRecyclerAdapter mRecyclerAdapter, Refresher refresher) {
        this.message = message;
        this.mRecyclerAdapter = mRecyclerAdapter;
        this.mRefresher = refresher;
        this.mMessageItems = messageItems;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mRefresher.setIsRefreshing(true);
    }

    @Override
    protected Integer doInBackground(Void[] objects) {
        if (message.getId() == null || message.getId().length() == 0) {
            for (int i = mMessageItems.size() - 1; i >= 0; i--) {
                MessageItem messageItem = mMessageItems.get(i);
                if (messageItem.getMessage().getTempId() == message.getTempId()) {
                    messageItem.getMessage().setState(message.getState());
                    return i;
                }
            }
        } else {
            for (int i = mMessageItems.size() - 1; i >= 0; i--) {
                MessageItem messageItem = mMessageItems.get(i);
                if (messageItem.getMessage().getId().equalsIgnoreCase(message.getId())) {
                    messageItem.setMessage(message);
                    return i;
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer position) {
        super.onPostExecute(position);

        if (position == null)
            return;

        mRecyclerAdapter.notifyItemChanged(position);

        mRefresher.setIsRefreshing(false);
    }
}
