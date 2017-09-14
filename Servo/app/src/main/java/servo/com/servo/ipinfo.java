package servo.com.servo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class ipinfo extends AppCompatActivity {
    static String ip;
    String para1;
    String para2;
    String para11;
    String para21;

    ProgressBar PB;

    String[] oids = {"1.3.6.1.2.1.1.1","1.3.6.1.2.1.1.2","1.3.6.1.2.1.1.3","1.3.6.1.2.1.1.5",
            "1.3.6.1.2.1.1.6","1.3.6.1.2.1.1.7","1.3.6.1.2.1.2.1","1.3.6.1.2.1.4.3","1.3.6.1.2.1.4.9","1.3.6.1.2.1.4.21.1.7",
    "1.3.6.1.2.1.25.2.2"};
    String[] oidsdesc = {"Systeme description","systeme ID","time work","Node name","location",
            "Services","network interfaces","Ip received","Ip delivered","Next hop","Host storage"};
    int iterator=0;
    String snmpfinale="";
    String sysid;
    String services = null;
    int servicesid;
    int iprec=0;
    int ipsucc=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_info);
        Intent intent = getIntent();
        ip = intent.getStringExtra(MainActivity.ip);
        para1 = intent.getStringExtra(MainActivity.par1);
        para2 = intent.getStringExtra(MainActivity.par2);
        para11 = intent.getStringExtra(MainActivity.par11);
        para21 = intent.getStringExtra(MainActivity.par21);

        LinearLayout llload = (LinearLayout) findViewById(R.id.main);
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l.gravity = Gravity.CENTER_HORIZONTAL;
        l.setMargins(0, 200, 0, 0);
        PB = new ProgressBar(this);
        PB.setIndeterminate(true);
        llload.addView(PB, l);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pingshow();
                }
            }, 1000);
        }else{
            //need conection hundler
        }
    }

    public void pingshow(){
        LinearLayout ll = (LinearLayout) findViewById(R.id.main);
        ll.removeAllViews();
        LinearLayout.LayoutParams layoutparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        String res = ping(ip);
        System.out.println(res);
        String recived = res.substring(res.indexOf("received")-2,res.indexOf("received")-1);
        TableRow tr = new TableRow(this);
        TextView tv = new TextView(this);
        if (!(res.contains("100%")) ){
            tv.setText("");
            tv.setBackgroundResource(R.drawable.ipstatuson);
            textparam.width=70;
            textparam.height=70;
            textparam.setMargins(30,11,5,5);
            textparam.gravity = Gravity.LEFT;
            textparam.gravity = Gravity.CENTER_VERTICAL;
            tr.addView(tv,textparam);
        } else {
            tv.setBackgroundResource(R.drawable.ipstatus);
            tv.setText("");
            textparam.width=70;
            textparam.height=70;
            textparam.setMargins(30,11,5,5);
            textparam.gravity = Gravity.LEFT;
            textparam.gravity = Gravity.CENTER_VERTICAL;
            tr.addView(tv,textparam);
        }
        tv = new TextView(this);
        tv.setText(ip);
        textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        textparam.setMargins(20,5,5,5);
        textparam.gravity = Gravity.CENTER_VERTICAL;
        tr.addView(tv,textparam);

        textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        if(!(res.contains("100%"))){
            tv = new TextView(this);
            tv.setText("(connected)");
            textparam.setMargins(15,15,15,15);
            textparam.gravity = Gravity.CENTER_VERTICAL;
            tr.addView(tv,textparam);
        } else {
            tv = new TextView(this);
            tv.setText("(disconnected)");
            textparam.setMargins(15,15,15,15);
            textparam.gravity = Gravity.CENTER_VERTICAL;
            tr.addView(tv,textparam);
        }

        tableparam.setMargins(0,0,0,0);
        LinearLayout lll = new LinearLayout(this);
        lll.setBackgroundResource(R.drawable.linearlayoutdisp);
        lll.addView(tr,tableparam);

        tv = new TextView(this);
        tv.setText(para1+" : "+para11+"\n"+para2+" : "+para21);
        textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        textparam.setMargins(120,5,5,10);
        textparam.gravity = Gravity.CENTER_VERTICAL;

        tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tr = new TableRow(this);
        tr.addView(tv,textparam);

        if(!(res.contains("100%"))) {
            tv = new TextView(this);
            tv.setText("delay : " + res.split("\n")[Integer.parseInt(recived) + 4].split("=")[1].split("/")[1]+"ms");
            textparam = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            textparam.setMargins(200, 5, 5, 5);
            textparam.gravity = Gravity.RIGHT;
            tr.addView(tv, textparam);
        }else{
            tv = new TextView(this);
            tv.setText("delay : NAN");
            textparam = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            textparam.setMargins(200, 5, 5, 5);
            textparam.gravity = Gravity.RIGHT;
            tr.addView(tv, textparam);
        }
        tableparam.setMargins(0,0,0,0);
        lll.addView(tr,tableparam);

        layoutparam.setMargins(16,16,16,16);
        layoutparam.setLayoutDirection(LinearLayout.VERTICAL);
        lll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(lll,layoutparam);
        stattime(res);
    }

    public void stattime(String res){
        LinearLayout ll = (LinearLayout) findViewById(R.id.main);
        LinearLayout.LayoutParams layoutparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        LinearLayout lll = new LinearLayout(this);
        lll.setBackgroundResource(R.drawable.linearlayoutdisp);


        TextView tv = new TextView(this);
        tv.setText("ping delays :");
        textparam.gravity=Gravity.LEFT;
        textparam.gravity=Gravity.CENTER_VERTICAL;
        textparam.setMargins(25,20,5,5);
        TableRow tr = new TableRow(this);
        tr.addView(tv,textparam);
        tableparam.setMargins(0,0,0,0);
        lll.addView(tr,tableparam);


        GraphView graph =  new GraphView(this);
        DataPoint[] dataPoints = new DataPoint[6];
        int line = 1;
        if(!(res.contains("100%")) ){
            for(int j = 0;j<6;j++){
                if(!res.split("\n")[line].equals("")){
                    if(Integer.parseInt(res.split("\n")[line].split(" ")[4].split("=")[1]) == j+1){
                        dataPoints[j] = new DataPoint(j,Float.parseFloat(res.split("\n")[line].split(" ")[6].split("=")[1]));
                        line++;
                    }else{
                        dataPoints[j] = new DataPoint(j,0);
                    }
                }else{
                    dataPoints[j] = new DataPoint(j,0);
                }
            }
        }else{
            for(int j = 0;j<6;j++){
                dataPoints[j] = new DataPoint(j,0);
            }
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.addSeries(series);

        tr = new TableRow(this);
        textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        textparam.setMargins(5,5,5,5);
        textparam.height=ll.getHeight();
        textparam.gravity = Gravity.CENTER_VERTICAL;
        tr.addView(graph,textparam);

        tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tableparam.setMargins(0,0,0,0);
        lll.addView(tr,tableparam);

        tv = new TextView(this);
        tv.setText(res.split("\n")[res.split("\n").length-2].split(",")[0]+" "+res.split("\n")[res.split("\n").length-2].split(",")[1]);
        textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        textparam.gravity=Gravity.LEFT;
        textparam.gravity=Gravity.CENTER_VERTICAL;
        textparam.setMargins(25,5,5,20);
        tr = new TableRow(this);
        tr.addView(tv,textparam);
        tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tableparam.setMargins(0,0,0,0);
        lll.addView(tr,tableparam);

        layoutparam.setMargins(16,16,16,16);
        layoutparam.setLayoutDirection(LinearLayout.VERTICAL);
        lll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(lll,layoutparam);
        ll.addView(PB);
        final snmpsend sp = new snmpsend();
        sp.setip(ip);
        sp.setiod(oids[iterator]);
        sp.execute();
    }

    public String ping(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c 6 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0){
                output.append(buffer, 0, i);
            }
            reader.close();

            // body.append(output.toString()+"\n");
            str = output.toString();
            // Log.d(TAG, str);
        } catch (IOException e) {
            // body.append("Error\n");
            str=e.toString();
        }
        return str;
    }

    public String snmp4j(String ip, String cmd, int i) throws IOException {
        final String TAG = "SNMP CLIENT";
        // TODO provide ip address of your agent here :)
        String ipAddress = ip;
        final String port = "161";

        // command to request from Server
        // TODO must change this value depend on your agent....
        final int SNMP_VERSION = SnmpConstants.version2c;
        String community = "public";

        // Create TransportMapping and Listen
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(SNMP_VERSION);


        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // create the PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(cmd)));
        pdu.setType(i);

        Snmp snmp = new Snmp(transport);

        // send the PDU
        ResponseEvent response = snmp.send(pdu, comtarget);

        // Process Agent Response
        if (response != null) {
            // extract the response PDU (could be null if timed out)
            PDU responsePDU = response.getResponse();
            // extract the address used by the agent to send the response:
            Address peerAddress = response.getPeerAddress();
            if (responsePDU != null) {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError) {
                    return response.getResponse().get(0).getVariable().toString();
                } else {
                    Log.d(TAG, "Error: Request Failed");
                    Log.d(TAG, "Error Status = " + errorStatus);
                    Log.d(TAG, "Error Index = " + errorIndex);
                    Log.d(TAG, "Error Status Text = " + errorStatusText);
                }
            } else {
                Log.d(TAG, "Error: Response PDU is null");
            }
        } else {
            Log.d(TAG, "Error: Agent Timeout... \n");
        }
        snmp.close();
        return null;
    }

    public class snmpsend extends AsyncTask<String,String,String>{

        String ip;
        String uiores;
        String iod;

        public void setip(String input){
            ip=input;
        }

        public void setiod(String input){
            iod=input;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String s) {
            if(iterator<oids.length){
                iterator--;
                if(uiores != null && !uiores.equals("")) {
                    if (oidsdesc[iterator].equals("systeme ID")) {
                        sysid = uiores;
                    } else if (oidsdesc[iterator].equals("Ip received") && !uiores.contains(".")) {
                        iprec = Integer.parseInt(uiores);
                    } else if (oidsdesc[iterator].equals("Ip delivered") && !uiores.contains(".")){
                        ipsucc = Integer.parseInt(uiores);
                    }else if (oidsdesc[iterator].equals("Services")){
                        servicesid = Integer.parseInt(uiores);
                    } else {
                        snmpfinale = snmpfinale + oidsdesc[iterator] + "::" + uiores + "\t";
                    }
                    iterator++;
                    final snmpsend sp = new snmpsend();
                    sp.setip(ip);
                    sp.setiod(oids[iterator]);
                    sp.execute();
                }else{
                    iterator++;
                    final snmpsend sp = new snmpsend();
                    sp.setip(ip);
                    sp.setiod(oids[iterator]);
                    sp.execute();
                }
            }else{
                if(sysid!=null){
                    if(sysid.split("\\.").length>5){
                        switch (sysid.split("\\.")[6]){
                            case "2" : {
                                snmpfinale = "System::IBM\t"+snmpfinale;
                                break;
                            }
                            case "4":{
                                snmpfinale = "System::unix\t"+snmpfinale;
                                break;
                            }
                            case "9":{
                                snmpfinale = "System::Cisco\t"+snmpfinale;
                                break;
                            }
                            case "11":{
                                snmpfinale = "System::HP\t"+snmpfinale;
                                break;
                            }
                        }
                    }
                }
                if(servicesid - 64 >= 0){
                    services="Application, ";
                    servicesid=servicesid - 64;
                }
                if(servicesid - 8 >= 0){
                    services=services+"Supports the TCP, ";
                    servicesid=servicesid - 8;
                }
                if(servicesid - 4 >= 0){
                    services=services+"Supports the IP, ";
                    servicesid=servicesid - 4;
                }
                if(servicesid - 2 >= 0){
                    services=services+"Bridge, ";
                    servicesid=servicesid - 2;
                }
                if(servicesid - 1 >= 0){
                    services=services+"Repeater ";
                    servicesid=servicesid - 1;
                }

                if(services!=null) {
                    snmpfinale = snmpfinale +"services::"+services;
                }

                if(iprec != 0){
                    drow(ipsucc,iprec-ipsucc);
                }else{
                    info(snmpfinale);
                }
            }
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                uiores = snmp4j(ip,"."+iod,PDU.GETNEXT);
                iterator++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void info(String fin){
        LinearLayout ll = (LinearLayout) findViewById(R.id.main);
        LinearLayout.LayoutParams layoutparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        ll.removeView(PB);

        LinearLayout lll = new LinearLayout(servo.com.servo.ipinfo.this);
        lll.setBackgroundResource(R.drawable.linearlayoutdisp);
        TextView[] tv = new TextView[fin.split("\t").length*3];
        TableRow[] tr = new TableRow[fin.split("\t").length];
        int kk=0;

        for(int tt=0;tt<fin.split("\t").length;tt++){
            textparam = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            tv[kk] = new TextView(this);
            tv[kk].setText(fin.split("\t")[tt].split("::")[0]);
            textparam.gravity=Gravity.LEFT;
            textparam.width=200;
            textparam.setMargins(25,0,0,0);
            tr[tt] = new TableRow(this);
            tr[tt].addView(tv[kk],textparam);
            kk++;

            textparam = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            tv[kk] = new TextView(this);
            tv[kk].setText(":");
            textparam.gravity=Gravity.LEFT;
            textparam.setMargins(0,0,0,0);
            tr[tt].addView(tv[kk],textparam);
            kk++;

            textparam = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            tv[kk] = new TextView(this);
            tv[kk].setText(fin.split("\t")[tt].split("::")[1]);
            textparam.gravity=Gravity.LEFT;
            textparam.setMargins(10,0,20,0);
            tr[tt].addView(tv[kk],textparam);
            kk++;
            tableparam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            tableparam.setMargins(0,10,0,10);
            lll.addView(tr[tt],tableparam);
        }

        layoutparam.setMargins(16,16,16,16);
        layoutparam.setLayoutDirection(LinearLayout.VERTICAL);
        lll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(lll,layoutparam);
    }

    public class snmpsendres extends AsyncTask<String,String,String>{

        String ip;
        String uiores;
        String iod;

        public void setip(String input){
            ip=input;
        }

        public void setiod(String input){
            iod=input;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String s) {
            System.out.println(uiores);
            super.onPostExecute(s);
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                uiores = snmp4j(ip,"."+iod,PDU.GET);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void drow(int yes,int no){
        LinearLayout ll = (LinearLayout) findViewById(R.id.main);
        LinearLayout.LayoutParams layoutparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        LinearLayout lll = new LinearLayout(this);
        lll.setBackgroundResource(R.drawable.linearlayoutdisp);

        ll.removeView(PB);
        TextView tv = new TextView(this);
        tv.setText("Traffic stats");
        textparam.gravity=Gravity.LEFT;
        textparam.gravity=Gravity.CENTER_VERTICAL;
        textparam.setMargins(25,20,5,5);
        TableRow tr = new TableRow(this);
        tr.addView(tv,textparam);
        tableparam.setMargins(0,0,0,0);
        lll.addView(tr,tableparam);


        PieChart pieChart ;
        ArrayList<Entry> entries ;
        ArrayList<String> PieEntryLabels ;
        PieDataSet pieDataSet ;
        PieData pieData ;
        pieChart = new PieChart(this);
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();
        entries.add(new BarEntry(yes,0));
        entries.add(new BarEntry(no,1));
        PieEntryLabels.add("Accepted");
        PieEntryLabels.add("Rejected");
        pieDataSet = new PieDataSet(entries, "");
        pieData = new PieData(PieEntryLabels, pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(pieData);
        pieChart.setDescription("");
        pieChart.animateY(3000);
        textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        textparam.gravity=Gravity.LEFT;
        textparam.gravity=Gravity.CENTER_VERTICAL;
        textparam.height=500;
        textparam.width=500;
        textparam.setMargins(25,5,5,20);

        tr = new TableRow(this);
        tr.addView(pieChart,textparam);

        tv = new TextView(this);
        if(yes>no){
            tv.setText("Accepted : "+yes+"\nRejected : "+no+"\n\nThe server traffic is healthy");
        }else{
            tv.setText("Accepted : "+yes+"\nRejected : "+no+"\n\nThe server traffic is unhealthy");
        }
        textparam = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        textparam.gravity=Gravity.CENTER_HORIZONTAL;
        textparam.gravity=Gravity.CENTER_VERTICAL;
        textparam.setMargins(20,5,5,5);
        tr.addView(tv,textparam);

        tableparam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tableparam.setMargins(0,0,0,0);
        lll.addView(tr,tableparam);

        layoutparam.setMargins(16,16,16,16);
        layoutparam.setLayoutDirection(LinearLayout.VERTICAL);
        lll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(lll,layoutparam);

        info(snmpfinale);
    }
}
