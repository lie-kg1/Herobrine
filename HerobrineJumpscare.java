package com.liekg1.herobrine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public final class HerobrineJumpscare extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register the event listener for the trigger
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Herobrine Jumpscare Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Herobrine Jumpscare Plugin Disabled!");
    }

    @EventHandler
    public void onPlayerTrigger(PlayerInteractEvent event) {
        // Simple mechanic: Right-click air or a block to test summon the jumpscare NPC
        if (event.getAction().name().contains("RIGHT_CLICK") && event.getPlayer().isOp()) {
            Player player = event.getPlayer();
            Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(3)); // 3 blocks ahead
            
            // 1. Summon Invisible ArmorStand holding Herobrine Head attributes
            ArmorStand herobrine = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            herobrine.setVisible(false);
            herobrine.setGravity(false);
            herobrine.setCustomName("Herobrine");
            herobrine.setCustomNameVisible(false);

            // 2. Play the jumpscare effects to nearby players
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0f, 0.5f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1, false, false));
            player.spawnParticle(Particle.ELDER_GUARDIAN, player.getLocation().add(0, 1, 0), 1);

            // 3. Make Herobrine vanish instantly after a split second (10 Minecraft ticks = 0.5s)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (herobrine.isValid()) {
                        herobrine.remove();
                    }
                }
            }.runTaskLater(this, 10L);
        }
    }
}
