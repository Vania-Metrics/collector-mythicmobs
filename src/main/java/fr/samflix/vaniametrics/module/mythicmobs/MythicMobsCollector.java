package fr.samflix.vaniametrics.module.mythicmobs;

import java.util.Locale;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;

import fr.samflix.vaniametrics.api.Collector;
import fr.samflix.vaniametrics.api.Counter;
import fr.samflix.vaniametrics.api.MetricRegistry;

/**
 * MythicMobs — what spawns, what dies, what despawns.
 *
 * <p>The difference between dying and despawning is all the information here. A KILLED mob is
 * content being played; a DESPAWNED mob is wasted work — chunk unload, entity limit, cleanup.
 * A server that spawns a hundred mobs and despawns ninety without a kill has a design problem
 * that no other metric would reveal.
 *
 * <p>The label is the mob TYPE, not its instance: MythicMobs declares a closed list of them in
 * the server files, which makes it exactly the kind of bounded set that makes a good label.
 */
public final class MythicMobsCollector implements Collector, Listener {

	private Counter spawns;
	private Counter deaths;
	private Counter despawns;

	@Override
	public String name() {
		return "mob";
	}

	@Override
	public String source() {
		return "MythicMobs";
	}

	@Override
	public void declare(MetricRegistry r) {
		spawns = r.counter("mob_spawns_total", "Custom mobs spawned.", "mob_type");
		deaths = r.counter("mob_deaths_total",
				"Custom mobs killed. \"killer\" distinguishes the player from everything else — a "
						+ "mob killed by the environment is not content being played.",
				"mob_type", "killer");
		despawns = r.counter("mob_despawns_total",
				"Custom mobs despawned WITHOUT being killed: chunk unload, entity limit, "
						+ "cleanup. A high ratio against spawns signals wasted work.",
				"mob_type");
	}

	@Override
	public void collect(MetricRegistry r) {
		// Everything is counted in the listeners.
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpawn(MythicMobSpawnEvent e) {
		spawns.inc(type(e.getMobType()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(MythicMobDeathEvent e) {
		deaths.inc(type(e.getMobType()),
				e.getKiller() instanceof org.bukkit.entity.Player ? "player" : "other");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDespawn(MythicMobDespawnEvent e) {
		despawns.inc(type(e.getMobType()));
	}

	private static String type(MythicMob mob) {
		if (mob == null) {
			return "unknown";
		}
		String n = mob.getInternalName();
		return n == null || n.isBlank() ? "unknown" : n.toLowerCase(Locale.ROOT);
	}
}
