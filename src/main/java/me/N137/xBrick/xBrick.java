package me.N137.xBrick;

import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class xBrick extends JavaPlugin implements Listener {
    public UUID developerUUID = UUID.fromString("c8984808-54ca-4a6a-a3cd-b518af0df4f7");
    public String developerName = null;


    public String resolveUsername(String UUIDString) {
        String url = "https://api.mojang.com/user/profiles/"+UUIDString.replace("-", "")+"/names";
        try {
            @SuppressWarnings("deprecation")
            String nameJson = IOUtils.toString(new URL(url));
            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
            String playerSlot = nameValue.get(nameValue.size()-1).toString();
            JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
            return nameObject.get("name").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return "N137";
    }

    @Override
    public void onEnable() {
        this.developerName = resolveUsername(developerUUID.toString());
        this.getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("xbrick")).setExecutor(this);
        this.getServer().getConsoleSender().sendMessage("xBrick developer by " + developerName + " has been enabled.");
    }

    @EventHandler
    public void onHitWithBrick(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player perpetrator = (Player) event.getDamager();
            if (perpetrator.getInventory().getItemInMainHand().getType().equals(Material.CLAY_BRICK)) {
                if (victim.hasPermission("xbrick.immune")) { return; }
                if (victim.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                    event.setCancelled(true);
                    return;
                }
                PotionEffectType type;
                PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 200, 1);
                PotionEffect weaknesses = new PotionEffect(PotionEffectType.WEAKNESS, 200, 1);
                PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 200 , 128);
                PotionEffect jumpboost = new PotionEffect(PotionEffectType.JUMP, 200,128);

                victim.addPotionEffect(blindness);
                victim.addPotionEffect(weaknesses);
                victim.addPotionEffect(slowness);
                victim.addPotionEffect(jumpboost);

                perpetrator.getInventory().getItemInMainHand().setAmount(perpetrator.getInventory().getItemInMainHand().getAmount() - 1);

                victim.sendMessage("");
                victim.sendMessage("                          §2§nYou have been knocked out!");
                victim.sendMessage("");

                perpetrator.sendMessage("");
                perpetrator.sendMessage("                     §2§nYou have knocked out " + victim.getName());
                perpetrator.sendMessage("");

            }
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("xbrick")) {
            this.sendHelp(sender);
        }
        return true;
    }


    public void sendHelp(CommandSender player) {
        player.sendMessage("");
        player.sendMessage("§2§n§lCommand Reference:");
        player.sendMessage("");
        player.sendMessage("§7/xbrick §8»§7 Show Command Reference");
        player.sendMessage("");
        player.sendMessage("§6xBrick§7 developed by §6" + this.developerName);
    }
}
