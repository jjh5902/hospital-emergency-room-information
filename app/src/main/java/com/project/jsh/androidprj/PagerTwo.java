package com.project.jsh.androidprj;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class PagerTwo extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private Button btn;
    private TextView txt;
    private APIThread apiThread;
    private Message msg;
    private DocumentBuilderFactory t_dbf = null;
    private DocumentBuilder t_db = null;
    private Document t_doc = null;
    private NodeList t_nodes = null;
    private Node t_node = null;
    private Element t_element = null;
    private InputSource t_is = new InputSource();

    private Handler mHnadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("threadreach", "here?");
            try
            {
                String str = "";
                String inf = "";
                t_is.setCharacterStream(new StringReader(msg.obj.toString()));
                t_doc = t_db.parse(t_is);
                t_nodes = t_doc.getElementsByTagName("item");
                List<Item> items = new ArrayList<>();
                Item[] card = new Item[t_nodes.getLength()];
                for(int i=0 ; i < t_nodes.getLength() ; i++){
                    t_node = t_nodes.item(i);
                    t_element = (Element)t_node;
                    str += "병원명 : " + t_element.getElementsByTagName("dutyName").item(0).getTextContent() + "\n";
                    str += "응급실 : " + t_element.getElementsByTagName("hvec").item(0).getTextContent() + "\n";
                    str += "수술실 : " + t_element.getElementsByTagName("hvoc").item(0).getTextContent() + "\n";
                    str += "구급차가용여부 : " + t_element.getElementsByTagName("hvamyn").item(0).getTextContent() + "\n";
                    inf += t_element.getElementsByTagName("hpid").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("dutyName").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("dutyTel3").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hvamyn").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv2").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv3").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv4").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv5").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv6").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv7").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv8").item(0).getTextContent() + "&&";
                    inf += t_element.getElementsByTagName("hv9").item(0).getTextContent() + "&&";

                    // hvcc, hvccc, hvicc (신경중환자, 신생중환자, 일반중환자는 호출시 오류 처리됨)
                    // inf 순서 : 기관코드 + 병원명 + 응급실전화 + 구급차가용여부 + 내과중환자실 + 외과중환자실 + 외과입원실(정형외과) + 신경과입원실 + 신경외과중환자실 + 약물중환자 + 화상중환자 + 외상중환자

                    card[i] = new Item(str, inf);
                    items.add(card[i]);
                    str = "";
                    inf = "";
                }
                recyclerView.setAdapter(new CardAdapter(getActivity().getApplicationContext(), items, R.layout.fragment_pager_two, getActivity()));

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager_two, container, false);

        btn = (Button)view.findViewById(R.id.button2);
        btn.setOnClickListener(this);

        try {
            t_dbf = DocumentBuilderFactory.newInstance();
            t_db = t_dbf.newDocumentBuilder();
            t_is = new InputSource();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        apiThread = new APIThread(mHnadler);
        apiThread.setDaemon(true);
        apiThread.start();

        recyclerView = (RecyclerView)view.findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button2) {
            msg = new Message();
            msg.what=2;
            apiThread.getFgHandler().sendMessage(msg);

        }
    }
}
