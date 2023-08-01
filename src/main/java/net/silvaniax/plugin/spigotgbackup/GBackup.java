package net.silvaniax.plugin.spigotgbackup;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;
import net.silvaniax.plugin.spigotgbackup.services.AuditLog;
import net.silvaniax.plugin.spigotgbackup.tasks.BackupTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GBackup extends JavaPlugin implements GBackupLogger {

    private final static String START_UP = "startup";
    private final static String INTERVAL = "timer";

    @Override
    public void onEnable() {
        LOGGER.info("GBackup enabled and operative");
        createConfig();

        FileConfiguration config = this.getConfig();

        BackupTask.registerTask(this, config.getInt(START_UP), config.getInt(INTERVAL));
        AuditLog.start(BackupTask.getTask(), config.getInt("port"), config.getString("secret_phrase"));
    }

    @Override
    public void onDisable() {
        BackupTask.unregisterTask();
        AuditLog.getAuditLogServer().stop();
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
