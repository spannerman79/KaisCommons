package net.kaikk.mc.kaiscommons.sponge;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

public class CommonSpongeUtils {
	public static String getNameFromId(UUID uuid) {
		Optional<GameProfile> profile = Sponge.getServer().getGameProfileManager().getCache().getOrLookupById(uuid);
		if (profile.isPresent() && profile.get().getName().isPresent()) {
			return profile.get().getName().get();
		}

		return "N/A";
	}

	public static BlockType getBlockType(String blockId) {
		return Sponge.getRegistry().getType(BlockType.class, blockId).get();
	}

	public static ItemType getItemType(String itemId) {
		return Sponge.getRegistry().getType(ItemType.class, itemId).get();
	}

	public static void spawnItem(ItemType itemType, int quantity, Location<World> location) {
		ItemStack newstack = ItemStack.builder().itemType(itemType).quantity(quantity).build();

		Extent extent = location.getExtent();
		Entity optional = extent.createEntity(EntityTypes.ITEM, location.getPosition());
		optional.offer(Keys.REPRESENTED_ITEM, newstack.createSnapshot());
		optional.offer(Keys.PICKUP_DELAY, 40);
		extent.spawnEntity(optional, Cause
				.source(EntitySpawnCause.builder().entity(optional).type(SpawnTypes.DROPPED_ITEM).build()).build());
	}
}
