package net.silvaniax.plugin.spigotgbackup.utils;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class WorldCompression implements GBackupLogger {

    private static final String BACKUPS_DIR = "./backups";
    private static final List<String> EXCLUDED_ELEMENTS = new ArrayList<>(
            List.of("session.lock")
    );

    public static File compress(List<String> worlds, String backupFile) {

        File backupDir = new File(BACKUPS_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdir();
        }

        File zipFile = null;
        try {

                try (FileOutputStream fos = new FileOutputStream( backupDir+ "/" + backupFile + ".zip")) {
                        ZipOutputStream zos = new ZipOutputStream(fos);

                        for (String world : worlds) {
                            File worldDir = new File("./" + world);
                            List<String> worldFiles = new ArrayList<>(populateFilesList(worldDir));

                            for (int i = 0; i < worldFiles.size(); i++) {
                                String file = worldFiles.get(i);
                                printProgress(world, i, worldFiles.size());
                                ZipEntry entry = new ZipEntry(
                                        world + "/" + file.substring(worldDir.getAbsolutePath().length()+1)
                                );
                                zos.putNextEntry(entry);
                                try (FileInputStream fis = new FileInputStream(file)) {
                                    byte[] buffer = new byte[1024];
                                    int len;
                                    while ((len = fis.read(buffer)) > 0) {
                                        zos.write(buffer, 0, len);
                                    }
                                    zos.closeEntry();
                                }
                            }

                            LOGGER.info("\"" + world +"\" compressed successfully.");
                            BACKUP_PROGRESS.clear();
                        }

                    zos.close();
                }

            zipFile = new File( backupDir + "/" + backupFile);

        } catch (IOException e) {
            LOGGER.warning("Unable to compress worlds");
            e.printStackTrace();
        }

        return zipFile;
    }

    private static List<String> populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        List<String> filesListInDir = new ArrayList<>();

        if (files != null) {
            for(File file : files){
                if (!EXCLUDED_ELEMENTS.contains(file.getName())) {
                    if(file.isFile()) filesListInDir.add(file.getAbsolutePath());
                    else filesListInDir.addAll(populateFilesList(file));
                }
            }
        }

        return filesListInDir;
    }

    private static final Set<String> BACKUP_PROGRESS = new HashSet<>();

    private static void printProgress(String world, int position, int size) {
        String progress = Integer.toString((position * 100) / size);
        if (progress.charAt(progress.length()-1) == '0' && !BACKUP_PROGRESS.contains(progress)) {
            BACKUP_PROGRESS.add(progress);
            LOGGER.info("Backup Progress of world \""+ world +"\" at " + progress + "%");
        }
    }

}
