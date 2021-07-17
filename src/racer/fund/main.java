package racer.fund;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;


public class main extends JavaPlugin{
	
	public static boolean showInfo=false;
	
	public int banValue;
	public String banWord="";
	int dangerCount;
	
	ArrayList<String> players=new ArrayList<String>();
	
	ActionListener refresher=new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int riskLevel=dangerCount/(players.size()+1);
			if(showInfo) {
				Bukkit.getConsoleSender().sendMessage("Risk Level: "+riskLevel);
				Bukkit.getConsoleSender().sendMessage(players+"");
			}
			if(riskLevel>banValue) {
				String susPlayer;
				int susValue = 0;
				for(int i=1;i<players.size();i=i+2) {
					if(Integer.parseInt(players.get(i))>susValue) {
						susValue=Integer.parseInt(players.get(i));
					}
				}
				susPlayer=players.get(players.indexOf(susValue+"")-1);
				
				banPlayer(susPlayer);
			}
			
			dangerCount=0;
			players.clear();
		}
	};
	
	private void banPlayer(String player) {
		Bukkit.getScheduler().runTask(this, new Runnable() {
	          public void run() {
	        	  Bukkit.getPlayer(player).kickPlayer(banWord);
	         }
		});
//		Bukkit.getBanList(Type.NAME).addBan(player, banWord, null, "raceR");
	}
	
	@Override
	public void onEnable() {
			this.getConfig().addDefault("BanPlayerWhenReaches", 2000);
			this.getConfig().addDefault("BanWord", "Floods Packets Detected, Goodbye! :]");
			this.getConfig().options().copyDefaults(true);
			saveConfig();
			
			banValue=this.getConfig().getInt("BanPlayerWhenReaches");
			banWord=this.getConfig().getString("BanWord");
			
			new Timer(1000, refresher).start();
			Bukkit.getConsoleSender().sendMessage("[FloodPacketDog Actived]");
			this.getCommand("fpdinfo").setExecutor(new showInfo());
			
			ProtocolManager manager=ProtocolLibrary.getProtocolManager();
			manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.getInstance()) {
					@Override
					public void onPacketReceiving(PacketEvent event) {
							String name=event.getPlayer().getName();
						
							dangerCount++;
							
							if(players.indexOf(name)==-1) {
								players.add(name);
								players.add(1+"");
							}
							else {
								players.set(players.indexOf(name)+1, Integer.parseInt(players.get(players.indexOf(name)+1))+1+"");
							}
					}
			});
	}
}

class showInfo implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
			
			if(arg3.length==0) {
				Bukkit.getConsoleSender().sendMessage("[FPD]: Wrong Command!");
				return false;
			}
			else if(arg3[0].equals("on")) {
				main.showInfo=true;
				return true;
			}
			else if(arg3[0].equals("off")) {
				main.showInfo=false;
				return true;
			}
			else {
				Bukkit.getConsoleSender().sendMessage("[FPD]: Wrong Command!");
				return false;
			}
	}
	
}

