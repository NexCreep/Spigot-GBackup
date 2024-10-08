package net.silvaniax.plugin.spigotgbackup;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;
import net.silvaniax.plugin.spigotgbackup.tasks.BackupTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class GBackup extends JavaPlugin implements GBackupLogger {

    private final static String START_UP = "startup";
    private final static String INTERVAL = "timer";

    @Override
    public void onEnable() {
        LOGGER.info("GBackup enabled and operative");
        createConfig();

        FileConfiguration config = this.getConfig();

        BackupTask.registerTask(this, config.getInt(START_UP), config.getInt(INTERVAL));
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
