package net.kaikk.mc.kaiscommons.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import net.md_5.bungee.api.plugin.Plugin;

public class CommonBungeeUtils {
	public static File copyAsset(Plugin instance, String assetName) {
		File file = new File(instance.getDataFolder(), assetName);
		file.getParentFile().mkdirs();
		if (!file.exists()) {
			try {
				Files.copy(getAsset(instance, assetName),
						file.getAbsoluteFile().toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return file;
	}
	
	public static InputStream getAsset(Plugin instance, String assetName) {
		return instance.getResourceAsStream("assets/"+instance.getDescription().getName().toLowerCase()+"/"+assetName);
	}
}
