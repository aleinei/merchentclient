/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easy.delievery;

import java.awt.BasicStroke;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Border;
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
import net.sf.dynamicreports.report.builder.style.BorderBuilder;
import net.sf.dynamicreports.report.builder.style.PenBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.LineSpacing;
import net.sf.dynamicreports.report.constant.LineStyle;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.StretchType;
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

/**
 *
 * @author Server
 */
public class PrintOrder {
    
    
    public static void PrintWorkOrder(JSONArray items, String user, int invoiceID, int CO, SQL sql, boolean isTakeAway, String dTime)
    {
        ArrayList<String> Sources = new ArrayList();
        for(int i = 0; i < items.length(); i ++)
        {
            try {
                JSONObject item = items.getJSONObject(i);
                String itemSource = "";
                try {
                    itemSource = item.getString("Source");
                } catch (JSONException ex)
                {
                    itemSource = "";
                }
                if(!Sources.contains(itemSource))
                {
                    Sources.add(itemSource);
                }
            } catch (JSONException ex) {
                Logger.getLogger(PrintOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for(int i = 0; i < Sources.size(); i++)
        {
            JSONArray items2 = new JSONArray();
            for(int x = 0;  x < items.length(); x++)
            {
                try {
                    JSONObject item = items.getJSONObject(x);
                    String itemSource = "";
                    try {
                        itemSource = item.getString("Source");
                    } catch (JSONException ex)
                    {
                        itemSource = "";
                    }
                    if(Sources.get(i).equals(itemSource))
                    {
                        items2.put(item);
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(PrintOrder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            PrintOrderToWork(items2, user, invoiceID,  Sources.get(i), CO, sql.GetPrinterNameFromSource(Sources.get(i)), isTakeAway, dTime);
        }
    }
    
    public static void PrintOrderToWork(JSONArray items, String user, int invoiceID,  String source, int CO, String printerName, boolean isTakeAway, String dTime)
    {
            try {
                DRDataSource dataSource = new DRDataSource("name", "qty");
                SimpleDateFormat forma = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                String date = forma.format(new Date());
                String invoice = "م" + invoiceID;
                JSONObject userObject = new JSONObject(user);
                String username = userObject.getString("name");
                String telephone = userObject.getString("phone");
                String address = userObject.getString("address");
                String service = isTakeAway? "تيك اواي" : "دليفري";
                for(int i = 0; i < items.length(); i++) {
                   JSONObject item = items.getJSONObject(i);
                   dataSource.add(item.getString("itemName"), item.getDouble("qty") + "");                   
                }
                StyleBuilder colStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setBorder(stl.pen(1f, LineStyle.SOLID)).setPadding(3);
                StyleBuilder titleStyle = stl.style().bold().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).underline();
                StyleBuilder leftHeaderStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setFontSize(12).setPadding(0).setLineSpacing(LineSpacing.SINGLE);
                StyleBuilder rightHeaderStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setFontSize(12).setLineSpacing(LineSpacing.SINGLE);
                StyleBuilder centerHeaderStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setPadding(0).setLineSpacing(LineSpacing.SINGLE);
                StyleBuilder numberStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setFontSize(12);
                PrintService[] services = PrinterJob.lookupPrintServices();
                PrintService s = null;
                for(int i = 0; i < services.length; i++) {
                    System.out.println(services[i].getName());
                    if(services[i].getName().equals(printerName)) {
                        s = services[i];
                    }
                
                }
                JasperReportBuilder b = report().setTemplate(Templates.reportTemplate).
                    setReportName("امر تشغيل").
                    setDataSource(dataSource).
                   addColumn(col.column("name", type.stringType()).setTitle("الصنف").setStyle(colStyle)).addColumn(col.column("qty", type.stringType()).
                            setTitle("الكمية").setStyle(colStyle)).
                    setPageFormat(PageType.A7).title(cmp.text("أمر تشغيل").setStyle(titleStyle))
                        .addPageHeader(cmp.text(invoice).setStyle(rightHeaderStyle)).
                        addPageHeader(cmp.horizontalList().add(cmp.text("الرقم: " + CO).setStyle(numberStyle)).add(cmp.text("القسم : " + source).setStyle(rightHeaderStyle).setWidth(100))).
                        addPageHeader(cmp.horizontalList().add(cmp.text("الوقت : " + date).setStyle(leftHeaderStyle)).add(cmp.text("خدمه: " + service).setStyle(rightHeaderStyle).setWidth(30)))
                        .addPageHeader(cmp.horizontalList().add(cmp.text("تسليم : " + dTime).setStyle(leftHeaderStyle)))
                        .addPageHeader(cmp.text("العميل : " + username).setStyle(rightHeaderStyle))
                        .addPageHeader(cmp.text("تليفون  : " + telephone).setStyle(rightHeaderStyle))
                        .addPageHeader(cmp.text(" عنوان: " + address).setStyle(rightHeaderStyle));
                 JasperPrint print = b.toJasperPrint();
                PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
                printRequestAttributeSet.add(new Copies(1));
                JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();
                PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                printServiceAttributeSet.add(new PrinterName(printerName, null));
                config.setPrintService(s);
                config.setPrintRequestAttributeSet(printRequestAttributeSet);
                config.setDisplayPrintDialog(false);
                config.setDisplayPageDialog(false);
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setConfiguration(config);
                exporter.exportReport();
            } catch (JSONException | JRException | DRException ex) {
                Logger.getLogger(PrintOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
     public static void PrintReceipt(JSONArray items, String user, int invoiceID, int CO, String printerName, boolean isTakeAway, String dTime)
    {
            try {
                DRDataSource dataSource = new DRDataSource("name", "qty", "value", "total");
                double totalValue = 0.0;
                SimpleDateFormat forma = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                String date = forma.format(new Date());
                String invoice = "م" + invoiceID;
                JSONObject userObject = new JSONObject(user);
                String username = userObject.getString("name");
                String telephone = userObject.getString("phone");
                String address = userObject.getString("address");
                String service = isTakeAway? "تيك اواي" : "دليفري";
                for(int i = 0; i < items.length(); i++) {
                   JSONObject item = items.getJSONObject(i);
                   dataSource.add(item.getString("itemName"), item.getDouble("qty") + "",String.valueOf(item.getDouble("itemPrice")), String.valueOf((item.getDouble("itemPrice") * item.getDouble("qty"))));
                   totalValue += item.getDouble("qty") * item.getDouble("itemPrice");
                }
                StyleBuilder colStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setBorder(stl.pen(1f, LineStyle.SOLID)).setPadding(3);
                StyleBuilder titleStyle = stl.style().bold().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).underline();
                StyleBuilder leftHeaderStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setFontSize(12).setPadding(0).setLineSpacing(LineSpacing.SINGLE);
                StyleBuilder rightHeaderStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setFontSize(8).setLineSpacing(LineSpacing.SINGLE);
                StyleBuilder centerHeaderStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setPadding(0).setLineSpacing(LineSpacing.SINGLE);
                StyleBuilder numberStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setFontSize(12);
                PrintService[] services = PrinterJob.lookupPrintServices();
                PrintService s = null;
                for(int i = 0; i < services.length; i++) {
                    System.out.println(services[i].getName());
                    if(services[i].getName().equals(printerName)) {
                        s = services[i];
                    }
                
                }
                JasperReportBuilder b = report().setTemplate(Templates.reportTemplate).
                    setReportName("فاتورة دليفري").
                    setDataSource(dataSource).
                   addColumn(col.column("total", type.stringType()).setTitle("الاجمالي").setStyle(colStyle)).addColumn(col.column("qty", type.stringType()).
                      setTitle("الكمية").setStyle(colStyle)).addColumn(col.column("value", type.stringType()).setTitle("السعر").setStyle(colStyle)).
                      addColumn(col.column("name", type.stringType()).setTitle("الصنف").setStyle(colStyle)).
                    setPageFormat(PageType.A7).title(cmp.text("فاتورة").setStyle(titleStyle))
                        .addPageHeader(cmp.text(invoice).setStyle(rightHeaderStyle)).
                        addPageHeader(cmp.horizontalList().add(cmp.text("الرقم: " + CO).setStyle(numberStyle))).
                        addPageHeader(cmp.horizontalList().add(cmp.text("الوقت : " + date).setStyle(leftHeaderStyle)).add(cmp.text("خدمه: " + service).setStyle(rightHeaderStyle).setWidth(30)))
                        .addPageHeader(cmp.horizontalList().add(cmp.text("تسليم : " + dTime).setStyle(leftHeaderStyle)))
                        .addPageHeader(cmp.text("العميل : " + username).setStyle(rightHeaderStyle))
                        .addPageHeader(cmp.text("تليفون  : " + telephone).setStyle(rightHeaderStyle))
                        .addPageHeader(cmp.text(" عنوان: " + address).setStyle(rightHeaderStyle))
                        .addPageFooter(cmp.text("القيمه: " + totalValue).setStyle(centerHeaderStyle));
                 JasperPrint print = b.toJasperPrint();
                PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
                printRequestAttributeSet.add(new Copies(1));
                JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();
                PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                printServiceAttributeSet.add(new PrinterName(printerName, null));
                config.setPrintService(s);
                config.setPrintRequestAttributeSet(printRequestAttributeSet);
                config.setDisplayPrintDialog(false);
                config.setDisplayPageDialog(false);
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setConfiguration(config);
                exporter.exportReport();
            } catch (JSONException | JRException | DRException ex) {
                Logger.getLogger(PrintOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}