/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;

import java.awt.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ahmed Stan
 */
public class LocalServer {
 
    boolean isListening = true;
    ServerSocket serverSocket;
    MainWindow callerWindow;
    ArrayList<LocalConnection> clients;
    public LocalServer(MainWindow callerWindow) {
        this.callerWindow = callerWindow;
        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(2551);
            callerWindow.SetServerStatus("Waiting for connections");
            callerWindow.LogMessage("Server ready for connections..");
            while(isListening) {
                LocalConnection client = new LocalConnection(serverSocket.accept(), callerWindow);
                client.start();
                clients.add(client);
            }
        } catch (IOException ex) {
            Logger.getLogger(LocalServer.class.getName()).log(Level.SEVERE, null, ex);
            callerWindow.SetServerStatus("Disconnected");
        }
    }
}
