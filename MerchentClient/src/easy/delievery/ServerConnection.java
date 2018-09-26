/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import net.sf.dynamicreports.examples.Templates;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/**
 *
 * @author Server
 */
public class ServerConnection {
    
    MainWindow callerWindow;
    boolean isConnected;
    Socket serverSocket;
    public ServerConnection(MainWindow caller) {
        callerWindow = caller;
        isConnected = false;
    }
    
    public void Connect() {
        if(!isConnected) {
            try {
                serverSocket = new Socket("185.181.10.83", 2550);
                //serverSocket = new Socket("196.218.98.134", 2550);
                //serverSocket = new Socket("41.39.215.97", 2550);
                BufferedReader reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
                SendConnection();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SendCheckOrders();
                    }
                }, 5000);
                callerWindow.SetClientStatus("Waiting for message");
                String line;
                while((line = reader.readLine()) != null) {
                    callerWindow.SetClientStatus("Handling message..");
                    HandleMessage(line);
                }
            } catch (IOException ex) {
                callerWindow.LogMessage(ex.toString());
                callerWindow.SetClientStatus("Disconnected");
            }
        }
    }
    
    public void Reload() {
        isConnected = false;
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverSocket = null;
        Connect();
    }
    
    public void HandleMessage(String message) {
        try {
            callerWindow.LogMessage(message);
            JSONObject msg = new JSONObject(message);
            if(msg.getString("Msg").toLowerCase().equals("print_order")) {
                JSONArray items = msg.getJSONArray("items");
                boolean isTakeAway = msg.getBoolean("takeaway");
                JSONObject user = msg.getJSONObject("user");
                SQL sql = new SQL();
                int invoiceId = sql.InsertInvoice(callerWindow, items, isTakeAway, user);
                if(invoiceId != -1) {
                String uriString = new File("msgrec.mp3").toURI().toString();
                 MediaPlayer player = new MediaPlayer( new Media(uriString));
                 player.play();
                double value = 0;
                    for(int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        value += item.getDouble("itemPrice") * item.getDouble("qty");
                    }
                    callerWindow.LogMessage("Recieved an order to print");
                    JSONObject obj = new JSONObject();
                    obj.put("Msg", "d_order_done");
                    obj.put("id", invoiceId);
                    obj.put("value", value);
                    SendMessage(obj.toString());
                } else {
                    callerWindow.LogMessage("Order failed to insert");
                }
            } else if(msg.getString("Msg").toLowerCase().equals("new_user")) {
                        String username = msg.getString("username");
                        String password = msg.getString("password");
                        String phone = msg.getString("phone");
                        String email = msg.getString("email");
                        String address1 = msg.getString("address1");
                        String address2 = msg.getString("building");
                        String floor = msg.getString("floor");
                        String apt = msg.getString("apt");
                        double lat = msg.getDouble("lat");
                        double longt = msg.getDouble("long");
                        SQL SQL = new SQL();
                        SQL.createNewCustomer(callerWindow, username, password, phone, email, address1,lat, longt);
                        /*if(user != null) 
                            callerWindow.LogMessage("New user registered (" + username + ")");
                            msg.put("id", user.getInt("id"));
                            SendMessage(msg.toString());
                        }*/
            }
            callerWindow.SetClientStatus("Waiting for message");
        } catch (JSONException ex) {
            callerWindow.LogMessage(ex.toString());
        }
    }
    
    public void ReceiveOrder(JSONArray items)
    {
        String currentSource = "";
        boolean isDifferentSource = false;
        for(int i = 0; i < items.length(); i++)
        {
            try {
                JSONObject item = items.getJSONObject(i);
                String source = item.getString("Source");
                if(currentSource.equals(""))
                {
                    currentSource = source;
                    continue;
                }
                if(!source.equals(currentSource))
                {
                    isDifferentSource = true;
                    break;
                }
            } catch (JSONException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(isDifferentSource)
        {
            JSONArray firstItems = new JSONArray();
            for(int i = 0; i < items.length(); i++)
            {
                try {
                    JSONObject item = items.getJSONObject(i);
                    String source = item.getString("Source");
                    if(source.equals(currentSource))
                    {
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void printOrder(JSONArray items, String source, String Name, boolean isTakeAway) {
         try {
            DRDataSource dataSource = new DRDataSource("name", "qty");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                dataSource.add(item.getString("itemName"), item.getInt("qty"));
            }
            
            StyleBuilder colStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
            StyleBuilder titleStyle = stl.style().bold().setFontSize(24).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
            StyleBuilder headerStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
            
            PrinterJob job = PrinterJob.getPrinterJob();
            PrintService[] services = job.lookupPrintServices();
            PrintService s = null;
            for(int i = 0; i < services.length; i++) {
                System.out.println(services[i].getName());
                if(services[i].getName().equals("XP-80C")) {
                    s = services[i];
                }
            }
            job.setPrintService(s);
            JasperReportBuilder b = report().setTemplate(Templates.reportTemplate).
                    setReportName("امر تشغيل").
                    setDataSource(dataSource).
                    addColumn(col.column("qty", type.integerType()).
                            setTitle("الكمية").setStyle(colStyle)).
                    addColumn(col.column("name", type.stringType()).setTitle("الصنف").setStyle(colStyle)).setPageFormat(PageType.A7).title(cmp.text("أمر التشغيل").setStyle(titleStyle)).
                    addPageHeader(cmp.text("العميل : "+ Name).setStyle(headerStyle)).addPageHeader(cmp.horizontalFlowList());
            
            JasperPrint print = b.toJasperPrint();
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
            printRequestAttributeSet.add(new Copies(1));
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
            SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();
            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
            printServiceAttributeSet.add(new PrinterName("XP-80C", null));
            config.setPrintService(s);
            config.setPrintRequestAttributeSet(printRequestAttributeSet);
            config.setDisplayPrintDialog(false);
            config.setDisplayPageDialog(false);
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setConfiguration(config);
            exporter.exportReport();
        /*} catch (DRException ex) {*/
            //Logger.getLogger(PrintingExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PrinterException ex) {
            callerWindow.LogMessage(ex.toString());
        } catch (JRException ex) {
            callerWindow.LogMessage(ex.toString());
        } catch (DRException ex) {
            callerWindow.LogMessage(ex.toString());
        } catch (JSONException ex) {
         callerWindow.LogMessage(ex.toString());
        }
    }

    public void SendConnection() {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            JSONObject connectMessage = new JSONObject();
            connectMessage.put("Msg", "reg_db");
            connectMessage.put("db", EasyDelievery.CURRENT_DB);
            connectMessage.put("type", "storeClient");
            out.println(connectMessage.toString());
        } catch (JSONException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            callerWindow.LogMessage(ex.toString());
            callerWindow.SetClientStatus("Unable to send the connect message");
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void SendCheckOrders() {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            JSONObject connectMessage = new JSONObject();
            connectMessage.put("Msg", "check_orders");
            out.println(connectMessage.toString());
        } catch (JSONException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            callerWindow.LogMessage(ex.toString());
            callerWindow.SetClientStatus("Unable to send the connect message");
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public void SendMessage(String message) {
        try {
            PrintWriter output = new PrintWriter( new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            output.println(message);
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close() {
        if(serverSocket != null) {
            try {
                try {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                    JSONObject obj = new JSONObject();
                    obj.put("Msg", "close_connection");
                    out.println(obj.toString());
                } catch (JSONException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
                serverSocket.close();
                serverSocket = null;
            } catch (IOException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
 