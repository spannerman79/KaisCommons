package net.kaikk.mc.kaiscommons.bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.kaikk.mc.kaiscommons.IMessages;


public class Messages implements IMessages {
	private Map<String, String> messages = new HashMap<String, String>();

	public Messages(JavaPlugin instance) {
		CommonBukkitUtils.copyAsset(instance, "messages.yml");

		@SuppressWarnings("deprecation")
		FileConfiguration defaultMessages = YamlConfiguration.loadConfiguration(CommonBukkitUtils.getAsset(instance, "messages.yml"));
		FileConfiguration sMessages = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "messages.yml"));

		messages.clear();
		for (String key : defaultMessages.getKeys(false)) {
			messages.put(key, ChatColor.translateAlternateColorCodes('&', defaultMessages.getString(key)));
		}
		for (String key : sMessages.getKeys(false)) {
			messages.put(key, ChatColor.translateAlternateColorCodes('&', sMessages.getString(key)));
		}
	}

	@Override
	public String get(String id) {
		String m = messages.get(id);
		if (m == null) {
			return ChatColor.RED + "Couldn't find the message id \""+id+"\"!";
		}
		return m;
	}

	@Override
	public String get(String id, String... replacements) {
		String m = messages.get(id);
		if (m == null) {
			return ChatColor.RED + "Couldn't find the message id \""+id+"\"!";
		}
		for (int i = 0; i<replacements.length; i++) {
			m = m.replace("%"+replacements[i], replacements[++i]);
		}

		return m;
	}
}
