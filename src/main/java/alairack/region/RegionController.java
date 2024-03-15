package alairack.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RegionController {
    private static Map<UUID, Location> map = new HashMap<>();
    private static FileConfiguration configuration = Region.getInstance().config;

    public static boolean setNewPoint(Player player, String id) {
        UUID uuid = player.getUniqueId();
        if (map.containsKey(uuid)) {
            Location secend_location = player.getLocation();
            secend_location.setY(secend_location.getY() - 1);
            Location first_location = map.get(uuid);
            BlockVector3 min = BlockVector3.at(first_location.getBlockX(), first_location.getBlockY(), first_location.getBlockZ());
            BlockVector3 max = BlockVector3.at(secend_location.getBlockX(), secend_location.getBlockY(), secend_location.getBlockZ());

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            World world = BukkitAdapter.adapt(player.getWorld());
            RegionManager regions = container.get(world);
            if (regions == null) {
                player.sendMessage("此世界不支持领地！");
            } else {
                if (regions.hasRegion(id)) {
                    player.sendMessage("已经有领地是这个编号了!");
                } else {
                    ProtectedRegion region = new ProtectedCuboidRegion(id, min, max);
                    region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
                    region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.ALLOW);
                    region.setFlag(Flags.TNT, StateFlag.State.ALLOW);
                    region.setFlag(Flags.LIGHTER, StateFlag.State.ALLOW);
                    region.setFlag(Flags.DENY_MESSAGE, "");
//                    region.setFlag(Flags.ENTRY_DENY_MESSAGE, "");
//                    region.setFlag(Flags.EXIT_DENY_MESSAGE, "");
//                    region.setFlag(Flags.FAREWELL_MESSAGE, "");
//                    region.setFlag(Flags.GREET_TITLE, "");
//                    region.setFlag(Flags.GREET_MESSAGE, "");
                    region.setFlag(Flags.BLOCK_BREAK.getRegionGroupFlag(), RegionGroup.MEMBERS);
                    region.setFlag(Flags.BLOCK_PLACE.getRegionGroupFlag(), RegionGroup.MEMBERS);
                    region.setFlag(Flags.TNT.getRegionGroupFlag(), RegionGroup.MEMBERS);
                    region.setFlag(Flags.LIGHTER.getRegionGroupFlag(), RegionGroup.MEMBERS);
                    region.setFlag(Flags.DENY_MESSAGE.getRegionGroupFlag(), RegionGroup.ALL);
//                    region.setFlag(Flags.ENTRY_DENY_MESSAGE.getRegionGroupFlag(), RegionGroup.ALL);
//                    region.setFlag(Flags.EXIT_DENY_MESSAGE.getRegionGroupFlag(), RegionGroup.ALL);
//                    region.setFlag(Flags.FAREWELL_MESSAGE.getRegionGroupFlag(), RegionGroup.ALL);
//                    region.setFlag(Flags.GREET_TITLE.getRegionGroupFlag(), RegionGroup.ALL);
//                    region.setFlag(Flags.GREET_MESSAGE.getRegionGroupFlag(), RegionGroup.ALL);
                    regions.addRegion(region);
                    map.remove(uuid);
                    player.sendMessage("领地创建成功！");
                    player.sendMessage(String.format("领地 %s 端点1为%s %s %s", id, first_location.getBlockX(), first_location.getBlockY(), first_location.getBlockZ()));
                    player.sendMessage(String.format("领地 %s 端点2为%s %s %s", id, secend_location.getBlockX(), secend_location.getBlockY(), secend_location.getBlockZ()));
                }
            }


        } else {
            Location location = player.getLocation();
            location.setY(location.getBlockY() - 1);
            map.put(player.getUniqueId(), location);
            player.sendMessage(String.format("已添加位置作为第一个端点  %s, %s, %s", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
        return true;
    }

    public static boolean addMember(Player owner, String id, Player member) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(owner.getWorld());
        RegionManager regions = container.get(world);
        if (regions != null) {
            ProtectedRegion protectedRegion = regions.getRegion(id);
            if (protectedRegion != null) {
                if (protectedRegion.getOwners().contains(owner.getUniqueId())) {
                    DefaultDomain members = protectedRegion.getMembers();
                    if (members.contains(member.getUniqueId())) {
                        owner.sendMessage("您要添加的玩家已经是这块领地的成员了！");
                    } else {
                        members.addPlayer(member.getUniqueId());
                        owner.sendMessage(String.format("已经把该玩家添加为 %s 领地的成员！", id));
                        owner.sendMessage(String.format("您已被添加为 %s 领地的成员！", id));
                        return true;
                    }
                } else {
                    owner.sendMessage("您并不是这块领地的主人!");
                }
            } else {
                owner.sendMessage("您指定的领地不存在，请检查领地编号！");
                return false;
            }
        } else {
            owner.sendMessage("该世界不支持领地！");
            return false;
        }
        return false;
    }

    public static boolean addOwner(Player owner, String id, Player new_owner) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(owner.getWorld());
        RegionManager regions = container.get(world);
        if (regions != null) {
            ProtectedRegion protectedRegion = regions.getRegion(id);
            if (protectedRegion != null) {
                if (protectedRegion.getOwners().contains(owner.getUniqueId())) {
                    DefaultDomain owners = protectedRegion.getOwners();
                    if (owners.contains(new_owner.getUniqueId())) {
                        owner.sendMessage("您要添加的玩家已经是这块领地的主人了！");
                    } else {
                        owners.addPlayer(new_owner.getUniqueId());
                        owner.sendMessage(String.format("已经把该玩家添加为 %s 领地的主人！", id));
                        new_owner.sendMessage(String.format("您已被添加为 %s 领地的主人！", id));
                        return true;
                    }
                } else {
                    owner.sendMessage("您并不是这块领地的主人!");
                }
            } else {
                owner.sendMessage("您指定的领地不存在，请检查领地编号！");
                return true;
            }
        } else {
            owner.sendMessage("该世界不支持领地！");
            return true;
        }
        return true;
    }

    public static boolean makeTool(Player maker, String regionID) {
        ItemStack stick = maker.getInventory().getItemInMainHand();

        ItemMeta meta = stick.getItemMeta();
        String nbt_name = configuration.getString("nbt-name");
        if (nbt_name == null || nbt_name.isEmpty()){
            Bukkit.getLogger().info("nbt name is empty");
        }
        else {
            meta.setDisplayName(nbt_name.replace('&', '§'));
        }

        String nbt_description = configuration.getString("nbt-description");
        if (nbt_description==null || nbt_description.isEmpty()){
            Bukkit.getLogger().info("nbt description is empty");
        }
        else {
            ArrayList<String> lore = new ArrayList<>();
            lore.add(nbt_description.replace('&', '§'));
            meta.setLore(lore);
        }

        stick.setItemMeta(meta);
        if (regionID == null) {
            NBT.modify(stick, nbt -> {
                nbt.setString("regionID", "random");
            });
            maker.sendMessage("已将NBT道具设置为随机获取一个领地");
//            maker.getInventory().addItem(stick);
            return true;
        }
        else if (regionID.isEmpty()){
            maker.sendMessage("请不要将领地编号输入为空格!");
            return true;
        } else {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            World world = BukkitAdapter.adapt(maker.getWorld());
            RegionManager regions = container.get(world);
            if (regions != null) {
                ProtectedRegion protectedRegion = regions.getRegion(regionID);
                if (protectedRegion == null) {
                    maker.sendMessage("您要绑定的领地不存在！请检查编号");
                    return true;
                } else {
                    NBT.modify(stick, nbt -> {
                        nbt.setString("regionID", regionID);
                    });
                    maker.sendMessage(String.format("已将领地'%s'绑定到NBT道具中", regionID));
//                    maker.getInventory().addItem(stick);
                    return true;
                }
            } else {
                maker.sendMessage("该世界不支持领地！");
            }
        }
        return true;
    }
    public static boolean useTool(Player user, String regionID){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(user.getWorld());
        RegionManager regions = container.get(world);
        if (regions != null) {
            if (regionID.equals("random")) {
                Map<String, ProtectedRegion> regionMap = regions.getRegions();
                List<String> availableRegionID = new ArrayList<>();
                for (Map.Entry<String, ProtectedRegion> entry : regionMap.entrySet()) {
                    if (!entry.getValue().getId().equals("__global__") && entry.getValue().getOwners().size() == 0){
                        availableRegionID.add(entry.getKey());
                    }
                }
                if (availableRegionID.isEmpty()){
                    user.sendMessage("目前没有任何领地可以获取");
                    return false;
                }
                int RandomIndex = (int) (Math.random()* availableRegionID.size());
                String randomRegionID = availableRegionID.get(RandomIndex);
                ProtectedRegion region = regionMap.get(randomRegionID);
                if (randomRegionID != null && !randomRegionID.isEmpty() && region != null){
                    region.getOwners().addPlayer(user.getUniqueId());
                    user.sendMessage(String.format("恭喜您获得了编号为'%s'的领地", randomRegionID));
                    return true;
                }
                }
            else {
                ProtectedRegion protectedRegion = regions.getRegion(regionID);
                if (protectedRegion == null) {
                    user.sendMessage("您要绑定的领地不存在！请检查编号");
                    return false;
                } else {
                    protectedRegion.getOwners().addPlayer(user.getUniqueId());
                    user.sendMessage(String.format("恭喜您获得了编号为'%s'的领地", regionID));
                    return true;
                }
            }

            }
        else {
            user.sendMessage("该世界不支持领地！");
            return false;
        }
        return true;
    }
}