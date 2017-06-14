package diplom.jodoapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import diplom.jodoapp.R;

public class HelpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        Button lone = (Button)view.findViewById(R.id.lone);
        lone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/Advantages_of_the_system/"));
                startActivity(browserIntent);
            }
        });
        Button ltwo = (Button)view.findViewById(R.id.ltwo);
        ltwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/How_to_start_working_with_the_system/"));
                startActivity(browserIntent);
            }
        });
        Button lthree = (Button)view.findViewById(R.id.lthree);
        lthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/How_to_start_working_with_the_system_for_employee/"));
                startActivity(browserIntent);
            }
        });
        Button lfour = (Button)view.findViewById(R.id.lfour);
        lfour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/How_to_choose_jabber_client/"));
                startActivity(browserIntent);
            }
        });
        Button lfive = (Button)view.findViewById(R.id.lfive);
        lfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/How_to_use_site/"));
                startActivity(browserIntent);
            }
        });
        Button lsix = (Button)view.findViewById(R.id.lsix);
        lsix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/How_to_use_site_employee/"));
                startActivity(browserIntent);
            }
        });
        Button one = (Button)view.findViewById(R.id.one);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/FAQ/"));
                startActivity(browserIntent);
            }
        });
        Button two = (Button)view.findViewById(R.id.two);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/Bots"));
                startActivity(browserIntent);
            }
        });
        Button three = (Button)view.findViewById(R.id.three);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/Commands_guide"));
                startActivity(browserIntent);
            }
        });
        Button four = (Button)view.findViewById(R.id.four);
        lfour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/Workflow/"));
                startActivity(browserIntent);
            }
        });
        Button five = (Button)view.findViewById(R.id.five);
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jodo.im/help/video/"));
                startActivity(browserIntent);
            }
        });
        return view;
    }
}
