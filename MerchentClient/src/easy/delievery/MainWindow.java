/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;

import com.sun.glass.events.KeyEvent;
import java.awt.Panel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Server
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    JFXPanel jfXPanel = new JFXPanel();
    ServerConnection connection;
    public MainWindow() {
        initComponents();
        setVisible(true);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {
                //window opened
            }

            @Override
            public void windowClosing(WindowEvent we) {
                //window closing
                connection.close();
            }

            @Override
            public void windowClosed(WindowEvent we) {
                //windowclosed
            }

            @Override
            public void windowIconified(WindowEvent we) {
                //windowIconified
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
                //
            }

            @Override
            public void windowActivated(WindowEvent we) {
                //
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
                //
            }
        });
    }
    
    public void Start() {
        connection = new ServerConnection(this);
        Runnable r = () -> {
            connection.Connect();
        };
        Thread t = new Thread(r);
        t.start();
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                LocalServer server = new LocalServer(MainWindow.this);
            }
            
        };
        
        Thread t2= new Thread(r2);
        t2.start();
    }
    public void Reload() {
        connection.SendConnection();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        serverMonitorPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        clientStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        serverStatus = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        logMessages = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Easy Delievery - Client");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                onKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jLabel1.setText("Client Status: ");

        clientStatus.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        clientStatus.setText("Not Connected");

        jLabel2.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jLabel2.setText("Server Status :");

        serverStatus.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        serverStatus.setText("Not Connected");

        logMessages.setEditable(false);
        logMessages.setColumns(20);
        logMessages.setRows(5);
        logMessages.setFocusable(false);
        jScrollPane2.setViewportView(logMessages);

        javax.swing.GroupLayout serverMonitorPaneLayout = new javax.swing.GroupLayout(serverMonitorPane);
        serverMonitorPane.setLayout(serverMonitorPaneLayout);
        serverMonitorPaneLayout.setHorizontalGroup(
            serverMonitorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverMonitorPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverMonitorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(serverMonitorPaneLayout.createSequentialGroup()
                        .addGroup(serverMonitorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(serverMonitorPaneLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(clientStatus))
                            .addGroup(serverMonitorPaneLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(serverStatus)))
                        .addGap(68, 68, 68))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE))
                .addGap(41, 41, 41))
        );
        serverMonitorPaneLayout.setVerticalGroup(
            serverMonitorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverMonitorPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverMonitorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(clientStatus))
                .addGap(18, 18, 18)
                .addGroup(serverMonitorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(serverStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 92, Short.MAX_VALUE)
                .addComponent(serverMonitorPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 93, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(serverMonitorPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    JSONArray users = new JSONArray();
    SQL sql;
    private void onKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_D && evt.isControlDown()) {
            ApplicationInit app = new ApplicationInit(true);
        } else if(evt.getKeyCode() == KeyEvent.VK_S && evt.isControlDown()) {
            PrintRequestWindow window = new PrintRequestWindow(true);
        }
    }//GEN-LAST:event_onKeyPressed

 
    public void SetClientStatus(String status) {
        clientStatus.setText(status);
    }
    
    public void SetServerStatus(String status) {
        serverStatus.setText(status);
    }
    public void LogMessage(String Message) {
       logMessages.append(Message + "\n");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel clientStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea logMessages;
    private javax.swing.JPanel serverMonitorPane;
    private javax.swing.JLabel serverStatus;
    // End of variables declaration//GEN-END:variables
}
