package diplom.jodoapp;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by arsen on 25.04.2017.
 */

public class XmppConnectionHolder{
    private AbstractXMPPConnection connection = null;

    private static XmppConnectionHolder instance = null;

    public synchronized static XmppConnectionHolder getInstance() {
        if(instance==null){
            instance = new XmppConnectionHolder();
        }
        return instance;
    }

    public void setConnection(XMPPTCPConnection connection){
        this.connection = connection;
    }

    public XMPPTCPConnection getConnection() {
        return (XMPPTCPConnection)this.connection;
    }

    public void destroyConnection(){
        connection = null;
        instance = null;
    }
}
