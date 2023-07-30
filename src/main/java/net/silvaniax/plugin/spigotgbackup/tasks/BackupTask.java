package net.silvaniax.plugin.spigotgbackup.tasks;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;
import net.silvaniax.plugin.spigotgbackup.utils.WorldCompression;
import net.silvaniax.plugin.spigotgbackup.tasks.dto.BackupDTO;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class BackupTask extends BukkitRunnable implements GBackupLogger {

    private static BackupTask task = null;
    private List<String> backupWorlds = null;

    private final String taskName;
    private final Set<BackupDTO> backupRegistry;

    Logger LOGGER = Bukkit.getLogger();

    private BackupTask() {
        taskName = "backup-task-" + UUID.randomUUID();
        backupRegistry = new TreeSet<>(((o1, o2) -> -(o1.getExecutionTime().compareTo(o2.getExecutionTime()))));
        populateExecutionRegistry();
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

        File backupFile = WorldCompression.compress(this.backupWorlds, execution.getBackupFile());
        //DriveManager.uploadBackup(backupFile);

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

    public void populateExecutionRegistry() {
        if (new File("./backups").exists()) {
            try (Stream<Path> paths = Files.walk(Paths.get("./backups"))) {
                paths.filter(Files::isRegularFile)
                        .filter((o) -> o.getFileName().toString().startsWith("backup-exec"))
                        .forEach((o) -> {
                            String name = o.getFileName().toString();
                            String[] sections = name.split("\\.");

                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                            Date executionTime;
                            try {
                                executionTime = formatter.parse(sections[1]);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            BackupDTO exec = new BackupDTO();
                            exec.setName(sections[0]);
                            exec.setExecutionTime(executionTime);
                            exec.setBackupFile(sections[0] + "." + sections[1]);

                            backupRegistry.add(exec);
                        });
            } catch (IOException e) {
               e.printStackTrace();
            }
        }
    }
}
