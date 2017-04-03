package it.slyce.messaging.utils.asyncTasks;

import java.util.List;

import it.slyce.messaging.message.Message;
import it.slyce.messaging.message.messageItem.MessageItem;
import it.slyce.messaging.message.messageItem.MessageRecyclerAdapter;
import it.slyce.messaging.utils.Refresher;

public class UpdateMessageTask extends AsyncTaskBase<Void, Void, Integer> {
    private List<MessageItem> mMessageItems;
    private boolean refresh;
    private Message message;
    private MessageRecyclerAdapter mRecyclerAdapter;
    private Refresher mRefresher;

    public UpdateMessageTask(Message message, List<MessageItem> messageItems, MessageRecyclerAdapter mRecyclerAdapter, Refresher refresher, boolean refresh) {
        this.message = message;
        this.mRecyclerAdapter = mRecyclerAdapter;
        this.mRefresher = refresher;
        this.mMessageItems = messageItems;
        this.refresh = refresh;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mRefresher.setIsRefreshing(true);
    }

    @Override
    protected Integer doInBackground(Void[] objects) {
        for (int i = mMessageItems.size() - 1; i >= 0; i--) {
            MessageItem messageItem = mMessageItems.get(i);
            Message message = messageItem.getMessage();
            String messageId = message.getId();
            if (messageId != null) {
                if (messageId.equalsIgnoreCase(this.message.getId())) {
                    messageItem.setMessage(this.message);
                    return i;
                }
            } else if (message.getTempId() == this.message.getTempId()) {
                message.setState(this.message.getState());
                message.setId(this.message.getId());
                return i;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer position) {
        super.onPostExecute(position);

        if (position != null && refresh)
            mRecyclerAdapter.notifyItemChanged(position);

        mRefresher.setIsRefreshing(false);
    }
}
