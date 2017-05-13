package diplom.jodoapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Random;

import diplom.jodoapp.ChatMessage;
import diplom.jodoapp.CommonMethods;
import diplom.jodoapp.MenuActivity;
import diplom.jodoapp.R;

public class TaskFragment extends Fragment {
    TextView taskView;
    private String user1 = "", user2 = "arsentest@jodo.im";
    String taskText;
    LinearLayout linearLayout;
    private EditText commandEditText;
    View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task, container, false);
        linearLayout = (LinearLayout)view.findViewById(R.id.contentTask);
        taskText = "";
        taskView = (TextView) view.findViewById(R.id.taskTextView);
        sendCommand("#tree");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                taskText = intent.getStringExtra("#tree");
                taskView.setText(taskText.split(":")[0]);
                RadioGroup radioGroup = new RadioGroup(view.getContext());
                ScrollView scrollView = new ScrollView(view.getContext());
                String taskMass[] = taskText.split(":")[1].split("\n");
                if (radioGroup.getChildCount() == 0)
                    for (int i = 0 + 1; i < taskMass.length; i++) {
                        RadioButton radioButton = new RadioButton(view.getContext());
                        radioButton.setText(taskMass[i]);
                        radioButton.setId(i);
                        radioGroup.addView(radioButton);
                    }
                if (scrollView.getChildCount()==1)
                    scrollView.removeViewAt(0);
                scrollView.addView(radioGroup);
                if (linearLayout.getChildCount()==2)
                    linearLayout.removeViewAt(1);
                linearLayout.addView(scrollView);
            }
        },new IntentFilter("#tree"));
        ImageButton addButton = (ImageButton)view.findViewById(R.id.addTaskButton);
        commandEditText = (EditText) view.findViewById(R.id.commandEditText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTask = commandEditText.getEditableText().toString();
                if (!nameTask.equalsIgnoreCase("")) {
                    sendCommand("+"+nameTask);
                }
                commandEditText.setText("");
                sendCommand("#tree");
            }
        });
        ImageButton deleteButton = (ImageButton)view.findViewById(R.id.deleteTaskButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView scrollView = (ScrollView)linearLayout.getChildAt(1);
                RadioGroup radioGroup = (RadioGroup)scrollView.getChildAt(0);
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(id);
                String taskNum = radioButton.getText().toString().split(". ")[0];
                try {
                    int num = Integer.parseInt(taskNum);
                    sendCommand("#close "+taskNum);
                    sendCommand("#tree");
                }catch (Exception e){
                    String newTasknum = taskNum.substring(1);
                    sendCommand("#close "+newTasknum);
                    sendCommand("#tree");
                }

            }
        });
        return view;
    }



    public void sendCommand(String command){
        Random random = new Random();
        final ChatMessage chatMessage = new ChatMessage(user1, user2, command, "" + random.nextInt(2100000000), true);
        chatMessage.setMsgID();
        chatMessage.body = command;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        MenuActivity activity = ((MenuActivity) getActivity());
        activity.getmService().xmpp.sendMessage(chatMessage);
    }
}
