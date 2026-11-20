package nl.djoleden.mc.cs;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class csmc extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Started DJO CS:MC");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Stopped DJO CS:MC");
    }

    NamespacedKey FIRE_RATE_KEY = new NamespacedKey(this, "fire_delay_ms");

    @SuppressWarnings("all")
    private void gunfire(
            Player p,
            ItemStack gun,
            Particle particle,
            int range,
            int damage,
            int fireRate,
            Sound sound,
            float soundPitch,
            int fireTicks,
            float explosionSizeOnHit) {
        if (p.hasCooldown(gun)) return;

        p.playSound(p.getLocation(), sound, SoundCategory.PLAYERS, 1, soundPitch);
        for (int i=0; i<range; i++) {
            Vector v = p.getLocation().getDirection().clone();
            v.add(new Vector(v.getX() * i, v.getY() * i, v.getZ() * i));

            Location l = p.getLocation().add(0, 1.5, 0).add(v);
            if (l.getBlock().getType().toString().contains("GLASS")) {
                l.getBlock().setType(Material.AIR);
                l.getWorld().playSound(l, Sound.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 1.5F);
            }
            if (l.getBlock().getType().isSolid()) {
                if (explosionSizeOnHit > 0) l.getWorld().createExplosion(l, explosionSizeOnHit);
                break;
            }

            p.getWorld().spawnParticle(particle, l, 2, 0, 0, 0, 0, null, true);
            for (Entity e : l.getWorld().getNearbyEntities(l, 0.25, 0.25, 0.25)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(damage);
                    return;
                }
            }
        }

        p.setCooldown(gun, fireRate);
    }

    @EventHandler
    public void interaction(PlayerInteractEvent e) {
        boolean validAction = (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK));
        if (!e.hasItem() || !validAction || !e.getPlayer().getWorld().getName().equals("CS-MC")) return;

        if (e.getItem().getType() == Material.IRON_HOE) {
            e.setCancelled(true);
            gunfire(e.getPlayer(), e.getItem(), Particle.END_ROD, 100, 12, 12, Sound.ENTITY_PLAYER_HURT, 0.5F, 0, 0);
        } else if (e.getItem().getType() == Material.DIAMOND_HOE) {
            e.setCancelled(true);
            gunfire(e.getPlayer(), e.getItem(), Particle.END_ROD, 200, 14, 5, Sound.ENTITY_PLAYER_HURT, 0.5F, 0, 0);
        } else if (e.getItem().getType() == Material.GOLDEN_HOE) {
            e.setCancelled(true);
            gunfire(e.getPlayer(), e.getItem(), Particle.FLAME, 150, 2, 2, Sound.ENTITY_ARROW_HIT_PLAYER, 2F, 80, 0);
        } else if (e.getItem().getType() == Material.STONE_SHOVEL) {
            e.setCancelled(true);
            gunfire(e.getPlayer(), e.getItem(), Particle.LARGE_SMOKE, 80, 18, 40, Sound.ENTITY_ALLAY_HURT, 0.75F, 0, 0);
        }
    }
}
