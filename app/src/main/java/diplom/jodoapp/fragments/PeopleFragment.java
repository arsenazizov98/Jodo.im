package diplom.jodoapp.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import diplom.jodoapp.MenuActivity;
import diplom.jodoapp.R;
import diplom.jodoapp.XMPP;

public class PeopleFragment extends Fragment {

    private View view;
    private ArrayList<String> friends;
    private ArrayList<String> id;
    private EditText friendEditText;
    LinearLayout contentPeople;
    private SQLiteDatabase dbContacts;
    private Roster roster;
    Set<RosterEntry> rosterFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        contentPeople = (LinearLayout)view.findViewById(R.id.contentPeople);
        ImageButton addFriend = (ImageButton) view.findViewById(R.id.addFriendButton);
        ImageButton deleteFriend = (ImageButton) view.findViewById(R.id.deleteFriendButton);
        ImageButton selectFriend = (ImageButton) view.findViewById(R.id.selectFriendButton);
        friendEditText = (EditText) view.findViewById(R.id.friendEditText);
        dbContacts = ((MenuActivity) getActivity()).getDataBaseContacts();
        friends = new ArrayList<>();
        roster = Roster.getInstanceFor(((((MenuActivity)getActivity()).getmService().xmpp).xmpptcpConnection));
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {
                createAllContacts();
            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {
                createAllContacts();
            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {
                createAllContacts();
            }

            @Override
            public void presenceChanged(Presence presence) {
                createAllContacts();
            }
        });
        rosterFriends = roster.getEntries();
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!friendEditText.getEditableText().toString().equalsIgnoreCase("")) {
                    String addFriendJID = friendEditText.getEditableText().toString();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("userJID",XMPP.login);
                    contentValues.put("friendJID", addFriendJID);
                    String TAG = "JoDo";
                    try {
                        roster.createEntry(addFriendJID, "", null);
                        dbContacts.insert("contacts",null,contentValues);
                        String fr = addFriendJID.split("@")[0];
                        Intent intent = new Intent("createFriendDB").putExtra("dbName",fr);
                        LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                    } catch (SmackException.NotLoggedInException e) {
                        Log.e(TAG,"NotLoggedInException");
                    } catch (SmackException.NoResponseException e) {
                        Log.e(TAG,"NoResponseException");
                    } catch (XMPPException.XMPPErrorException e) {
                        Log.e(TAG,"XMPPErrorException");
                    } catch (SmackException.NotConnectedException e) {
                        Log.e(TAG,"NotConnectedException");
                    }
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
                for (RosterEntry selectFriend:rosterFriends) {
                    if (selectFriend.getUser().equals(deleteFriend))
                        try {
                            roster.removeEntry(selectFriend);
                        } catch (SmackException.NotLoggedInException e) {
                            e.printStackTrace();
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                }

                createAllContacts();
            }
        });
        selectFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView scrollView = (ScrollView)contentPeople.getChildAt(1);
                RadioGroup radioGroup = (RadioGroup)scrollView.getChildAt(0);
                int idB = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(idB);
                String selectFriend = radioButton.getText().toString();
                XMPP.receiver = selectFriend;
                String fr = selectFriend.split("@")[0];
                final String sql = "create table if not exists "+XMPP.login+"(" +
                        "id integer primary key autoincrement," +
                        "body text," +
                        "isMy text," +
                        "isRead text" + ");";
                try {
                    ((MenuActivity) getActivity()).getDBFriends().get(fr).execSQL(sql, new String[]{});
                }catch (NullPointerException e ){
                    try{
                    Intent intent = new Intent("createFriendDB").putExtra("dbName",fr);
                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                    ((MenuActivity) getActivity()).getDBFriends().get(fr).execSQL(sql, new String[]{});
                    }catch (NullPointerException ee){
                        Intent intent1 = new Intent("createTable").putExtra("selectDB", fr);
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent1);
                        ((MenuActivity) getActivity()).getDBFriends();
                    }
                }
                Intent intent = new Intent("canReadDB").putExtra("dbName", fr);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                Intent setReceiver = new Intent("setReceiver").putExtra("setReceiver",selectFriend);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(setReceiver);
            }
        });
        for (int j = 0; j < 3; j ++) {
            createAllContacts();
        }
        return view;
    }

    //создание radioButton для каждого контакта
    public void createAllContacts(){
        RadioGroup radioGroup = new RadioGroup(view.getContext());
        ScrollView scrollView = new ScrollView(view.getContext());
        int i = 0;
        rosterFriends = roster.getEntries();
        dbContacts.delete("contacts",null,null);
        for (RosterEntry selectFriend:rosterFriends) {
            friends.add(selectFriend.getUser());
            ContentValues contentValues = new ContentValues();
            contentValues.put("userJID",XMPP.login);
            contentValues.put("friendJID", selectFriend.getUser());
            dbContacts.insert("contacts",null,contentValues);
            RadioButton radioButton = new RadioButton(view.getContext());
            radioButton.setId(i);
            radioButton.setText(selectFriend.getUser());
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
            i++;

        }
        if (scrollView.getChildCount() == 1)
        scrollView.removeViewAt(0);
        scrollView.addView(radioGroup);
        if (contentPeople.getChildCount() == 2)
                contentPeople.removeViewAt(1);
        contentPeople.addView(scrollView);
    }
}
