package diplom.jodoapp;

/**
 * Created by arsen on 28.04.2017.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null; //заимствовано с форума StackOverFlow
    ArrayList<ChatMessage> chatMessageList; //саморасширяющий массив сообщений


    public ChatAdapter(Activity activity, ArrayList<ChatMessage> list) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //заимствовано с форума StackOverFlow
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //инициализация нового textView для сообщения
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = chatMessageList.get(position);
        View messageView = convertView;
        if (convertView == null)
            messageView = inflater.inflate(R.layout.text_message, null);

        TextView msg = (TextView) messageView.findViewById(R.id.message_text);
        msg.setText(message.body);
        LinearLayout layout = (LinearLayout) messageView.findViewById(R.id.massageLL_forBackground);
        LinearLayout parent_layout = (LinearLayout) messageView.findViewById(R.id.message_parentLL_forGravity);

        if (message.isMy) {
            layout.setBackgroundResource(R.drawable.message_bg_send);
            parent_layout.setGravity(Gravity.RIGHT);
        }
        else {
            layout.setBackgroundResource(R.drawable.message_bg_receive);
            parent_layout.setGravity(Gravity.LEFT);
        }
        msg.setTextColor(Color.BLACK);
        return messageView;
    }

    //добавление сообщения в массив
    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }
}