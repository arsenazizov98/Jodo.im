package diplom.jodoapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEdit;
    private EditText passEdit;
    private Button authorisation;
    private Button registration;
    private final String HOST = "jodo.im";
    private final String DOMAIN = "jodo.im";
    private final int port = 5222;
    private String login = "";
    private String pass = "";
    private String resource = "activity_login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEdit = (EditText) findViewById(R.id.login);
        passEdit = (EditText) findViewById(R.id.pass);
        authorisation = (Button) findViewById(R.id.autorisation);
        registration = (Button) findViewById(R.id.registration);
        final Animation animationButAuth = AnimationUtils.loadAnimation(this,R.anim.scale_button);
        final Animation animationButReg = AnimationUtils.loadAnimation(this,R.anim.scale_button_wait);
        Animation animationLogin = AnimationUtils.loadAnimation(this,R.anim.myscale);
        final Animation animationTwoLogin = AnimationUtils.loadAnimation(this,R.anim.scale_two);
        final Animation animationTreeLogin = AnimationUtils.loadAnimation(this,R.anim.scale_tree);
        Animation animationPass = AnimationUtils.loadAnimation(this,R.anim.scale_wait);
        final Animation animationTwoPass = AnimationUtils.loadAnimation(this,R.anim.scale_two);
        final Animation animationTreePass = AnimationUtils.loadAnimation(this,R.anim.scale_tree);
        animationTwoLogin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loginEdit.startAnimation(animationTreeLogin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationLogin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loginEdit.startAnimation(animationTwoLogin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationTwoPass.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                passEdit.startAnimation(animationTreePass);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationPass.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                passEdit.startAnimation(animationTwoPass);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        passEdit.startAnimation(animationPass);
        loginEdit.startAnimation(animationLogin);
        authorisation.startAnimation(animationButAuth);
        registration.startAnimation(animationButReg);
        authorisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login = loginEdit.getText().toString();
                pass = passEdit.getText().toString();
                XMPPTCPConnectionConfiguration.Builder configConnect = XMPPTCPConnectionConfiguration.builder();
                configConnect.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                configConnect.setHost(HOST);
                configConnect.setServiceName(DOMAIN);
                configConnect.setResource(resource);
                configConnect.setPort(port);
                configConnect.setUsernameAndPassword(login, pass);
                final XMPPTCPConnection xmppConnection = new XMPPTCPConnection(configConnect.build());
                AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        boolean isCon = false;
                        try {
                            xmppConnection.connect();
                            isCon = true;
                        }catch (SmackException e ){

                        }catch (XMPPException e){

                        }catch(IOException e){

                        }
                        return isCon;
                    }
                };
                connectionThread.execute();
                boolean isCon = false;
                try {
                    isCon = connectionThread.get();
                }catch(ExecutionException e){

                }catch (InterruptedException e){

                }
                if (xmppConnection.isConnected()&&isCon)
                    loginEdit.setText("IT`S WORKED");
                else
                    loginEdit.setText("don`t work((((");
            }
        });
    }
}
