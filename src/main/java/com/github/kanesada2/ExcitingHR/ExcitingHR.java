package com.github.kanesada2.ExcitingHR;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ExcitingHR extends JavaPlugin implements Listener{

	private ExcitingHRListener listener;
	private ExcitingHRCommandExecutor commandExecutor;

	@Override
    public void onEnable() {
		this.saveDefaultConfig();
        listener = new ExcitingHRListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        commandExecutor = new ExcitingHRCommandExecutor(this);
        getCommand("ExcitingHR").setExecutor(commandExecutor);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        getLogger().info("ExcitingHR Enabled!");
    }

    @Override
    public void onDisable() {
    }
}
