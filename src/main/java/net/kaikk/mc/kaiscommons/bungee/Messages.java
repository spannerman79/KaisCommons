package net.kaikk.mc.kaiscommons.bungee;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.kaikk.mc.kaiscommons.IMessages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;


public class Messages implements IMessages {
	private Map<String, String> messages = new HashMap<String, String>();

	public Messages(Plugin instance) throws IOException {
		CommonBungeeUtils.copyAsset(instance, "messages.yml");

		Configuration defaultMessages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(CommonBungeeUtils.getAsset(instance, "messages.yml"));
		Configuration sMessages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getDataFolder(), "messages.yml"));
		
		messages.clear();
		for (String key : defaultMessages.getKeys()) {
			messages.put(key, ChatColor.translateAlternateColorCodes('&', defaultMessages.getString(key)));
		}
		for (String key : sMessages.getKeys()) {
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
