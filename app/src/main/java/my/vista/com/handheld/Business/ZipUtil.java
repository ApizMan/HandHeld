package my.vista.com.handheld.Business;

import android.util.Log; 
import java.io.BufferedInputStream; 
import java.io.BufferedOutputStream; 
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream; 
  
public class ZipUtil { 
  private static final int BUFFER = 2048; 
 
  private String[] _files; 
  private String _zipFile; 
 
  public ZipUtil(String[] files, String zipFile) { 
    _files = files; 
    _zipFile = zipFile; 
  } 
 
  public void zip() { 
    try  { 
      BufferedInputStream origin = null; 
      FileOutputStream dest = new FileOutputStream(_zipFile); 
 
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
 
      byte data[] = new byte[BUFFER]; 
 
      for(int i=0; i < _files.length; i++) { 
        Log.v("Compress", "Adding: " + _files[i]); 
        FileInputStream fi = new FileInputStream(_files[i]); 
        origin = new BufferedInputStream(fi, BUFFER); 
        ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1)); 
        out.putNextEntry(entry); 
        int count;
        while ((count = origin.read(data, 0, BUFFER)) != -1) { 
          out.write(data, 0, count); 
        } 
        origin.close(); 
      } 
 
      out.close(); 
    } catch(Exception e) { 
      e.printStackTrace(); 
    } 
 
  } 
  
  private String _location; 

  public ZipUtil(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location;

    _dirChecker(""); 
  } 

  public void unzip() { 
    try  { 
      FileInputStream fin = new FileInputStream(_zipFile); 
      ZipInputStream zin = new ZipInputStream(fin); 
      ZipEntry ze = null; 
      while ((ze = zin.getNextEntry()) != null) { 
        Log.v("Decompress", "Unzipping " + ze.getName()); 

        if(ze.isDirectory()) { 
          _dirChecker(ze.getName()); 
        } else { 
          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
          for (int c = zin.read(); c != -1; c = zin.read()) { 
            fout.write(c); 
          } 

          zin.closeEntry(); 
          fout.close(); 
        } 

      } 
      zin.close(); 
    } catch(Exception e) { 
      Log.e("Decompress", "unzip", e); 
    } 

  } 

  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 

    if(!f.isDirectory()) { 
      f.mkdirs(); 
    }
  }
} 