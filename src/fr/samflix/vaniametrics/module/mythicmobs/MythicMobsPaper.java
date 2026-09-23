package fr.samflix.vaniametrics.module.mythicmobs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.samflix.vaniametrics.api.VaniaMetrics;
import fr.samflix.vaniametrics.api.VaniaMetricsProvider;

/**
 * Métriques des mobs personnalisés MythicMobs.
 *
 * <p>Apparitions, morts et disparitions : la différence entre les deux dernières dit si le contenu est joué ou perdu.
 */
public final class MythicMobsPaper extends JavaPlugin {

	private MythicMobsCollector collecteur;

	@Override
	public void onEnable() {
		VaniaMetrics metriques = VaniaMetricsProvider.get();
		collecteur = new MythicMobsCollector();
		metriques.enregistrer(collecteur);
		Bukkit.getPluginManager().registerEvents(collecteur, this);
	}

	@Override
	public void onDisable() {
		if (collecteur != null) {
			VaniaMetricsProvider.chercher().ifPresent(m -> m.retirer(collecteur));
		}
	}
}
