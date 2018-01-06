package com.project.jsh.androidprj;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class APIThread extends Thread {
    final private String callback_url1 = "http://apis.data.go.kr/B552657/ErmctInfoInqireService/getEgytListInfoInqire"; // emergency gps (n3)
    final private String callback_url2 = "http://apis.data.go.kr/B552657/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire"; // emergency room realtime information (n1) for PagerTwo class
    final private String callback_url3 = "http://apis.data.go.kr/B552657/ErmctInfoInqireService/getEgytBassInfoInqire"; // emergency room realtime information (n5) for CardContent class
    final private String key = "Your Key";
    // Put your key!
    //====================================================================================================

    private Handler thHandler = null;
    private Handler fgHandler = null;
    private String xml = "";
    private String hid = ""; // for CardContent (more detail information)
    private static String Q0 = "";
    private static String Q1 = "";
    private static double latitude = 0;
    private static double longitude = 0;
    private URL url = null;
    private BufferedReader in = null;
    private String inLine = "";

    public Handler getFgHandler() {
        return thHandler;
    }

    public APIThread(Handler h) {
        fgHandler = h;
    }

    public APIThread(Handler h, String hid) {
        fgHandler = h;
        this.hid = hid;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    //====================================================================================================

    @Override
    public void run() {

        if(hid.isEmpty()) {
            Looper.prepare();
            thHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Message retMsg = new Message();
                    switch (msg.what) {
                        case 1: // Take emergency room gps
                            Log.d("THREAD", "thread1 start");
                            xml = "";
                            String[] split = msg.obj.toString().split("&");
                            try {
                                Q0 = URLEncoder.encode(split[0], "UTF-8");
                                Q1 = URLEncoder.encode(split[1], "UTF-8");
                                latitude = Double.parseDouble(split[2]);
                                longitude = Double.parseDouble(split[3]);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error1");
                            }

                            try {
                                url = new URL(callback_url1 + key + "&" + "Q0=" + Q0 + "&" + "Q1=" + Q1);
                                Log.d("URL", url + "<-");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error2");

                            }

                            try {
                                in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error3");
                            }

                            try {
                                while ((inLine = in.readLine()) != null) {
                                    xml = inLine;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THEREAD_ERR", "thread_error4");
                            }

                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thrread_error5");
                            }

                            Log.d("XMLRET", xml + "<-");
                            retMsg.obj = xml;
                            fgHandler.sendMessage(retMsg);
                            break;

                        //====================================================================================================

                        case 2: // Take emergency room information
                            Log.d("THREAD", "thread2 start");
                            xml = "";

                            if(Q0.isEmpty() || Q1.isEmpty())
                                break;

                            try {
                                url = new URL(callback_url2 + key + "&" + "STAGE1=" + Q0 + "&" + "STAGE2=" + Q1);
                                Log.d("URL", url + "<-");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error1");
                            }

                            try {
                                in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thread error2");
                            }

                            try {
                                while ((inLine = in.readLine()) != null) {
                                    xml += inLine;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THEREAD_ERR", "thread_error3");
                            }

                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("THREAD_ERR", "thrread_error4");
                            }

                            Log.d("XMLRET", xml + "<-");
                            retMsg.what = 2;
                            retMsg.obj = xml;
                            fgHandler.sendMessage(retMsg);

                            break;

                        //====================================================================================================
                    }
                }
            };
            Looper.loop();
        }
        else {  // It is not looper thread.
            xml = "";
            String inLine = "";
            Message retMsg = new Message();
            Log.d("THREAD", "thread3 start");

            try {
                url = new URL(callback_url3 + key + "&" + "HPID=" + hid);
                Log.d("URL", url + "<-");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("THREAD_ERR", "thread error1");
            }

            try {
                in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("THREAD_ERR", "thread error2");
            }

            try {
                while ((inLine = in.readLine()) != null) {
                    xml += inLine;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("THEREAD_ERR", "thread_error3");
            }

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("THREAD_ERR", "thrread_error4");
            }

            Log.d("XMLRET", xml + "<-");
            retMsg.what = 3;
            retMsg.obj = xml;
            fgHandler.sendMessage(retMsg);
        }
    }
}
