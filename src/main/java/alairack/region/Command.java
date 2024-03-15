package alairack.region;

import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
        if (args.length >= 1) {
            if (args[0].equals("nbt")) {
                String regionID = null;
                if (args.length > 1) {
                    regionID = args[1];
                }
                if (!(((Player) sender).getInventory().getItemInMainHand().getType().equals(Material.AIR)))
                {
                    return RegionController.makeTool((Player) sender, regionID);
                }
                sender.sendMessage("您必须手持物品以转换为领地工具!");
                return true;
            }
            else {
                if (args.length > 1){
                    if (args[0].equals("create")) {

                        String id = args[1];
                        RegionController.setNewPoint((Player) sender, id);
                        return true;
                    }
                    else if (args[0].equals("addMember")) {
                        if (args.length >2){
                            String id = args[1];
                            String memberName = args[2];
                            Player member = sender.getServer().getPlayer(memberName);
                            if (member != null){
                                return RegionController.addMember((Player) sender, id, member);

                            }
                            else {
                                sender.sendMessage("找不到您指定的玩家!");
                            }
                        }
                    }
                }


            }
            }
        else {
            sender.sendMessage("您输入的参数过少");
            return true;
        }
        }
        else {
            return true;
        }
        return true;
    }
}

