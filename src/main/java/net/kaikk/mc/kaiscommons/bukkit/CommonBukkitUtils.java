package net.kaikk.mc.kaiscommons.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

public class CommonBukkitUtils {
	public static void copyAsset(JavaPlugin instance, String assetName) {
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
	}
	
	public static InputStream getAsset(JavaPlugin instance, String assetName) {
		return instance.getResource("assets/"+instance.getName().toLowerCase()+"/"+assetName);
	}
	
	public static boolean isFakePlayer(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if(player==p) {
				return false;
			}
		}
		return true;
	}
	
	public static String locationToString(Location location) {
		return "[" + location.getWorld().getName() + ", " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "]";
	}
	
	public static Block getTargetBlock(Player player, int maxDistance) {
		return getTargetBlock(player, maxDistance, Collections.emptySet());
	}
	
	public static Block getTargetBlock(Player player, int maxDistance, Set<Material> transparentMaterials) {
		final BlockIterator iterator = new BlockIterator(player.getLocation(), player.getEyeHeight(), maxDistance);
		Block result = player.getLocation().getBlock().getRelative(BlockFace.UP);
		while (iterator.hasNext()) {
			result = iterator.next();
			if (result.getType() != Material.AIR && !transparentMaterials.contains(result.getType())) {
				return result;
			}
		}

		return result;
	}
}
