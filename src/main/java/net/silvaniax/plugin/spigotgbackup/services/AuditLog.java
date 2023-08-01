package net.silvaniax.plugin.spigotgbackup.services;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.util.JavalinLogger;
import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;
import net.silvaniax.plugin.spigotgbackup.tasks.BackupTask;
import net.silvaniax.plugin.spigotgbackup.tasks.dto.BackupDTO;

public class AuditLog implements GBackupLogger {

    private static AuditLog auditLogServer = null;

    private static BackupTask task;

    private static String secretPhrase = null;

    private final Javalin app;

    private AuditLog(BackupTask task, Integer port, String secretPhrase) {

        AuditLog.secretPhrase = secretPhrase;

        AuditLog.task = task;
        JavalinLogger.enabled = false;
        this.app = Javalin.create().start(port);
        this.app.get("/backups/log/{secret}", AuditLog::renderLog);
        LOGGER.info(String.format("Web log listening on *:%s", port));
    }

    private static void renderLog(Context ctx) {

        if (!ctx.pathParam("secret").equals(secretPhrase)) {
            throw new ForbiddenResponse("Unauthorized");
        }

        StringBuilder result = new StringBuilder();

        result.append(String.format("Active backup task: %s\n", task.getTaskName()));
        result.append(String.format("Total available local backup: %d\n", task.getBackupRegistry().size()));
        result.append("------------------------------------------\n");
        for (BackupDTO exec : task.getBackupRegistry()) {
            result.append(String.format("%s | %s | %s\n", exec.getName(), exec.getExecutionTime(), exec.getBackupFile()));
        }
        result.append("------------------------------------------\n");

        ctx.result(result.toString());
    }

    public void stop(){
        this.app.stop();
        LOGGER.info("Web log was stopped");
    };

    public static AuditLog getAuditLogServer() {
        return auditLogServer;
    }

    public static void start(BackupTask task, Integer port, String secretPhrase) {
        if (auditLogServer == null) {
            auditLogServer = new AuditLog(task, port, secretPhrase);
        }
    }

}
