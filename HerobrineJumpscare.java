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
        // Register events to listen for the player interaction trigger
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Herobrine Jumpscare Plugin successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Herobrine Jumpscare Plugin disabled.");
    }

    @EventHandler
    public void onPlayerTrigger(PlayerInteractEvent event) {
        // Trigger condition: Operator player right-clicks to test the jumpscare
        if (event.getAction().name().contains("RIGHT_CLICK") && event.getPlayer().isOp()) {
            Player player = event.getPlayer();
            
            // Calculate spawn location 3 blocks directly in front of where the player is looking
            Location spawnLoc = player.getLocation().add(player.getLocation().getDirection().multiply(3));
            // Adjust height so the NPC stands level with the player's view line
            spawnLoc.setY(player.getLocation().getY()); 

            // 1. Spawn the Herobrine NPC Base (Invisible ArmorStand)
            ArmorStand herobrine = (ArmorStand) player.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
            herobrine.setVisible(false);
            herobrine.setGravity(false);
            herobrine.setArms(true);
            herobrine.setBasePlate(false);
            herobrine.setCustomName("Herobrine");
            herobrine.setCustomNameVisible(false);

            // 2. Generate and Apply the Herobrine Skin via Base64 File Textures
            ItemStack herobrineHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) herobrineHead.getItemMeta();

            if (headMeta != null) {
                // Anonymous profile generation using a static random UUID signature
                PlayerProfile skinProfile = Bukkit.createPlayerProfile(UUID.randomUUID(), "Herobrine");
                
                // Base64 string pointing directly to Mojang's official Herobrine asset server link
                String base64Texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk5YWQ3YTA0MzE2OTI5OTRiNmM0MTJjN2VhZmI5ZTBmYzQ5OTc1MjQwYjczYTI3ZDI0ZWQ3OTcwMzVmYjg5NCJ9fX0=";
                
                skinProfile.setProperty(new ProfileProperty("textures", base64Texture));
                headMeta.setOwnerProfile(skinProfile);
                herobrineHead.setItemMeta(headMeta);
            }

            // Equip the custom skin skull onto the NPC armorstand structure
            if (herobrine.getEquipment() != null) {
                herobrine.getEquipment().setHelmet(herobrineHead);
            }

            // 3. Execute the Jumpscare Sensory Effects
            // Play low-pitched enderman scream directly at the player
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0f, 0.5f);
            
            // Give a sudden 2-second blindness/darkness visual overlay effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1, false, false));
            
            // Explode a giant elder guardian particle directly in front of their eyes
            player.spawnParticle(Particle.ELDER_GUARDIAN, player.getLocation().add(player.getLocation().getDirection().multiply(1.5)), 1);

            // 4. Clean up: Force Herobrine to vanish instantly after 10 ticks (0.5 seconds)
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
