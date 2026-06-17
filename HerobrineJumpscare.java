package com.liekg1.herobrine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.ProfileProperty;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public final class HerobrineJumpscare extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Herobrine Jumpscare Plugin successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Herobrine Jumpscare Plugin disabled.");
    }

    @EventHandler
    public void onPlayerTrigger(PlayerInteractEvent event) {
        // You can change this condition to trigger whenever you want!
        if (event.getAction().name().contains("RIGHT_CLICK")) {
            Player player = event.getPlayer();
            
            // Calculate a location 3 blocks directly in front of the player's eyes
            Location spawnLoc = player.getLocation().add(player.getLocation().getDirection().multiply(3));
            spawnLoc.setY(player.getLocation().getY()); // Keep it on ground level

            // Spawn the Herobrine ArmorStand entity
            ArmorStand herobrine = (ArmorStand) player.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
            herobrine.setVisible(false);
            herobrine.setBasePlate(false);
            herobrine.setArms(true);
            herobrine.setCustomName("Herobrine");
            herobrine.setCustomNameVisible(false);

            // Create Herobrine custom head item
            ItemStack herobrineHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) herobrineHead.getItemMeta();
            
            if (headMeta != null) {
                PlayerProfile skinProfile = Bukkit.createPlayerProfile(UUID.randomUUID(), "Herobrine");
                // Base64 value for standard Herobrine skin textures
                String base64Texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU4YmU5NTg3ZThjMWY3OTNhN2I4MWI2YjVlYTYzNzg2NDg3OWZjN2IyNWYwNThmMDkyNTU0YTYzNjU0OGU1In19fQ==";
                skinProfile.setProperty(new ProfileProperty("textures", base64Texture));
                headMeta.setOwnerProfile(skinProfile);
                herobrineHead.setItemMeta(headMeta);
            }
            
            // Equip the item on the armor stand
            if (herobrine.getEquipment() != null) {
                herobrine.getEquipment().setHelmet(herobrineHead);
            }

            // Audio/Visual Scare Effects
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0f, 0.5f);
            
            // Give temporary darkness blindness
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1, false, false));
            
            // Trigger Elder Guardian jumpscare flash image
            player.spawnParticle(Particle.ELDER_GUARDIAN, player.getLocation().add(player.getLocation().getDirection().multiply(1.5)), 1);

            // Remove the Herobrine entity automatically after 1.5 seconds (30 ticks)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (herobrine.isValid()) {
                        herobrine.remove();
                    }
                }
            }.runTaskLater(this, 30L);
        }
    }
}
