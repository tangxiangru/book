package com.example.book;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SHOW_RESPONSE = 0;
    private EditText edt;
    private Button btn;
    private ListView rtv;
 //   private ListView rtv2;
    private List<Book> bookList  = new ArrayList<Book>();
    private ListAdapter listAdapter;



    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    parseJSONWithJSONObject(response.toString());

                    listAdapter = new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return bookList.size();
                        }

                        @Override
                        public Object getItem(int i) {
                            return null;
                        }

                        @Override
                        public long getItemId(int i) {
                            return i;
                        }

                        @Override
                        public View getView(int i, View view, ViewGroup viewGroup) {
                            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item,null);
                            ((TextView)view.findViewById(R.id.name)).setText(bookList.get(i).getTitle());
                            Log.d("title",bookList.get(i).getTitle());
                            return view;
                        }
                    };
//                    listAdapter = new SimpleAdapter(MainActivity.this,bookList,)
                    rtv.setAdapter(listAdapter);

            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rtv = (ListView) findViewById(R.id.rtv);

        edt = (EditText) findViewById(R.id.edt);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);



    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            sendRequestWithHttpURLConnection();
            String inputText = edt.getText().toString();
            Toast.makeText(MainActivity.this, inputText, Toast.LENGTH_SHORT).show();
        }
    }





    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection huc = null;
                try {
                    URL url = new URL("https://api.douban.com/v2/book/search?q=" + edt.getText().toString());
                    huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestMethod("GET");
                    InputStream ins = huc.getInputStream();
                    BufferedReader brd = new BufferedReader(new InputStreamReader(ins));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = brd.readLine()) != null) {
                        response.append(line);
                    }
                    Message msg = new Message();
                    msg.what = SHOW_RESPONSE;
                    msg.obj = response.toString();
                    handler.sendMessage(msg);

                } catch (Exception exc) {
                    exc.printStackTrace();
                } finally {
                    if (huc != null) {
                        huc.disconnect();
                    }
                }
            }
        }).start();
    }


    public void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            int total = object.getInt("total");
            JSONArray array = object.getJSONArray("books");
            bookList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Book book = new Book();
                book.title = ((JSONObject)array.get(i)).getString("title");
                book.summary = ((JSONObject)array.get(i)).getString("summary");
                bookList.add(book);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}







