package net.silvaniax.plugin.spigotgbackup.tasks.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

public class BackupDTO {

    private String name;
    private Date executionTime;
    private String backupFile;

    /* Getters */
    public String getName() {
        return name;
    }
    public Date getExecutionTime() {
        return executionTime;
    }
    public String getBackupFile() {
        return backupFile;
    }

    /* Setters */
    public void setName(String name) {
        this.name = name;
    }
    public void setExecutionTime(Date executionTime) {
        this.executionTime = executionTime;
    }
    public void setBackupFile(String backupFile) {
        this.backupFile = backupFile;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("executionTime", executionTime)
                .append("backupFile", backupFile)
                .toString();
    }
}
