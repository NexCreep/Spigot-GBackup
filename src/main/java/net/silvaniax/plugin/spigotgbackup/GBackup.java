package net.silvaniax.plugin.spigotgbackup;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;
import net.silvaniax.plugin.spigotgbackup.tasks.BackupTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class GBackup extends JavaPlugin implements GBackupLogger {

    @Override
    public void onEnable() {
        LOGGER.info("GBackup enabled and operative");
        BackupTask.registerTask(this,10, 2);
    }

    @Override
    public void onDisable() {
        BackupTask.unregisterTask();
        LOGGER.info("GBackup disabled");
    }
}
