package servo.com.servo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    int gfier;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        gfier = 0;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if(info != null && info.isConnected() && gfier == 0){
                    new getheader().execute();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
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

    public void pingshow(final String [][] content){
        int numb=0;
        for(int j=1;j<content.length;j++){
            if(content[j][0]!=null && !content[j][0].equals("")) {
                String res = ping(content[j][0]);
                if (res.contains("100%")){
                    numb++;
                }
            }
        }
        if(numb != 0 ){
            sendNotification(numb);
        }
        gfier = 0;
    }

    public void sendNotification(int i) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 777, intent, 0);
        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setSmallIcon(R.drawable.ipstatuson);
        mBuilder.setContentTitle("Warning");
        mBuilder.setContentText(i+" IP down");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(777, mBuilder.build());
    }

    public class getips extends AsyncTask<String, String, String> {
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
            gfier = 1;
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (responseCode != 404) {
                new getips().execute();
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
    public void onDestroy() {
        gfier = 1;
    }
}
