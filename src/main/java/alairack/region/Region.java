package alairack.region;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class Region extends JavaPlugin {
    private static alairack.region.Region instance;

    private MultiverseCore multiverseCore = this.initMultiverseCore();
    FileConfiguration config = getConfig();

    @Override
    public void onEnable ( ) {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getCommand("ar").setExecutor(new Command());
        this.getCommand("addOwner").setExecutor(new AddOwner());

        this.initWorld(this.getServer().getWorlds().get(0));
        this.initMultiverseWorld();

        getLogger ( ).info( "RedTide的领地插件已开启" ) ;
        config.addDefault("nbt-name", "");
        config.addDefault("nbt-description", "");
        config.options().copyDefaults(true);
        saveConfig();

        instance = this;
    }
    @Override
    public void onDisable ( ) {
        getLogger ( ).info( "RedTide的领地插件已关闭" ) ;
        instance = null;
    }

    public static alairack.region.Region getInstance(){
        return instance;
    }

    @Nullable
    private MultiverseCore initMultiverseCore(){
        return (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    }

    public void initMultiverseWorld(){
        if (this.multiverseCore != null){
            for (MultiverseWorld world : this.multiverseCore.getMVWorldManager().getMVWorlds()){
                this.initWorld(world.getCBWorld());
            }
        }
    }
    public void initWorld(org.bukkit.World bukkitWorld){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(bukkitWorld);
        getLogger().info(String.format("init world %s", world.getName()));
        RegionManager regions = container.get(world);
        GlobalProtectedRegion region;
        if (!regions.hasRegion("__global__")) {
            region = new GlobalProtectedRegion("__global__");
        }
        else {
            region = (GlobalProtectedRegion) regions.getRegion("__global__");
            if (region.getOwners() != null){
                region.getOwners().clear();
                region.getMembers().clear();
            }
        }
        region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
        region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
        region.setFlag(Flags.BLOCK_BREAK.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
        region.setFlag(Flags.BLOCK_PLACE.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);

        region.setFlag(Flags.DENY_MESSAGE, "");
        region.setFlag(Flags.DENY_MESSAGE.getRegionGroupFlag(), RegionGroup.ALL);
        regions.addRegion(region);
    }
}