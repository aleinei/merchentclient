/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;


public class EasyDelievery {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        MainWindow window = new MainWindow();
        ServerConnection connection = new ServerConnection(window);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                connection.Connect();
            }
        };
        Thread t = new Thread(r);
        t.start();
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                LocalServer server = new LocalServer(window);
            }
            
        };
        
        Thread t2= new Thread(r2);
        t2.start();
    }
    
}
