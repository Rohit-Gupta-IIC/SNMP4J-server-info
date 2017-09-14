package servo.com.servo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.os.SystemClock.sleep;

public class MainActivity extends AppCompatActivity {
    public static final String ip = "servo.com.servo.IP";
    public static final String par1 = "servo.com.servo.PAR1";
    public static final String par2 = "servo.com.servo.PAR2";
    public static final String par11 = "servo.com.servo.PAR11";
    public static final String par21 = "servo.com.servo.PAR21";
    ProgressBar PB;
    public static int j=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout llload = (LinearLayout) findViewById(R.id.mainlayout);
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l.gravity = Gravity.CENTER_HORIZONTAL;
        l.setMargins(0, 50, 0, 0);
        PB = new ProgressBar(this);
        PB.setIndeterminate(true);
        llload.addView(PB, l);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            new getheader().execute();
        }else{
            //need conection hundler
        }
    }

    public void pingshow(final String [][] content){
        LinearLayout ll = (LinearLayout) findViewById(R.id.mainlayout);
        LinearLayout[] lll = new LinearLayout[content.length];
        TableRow[] tr = new TableRow[content.length];
        TextView[] tv = new TextView[content.length];
        Button[] bt = new Button[content.length];
        int k=0;
        int l=0;
        int tk = 0;
        int b = 0;
        if(j<content.length){
            if(content[j][0]!=null && !content[j][0].equals("")) {
                LinearLayout.LayoutParams layoutparam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams tableparam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                TableRow.LayoutParams textparam = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                ll.removeView(PB);
                String res = ping(content[j][0]);
                tr[tk] = new TableRow(this);
                if (!(res.contains("100%"))){
                    tv[k] = new TextView(this);
                    tv[k].setText("");
                    tv[k].setBackgroundResource(R.drawable.ipstatuson);
                    textparam.width=70;
                    textparam.height=70;
                    textparam.setMargins(30,11,5,5);
                    textparam.gravity = Gravity.LEFT;
                    textparam.gravity = Gravity.CENTER_VERTICAL;
                    tr[tk].addView(tv[k],textparam);
                    k++;
                } else {
                    tv[k] = new TextView(this);
                    tv[k].setBackgroundResource(R.drawable.ipstatus);
                    tv[k].setText("");
                    textparam.width=70;
                    textparam.height=70;
                    textparam.setMargins(30,11,5,5);
                    textparam.gravity = Gravity.LEFT;
                    textparam.gravity = Gravity.CENTER_VERTICAL;
                    tr[tk].addView(tv[k],textparam);
                    k++;
                }
                tv[k] = new TextView(this);
                tv[k].setText(content[j][0]);
                textparam = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                textparam.setMargins(20,5,5,5);
                textparam.gravity = Gravity.CENTER_VERTICAL;
                tr[tk].addView(tv[k],textparam);
                k++;


                textparam = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                if (!(res.contains("100%"))) {
                    tv[k] = new TextView(this);
                    tv[k].setText("(connected)");
                    textparam.setMargins(15,15,15,15);
                    textparam.gravity = Gravity.CENTER_VERTICAL;
                    tr[tk].addView(tv[k],textparam);
                    k++;
                } else {
                    tv[k] = new TextView(this);
                    tv[k].setText("(disconnected)");
                    textparam.setMargins(15,15,15,15);
                    textparam.gravity = Gravity.CENTER_VERTICAL;
                    tr[tk].addView(tv[k],textparam);
                    k++;
                }

                tableparam.setMargins(0,0,0,0);
                lll[l] = new LinearLayout(this);
                lll[l].setBackgroundResource(R.drawable.linearlayoutdisp);
                lll[l].addView(tr[tk],tableparam);
                tk++;

                tv[k] = new TextView(this);
                tv[k].setText(content[0][1]+" : "+content[j][1]+"\n"+content[0][2]+" : "+content[j][2]);
                textparam = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                textparam.setMargins(120,5,5,10);
                textparam.width=350;
                textparam.gravity = Gravity.CENTER_VERTICAL;

                tableparam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tr[tk] = new TableRow(this);
                tr[tk].addView(tv[k],textparam);
                k++;
                System.out.println(res);
                if(!(res.contains("100%")) ) {
                    if(!(res.contains("50%"))) {
                        tv[k] = new TextView(this);
                        tv[k].setText("delay : " + res.split("\n")[2 + 4].split("=")[1].split("/")[1]+"ms");
                        textparam = new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        textparam.setMargins(200, 5, 5, 5);
                        textparam.gravity = Gravity.RIGHT;
                        tr[tk].addView(tv[k], textparam);
                        k++;
                    }else{
                        tv[k] = new TextView(this);
                        tv[k].setText("delay : " + res.split("\n")[1 + 4].split("=")[1].split("/")[1]+"ms");
                        textparam = new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        textparam.setMargins(200, 5, 5, 5);
                        textparam.gravity = Gravity.RIGHT;
                        tr[tk].addView(tv[k], textparam);
                        k++;
                    }
                }else{
                    tv[k] = new TextView(this);
                    tv[k].setText("delay : NAN");
                    textparam = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    textparam.setMargins(200, 5, 5, 5);
                    textparam.gravity = Gravity.RIGHT;
                    tr[tk].addView(tv[k], textparam);
                    k++;
                }
                textparam = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                bt[b] = new Button(this);
                bt[b].setBackgroundResource(R.drawable.moreinfo);
                final int finalJ = j;
                bt[b].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendintent(content[finalJ][0],content[finalJ][1],content[finalJ][2],content[0][1],content[0][2]);
                    }
                });
                textparam.gravity=Gravity.RIGHT;
                textparam.gravity=Gravity.CENTER_VERTICAL;
                textparam.setMargins(5,5,5,5);
                tr[tk].addView(bt[b],textparam);
                b++;

                tableparam.setMargins(0,0,0,0);
                lll[l].addView(tr[tk],tableparam);
                tk++;

                layoutparam.setMargins(16,16,16,16);
                layoutparam.setLayoutDirection(LinearLayout.VERTICAL);
                lll[l].setOrientation(LinearLayout.VERTICAL);
                ll.addView(lll[l],layoutparam);
                l++;
                if(content[j+1][0]!=null){
                    ll.addView(PB);
                }
                j++;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pingshow(content);
                    }
                }, 1000);
            }
        }
    }

    public void sendintent(String cont,String p11,String p21,String p1,String p2){
        Intent intent = new Intent(this, ipinfo.class);
        intent.putExtra(ip, cont);
        intent.putExtra(par1, p1);
        intent.putExtra(par2, p2);
        intent.putExtra(par11, p11);
        intent.putExtra(par21, p21);
        startActivity(intent);
    }

    public String ping(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c 2 " + url);
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

    public class getips extends AsyncTask<String, String, String>{
        String[][] content = new String[500][500];
        int i = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            pingshow(content);
        }


        @Override
        protected String doInBackground(String... params) {

            try {
                URL NoteJs = new URL("https://rsupervision.000webhostapp.com/textdata/datas.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(NoteJs.openStream()));
                String line;
                while((line = in.readLine()) != null){
                    String[] cont = line.split("\t");
                    content[i][0] = cont[0];
                    content[i][1] = cont[1];
                    content[i][2] = cont[2];
                    i++;
                }
                in.close();
            } catch (Exception e) {
            }
            return null;
        }
    }

    public class getheader extends AsyncTask <String, String, String> {

        int responseCode=404;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (responseCode != 404) {
                new getips().execute();
            } else {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainlayout);
                LinearLayout.LayoutParams erreurparam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                erreurparam.gravity= Gravity.CENTER_VERTICAL;
                TextView tv = new TextView(MainActivity.this);
                tv.setText("Contact the network service");
                linearLayout.addView(tv,erreurparam);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final URL url = new URL("https://rsupervision.000webhostapp.com/textdata/datas.txt");
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("HEAD");
                responseCode = huc.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option,menu);
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("servo.com.servo.MyService".equals(service.service.getClassName())) {
                menu.findItem(R.id.notif).setChecked(true);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notif:
                if (item.isChecked()) {
                    item.setChecked(false);
                    stopService(new Intent(this, MyService.class));
                } else {
                    item.setChecked(true);
                    startService(new Intent(this, MyService.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
