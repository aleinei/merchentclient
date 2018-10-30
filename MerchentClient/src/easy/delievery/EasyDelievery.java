/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;

import com.lowagie.text.pdf.codec.Base64;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class EasyDelievery {
    public static String ENC_KEY = "esiSOFT1";
    public static Key AES_KEY = new SecretKeySpec(ENC_KEY.getBytes(),"DES");
    public static String DB_FILE_NAME = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "/ESI/" + "db.confg";
    public static String PW_FILE_NAME = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "/ESI/" + "pw.confg";
    public static String ESI_FILES_DIRECTORY = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "/ESI/";
    public static String CURRENT_DB = "";
    public static String CURRENT_PW = "";
    public static String CURRENT_METHOD = "";
    public static MainWindow window;
    public static String PRINT_WORK_ORDER = "work_order";
    public static String PRINT_RECEIPT = "receipt";
    public static String PRINT_BOTH = "both";
    public static String NONE = "None";
    public static String CURRENT_TYPES = "";
    public static void main(String[] args) {
        File directory = new File(ESI_FILES_DIRECTORY);
        if(!directory.exists()) {
            directory.mkdirs();
        }
        File dbFile = new File(DB_FILE_NAME);
        File pwFile = new File(PW_FILE_NAME);
        if(dbFile.exists() && pwFile.exists()) {
            try {
                FileReader dbFileReader = new FileReader(DB_FILE_NAME);
                BufferedReader bReader = new BufferedReader(dbFileReader);
                CURRENT_DB = bReader.readLine();
                CURRENT_METHOD = bReader.readLine();
                CURRENT_TYPES = bReader.readLine();
                System.out.println(CURRENT_DB);
                System.out.println(CURRENT_METHOD);
                System.out.println(CURRENT_TYPES);
                
                FileReader pwFileReader = new FileReader(PW_FILE_NAME);
                BufferedReader pwReader = new BufferedReader(pwFileReader);
                String dbPassword = pwReader.readLine();
                
                //decrypting
                byte[] db1Password = Base64.decode(dbPassword);
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.DECRYPT_MODE, EasyDelievery.AES_KEY);     
                String decrypted = new String(cipher.doFinal(db1Password));
                CURRENT_PW = decrypted;
                System.out.println("Current_PW: "+CURRENT_PW);
            } catch (Exception ex) {
                Logger.getLogger(EasyDelievery.class.getName()).log(Level.SEVERE, null, ex);
                ApplicationInit app = new ApplicationInit(false);
                JOptionPane.showMessageDialog(app,  "Unable to read the configuration file, please try to configure your application again", "File read failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            window = new MainWindow();
            window.Start();
        } else {
            ApplicationInit app = new ApplicationInit(false);
        }
    }
    
}
