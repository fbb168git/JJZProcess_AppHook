package com.fbb.xposed;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by fengbb on 2017/12/26.
 */

public class MainConnection {
    public static final String TAG = "MainConnection";

    static class ServerThread extends Thread {
        boolean isLoop = true;
        public void setIsLoop(boolean isLoop) {
            this.isLoop = isLoop;
        }
        @Override
        public void run() {
            XposedBridge.log("running");
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(9000);
                while (isLoop) {
                    Socket socket = serverSocket.accept();
                    Log.d(TAG, "accept");
                    XposedBridge.log("accept");
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    String value = inputStream.readUTF();

                    String sign = SignUtil.sign(value);

                    outputStream.writeUTF(sign);
                    outputStream.flush();
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                XposedBridge.log("Exception");
            } finally {
                Log.d(TAG, "destory");
                XposedBridge.log("openConnect");
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static ServerThread serverThread;
    public static void openConnect() {
        XposedBridge.log("openConnect");
        if(serverThread == null || !serverThread.isAlive()){
            serverThread = new ServerThread();
            serverThread.start();
        }
    }

    public static void closeConnect(){
        serverThread.setIsLoop(false);

    }
}
