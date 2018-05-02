package com.ansari.medicalpump.network;

import android.util.Log;

import com.ansari.medicalpump.App;
import com.ansari.medicalpump.network.structs.mp.MPDeviceStatus;
import com.ansari.medicalpump.network.structs.mp.MPExecute;
import com.ansari.medicalpump.network.structs.mp.MPInjectionMode;
import com.ansari.medicalpump.network.structs.mp.MPInjectionSpeed;
import com.ansari.medicalpump.network.structs.mp.MPInjectionVolume;
import com.ansari.medicalpump.network.utils.PacketUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class NetworkManager {

    private static NetworkManager instance;
    private String server_address;
    private int server_port;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private OnDataReceivedListener listener;
    private BlockingQueue<byte[]> sendQueue = new ArrayBlockingQueue<>(20);
    private Thread sendThread = null;
    private Thread connectThread = null;

    private boolean sendNext = true;

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                while (!sendNext)
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                try {
                    out = socket.getOutputStream();
                    byte[] data = sendQueue.take();
                    out.write(data, 0, data.length);
                    sendNext = false;
                } catch (Exception e) {
                    Log.e(NetworkManager.class.getName(), "Send Failure: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        }
    };
    private Runnable connectAndReceiveRunnable = new Runnable() {
        @Override
        public void run() {
            int bufferSize;
            byte[] buffer;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(server_address, server_port), 5000);


                bufferSize = socket.getReceiveBufferSize();
                in = socket.getInputStream();
                out = socket.getOutputStream();

                NetworkManager.this.listener.onConnectionCreated();

                ByteArrayOutputStream byteArrayOutputStream;


                while (true) {

                    buffer = new byte[bufferSize];
                    byteArrayOutputStream = new ByteArrayOutputStream(1024);

                    int bytesRead = 0;
                    while ((bytesRead = in.read(buffer)) > 0) {

                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        if (in.available() <= 0)
                            break;
                    }

                    parsePacket(buffer);
                }


            } catch (IOException e) {
                Log.e(NetworkManager.class.getName(), "IO Exception: " + e.getMessage());
            } catch (Exception e) {
                Log.e(NetworkManager.class.getName(), "General Failure: " + e.getMessage());
            }
        }
    };

    public static NetworkManager getInstance(OnDataReceivedListener _listener) {

        if (instance == null) {
            return new NetworkManager(_listener);
        }
        return instance;
    }

    private NetworkManager(OnDataReceivedListener _listener) {
        instance = this;
        this.listener = _listener;
        server_address = NetConfig.DEVICE_IP;
        server_port = NetConfig.DEVICE_PORT;
    }

    public void connect() {

        if (connectThread == null || !connectThread.isAlive()) {
            Log.v(NetworkManager.class.getName(), "start new connection thread");
            connectThread = new Thread(connectAndReceiveRunnable);
            connectThread.start();
        }

    }


    public void send(final byte[] dataToSend) {

        try {
            sendQueue.put(dataToSend);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (sendThread == null || !sendThread.isAlive()) {
            Log.v(NetworkManager.class.getName(), "start new send thread");

            sendThread = new Thread(sendRunnable);
            sendThread.start();
        }
    }


    private void parsePacket(final byte[] receivedData) {

        try {
            final AbstractPacket abs = new AbstractPacket().toObject(receivedData);
            sendNext = true;
            App.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        switch (PacketUtility.convertTwoBytesToIntAddressViceVerca(abs.add_var)) {
                            case MPExecute.ADDRESS:
                                if (abs.cmd == AbstractPacket.WRITE_CMD)
                                    NetworkManager.this.listener.onWriteMPExecute(new MPExecute().toObject(receivedData));
                                else
                                    NetworkManager.this.listener.onReadMPExecute(new MPExecute().toObject(receivedData));
                                break;
                            case MPInjectionSpeed.ADDRESS:
                                if (abs.cmd == AbstractPacket.WRITE_CMD)
                                    NetworkManager.this.listener.onWriteInjectionSpeed(new MPInjectionSpeed().toObject(receivedData));
                                else
                                    NetworkManager.this.listener.onReadInjectionSpeed(new MPInjectionSpeed().toObject(receivedData));
                                break;
                            case MPInjectionVolume.ADDRESS:
                                if (abs.cmd == AbstractPacket.WRITE_CMD)
                                    NetworkManager.this.listener.onWriteInjectionVolume(new MPInjectionVolume().toObject(receivedData));
                                else
                                    NetworkManager.this.listener.onReadInjectionVolume(new MPInjectionVolume().toObject(receivedData));
                                break;
                            case MPDeviceStatus.ADDRESS:
                                NetworkManager.this.listener.onReadDeviceStatus(new MPDeviceStatus().toObject(receivedData));
                                break;
                            case MPInjectionMode.ADDRESS:
                                NetworkManager.this.listener.onReadInjectionMode(new MPInjectionMode().toObject(receivedData));
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void restart() {
        Log.v(NetworkManager.class.getName(), "Restart ...");

        shutDown();
        connect();
    }

    public void shutDown() {

        Log.v(NetworkManager.class.getName(), "shut down");

        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (out != null) {
                out.flush();
                out.close();
                out = null;
            }

            if (in != null) {
                in.close();
                in = null;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

