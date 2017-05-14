package diplom.jodoapp.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import java.util.ArrayList;
import diplom.jodoapp.MenuActivity;
import diplom.jodoapp.R;
import diplom.jodoapp.XMPP;

public class PeopleFragment extends Fragment {

    private View view;
    String selectFriend;
    private ArrayList<String> friends;
    private ArrayList<String> id;
    private EditText friendEditText;
    LinearLayout contentPeople;
    MenuActivity activity;
    SQLiteDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        contentPeople = (LinearLayout)view.findViewById(R.id.contentPeople);
        ImageButton addFriend = (ImageButton) view.findViewById(R.id.addFriendButton);
        ImageButton deleteFriend = (ImageButton) view.findViewById(R.id.deleteFriendButton);
        friendEditText = (EditText) view.findViewById(R.id.friendEditText);
        activity = ((MenuActivity) getActivity());
        db = activity.getDataBase();
        friends = new ArrayList<>();
        createAllContacts();
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!friendEditText.getEditableText().toString().equalsIgnoreCase("")) {
                    String addFriendJID = friendEditText.getEditableText().toString();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("userJID",XMPP.login);
                    contentValues.put("friendJID", addFriendJID);
                    db.insert("contacts",null,contentValues);
                    createAllContacts();
                }
            }
        });
        deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView scrollView = (ScrollView)contentPeople.getChildAt(1);
                RadioGroup radioGroup = (RadioGroup)scrollView.getChildAt(0);
                int idB = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(idB);
                String deleteFriend = radioButton.getText().toString();
                friendEditText.setText(deleteFriend);
                ContentValues contentValues = new ContentValues();
                contentValues.put("friendJID",deleteFriend);
                db.delete("contacts","id = " + Integer.parseInt(id.get(idB)),null);
                createAllContacts();
            }
        });


        return view;
    }

    //создание radioButton для каждого контакта
    public void createAllContacts(){
        RadioGroup radioGroup = new RadioGroup(view.getContext());
        ScrollView scrollView = new ScrollView(view.getContext());
        Cursor cursor = db.query("contacts",null,null,null,null,null,null);
        friends = new ArrayList<>();
        id = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int indexFriendJID = cursor.getColumnIndex("friendJID");
            int indexId = cursor.getColumnIndex("id");
            do {
                friends.add(cursor.getString(indexFriendJID));
                id.add(String.valueOf(cursor.getInt(indexId)));
            }while (cursor.moveToNext());
            if (radioGroup.getChildCount() == 0)
                for (int i = 0 + 1, n = friends.size(); i < n; i++) {
                    RadioButton radioButton = new RadioButton(view.getContext());
                    radioButton.setText(friends.get(i));
                    radioButton.setId(i);
                    if (i == 1) {
                        radioButton.setChecked(true);
                    }
                    radioGroup.addView(radioButton);
                }
            if (scrollView.getChildCount() == 1)
                scrollView.removeViewAt(0);
            scrollView.addView(radioGroup);
            if (contentPeople.getChildCount() == 2)
                contentPeople.removeViewAt(1);
            contentPeople.addView(scrollView);
        }
    }
}
