package fr.samflix.vaniametrics.module.mythicmobs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.samflix.vaniametrics.api.VaniaMetrics;
import fr.samflix.vaniametrics.api.VaniaMetricsProvider;

/**
 * MythicMobs custom mob metrics.
 *
 * <p>Spawns, deaths and despawns: the difference between the last two tells whether content is
 * played or wasted.
 */
public final class MythicMobsPaper extends JavaPlugin {

	private MythicMobsCollector collector;

	@Override
	public void onEnable() {
		VaniaMetrics metrics = VaniaMetricsProvider.get();
		collector = new MythicMobsCollector();
		metrics.register(collector);
		Bukkit.getPluginManager().registerEvents(collector, this);
	}

	@Override
	public void onDisable() {
		if (collector != null) {
			VaniaMetricsProvider.find().ifPresent(m -> m.unregister(collector));
		}
	}
}
