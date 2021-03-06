package cn.nukkit.network.protocol;

import java.util.UUID;

/**
 * Created by on 15-10-13.
 */
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.LOGIN_PACKET;

    public String username;

    public int protocol1;
    public int protocol2;

    public long clientId;
    public UUID clientUUID;

    public String serverAddress;
    public String clientSecret;

    public boolean slim = false;
    public String skin = null;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.username = this.getString();
        this.protocol1 = this.getInt();
        this.protocol2 = this.getInt();
        if (protocol1 < Info.CURRENT_PROTOCOL) {
            this.setBuffer(null, 0);
            return;
        }
        this.clientId = this.getLong();
        this.clientUUID = this.getUUID();
        this.serverAddress = this.getString();
        this.clientSecret = this.getString();

        this.slim = this.getByte() > 0;
        this.skin = this.getString();
    }


    @Override
    public void encode() {

    }

}
