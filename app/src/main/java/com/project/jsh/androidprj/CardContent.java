package com.project.jsh.androidprj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class CardContent extends AppCompatActivity implements View.OnClickListener{
    private TextView[]text = new TextView[5];
    private int []txt = {R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5};
    private RecyclerView ContentRecyclerView;
    private static final int REQUEST_PHONE_CALL = 1;
    private APIThread apiThread;
    private Message msg;
    private DocumentBuilderFactory t_dbf = null;
    private DocumentBuilder t_db = null;
    private Document t_doc = null;
    private NodeList t_nodes = null;
    private Node t_node = null;
    private Element t_element = null;
    private InputSource t_is = new InputSource();
    private String hid;
    private String inf;
    private String temp;
    List<Item> items = new ArrayList<>();

    private Handler mHnadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try
            {
                t_is.setCharacterStream(new StringReader(msg.obj.toString()));
                t_doc = t_db.parse(t_is);
                t_nodes = t_doc.getElementsByTagName("item");
                t_node = t_nodes.item(0);
                t_element = (Element)t_node;
                text[2].setText(text[2].getText().toString() + t_element.getElementsByTagName("dutyAddr").item(0).getTextContent());
                double lat =  Double.parseDouble(t_element.getElementsByTagName("wgs84Lat").item(0).getTextContent());
                double logt =  Double.parseDouble(t_element.getElementsByTagName("wgs84Lon").item(0).getTextContent());
                text[3].setText(text[3].getText().toString() + Math.round(distance(apiThread.getLatitude(), apiThread.getLongitude(),lat,logt)*100)/100.0 + "km");
                ContentRecyclerView.setAdapter(new CardAdapter(getApplicationContext(), items, R.layout.activity_card_content)); // 밑에 태그 없을 시 종료되므로 일단 한 번 출력
                temp = t_element.getElementsByTagName("MKioskTy1").item(0).getTextContent();
                if(temp != null && temp.equals("Y"))
                    items.add(new Item("뇌출현 수술 가능"));
                temp = t_element.getElementsByTagName("MKioskTy2").item(0).getTextContent();
                if(temp != null && temp.equals("Y"))
                    items.add(new Item("뇌경색 재관류 가능"));
                temp = t_element.getElementsByTagName("MKioskTy3").item(0).getTextContent();
                if(temp != null && temp.equals("Y"))
                    items.add(new Item("심근경색 재관류 가능"));
                temp = t_element.getElementsByTagName("MKioskTy4").item(0).getTextContent();
                if(temp != null && temp.equals("Y"))
                    items.add(new Item("복부손상 수술 가능 가능"));
                temp = t_element.getElementsByTagName("MKioskTy5").item(0).getTextContent();
                if(temp != null && temp.equals("Y"))
                    items.add(new Item("사지접합 수술 가능 가능"));
                ContentRecyclerView.setAdapter(new CardAdapter(getApplicationContext(), items, R.layout.activity_card_content));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_content);
        Log.d("LifeCycle", "CREATE");

        try {
            t_dbf = DocumentBuilderFactory.newInstance();
            t_db = t_dbf.newDocumentBuilder();
            t_is = new InputSource();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        inf = this.getIntent().getStringExtra("information");
        StringTokenizer st = new StringTokenizer(inf, "&&");
        hid = st.nextToken();


        apiThread = new APIThread(mHnadler, hid);
        apiThread.setDaemon(true);
        apiThread.start();

        for(int i = 0 ; i < text.length ; i++)
            text[i] = (TextView)findViewById(txt[i]);
        text[1].setOnClickListener(this);

        temp = st.nextToken();
        text[0].setText(text[0].getText().toString() + temp);
        temp = st.nextToken();
        text[1].setText(text[1].getText().toString() + temp);
        temp = st.nextToken();
        text[4].setText(text[4].getText().toString() + temp);
        if(temp.equals("Y"))
            text[4].setBackgroundResource(R.drawable.cardlist_style_positive);

        int check=0;
        while(st.hasMoreTokens()) {
            temp = st.nextToken();
            if(Integer.parseInt(temp.toString()) > 0) {
                switch (check) {
                    case 0:
                        temp = "내과중환자실 \n" + temp;
                        break;
                    case 1:
                        temp = "외과중환자실 \n" + temp;
                        break;
                    case 2:
                        temp = "외과입원실(정형외과) \n" + temp;
                        break;
                    case 3:
                        temp = "신경과입원실 \n" + temp;
                        break;
                    case 4:
                        temp = "신경외과중환자실 \n" + temp;
                        break;
                    case 5:
                        temp = "약물중환자 \n" + temp;
                        break;
                    case 6:
                        temp = "화상중환자 \n" + temp;
                        break;
                    case 7:
                        temp = "외상중환자 \n" + temp;
                        break;
                }
                items.add(new Item(temp));
            }
            check++;
        }
        ContentRecyclerView = (RecyclerView)findViewById(R.id.ContentRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        ContentRecyclerView.setHasFixedSize(true);
        ContentRecyclerView.setLayoutManager(layoutManager);


//        병원명 + 응급실전화 + 구급차가용여부 + 내과중환자실 + 외과중환자실 + 외과입원실(정형외과) + 신경과입원실 + 신경외과중환자실 + 약물중환자 + 화상중환자 + 외상중환자
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tv2) {
            String c_number = text[1].getText().toString().split("\n")[1].replaceAll("-","");
            Intent intent  = new Intent("android.intent.action.CALL", Uri.parse("tel:" + c_number));

            if (ContextCompat.checkSelfPermission(CardContent.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CardContent.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
            }
            else
            {
                startActivity(intent);
            }
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
