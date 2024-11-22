import java.io.*;
import java.nio.file.*;
import java.util.*;

public class OPLManager {
    private String usbDrivePath;
    private String oplPath;
    private String isoFolderPath;

    public OPLManager(String usbDrivePath) {
        this.usbDrivePath = usbDrivePath;
        this.oplPath = usbDrivePath + "/OPL/games";
        this.isoFolderPath = usbDrivePath + "/iso";
    }

    public void createOplStructure() {
        File oplDir = new File(oplPath);
        if (!oplDir.exists()) {
            oplDir.mkdirs();
        }
    }

    public String copyGameToUsb(String isoPath) {
        File isoFile = new File(isoPath);
        if (isoFile.exists()) {
            String gameName = isoFile.getName();
            String destinationPath = oplPath + "/" + gameName;
            try {
                Files.copy(isoFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Le jeu " + gameName + " a été copié vers " + destinationPath);
                return gameName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Le fichier " + isoPath + " n'existe pas.");
        }
        return null;
    }

    public void updateGamesCfg(String gameName) {
        File gamesCfgFile = new File(usbDrivePath + "/OPL/games.cfg");
        try {
            if (!gamesCfgFile.exists()) {
                gamesCfgFile.createNewFile();
            }
            try (FileWriter writer = new FileWriter(gamesCfgFile, true);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write("game_title = iso:/games/" + gameName + "\n");
                System.out.println("Le fichier games.cfg a été mis à jour.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchAndCopyIso() {
        createOplStructure();
        File isoFolder = new File(isoFolderPath);
        if (isoFolder.exists() && isoFolder.isDirectory()) {
            File[] isoFiles = isoFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".iso"));
            if (isoFiles != null) {
                for (File isoFile : isoFiles) {
                    String isoPath = isoFile.getAbsolutePath();
                    String gameName = copyGameToUsb(isoPath);
                    if (gameName != null) {
                        updateGamesCfg(gameName);
                    }
                }
            } else {
                System.out.println("Aucun fichier ISO trouvé dans " + isoFolderPath);
            }
        } else {
            System.out.println("Le dossier " + isoFolderPath + " n'existe pas.");
        }
    }

    public static void main(String[] args) {
        String usbDrive = "D:"; 
        OPLManager oplManager = new OPLManager(usbDrive);
        oplManager.searchAndCopyIso();
    }
}
