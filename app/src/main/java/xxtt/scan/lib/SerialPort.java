package xxtt.scan.lib;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPort {
    private static final String TAG = "SerialPort";

    /*
     * Do not remove or rename the field mFd: it is used by native method
     * close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private boolean trig_on = false;

    static {
	System.load("/data/data/xxtt.scan/lib/libdevapi.so");
	System.load("/data/data/xxtt.scan/lib/libSerialPort.so");
    }

    public SerialPort(int port, int baudrate, int flags)
	    throws SecurityException, IOException {

	mFd = open(port, baudrate);
	if (mFd == null) {
	    Log.e(TAG, "native open returns null");
	    throw new IOException();
	}
	mFileInputStream = new FileInputStream(mFd);
	mFileOutputStream = new FileOutputStream(mFd);

    }

    // Getters and setters
    public InputStream getInputStream() {
	return mFileInputStream;
    }

    public OutputStream getOutputStream() {
	return mFileOutputStream;
    }

    public void zigbee_poweron() {
	zigbeepoweron();
    }

    public void printer_poweron() {

    }

    public void printer_poweroff() {

    }

    public void psam_poweron() {
	psampoweron();
    }

    public void psam_poweroff() {
	psampoweroff();
	// scaner_trigoff();
    }

    public void scaner_poweron() {
	scanerpoweron();
	scaner_trigoff();
    }

    public void scaner_poweroff() {
	scanerpoweroff();
	// scaner_trigoff();
    }

    public void scaner_trigon() {
	scanertrigeron();
	trig_on = true;
    }

    public void scaner_trigoff() {
	scanertrigeroff();
	trig_on = false;
    }

    public boolean scaner_trig_stat() {
	return trig_on;
    }
    // JNI

    private native static FileDescriptor open(int port, int baudrate);

    public native void close(int port);

    public native void zigbeepoweron();

    public native void scanerpoweron();

    public native void scanerpoweroff();

    public native void psampoweron();

    public native void psampoweroff();

    public native void scanertrigeron();

    public native void scanertrigeroff();

    static {
	// System.loadLibrary("/data/data/com.example.uartdemo/lib/devapi.so");
	// System.loadLibrary("/data/data/com.example.uartdemo/lib/serialport.so");
    }
}
