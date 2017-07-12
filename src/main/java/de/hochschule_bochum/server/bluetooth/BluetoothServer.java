package de.hochschule_bochum.server.bluetooth;

import com.intel.bluetooth.BlueCoveImpl;
import de.hochschule_bochum.server.Callback;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 06.07.2017.
 */
public class BluetoothServer {
    private StreamConnection sc;
    private String name;
    private ReceiveThread receiveThread;
    private SendThread sendThread;
    private boolean connected = false;

    public BluetoothServer(String name) {
        this.name = name;
    }

    public boolean startServer(Callback<String> receiveCallback, Callback<RemoteDevice> disconnectCallback) {
        try {
            //Create a UUID for SPP
            UUID uuid = new UUID("2220b4c4b5314002a17fbd180a62cdf5", false);
            //Create the servicve url
            String connectionString = "btspp://localhost:" + uuid + ";name=" + name;

            //open server url
            StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

            //Wait for client connection
            System.out.println("\nBluetoothServer Started. Waiting for clients to connect...");
            sc = streamConnNotifier.acceptAndOpen();

            RemoteDevice dev = RemoteDevice.getRemoteDevice(sc);

            System.out.println("Remote device address: " + dev.getBluetoothAddress());
            System.out.println("Remote device name: " + dev.getFriendlyName(true));

            receiveThread = new ReceiveThread(sc.openInputStream(), receiveCallback);
            receiveThread.start();

            sendThread = new SendThread(sc.openOutputStream());
            sendThread.start();

            connected = true;

            Timer watchtimer = new Timer();
            watchtimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        RemoteDevice.getRemoteDevice(sc);
                    } catch (IOException ignored) {
                        connected = false;
                        disconnectCallback.callback(dev);
                        close();
                        cancel();
                    }

                }
            }, 1000, 1000);

            return true;
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public void close() {
        if (sc != null) {
            try {
                sendThread.interrupt();
                receiveThread.interrupt();
                sc.close();
                BlueCoveImpl.shutdown();
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(String msg) {
        sendThread.send(msg);
    }

    public class SendThread extends Thread {

        private PrintWriter printWriter;

        public SendThread(OutputStream output) {
            printWriter = new PrintWriter(new OutputStreamWriter(output));
        }

        public void send(String message) {
            printWriter.write(message + "\n");
            printWriter.flush();
        }

        @Override
        public void interrupt() {
            super.interrupt();
            printWriter.flush();
            printWriter.close();
        }
    }

    public class ReceiveThread extends Thread {

        private BufferedReader buffR;
        private Callback<String> callback;

        public ReceiveThread(InputStream in, Callback<String> callback) {
            buffR = new BufferedReader(new InputStreamReader(in));
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                String lastInput;
                while ((lastInput = buffR.readLine()) != null) {
                    callback.callback(lastInput);
                }
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            try {
                buffR.close();
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public static void main(String[] args) {
        BluetoothServer bluetoothServer = new BluetoothServer("HS_BOCHUM");
        bluetoothServer.startServer(System.out::println, device -> System.out.println(device.getBluetoothAddress() + " disconnected"));
        while (bluetoothServer.isConnected()) {
            bluetoothServer.send("New Message!");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
