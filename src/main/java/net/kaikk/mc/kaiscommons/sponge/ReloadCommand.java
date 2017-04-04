package net.kaikk.mc.kaiscommons.sponge;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ReloadCommand implements CommandExecutor {
	private final ReloadablePlugin instance;
	
	public ReloadCommand(ReloadablePlugin instance) {
		this.instance = instance;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			instance.load();
			src.sendMessage(Text.of(TextColors.GREEN, "Plugin reloaded succesfully"));
			return CommandResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			src.sendMessage(Text.of(TextColors.RED, "An error occurred while the plugin was reloaded"));
			return CommandResult.success();
		}
		
	}

}
