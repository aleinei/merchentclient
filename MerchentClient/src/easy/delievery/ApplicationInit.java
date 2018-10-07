/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jdk.nashorn.internal.scripts.JO;

/**
 *
 * @author Ahmed Mahmoud
 */
public class ApplicationInit extends javax.swing.JFrame {

    /**
     * Creates new form ApplicationInit
     */
    boolean isEditing;
    public ApplicationInit(boolean isEdit) {
        initComponents();
        setVisible(true);
        setLocationRelativeTo(null);
        if(isEdit) {
            appTitle.setText("Edit your database name");
            setTitle("Edit application");
            dbConfirm.setText("Edit");
        }
        isEditing = isEdit;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dbNameField = new javax.swing.JTextField();
        appTitle = new javax.swing.JLabel();
        dbConfirm = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Application Startup");
        setResizable(false);

        dbNameField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        appTitle.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        appTitle.setText("Please enter the database name to continue");

        dbConfirm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dbConfirm.setText("Start");
        dbConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbConfirmActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(appTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(198, 198, 198)
                                .addComponent(dbConfirm))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(139, 139, 139)
                                .addComponent(dbNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(appTitle)
                .addGap(35, 35, 35)
                .addComponent(dbNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(dbConfirm)
                .addContainerGap(107, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dbConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbConfirmActionPerformed
        // TODO add your handling code here:
        String dbName = dbNameField.getText();
        if(dbName.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane,  "You need to enter the database name to continue", "Empty Database name", JOptionPane.ERROR_MESSAGE);
        } else {
            File dbFile = new File(EasyDelievery.DB_FILE_NAME);
            if(!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(rootPane,  "Unable to create the configuration file, please try again", "File create failed", JOptionPane.WARNING_MESSAGE);
                    Logger.getLogger(ApplicationInit.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
            }
            try {
                FileWriter fr = new FileWriter(EasyDelievery.DB_FILE_NAME, false);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(dbName);
                br.write("\n");
                if(!EasyDelievery.CURRENT_METHOD.isEmpty()) {
                    br.write(EasyDelievery.CURRENT_METHOD);
                    br.write("\n");
                }
                if(!EasyDelievery.CURRENT_TYPES.isEmpty()) {
                    br.write(EasyDelievery.CURRENT_TYPES);
                }
                br.close();
                EasyDelievery.CURRENT_DB = dbName;
            } catch (IOException ex) {
                Logger.getLogger(ApplicationInit.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane,  "Unable to create the configuration file, please try again", "File create failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(!isEditing) {
             PrintRequestWindow window = new PrintRequestWindow(false);
            } else {
                EasyDelievery.window.Reload();
            }
            this.dispose();
        }
    }//GEN-LAST:event_dbConfirmActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appTitle;
    private javax.swing.JButton dbConfirm;
    private javax.swing.JTextField dbNameField;
    // End of variables declaration//GEN-END:variables
}
