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
        PrintingDocument doc = new PrintingDocument("2650");
        //PrintingDocument doc = new PrintingDocument("2000");
        if(info.IsClamping.equalsIgnoreCase("True")) {
            doc = new PrintingDocument("2700");
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

            doc.DrawImageName(-5,0,"small.png");

            doc.DrawTextFlow(150, 60, 40, 670, "C", "MAJLIS BANDARAYA KUANTAN");
            doc.DrawText(200, 50, 25,"NOTIS KESALAHAN SERTA TAWARAN MENGKOMPAUN");
            doc.DrawText(240, 30, 25,"DI BAWAH PERINTAH PENGANGKUTAN JALAN");
            doc.DrawText(220, 30, 25,"(PERUNTUKAN MENGENAI TEMPAT LETAK KERETA)");
            doc.DrawText(270, 30, 25,"MAJLIS PERBANDARAN KUANTAN 2005");

            doc.DrawBarcode128(10, 75, 70, info.NoticeSerialNo);
            doc.DrawBarcode128(500, 0, 70, "H76255");

            doc.DrawText(10, 100, 30,   "NO. KOMPAUN");
            doc.DrawText(500, 0, 30, "KOD HASIL");

            doc.DrawText(10, 40, 35, info.NoticeSerialNo);
            doc.DrawText(500, 0, 35, "H76255");

            doc.DrawText(10, 60, 20, "TARIKH");
            doc.DrawText(230, 0, 25, ": " + CacheManager.GetDateString(info.OffenceDateTime));
            doc.DrawText(10, 30, 20, "WAKTU");
            doc.DrawText(230, 0, 25, ": " + CacheManager.GetTimeString(info.OffenceDateTime));

            doc.DrawText(10, 50, 30, "Kepada Pemandu / Pemilik Kenderaan :");

            doc.DrawText(10, 35, 20, "NO KENDERAAN");
            doc.DrawText(230, 0, 25, ": " + info.VehicleNo);
            doc.DrawText(10, 30, 20, "JENIS KENDERAAN");
            doc.DrawText(230, 0, 25, ": " + info.VehicleType);

            doc.DrawText(10, 30, 20, "NO CUKAI");
            doc.DrawText(230, 0, 25, ": " + info.RoadTaxNo);

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

            doc.DrawText(10, 30, 20, "ZON");
            doc.DrawText(230, 0, 25, ": " + info.OffenceLocationArea);

            String location = "";
            if(info.OffenceLocation.length() > 0)
                location = info.OffenceLocation;
            else
                location = info.SummonLocation;

            doc.DrawText(10, 30, 20, "LOKASI");
            doc.DrawText(230, 0, 25, ": " + location);

            doc.DrawText(10, 30, 20, "NO PETAK");
            doc.DrawText(230, 0, 25, ": " + "");

            doc.DrawTextFlow(0, 60, 30, 800, "J",
                    "SILA AMBIL PERHATIAN BAHAWA TUNA/PUAN SEPERTIMANA " +
                            "TARIKH DAN WAKTU YANG DINYATAKAN TUAN/PUAN TELAH " +
                            "DIDAPATI MELAKUKAN KESALAHAN SEPERTI BERIKUT: ");

            doc.DrawText(0, 120, 20, "PERUNTUKAN UNDANG-UNDANG :");
            doc.DrawText(50, 30, 25, info.OffenceAct);

            doc.DrawText(0, 60, 20, "SEKSYEN / KAEDAH / PERINTAH :");
            doc.DrawText(50, 30, 25, "PERINTAH " + info.ResultCode);

            doc.DrawText(0, 60, 20, "KESALAHAN :");
            doc.DrawTextFlow(50, 30, 25, 700, "J", info.Offence);

            doc.DrawText(0, 150, 20, "DIKELUARKAN OLEH");
            doc.DrawText(230, 0, 25, ": " + CacheManager.officerId);

            doc.DrawText(0, 30, 20, "KOD SAKSI");
            doc.DrawText(230, 0, 25, ": " + "TIADA");


            doc.DrawTextFlow(0, 130, 25, 400, "C",
                    ".....................................................\\&" +
                            "b.p. DATUK BANDAR\\&" +
                            "MAJLIS BANDARAYA KUANTAN");


            doc.DrawText(0, 100, 25, "-----------------------------------------------");

            doc.DrawText(0, 50, 23, "NO. KOMPAUN");
            doc.DrawText(230, 0, 23, ": " + info.NoticeSerialNo);

            doc.DrawText(420, 0, 23, "SEKSYEN KESALAHAN");
            doc.DrawText(680, 0, 23, ": " + info.ResultCode);

            doc.DrawText(0, 30, 23, "TARIKH");
            doc.DrawText(230, 0, 23, ": " + CacheManager.GetDateString(info.OffenceDateTime));

            doc.DrawText(420, 0, 23, "KOD HASIL");
            doc.DrawText(680, 0, 23, ": " + "H76255");

            doc.DrawText(0, 30, 23, "NO KENDERAAN");
            doc.DrawText(230, 0, 23, ": " + info.VehicleNo);

            doc.DrawTextFlow(0, 60, 30, 800, "J",
                    "TAWARAN UNTUK MENGKOMPAUN KESALAHAN INI " +
                            "BERKUATKUASA DARI TARIKH NOTIS INI DIKELUARKAN. JIKA " +
                            "SEKIRANYA KOMPAUN INI TIDAK DIJELASKAN DALAM TEMPOH " +
                            "TERSEBUT MAKA TINDIKAN UNDANG - UNDANG AKAN " +
                            "DITERUSKAN. ");

            if (info.CompoundAmountDesc1 != null && info.CompoundAmountDesc1.length() != 0) {
                doc.DrawTextFlow(0, 200, 30, 400, "C", info.CompoundAmountDesc1);
                if (info.CompoundAmountDesc2 != null && info.CompoundAmountDesc2.length() != 0) {
                    doc.DrawTextFlow(400, 0, 30, 400, "C", info.CompoundAmountDesc2);
                }
            }
            if (info.CompoundAmountDesc1 != null && info.CompoundAmountDesc1.length() != 0) {
                doc.DrawTextFlow(0, 30, 40, 400, "C", "RM " + String.format("%.2f", info.CompoundAmount1));
                if (info.CompoundAmountDesc2 != null && info.CompoundAmountDesc2.length() != 0) {
                    doc.DrawTextFlow(400, 0, 40, 400, "C", "RM " + String.format("%.2f", info.CompoundAmount2));
                }
            }

            doc.DrawTextFlow(0, 60, 28, 800, "J",
                    "Tempoh bayaran kompaun dikira dari tarikh kesalahan dilakukan " +
                            "termasuk hari ahad dan hari kelepasan Am ");

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
