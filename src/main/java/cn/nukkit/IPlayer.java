package cn.nukkit;

import cn.nukkit.permission.ServerOperator;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface IPlayer extends ServerOperator {

    boolean isOnline();

    String getName();

    boolean isBanned();

    void setBanned(boolean value);

    boolean isWhitelisted();

    void setWhitelisted(boolean value);

    Player getPlayer();

    Long getFirstPlayed();

    Long getLastPlayed();

    Object hasPlayedBefore();

}
