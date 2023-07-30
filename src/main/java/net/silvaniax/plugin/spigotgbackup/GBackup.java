package net.silvaniax.plugin.spigotgbackup;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;
import net.silvaniax.plugin.spigotgbackup.tasks.BackupTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class GBackup extends JavaPlugin implements GBackupLogger {

    @Override
    public void onEnable() {
        LOGGER.info("GBackup enabled and operative");
        createConfig();
        BackupTask.registerTask(this,10, 2);
    }

    @Override
    public void onDisable() {
        BackupTask.unregisterTask();
        LOGGER.info("GBackup disabled");
    }


    private void createConfig(){
        File configFile = new File("./plugins/GBackup/config.yml");
        if (!configFile.exists()) {
            LOGGER.info("No config file! Creating new one.");
            this.saveDefaultConfig();
        }
    }

}
