package com.joojn.server.plugin;

public interface CommandExecutor {

    public void onCommand(Sender sender, String alias, String[] args);

    public String[] getAliases();

}
