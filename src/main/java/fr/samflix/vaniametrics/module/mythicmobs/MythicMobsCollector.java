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
 * MythicMobs — ce qui apparaît, ce qui meurt, ce qui disparaît.
 *
 * <p>LA DIFFÉRENCE ENTRE MOURIR ET DISPARAÎTRE EST TOUTE L'INFORMATION. Un mob TUÉ est du jeu qui
 * se joue ; un mob DISPARU est du travail perdu — déchargement de chunk, limite d'entités, nettoyage
 * automatique. Un serveur où l'on fait apparaître cent mobs dont quatre-vingt-dix disparaissent
 * sans être tués a un problème de conception, et aucune autre métrique ne le dirait.
 *
 * <p>L'étiquette est le TYPE de mob, pas son instance : MythicMobs en déclare une liste fermée,
 * écrite dans les fichiers du serveur. C'est exactement le genre d'ensemble borné qui fait une
 * bonne étiquette.
 */
public final class MythicMobsCollector implements Collector, Listener {

	private Counter apparitions;
	private Counter morts;
	private Counter disparitions;

	@Override
	public String nom() {
		return "mob";
	}

	@Override
	public String origine() {
		return "MythicMobs";
	}

	@Override
	public void declarer(MetricRegistry r) {
		apparitions = r.counter("mob_spawns_total", "Mobs personnalisés apparus.", "mob_type");
		morts = r.counter("mob_deaths_total",
				"Mobs personnalisés tués. « killer » distingue le joueur du reste — un mob tué "
						+ "par l'environnement n'est pas du jeu qui se joue.",
				"mob_type", "killer");
		disparitions = r.counter("mob_despawns_total",
				"Mobs personnalisés disparus SANS être tués : chunk déchargé, limite d'entités, "
						+ "nettoyage. Un rapport élevé avec les apparitions signale du travail "
						+ "perdu.",
				"mob_type");
	}

	@Override
	public void relever(MetricRegistry r) {
		// Tout est compté dans les écouteurs.
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpawn(MythicMobSpawnEvent e) {
		apparitions.inc(type(e.getMobType()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(MythicMobDeathEvent e) {
		morts.inc(type(e.getMobType()),
				e.getKiller() instanceof org.bukkit.entity.Player ? "player" : "other");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDespawn(MythicMobDespawnEvent e) {
		disparitions.inc(type(e.getMobType()));
	}

	private static String type(MythicMob mob) {
		if (mob == null) {
			return "unknown";
		}
		String n = mob.getInternalName();
		return n == null || n.isBlank() ? "unknown" : n.toLowerCase(Locale.ROOT);
	}
}
