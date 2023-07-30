package net.silvaniax.plugin.spigotgbackup.tasks;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;
import net.silvaniax.plugin.spigotgbackup.services.WorldCompression;
import net.silvaniax.plugin.spigotgbackup.tasks.dto.BackupDTO;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class BackupTask extends BukkitRunnable implements GBackupLogger {

    private static BackupTask task = null;
    private List<String> backupWorlds = null;

    private final String taskName;
    private final Set<BackupDTO> backupRegistry;

    Logger LOGGER = Bukkit.getLogger();

    private BackupTask() {
        taskName = "backup-task-" + UUID.randomUUID();
        backupRegistry = new TreeSet<>((Comparator.comparing(BackupDTO::getExecutionTime)));
        LOGGER.info("New backup task was registered with ID \"" + taskName + "\"");
    }

    @Override
    public void run() {
        Date now = new Date();
        BackupDTO execution = new BackupDTO();
        execution.setName("backup-exec-" + UUID.randomUUID());
        execution.setExecutionTime(now);
        String nowFormatted = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(now);
        execution.setBackupFile(execution.getName()+"."+nowFormatted);

        LOGGER.info("Started new backup execution with ID \"" + execution.getName() + "\"");

        WorldCompression.compress(this.backupWorlds, execution.getBackupFile());

        backupRegistry.add(execution);

        LOGGER.info("Task have run at " + now);
        LOGGER.info("Task info: " + execution);
    }

    public String getTaskName() {
        return taskName;
    }

    public Set<BackupDTO> getBackupRegistry() {
        return backupRegistry;
    }

    public static BackupTask getTask() {
        return task;
    }

    public static void registerTask(Plugin plugin, Integer delay, Integer interval) {
        if (task == null) {
            task = new BackupTask();
            task.backupWorlds = plugin.getConfig().getStringList("backup_worlds");
            task.runTaskTimerAsynchronously(plugin, 20L * delay, (20 * 3600) * interval);
        } else {
            GBackupLogger.LOGGER.info("There is already a operative task with ID \"" + task.getTaskId() + "\"");
        }
    }

    public static void unregisterTask() {
        if (task != null) {
            GBackupLogger.LOGGER.info("Task with ID \"" + task.getTaskName() + "\" was cancelled");
            task = null;
        }
    }
}
