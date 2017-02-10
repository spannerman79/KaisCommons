package net.kaikk.mc.kaiscommons.sponge;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import net.kaikk.mc.kaiscommons.IMessages;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class Messages implements IMessages {
	private Map<String, String> messages = new HashMap<String, String>();

	public Messages(Object instance, Path configDir) {
		//load defaults
		try {
			Asset asset = Sponge.getAssetManager().getAsset(instance, "messages.yml").get();
			YAMLConfigurationLoader defaultsLoader = YAMLConfigurationLoader.builder().setURL(asset.getUrl()).build();
			ConfigurationNode defaults = defaultsLoader.load();

			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(configDir.resolve("messages.conf")).build();
			ConfigurationNode root = loader.load();
			root.mergeValuesFrom(defaults);
			loader.save(root);

			for (Entry<Object, ? extends ConfigurationNode> entry : root.getChildrenMap().entrySet()) {
				messages.put(entry.getKey().toString(), entry.getValue().getString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Text getText(String id) {
		String m = messages.get(id);
		if (m == null) {
			return Text.of(TextColors.RED, "Couldn't find the message id \""+id+"\"!");
		}

		return TextSerializers.FORMATTING_CODE.deserialize(m);
	}

	public Text getText(String id, String... replacements) {
		String m = messages.get(id);
		if (m == null) {
			return Text.of(TextColors.RED, "Couldn't find the message id \""+id+"\"!");
		}
		for (int i = 0; i<replacements.length; i++) {
			m = m.replace("%"+replacements[i], replacements[++i]);
		}

		return TextSerializers.FORMATTING_CODE.deserialize(m);

	}

	@SuppressWarnings("deprecation")
	@Override
	public String get(String id) {
		return TextSerializers.LEGACY_FORMATTING_CODE.serialize(this.getText(id));
	}

	@SuppressWarnings("deprecation")
	@Override
	public String get(String id, String... replacements) {
		return TextSerializers.LEGACY_FORMATTING_CODE.serialize(this.getText(id, replacements));
	}
}
