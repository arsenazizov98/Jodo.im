package diplom.jodoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class ChatFragment extends Fragment {

    private LinearLayout chatFragment;
    TextView textView222;
    TextView textView22;
    LinearLayout.LayoutParams lParams;
    private Button button;
    int x = 0;
    int y = 0;
    View view;

    int i =10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //chat();
        view = inflater.inflate(R.layout.fragment_chat,container,false);
        chatFragment = (LinearLayout) view.findViewById(R.id.chat);
        button = (Button) view.findViewById(R.id.button2);
        lParams = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        lParams.gravity = Gravity.RIGHT;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView222 = new TextView(getActivity());
                textView22 = new TextView(getActivity());
                textView222.setLayoutParams(lParams);
                textView222.setGravity(Gravity.RIGHT);
                textView22.setGravity(Gravity.RIGHT);
                textView222.setText(((EditText)view.findViewById(R.id.editText)).getText());
                textView22.setLayoutParams(lParams);
                textView22.setText(((EditText)view.findViewById(R.id.editText)).getText());
                chatFragment.addView(textView222,lParams);
                chatFragment.addView(textView22,lParams);
            }
        });
        return view;
    }
}
