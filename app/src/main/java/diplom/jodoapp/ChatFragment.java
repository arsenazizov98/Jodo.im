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
import android.widget.TextView;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class ChatFragment extends Fragment {

    private LinearLayout chatFragment;
    TextView textViewMessage;
    LinearLayout.LayoutParams lParams;
    private Button button;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat,container,false);
        chatFragment = (LinearLayout) view.findViewById(R.id.chat);
        button = (Button) view.findViewById(R.id.button2);
        lParams = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        lParams.gravity = Gravity.RIGHT;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMessage = new TextView(getActivity());
                textViewMessage.setLayoutParams(lParams);
                textViewMessage.setGravity(Gravity.RIGHT);
                textViewMessage.setText(((EditText)view.findViewById(R.id.editText)).getText());
                chatFragment.addView(textViewMessage,lParams);
            }
        });
        return view;
    }
}
