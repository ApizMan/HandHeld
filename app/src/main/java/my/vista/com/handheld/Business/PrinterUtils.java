package my.vista.com.handheld.Business;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;

import my.vista.com.handheld.Entity.NoticeInfo;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.Entity.HandheldInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class PrinterUtils.
 */
public final class PrinterUtils
{
    /** The Connection. */
    public static Connection Connection;

    /** The Printer. */
    public static ZebraPrinter Printer;

    /** The Language. */
    public static PrinterLanguage Language;

    /** The Mac address. */
    public static String MacAddress;

    /**
     * Creates the connection.
     *
     * @param macAddress
     *            the mac address
     */
    public static void CreateConnection(String macAddress) throws InterruptedException {
        try {
            MacAddress = macAddress;
            Connection = new BluetoothConnectionInsecure(macAddress);
        }
        catch(Exception e) {
            throw e;
        }
    }

    public static void ClearSocket() {
        try {
            //global variable
            BluetoothSocket mBSocket = null;

            // inside doInBackground() function
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                try {
                    for (BluetoothDevice bt : mBluetoothAdapter.getBondedDevices()) {
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(bt.getAddress());
                        try {
                            Method m = device.getClass()
                                    .getMethod("removeBond", (Class[]) null);
                            m.invoke(device, (Object[]) null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void OpenConnection() throws ConnectionException, ZebraPrinterLanguageUnknownException, InterruptedException {
        try {
            ClearSocket();
            CloseConnection();

            if(!Connection.isConnected())
                Connection.open();

            Printer = ZebraPrinterFactory.getInstance(Connection);
            Language = Printer.getPrinterControlLanguage();
        } catch(Exception e) {
            throw e;
        }
    }
	
	public static PrintingDocument CreateTestPrint(String MacAddress) {
        PrintingDocument doc = new PrintingDocument("350");

        try {
            doc.DrawText(0,10,30, MacAddress);
        }
        catch(Exception ex) {

        }

        return doc;
	}

    public static PrintingDocument CreateReportPrint(HandheldInfo info) {
        PrintingDocument doc = new PrintingDocument("500");

        try {
            doc.DrawText(0,10,20, "Bhg/Zon/Unit");
            doc.DrawText(300,0,20, ": " + info.OfficerZone);
            doc.DrawText(0,30,20, "Handheld ID");
            doc.DrawText(300,0,20, ": " + info.HandheldID);
            doc.DrawText(0,30,20, "ID Log Masuk");
            doc.DrawText(300,0,20, ": " + info.OfficerDetails);
            doc.DrawText(0,30,20, "Jumlah Notis Keseluruhan");
            doc.DrawText(300,0,20, ": " + info.TotalNoticeIssued);
            doc.DrawText(0,30,20, "Jumlah Gambar");
            doc.DrawText(300,0,20, ": " + info.TotalImage);
            doc.DrawText(0,30,20, "Jumlah Transaksi Bayaran");
            doc.DrawText(300,0,20, ": " + info.TotalTransaction);
            doc.DrawText(0,30,20, "Total Amoun Bayaran");
            doc.DrawText(300,0,20, ": " + "RM " + info.TotalAmountCollected);
        }
        catch(Exception ex) {

        }

        return doc;
    }

    public static PrintingDocument CreateReceipt(NoticeInfo info) {
        PrintingDocument doc = new PrintingDocument("1300");

        try {
            InputStream stBmp = null;

            try {
                stBmp = CacheManager.mContext.getAssets().open("logo.png");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeStream(stBmp);

            Printer.storeImage("logo.png", new ZebraImageAndroid(bmp), bmp.getWidth(), bmp.getHeight());

            try {
                stBmp = CacheManager.mContext.getAssets().open("mp.png");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            bmp = BitmapFactory.decodeStream(stBmp);

            Printer.storeImage("mp.png", new ZebraImageAndroid(bmp), bmp.getWidth(), bmp.getHeight());

            doc.DrawImageName(150,0,"mp.png");
            doc.DrawImageName(450,0,"logo.png");

            doc.DrawTextFlow(0, 200, 20, 800, "C", "--------------------------------------------");
            doc.DrawTextFlow(0, 30, 40, 800, "C", "Vista");
            doc.DrawTextFlow(0, 40, 30, 800, "C", "Resit");
            doc.DrawTextFlow(0, 60, 30, 800, "C", "NO KOMPAUN : " + info.NoticeNo);
            doc.DrawTextFlow(0, 30, 20, 800, "C", "--------------------------------------------");


            doc.DrawText(0, 60, 30, "TARIKH & WAKTU");
            doc.DrawText(250, 0, 35, ": " + CacheManager.GetLongDateString(new Date()) + " " + CacheManager.GetTimeString(new Date()));

            if(info.ClampingPaidAmount > 0 && info.PaidAmount > 0) {
                doc.DrawText(0, 40, 30, "AMOUN KOMPAUN");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.PaidAmount));
                doc.DrawText(0, 40, 30, "AMOUN KAPIT");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.ClampingPaidAmount));
            } else if(info.PaidAmount > 0) {
                doc.DrawText(0, 40, 30, "AMOUN KOMPAUN");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.PaidAmount));
            } else {
                doc.DrawText(0, 40, 30, "AMOUN KAPIT");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.ClampingPaidAmount));
            }

            doc.DrawTextFlow(0, 40, 20, 800, "C", "--------------------------------------------");

            doc.DrawText(0, 40, 20, "TARIKH");
            doc.DrawText(180, 0, 25, ": " + CacheManager.GetDateString(info.OffenceDateTime));
            doc.DrawText(400, 0, 20, "WAKTU");
            doc.DrawText(500, 0, 25, ": " + CacheManager.GetTimeString(info.OffenceDateTime));

            doc.DrawText(0, 30, 20, "NO KENDERAAN");
            doc.DrawText(180, 0, 25, ": " + info.VehicleNo);

            doc.DrawText(0, 30, 20, "TAWARAN KOMPAUN");
            doc.DrawText(180, 0, 25, ": " + "RM " + String.format("%.2f", info.CompoundAmount));

            doc.DrawText(0, 30, 20, "DIKELUARKAN OLEH");
            doc.DrawText(180, 0, 25, ": " + CacheManager.officerId);

            doc.DrawTextFlow(0, 40, 20, 800, "C", "--------------------------------------------");

            doc.DrawText(0, 40, 30, "NO RESIT");
            doc.DrawText(250, 0, 35, ": " + info.ReceiptNumber);

            doc.DrawTextFlow(0, 90, 30, 800, "C", "TERIMA KASIH");
            doc.DrawTextFlow(0, 40, 30, 800, "C", "Majlis Bandaraya Kuala Terengganu");

        }
        catch(Exception ex) {

        }

        return doc;
    }

    public static PrintingDocument CreateReceipt(NoticeInfo info, Date paymentDate) {
        PrintingDocument doc = new PrintingDocument("1300");

        try {
            InputStream stBmp = null;

            try {
                stBmp = CacheManager.mContext.getAssets().open("logo.png");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeStream(stBmp);

            Printer.storeImage("logo.png", new ZebraImageAndroid(bmp), bmp.getWidth(), bmp.getHeight());

            try {
                stBmp = CacheManager.mContext.getAssets().open("mp.png");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            bmp = BitmapFactory.decodeStream(stBmp);

            Printer.storeImage("mp.png", new ZebraImageAndroid(bmp), bmp.getWidth(), bmp.getHeight());

            doc.DrawImageName(150,0,"mp.png");
            doc.DrawImageName(450,0,"logo.png");

            doc.DrawTextFlow(0, 200, 20, 800, "C", "--------------------------------------------");
            doc.DrawTextFlow(0, 30, 40, 800, "C", "Vista");
            doc.DrawTextFlow(0, 40, 30, 800, "C", "Resit");
            doc.DrawTextFlow(0, 60, 30, 800, "C", "NO KOMPAUN : " + info.NoticeNo);
            doc.DrawTextFlow(0, 30, 20, 800, "C", "--------------------------------------------");


            doc.DrawText(0, 60, 30, "TARIKH & WAKTU");
            doc.DrawText(250, 0, 35, ": " + CacheManager.GetLongDateString(paymentDate) + " " + CacheManager.GetTimeString(paymentDate));

            if(info.ClampingPaidAmount > 0 && info.PaidAmount > 0) {
                doc.DrawText(0, 40, 30, "AMOUN KOMPAUN");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.PaidAmount));
                doc.DrawText(0, 40, 30, "AMOUN KAPIT");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.ClampingPaidAmount));
            } else if(info.PaidAmount > 0) {
                doc.DrawText(0, 40, 30, "AMOUN KOMPAUN");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.PaidAmount));
            } else {
                doc.DrawText(0, 40, 30, "AMOUN KAPIT");
                doc.DrawText(250, 0, 35, ": " + "RM " + String.format("%.2f", info.ClampingPaidAmount));
            }

            doc.DrawTextFlow(0, 40, 20, 800, "C", "--------------------------------------------");

            doc.DrawText(0, 40, 20, "TARIKH");
            doc.DrawText(180, 0, 25, ": " + CacheManager.GetDateString(info.OffenceDateTime));
            doc.DrawText(400, 0, 20, "WAKTU");
            doc.DrawText(500, 0, 25, ": " + CacheManager.GetTimeString(info.OffenceDateTime));

            doc.DrawText(0, 30, 20, "NO KENDERAAN");
            doc.DrawText(180, 0, 25, ": " + info.VehicleNo);

            doc.DrawText(0, 30, 20, "TAWARAN KOMPAUN");
            doc.DrawText(180, 0, 25, ": " + "RM " + String.format("%.2f", info.CompoundAmount));

            doc.DrawText(0, 30, 20, "DIKELUARKAN OLEH");
            doc.DrawText(180, 0, 25, ": " + CacheManager.officerId);

            doc.DrawTextFlow(0, 40, 20, 800, "C", "--------------------------------------------");

            doc.DrawText(0, 40, 30, "NO RESIT");
            doc.DrawText(250, 0, 35, ": " + info.ReceiptNumber);

            doc.DrawTextFlow(0, 90, 30, 800, "C", "TERIMA KASIH");
            doc.DrawTextFlow(0, 40, 30, 800, "C", "Majlis Bandaraya Kuala Terengganu");

        }
        catch(Exception ex) {

        }

        return doc;
    }

    public static PrintingDocument CreateNotice(SummonIssuanceInfo info) {
        PrintingDocument doc = new PrintingDocument("2400");
        //PrintingDocument doc = new PrintingDocument("2000");
        if(info.IsClamping.equalsIgnoreCase("True")) {
            doc = new PrintingDocument("2450");
        }

        try {
            InputStream stBmp = null;

            try {
                stBmp = CacheManager.mContext.getAssets().open("small.png");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeStream(stBmp);

            Printer.storeImage("small.png", new ZebraImageAndroid(bmp), bmp.getWidth(), bmp.getHeight());

            try {
                stBmp = CacheManager.mContext.getAssets().open("sign.png");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            bmp = BitmapFactory.decodeStream(stBmp);

            Printer.storeImage("sign.png", new ZebraImageAndroid(bmp), bmp.getWidth(), bmp.getHeight());

            doc.DrawImageName(0,0,"small.png");

            doc.DrawTextFlow(130, 0, 35, 670, "C", "MAJLIS BANDARAYA KUALA TERENGGANU");
            doc.DrawTextFlow(130, 35, 25, 670, "C", "AKTA PENGANGKUTAN JALAN 1987 (AKTA 333)\\&" +
                    "PERINTAH LALULINTAS JALAN (TEMPAT LETAK KERETA)\\&MAJLIS BANDARAYA KUALA TERENGGANU 1995");

            doc.DrawBarcode39(150, 100, 70, info.NoticeSerialNo);

            doc.DrawText(10, 110, 25, "Kepada Pemandu / Pemilik Kenderaan :");

            doc.DrawText(10, 35, 20, "NO KENDERAAN");
            doc.DrawText(230, 0, 25, ": " + info.VehicleNo);
            doc.DrawText(10, 30, 20, "JENIS KENDERAAN");
            doc.DrawText(230, 0, 25, ": " + info.VehicleType);

            String makeModel = "";
            if(info.VehicleMake.length() != 0)
                makeModel += info.VehicleMake;
            else
                makeModel += info.SelectedVehicleMake;

            if(info.VehicleModel.length() != 0)
                makeModel += " " + info.VehicleModel;
            else
                makeModel += " " + info.SelectedVehicleModel;
            doc.DrawText(10, 30, 20, "MODEL KENDERAAN");
            doc.DrawText(230, 0, 25, ": " + makeModel);

            doc.DrawText(10, 30, 20, "WARNA");
            doc.DrawText(230, 0, 25, ": " + info.VehicleColor);

            doc.DrawText(10, 30, 20, "NO CUKAI JALAN");
            doc.DrawText(230, 0, 25, ": " + info.RoadTaxNo);

            doc.DrawText(10, 30, 20, "ZON");
            doc.DrawText(230, 0, 25, ": " + info.OffenceLocationArea);

            String location = "";
            if(info.OffenceLocation.length() > 0)
                location = info.OffenceLocation;
            else
                location = info.SummonLocation;

            doc.DrawText(10, 30, 20, "TEMPAT/JALAN");
            doc.DrawText(230, 0, 25, ": " + location);

            doc.DrawText(10, 30, 20, "TARIKH KESALAHAN");
            doc.DrawText(230, 0, 25, ": " + CacheManager.GetDateString(info.OffenceDateTime));
            doc.DrawText(10, 30, 20, "WAKTU DIKELUARKAN");
            doc.DrawText(230, 0, 25, ": " + CacheManager.GetTimeString(info.OffenceDateTime));

            doc.DrawText(0, 60, 20, "PERUNTUKAN UNDANG-UNDANG :");
            doc.DrawText(50, 30, 25, info.OffenceAct);

            doc.DrawText(0, 60, 20, "SEKSYEN / KAEDAH / PERINTAH :");
            doc.DrawText(50, 30, 25, info.OffenceSection);

            doc.DrawText(0, 60, 20, "KESALAHAN :");
            doc.DrawTextFlow(50, 30, 25, 700, "J", info.Offence);

            doc.DrawText(0, 110, 20, "BUTIR-BUTIR");
            doc.DrawText(0, 60, 20, "DIKELUARKAN OLEH");
            doc.DrawText(230, 0, 25, ": " + CacheManager.officerId);

            doc.DrawText(0, 60, 20, "PENGUATKUASA/ WARDEN LALU LINTAS");
            doc.DrawText(500, 0, 20, "TARIKH : " + CacheManager.GetDateString(new Date()));

            doc.DrawTextFlow(0, 60, 25, 800, "J",
                    "KEPADA PEMANDU/PEMILIK KENDERAAN BERNOMBOR SEPERTI DI ATAS, " +
                    "DENGAN INI DIBERITAHU BAHAWA PADA TARIKH NOTIS INI, KESALAHAN " +
                    "SEPERTIMANA BUTIR-BUTIR DI ATAS, TELAH DILAKUKAN DENGAN KESALAHAN " +
                    "DI TEMPAT DAN WAKTU YANG DINYATAKAN.");

            doc.DrawText(0, 130, 25, "-----------------------------------------------");

            doc.DrawTextFlow(0, 30, 30, 800, "C", "TAWARAN MENGKOMPAUN");

            doc.DrawTextFlow(0, 60, 25, 800, "J",
                    "Pada menjalankan kuasa yang diberi oleh perenggan 120 (1)(e) Akta Pengangkutan Jalan " +
                            "1987 (Akta 333), saya bersedia dan dengan ini menawarkan untuk mengkompaunkan " +
                            "dengan kesalahan ini dengan bayaran wang sebagaimana berikut :");

            doc.DrawText(0, 110, 25, "Kadar Bayaran Kompaun");

            if (info.CompoundAmountDesc1 != null && info.CompoundAmountDesc1.length() != 0) {
                doc.DrawTextFlow(0, 60, 20, 250, "C", info.CompoundAmountDesc1);
                if (info.CompoundAmountDesc2 != null && info.CompoundAmountDesc2.length() != 0) {
                    doc.DrawTextFlow(280, 0, 20, 250, "C", info.CompoundAmountDesc2);
                }
                if (info.CompoundAmountDesc3 != null && info.CompoundAmountDesc3.length() != 0) {
                    doc.DrawTextFlow(560, 0, 20, 250, "C", info.CompoundAmountDesc3);
                }
            }
            if (info.CompoundAmountDesc1 != null && info.CompoundAmountDesc1.length() != 0) {
                doc.DrawTextFlow(0, 30, 25, 250, "C", "RM " + String.format("%.2f", info.CompoundAmount1));
                if (info.CompoundAmountDesc2 != null && info.CompoundAmountDesc2.length() != 0) {
                    doc.DrawTextFlow(280, 0, 25, 250, "C", "RM " + String.format("%.2f", info.CompoundAmount2));
                }
                if (info.CompoundAmountDesc3 != null && info.CompoundAmountDesc3.length() != 0) {
                    doc.DrawTextFlow(560, 0, 25, 250, "C", "RM " + String.format("%.2f", info.CompoundAmount3));
                }
            }

            doc.DrawTextFlow(0, 60, 25, 800, "J",
                    "Tuan/Puan bolehlah menyempurnakan tawaran mengkompaun tersebut dengan membuat " +
                            "bayaran secara:-");

            doc.DrawText(30, 90, 25, "(a)");
            doc.DrawText(60, 0, 25, "Tunai : atau");

            doc.DrawText(30, 60, 25, "(b)");

            doc.DrawTextFlow(60, 0, 25, 740, "J",
                    "Kiriman wang, wang pos, perintah juruwang, cek jurubank atau bank draf yang " +
                            " dibuat untuk dibayar atas nama Datuk Bandar Majlis Bandaraya Kuala Terengganu " +
                            " dan dipalang \"Akaun Penerima Sahaja\"");

            doc.DrawTextFlow(60, 110, 25, 740, "J",
                    "Sebagaimana tempat dan alamat serta pada waktu yang dinyatakan di belakang " +
                            " Tawaran Mengkompaun ini.");

            doc.DrawTextFlow(0, 110, 25, 760, "J",
                    "Tawaran ini habis tempoh selepas empat belas (14) hari dari tarikh notis ini dan jika bayaran " +
                            "bayaran penuh yang ditetapkan bagi tawaran mengkompaun kesalahan ini tidak diterima " +
                            "dalam tempoh tersebut, tindakan penguatkuasaan undang-undang akan dibuat mengenai " +
                            "kesalahan tersebut.");

            doc.DrawTextFlow(400, 170, 25, 400, "C",
                    "Saya yang menurut perintah");

            doc.DrawImageName(420,20,"sign.png");

            doc.DrawTextFlow(0, 50, 25, 400, "L",
                    "Catitan : .......................................\\&" +
                            "....................................................");

            doc.DrawTextFlow(400, 30, 25, 400, "C",
                    ".....................................................\\&" +
                            "Wan Marahakim Bin Wan Salleh\\&" +
                            "Pengarah Undang-Undang\\&" +
                            "Majlis Bandaraya Kuala Terengganu");

            if(info.IsClamping.equalsIgnoreCase("True")) {
                doc.DrawTextFlow(0, 150, 20, 800, "C", "KENDERAAN TELAH DIKUNCI TAYAR");
            }
        }
        catch(Exception ex) {

        }

        return doc;
    }

    /**
     * Prints the.
     *
     * @param document
     *            the document
     * @throws ZebraPrinterLanguageUnknownException
     * @throws IOException
     * @throws ConnectionException
     */
    public static void Print(PrintingDocument document) throws ZebraPrinterLanguageUnknownException,  InterruptedException, ConnectionException, IOException {
        try {
            if (!Connection.isConnected()) {
                OpenConnection();
            }

            if(Language != PrinterLanguage.ZPL) {
                String language = "! U1 setvar \"device.languages\" \"zpl\"";
                //String language = "! U1 setvar \"device.languages\" \"line_print\"";
                Connection.write(language.getBytes());
                if (Connection instanceof BluetoothConnectionInsecure) {
                    Thread.sleep(500);
                }
                Language = Printer.getPrinterControlLanguage();
            }

            if (Language == PrinterLanguage.ZPL) {
				try {
				    String start = "^XA^POI^PW800^MNN^LL" + document.PaperLength + "^LH0,0 \r\n";
                    Connection.write(start.getBytes());

					byte[] docBytes = document.GetPrintingData().getBytes();
					Connection.write(docBytes);

					Connection.write("^XZ".getBytes());
					if (Connection instanceof BluetoothConnectionInsecure) {
						Thread.sleep(500);
					}
				} catch (ConnectionException e) {
				}
            }
            else {
                throw new ZebraPrinterLanguageUnknownException("Wrong Language - Current Printer Language is " + Language);
            }
        }
        catch (ConnectionException e) {
            throw e;
        }
        catch (InterruptedException e) {
            throw e;
        }
        catch (ZebraPrinterLanguageUnknownException e) {
            throw e;
        }
        finally {
            System.gc();
            Runtime.getRuntime().gc();
        }
    }

    public static void ClearJobs() throws ZebraPrinterLanguageUnknownException,  InterruptedException, ConnectionException, IOException {
        try {
            if (!Connection.isConnected()) {
                OpenConnection();
            }

            if(Language != PrinterLanguage.ZPL) {
                String language = "! U1 setvar \"device.languages\" \"zpl\"";
                //String language = "! U1 setvar \"device.languages\" \"line_print\"";
                Connection.write(language.getBytes());
                if (Connection instanceof BluetoothConnectionInsecure) {
                    Thread.sleep(500);
                }
                Language = Printer.getPrinterControlLanguage();
            }

            if (Language == PrinterLanguage.ZPL) {
                try {
                    Connection.write("~JA".getBytes());
                    if (Connection instanceof BluetoothConnectionInsecure) {
                        Thread.sleep(5000);
                    }
                } catch (ConnectionException e) {
                }
            }
            else {
                throw new ZebraPrinterLanguageUnknownException("Wrong Language - Current Printer Language is " + Language);
            }
        }
        catch (ConnectionException e) {
            throw e;
        }
        catch (InterruptedException e) {
            throw e;
        }
        catch (ZebraPrinterLanguageUnknownException e) {
            throw e;
        }
        finally {
            System.gc();
            Runtime.getRuntime().gc();
        }
    }

    public static void CloseConnection() throws InterruptedException, ConnectionException {
        try {
            if((Connection.isConnected() ) || ( Connection != null)) {
                Printer = null;
                Connection.close();
                Thread.sleep(1000);
            }
        }
        catch(ConnectionException e) {
            throw e;
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            throw e;
        }
        finally {
            System.gc();
            Runtime.getRuntime().gc();
        }
    }
}
