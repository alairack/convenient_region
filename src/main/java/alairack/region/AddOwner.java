package alairack.region;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddOwner implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 1) {
                String id = args[0];
                String ownerName = args[1];
                Player owner = sender.getServer().getPlayer(ownerName);
                if (owner != null) {
                    return RegionController.addOwner((Player) sender, id, owner);
                } else {
                    sender.sendMessage("找不到您指定的玩家!");
                }
            } else {
                sender.sendMessage("您输入的参数过少");
            }
        }
        return true;
    }
}
