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

                Double knockoutChance = Math.random();
                if (knockoutChance > 0.6) {
                    Double severity = Math.random();

                    if (severity < 0.6)  {    // 0 -> 6
                        knockout(perpetrator, victim,
                                true, false, true, true, false,
                                1, 0, 3, 3, 0);
                    } else if (severity > 0.6 && severity < 0.8) {
                        knockout(perpetrator, victim,
                                true, true, true, true, false,
                                10, 10, 10, 10, 0);
                    } else {
                        knockout(perpetrator, victim,
                                true, true, true, true, true,
                                15, 15, 15, 15, 10);
                    }

                } else {
                    victim.sendMessage("");
                    victim.sendMessage("      §c"+perpetrator.getName()+" tried to knock you out with a brick");
                    victim.sendMessage("      §cbut you remained conscious!");
                    victim.sendMessage("");

                    perpetrator.sendMessage("");
                    perpetrator.sendMessage("      §cYou have tried to knocked out " + victim.getName() + " with a brick");
                    perpetrator.sendMessage("      §cbut they remained conscious");
                    perpetrator.sendMessage("");
                }

                perpetrator.getInventory().getItemInMainHand().setAmount(perpetrator.getInventory().getItemInMainHand().getAmount() - 1);


            }
        }

    }

    public void knockout(Player perpetrator, Player victim,
      boolean applyBlindness, boolean applyWeakness, boolean applySlowness, boolean applyJumpboost, boolean applyConfusion,
      int blindnessDuration, int weaknessDuration, int slownessDuration, int jumpboostDuration, int confusionDuration) {

        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, blindnessDuration*20, 1);
        PotionEffect weaknesses = new PotionEffect(PotionEffectType.WEAKNESS, weaknessDuration*20, 1);
        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, slownessDuration*20 , 128);
        PotionEffect jumpboost = new PotionEffect(PotionEffectType.JUMP, jumpboostDuration*20,128);
        PotionEffect confusion = new PotionEffect(PotionEffectType.CONFUSION, confusionDuration*20, 1);

       if (applyBlindness) victim.addPotionEffect(blindness);
       if (applyWeakness) victim.addPotionEffect(weaknesses);
       if (applySlowness) victim.addPotionEffect(slowness);
       if (applyJumpboost) victim.addPotionEffect(jumpboost);
       if (applyConfusion) victim.addPotionEffect(confusion);

        victim.sendMessage("");
        victim.sendMessage("      §2§nYou have been knocked out!");
        victim.sendMessage("");

        perpetrator.sendMessage("");
        perpetrator.sendMessage("      §2§nYou have knocked out " + victim.getName());
        perpetrator.sendMessage("");
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
