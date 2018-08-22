/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Ahmed Stan
 */
public class LocalConnection extends Thread{
    Socket connection;
    MainWindow callerWindow;
    
    public LocalConnection(Socket socket, MainWindow caller) {
        connection = socket;
        callerWindow = caller;
        callerWindow.LogMessage("New connection");
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"), true);
            String line;
            while((line = input.readLine()) != null) {
                callerWindow.LogMessage(line);
                HandleMessage(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(LocalConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void HandleMessage(String message) {
        try {
            JSONObject msg = new JSONObject(message);
            if(msg.getString("Msg").equals("kitchen_order")) {
                
            }
            callerWindow.LogMessage(message);
        } catch (JSONException ex) {
            Logger.getLogger(LocalConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
