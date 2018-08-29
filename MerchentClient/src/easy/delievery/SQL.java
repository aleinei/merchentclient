/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Ahmed Stan
 */
public class SQL {
    
    
    public String DBName;
    
    public SQL()
    {
        this.DBName = EasyDelievery.CURRENT_DB;
    }
    public Connection Connect() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName="+ DBName +";user=sa;password=AbdulRahman02^");
        if(con != null) {
            return con;
        } else {
            return null;
        }
    }
        public JSONObject createNewCustomer(MainWindow window, String username, String password, String phone, String email, String address1, String address2, String floor, String apt, double lat, double longt) {
        JSONObject user = new JSONObject();
        try {
            Connection con = this.Connect();
            if(con != null) {
                int Id = getNextId("Customers");
                String completeAddress = "ع" + address2 + " د" + floor + " ش" + apt + " " + address1;
                String values = "'" + Id + "',";
                values += "'1',";
                values += "'" + username + "',";
                values += "'" + password + "',";
                values += "'" + phone + "',";
                values += "'" + email + "',";
                values += "'" + completeAddress + "',";
                values += "" + lat + ",";
                values += "" + longt + "";
                String query = "INSERT INTO Customers (Id, Type, Name, Password, Telephone, [E-mail], Address1, Latitude, Longitude) VALUES (" + values + ")";
                try(Statement stmt = con.createStatement()) {
                    int rowsaffected = stmt.executeUpdate(query);
                    if(rowsaffected != 0) {
                        user.put("Msg", "user_created");
                    } else {
                        user.put("Msg", "user_failed");
                    }
                    return user;
                } catch (JSONException ex) {
                    window.LogMessage(ex.getMessage());
                } 
            }
        } catch (SQLException ex) {
            window.LogMessage(ex.getMessage());
        }
            
        return null;
    }
    public boolean InsertInvoice(int cstId, JSONArray items) {
        try {
                    Connection con = this.Connect();
                    int invoiceId = getNextId("Invoice_Order");
                    String invoiceValues = "'" + invoiceId + "',";
                    invoiceValues += "'0',";
                    invoiceValues += "'"+cstId+"',";
                    invoiceValues += "'1',";
                    invoiceValues += "'0',";
                    invoiceValues += "'1',";
                    invoiceValues += "'5',";
                    invoiceValues += "'0',";
                    invoiceValues += "'1',";
                    invoiceValues += "'0'";
                    String query1 = "INSERT INTO Invoice_Order (ID, CO, Cust, Type, myUser, Status, Store, myTable, IT, SP) VALUES (" + invoiceValues + ")";
                    try(Statement stmt = con.createStatement()) {
                        int rows = stmt.executeUpdate(query1);
                        if(rows > 0) {
                            for(int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                int Id = getNextId("Invoice_Order_Details");
                                String values = "'" + Id + "',";
                                values += "'" + invoiceId + "',";
                                values += "'0',";
                                values += "'" + item.getInt("itemId") + "',";
                                values += "'" + item.getString("itemName") + "',";
                                values += "'" + item.getDouble("itemPrice") + "',";
                                values += "'" + item.getInt("qty") * -1 + "',";
                                values += "'2',";
                                values += "'5',";
                                values += "'1'";
                                String query = "INSERT INTO Invoice_Order_Details "
                                        + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat) VALUES (" + values + ")";
                                try(Statement stmt2 = con.createStatement()) {
                                    int rows2 = stmt2.executeUpdate(query);
                                    if(rows2 <= 0) {
                                        return false;
                                    }
                                    System.out.println("Updated order with new invoice " + invoiceId);
                                }
                            }
                            return true;
                        } else {
                            return false;
                        }
                    } catch (JSONException ex) {
                Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
       return false;
    }
    public int InsertInvoice(int cstId, JSONArray items, boolean isTakeAway, String user) {
        try {
                    Connection con = this.Connect();
                    int invoiceId = getNextId("Invoice_Order");
                    int nextCO = -1;
                    String invoiceValues = "'" + invoiceId + "',";
                    if(isTakeAway)
                        nextCO = getNextCO(0);
                    else
                        nextCO = getNextCO(1);
                    invoiceValues += "'"+ nextCO +"',";
                    invoiceValues += "'"+cstId+"',";
                    invoiceValues += "'1',";
                    invoiceValues += "'0',";
                    invoiceValues += "'1',";
                    invoiceValues += "'5',";
                    invoiceValues += "'0',";
                    if(isTakeAway)
                        invoiceValues += "'0',";
                    else
                        invoiceValues += "'1',";
                    invoiceValues += "'0'";
                    String query1 = "INSERT INTO Invoice_Order (ID, CO, Cust, Type, myUser, Status, Store, myTable, IT, SP) VALUES (" + invoiceValues + ")";
                    try(Statement stmt = con.createStatement()) {
                        int rows = stmt.executeUpdate(query1);
                        if(rows > 0) {
                            for(int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                int Id = getNextId("Invoice_Order_Details");
                                String values = "'" + Id + "',";
                                values += "'" + invoiceId + "',";
                                values += "'0',";
                                values += "'" + item.getInt("itemId") + "',";
                                values += "'" + item.getString("itemName") + "',";
                                values += "'" + item.getDouble("itemPrice") + "',";
                                values += "'" + item.getDouble("qty") * -1 + "',";
                                values += "'2',";
                                values += "'5',";
                                values += "'1'";
                                String query = "INSERT INTO Invoice_Order_Details "
                                        + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat) VALUES (" + values + ")";
                                try(Statement stmt2 = con.createStatement()) {
                                    int rows2 = stmt2.executeUpdate(query);
                                    if(rows2 <= 0) {
                                        System.out.println("rows2 was not done");
                                        return -1;
                                    }
                                    boolean hasExtra = item.getBoolean("hasextra");
                                    boolean hasadd = item.getBoolean("hasadd");
                                    boolean hasWithout = item.getBoolean("haswithout");
                                    if(hasExtra) {
                                        JSONArray extraItems = item.getJSONArray("extraitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextId("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'4'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                    if(hasadd) {
                                        JSONArray extraItems = item.getJSONArray("addableitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextId("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'2'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                    if(hasWithout) {
                                        JSONArray extraItems = item.getJSONArray("withoutitems");
                                        for(int e = 0; e < extraItems.length(); e++) {
                                            JSONObject ei = extraItems.getJSONObject(e);
                                            int eID = getNextId("Invoice_Order_Details");
                                            String queryValues = "'" + eID + "',";
                                            queryValues += "'" + invoiceId + "',";
                                            queryValues += "'" + Id + "',";
                                            queryValues += "'" + ei.getInt("id") + "',";
                                            queryValues += "'" + ei.getString("name") + "',";
                                            queryValues += "'" + ei.getDouble("price") + "',";
                                            queryValues += "'" + ei.getDouble("qty") + "',";
                                            queryValues += "'2',";
                                            queryValues += "'5',";
                                            queryValues += "'1',";
                                            queryValues += "'3'";
                                            String extraQuery = "INSERT INTO Invoice_Order_Details "
                                                    + "(ID, Invoice_Order_ID, Parent, DrugID, ModifiedName, Price, Quantity, Supplier, Store, myStat, ComponentType) VALUES (" + queryValues + ")";
                                            try(Statement stmt3 = con.createStatement()) {
                                                stmt3.execute(extraQuery);
                                                stmt3.close();
                                            }
                                        }
                                    }
                                }
                            }
                            PrintOrder.PrintWorkOrder(items, user, invoiceId, nextCO, this);
                            return invoiceId;
                        } else {
                            System.out.println("rows was not done");
                            return -1;
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
                    }
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
       return -1;
    }
        private int getNextId(String tableName) {
        try {
            Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT max(Id) as Id FROM  " + tableName;
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) {
                        return rs.getInt("Id") + 1;
                    }
                }
            }
 
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
     private int getNextCO(int IT)
    {
        try {
            Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT max(CO) as CO FROM Invoice_Order WHERE Type = 1 AND Store = 5 AND IT = '" + IT + "'" ;
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) {
                        return rs.getInt("CO") + 1;
                    }
                }
            }
 
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }
    private boolean InvoiceExists(int invoiceId) {
        try {
            Connection con = this.Connect();
            if(con != null) {
                String query = "SELECT ID FROM Invoice_Order WHERE ID = '" + invoiceId + "'";
                try(Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()) return true;
                    else return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public JSONObject getKitchenOrders() {
        JSONObject object = new JSONObject();
        try {
            object.put("Msg", "kitchen_order");
            Connection con = this.Connect();
            String query = "SELECT DISTINCT dbo.NewGridInvoiceDetails.Invoice_Order_Id as Id, dbo.Invoice_Order.CO as myCO, dbo.Invoice_Order.[myTable], dbo.NewGridInvoiceDetails.[Destination] , dbo.Invoice_Order.[It] , case  dbo.Invoice_Order.[It] when 0 then 'طلب ' + dbo.NewGridInvoiceDetails.[Destination] + '، رقم: '+   cast(dbo.Invoice_Order.CO as nvarchar) when 1 then 'طلب ' + dbo.NewGridInvoiceDetails.[Destination] + '، رقم: '+  cast(dbo.Invoice_Order.CO as nvarchar) + ' العميل: ' + dbo.NewGridInvoiceDetails.Account  when 2 then 'طلب ' + dbo.NewGridInvoiceDetails.[Destination]  + '، ترابيزه رقم: ' + cast(dbo.Invoice_Order.[myTable] as nvarchar)  end As CO FROM dbo.NewGridInvoiceDetails INNER JOIN dbo.Invoice_Order ON dbo.NewGridInvoiceDetails.Invoice_Order_Id =  dbo.Invoice_Order.Id \n" +
                               "Where (dbo.NewGridInvoiceDetails.Parent = 0) And (dbo.NewGridInvoiceDetails.Kitchen Is Null And Not dbo.NewGridInvoiceDetails.Ordered Is Null) And (CONVERT(Char(10), dbo.NewGridInvoiceDetails.Modified, 120) = CONVERT(Char(10), GETDATE(), 120)) AND  (NOT (dbo.NewGridInvoiceDetails.Ordered IS NULL)) AND  (dbo.NewGridInvoiceDetails.SourceId=1)";
        } catch (JSONException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return object;
    }
    
    public String GetPrinterNameFromSource(String source)
    {
        try {
            Connection con = this.Connect();
            if(con != null)
            {
                String query = "SELECT ReportID From Sources WHERE Name = '" + source + "'";
                try(Statement stmt = con.createStatement())
                {
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        int reportID = rs.getInt("ReportID");
                        String query2 = "SELECT Printer FROM ESIReports WHERE Id = " + reportID;
                        try(Statement stmt2 = con.createStatement())
                        {
                            ResultSet rs2 = stmt2.executeQuery(query2);
                            if(rs2.next())
                                return rs2.getString("Printer");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
