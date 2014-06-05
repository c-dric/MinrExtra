package org.minr.MinrExtra;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;

public class Extra extends JavaPlugin {

	private ItemStack setSkin(ItemStack item, String nick) {
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(nick);
		item.setItemMeta(meta);
		return item;
	}

	public String path = "plugins" + File.separator + "MinrExtra" + File.separator;
	public Server server;

	public void onEnable() {
		this.server = this.getServer();
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println("[" + pdfFile.getName() + "] (version " + pdfFile.getVersion() + ") is enabled!");
	}
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println("[" + pdfFile.getName() + "] (version " + pdfFile.getVersion() + ") disabled. :(");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			Player player = (Player)sender;
			if(args.length < 1)
				return help(player);
			else if(args[0].equalsIgnoreCase("mine") && (sender.isOp()) && (args.length > 1))
				return mine(player, args[1]);
			else if(args[0].equalsIgnoreCase("restore") && (sender.isOp()) && (args.length > 1))
				return restore(player, args[1]);
			else if(args[0].equalsIgnoreCase("rocket") && (sender.isOp()) && (args.length > 3))
				return rocket(player, args[1], args[2], args[3]);
			else if(args[0].equalsIgnoreCase("skull") && (sender.isOp()) && (args.length > 2))
				return skull(player, args[1], args[2]);
			else if(args[0].equalsIgnoreCase("tp") && (sender.isOp()) && (args.length > 5))
				return tp(player, args[1], args[2], args[3], args[4], args[5]);			
			else
				return help(player);
		} catch(Exception e) {
			if(e instanceof NumberFormatException) {
				sender.sendMessage(ChatColor.RED + "Coordinates are in wrong format!");
			}
			e.printStackTrace();
			return false;
		}
	}

	public boolean mine(Player player, String p) {

		if ( Integer.parseInt(p) < 13 ) {
			String name = player.getName();
			Location bloc = player.getLocation();
			World world = bloc.getWorld();
			String w = world.getName();
			int x = bloc.getBlockX();
			int y = bloc.getBlockY();
			int z = bloc.getBlockZ();
			String where = w + "," + String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
			float power = new Float(p);
			boolean setFire = false;
			boolean breakBlocks = false;
			world.createExplosion(x, y, z, power, setFire, breakBlocks);
			System.out.println("[MinrExtra] " + name + " exploded at " + where);
		}
		return true;
	}

	public boolean rocket(Player player, String v1, String v2, String v3) {
		String name = player.getName();
		Location bloc = player.getLocation();
		World world = bloc.getWorld();
		String w = world.getName();
		int x = bloc.getBlockX();
		int y = bloc.getBlockY();
		int z = bloc.getBlockZ();
		String where = w + "," + String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
		int one = Integer.parseInt(v1);
		int two = Integer.parseInt(v2);
		int three = Integer.parseInt(v3);
		player.setVelocity(new Vector(one, two, three));
		System.out.println("[MinrExtra] " + name + " rocketed at " + where);
		return true;
	}

	public boolean skull(Player player, String nick, String amount) {
		int a = Integer.parseInt(amount);
		player.getInventory().addItem(setSkin(new ItemStack(Material.SKULL_ITEM, a, (byte) 3), nick));
		return true;
	}

	public boolean tp(Player player, String sx, String sy, String sz, String yaw, String pitch) {

		double x = Double.parseDouble(sx);
		double y = Double.parseDouble(sy);
		double z = Double.parseDouble(sz);

		Location playerLocation = player.getPlayer().getLocation();
		Location location = new Location(player.getPlayer().getWorld(), x, y, z, playerLocation.getYaw(), playerLocation.getPitch());
		location.setYaw(Float.parseFloat(yaw));
		location.setPitch(Float.parseFloat(pitch));

		if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000)
		{
			return false;
		}

		player.teleport(location);

		return true;
	}

	public boolean help(Player player) {
		if (player.isOp()) {
			player.sendMessage(ChatColor.GOLD + "Coming Soon");
		} else {
			player.sendMessage(ChatColor.GOLD + "Coming Soon");
		}
		return true;
	}

	public boolean restore(Player player, String area) {

		// Ensure WorldEdit is available

		WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");

		if (wep == null) {

			// No WE. No TerrainManager!

			player.sendMessage(ChatColor.RED + "It looks like someone world-edited WE.");

			return true;

		}

		// Get player and world data
		
		World world = player.getPlayer().getWorld();
		String name = player.getName();
		Location bloc = player.getLocation();
		String w = world.getName();
		int x = bloc.getBlockX();
		int y = bloc.getBlockY();
		int z = bloc.getBlockZ();
		String where = w + "," + String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
		
		// Where is the schematic ?
		
		String dir = wep.getDataFolder()+File.separator+"schematics"+File.separator;
		area = dir + area + ".schematic";
		File schem = new File(area);

		TerrainManager tm = new TerrainManager(wep, world);

		try {

			// Pasting the schematic 

			tm.loadSchematic(schem);

			} catch (FilenameException e) {

				// thrown by WorldEdit - it doesn't like the file name/location etc.
				player.sendMessage(ChatColor.YELLOW + "Error thrown by WorldEdit - it doesn't like the file name/location etc.");

			} catch (DataException e) {

				// thrown by WorldEdit - problem with the data
				player.sendMessage(ChatColor.YELLOW + "Error thrown by WorldEdit - problem with the data");

			} catch (IOException e) {

				// problem with opening/reading the file
				player.sendMessage(ChatColor.YELLOW + "Problem with opening/reading the file");

			} catch (MaxChangedBlocksException e) {

				// thrown by WorldEdit - the schematic is larger than the configured block limit for the player
				player.sendMessage(ChatColor.YELLOW + "Error thrown by WorldEdit - the schematic is larger than the configured block limit for the player");

			} catch (EmptyClipboardException e) {

				// thrown by WorldEdit - should be self-explanatory
				player.sendMessage(ChatColor.YELLOW + "EmptyClipboardException");

			}

		System.out.println("[MinrExtra] " + name + " restored " + area + " at " + where);

		return true;

	}

}
