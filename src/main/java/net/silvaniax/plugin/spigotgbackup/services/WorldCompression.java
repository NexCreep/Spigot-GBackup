package net.silvaniax.plugin.spigotgbackup.services;

import net.silvaniax.plugin.spigotgbackup.abstracts.GBackupLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class WorldCompression implements GBackupLogger {

    public static File compress(String world, String backupFile) {
        File zipFile = null;
        try {
            File worldDir = new File("./" + world);
            List<String> worldFiles = new ArrayList<>(populateFilesList(worldDir));

            try (FileOutputStream fos = new FileOutputStream("./backup/" + backupFile + ".zip")) {
                ZipOutputStream zos = new ZipOutputStream(fos);
                for (String file : worldFiles) {
                    ZipEntry entry = new ZipEntry(file.substring(worldDir.getAbsolutePath().length()+1));
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
                zos.close();
            }

            zipFile = new File("./backup/" + backupFile);

        } catch (IOException e) {
            LOGGER.warning("Unable to compress world \"" + world + "\"");
            e.printStackTrace();
        }

        return zipFile;
    }

    private static List<String> populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        List<String> filesListInDir = new ArrayList<>();

        if (files != null) {
            for(File file : files){
                if(file.isFile()) filesListInDir.add(file.getAbsolutePath());
                else filesListInDir.addAll(populateFilesList(file));
            }
        }

        return filesListInDir;
    }

}
