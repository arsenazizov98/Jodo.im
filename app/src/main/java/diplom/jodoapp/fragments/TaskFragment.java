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
import diplom.jodoapp.XMPP;

public class TaskFragment extends Fragment {
    TextView taskView;
    private String user1 = "", user2 = "";
    String taskText;
    LinearLayout linearLayout;
    private EditText commandEditText;
    MenuActivity activity;
    View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task, container, false);
        activity = (MenuActivity) getActivity();
        linearLayout = (LinearLayout)view.findViewById(R.id.contentTask);
        taskText = "";
        taskView = (TextView) view.findViewById(R.id.taskTextView);
        user2 = XMPP.receiver;
        sendCommand(getResources().getString(R.string.tree_command));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                taskText = intent.getStringExtra("#tree");
                if (!taskText.contains(getResources().getString(R.string.no_task))) {
                    String splitStr = "";
                    if (taskText.contains(getResources().getString(R.string.task_tree_en)))
                        splitStr = getResources().getString(R.string.task_tree_en);
                    else
                        splitStr = getResources().getString(R.string.task_tree_ru);
                    try {
                        taskView.setText(taskText.split(splitStr)[0]);
                        RadioGroup radioGroup = new RadioGroup(view.getContext());
                        ScrollView scrollView = new ScrollView(view.getContext());
                        String taskMass[] = taskText.split(splitStr)[1].split("\n");
                        if (radioGroup.getChildCount() == 0)
                            for (int i = 0 + 1; i < taskMass.length; i++) {
                                RadioButton radioButton = new RadioButton(view.getContext());
                                radioButton.setText(taskMass[i]);
                                radioButton.setId(i);
                                if (i == 1) {
                                    radioButton.setChecked(true);
                                }
                                radioGroup.addView(radioButton);
                            }
                        if (scrollView.getChildCount() == 1)
                            scrollView.removeViewAt(0);
                        scrollView.addView(radioGroup);
                        if (linearLayout.getChildCount() == 2)
                            linearLayout.removeViewAt(1);
                        linearLayout.addView(scrollView);
                    }catch (Exception e){

                    }
                }
                else {
                    if (linearLayout.getChildCount() == 2)
                        linearLayout.removeViewAt(1);
                }
            }
        },new IntentFilter("#tree"));
        ImageButton addButton = (ImageButton)view.findViewById(R.id.addTaskButton);
        commandEditText = (EditText) view.findViewById(R.id.commandEditText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTask = commandEditText.getEditableText().toString();
                if (!nameTask.equalsIgnoreCase("")) {
                    try {
                        ScrollView scrollView = (ScrollView) linearLayout.getChildAt(1);
                        RadioGroup radioGroup = (RadioGroup) scrollView.getChildAt(0);
                        int id = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) radioGroup.findViewById(id);
                        String taskNum = radioButton.getText().toString().split(". ")[0];
                        try {
                            int num = Integer.parseInt(taskNum);
                            sendCommand("+" + taskNum + nameTask);
                            sendCommand(getResources().getString(R.string.tree_command));
                        } catch (Exception e) {
                            String newTaskNum = taskNum.substring(1);
                            sendCommand("+" + newTaskNum + nameTask);
                            sendCommand(getResources().getString(R.string.tree_command));
                        }
                    }catch(Exception e){
                        taskView.setText(getResources().getString(R.string.no_task));
                    }
                }
                commandEditText.setText("");
                sendCommand(getResources().getString(R.string.tree_command));
            }
        });
        ImageButton deleteButton = (ImageButton)view.findViewById(R.id.deleteTaskButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.close_command));
            }
        });
        ImageButton upButton = (ImageButton)view.findViewById(R.id.upTaskButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.up_command));
            }
        });

        ImageButton downButton = (ImageButton)view.findViewById(R.id.downTaskButton);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.down_command));
            }
        });

        ImageButton doneButton = (ImageButton)view.findViewById(R.id.doneTaskButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.done_command));
            }
        });
        ImageButton okButton = (ImageButton)view.findViewById(R.id.okTaskButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.ok_command));
            }
        });
        ImageButton stopButton = (ImageButton)view.findViewById(R.id.stopTaskButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask("#stop");
            }
        });
        ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancelTaskButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask("#cancel");
            }
        });
        ImageButton topButton = (ImageButton)view.findViewById(R.id.topTaskButton);
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask("#top");
            }
        });
        ImageButton bottomButton = (ImageButton)view.findViewById(R.id.bottomTaskButton);
        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask("#bottom");
            }
        });
        ImageButton startButton = (ImageButton)view.findViewById(R.id.startTaskButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.start_command));
            }
        });
        /*ImageButton noButton = (ImageButton)view.findViewById(R.id.noTaskButton);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.no_command));
            }
        });*/
        ImageButton rmButton = (ImageButton)view.findViewById(R.id.rmTaskButton);
        rmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.rm_command));

            }
        });
        ImageButton refreshButton = (ImageButton)view.findViewById(R.id.refreshTaskButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(getResources().getString(R.string.tree_command));
            }
        });
        ImageButton treeAllButton = (ImageButton)view.findViewById(R.id.treeAllTaskButton);
        treeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(getResources().getString(R.string.treeall_command));
            }
        });
        ImageButton freezeButton = (ImageButton)view.findViewById(R.id.freezeTaskButton);
        freezeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdTask(getResources().getString(R.string.freeze_command));
            }
        });
        return view;
    }

    private void sendCommand(String command){
        Random random = new Random();
        final ChatMessage chatMessage = new ChatMessage(user1, user2, command, "" + random.nextInt(2100000000), true);
        chatMessage.setMsgID();
        chatMessage.body = command;
        chatMessage.Date = CommonMethods.getCurrentDate();
        chatMessage.Time = CommonMethods.getCurrentTime();
        activity.getmService().xmpp.sendMessage(chatMessage);
    }

    private void getIdTask(String command){
        try {
            ScrollView scrollView = (ScrollView) linearLayout.getChildAt(1);
            RadioGroup radioGroup = (RadioGroup) scrollView.getChildAt(0);
            int id = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) radioGroup.findViewById(id);
            String taskNum = radioButton.getText().toString().split(". ")[0];
            try {
                int num = Integer.parseInt(taskNum);
                sendCommand(command + " " + taskNum);
                sendCommand(getResources().getString(R.string.tree_command));
            } catch (Exception e) {
                String newTaskNum = taskNum.substring(1);
                sendCommand(command +" " + newTaskNum);
                sendCommand(getResources().getString(R.string.tree_command));
            }
        }catch(Exception e){
            taskView.setText(getResources().getString(R.string.no_task));
        }
    }
}
