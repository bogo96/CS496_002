package com.example.user.cs496_002;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    Handler handler;

    // ListViewAdapter의 생성자
    public ListViewAdapter() {
            handler = new Handler();
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        final View view = convertView;

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView nameTextView = convertView.findViewById(R.id.textView1);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem listViewItem = listViewItemList.get(position);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                // TODO Auto-generated method stub
                try{
                    // 걍 외우는게 좋다 -_-;
                    final ImageView iv = (ImageView)view.findViewById(R.id.imageView);
                    URL url = new URL(listViewItem.getLink());
                    InputStream is = url.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            iv.setImageBitmap(bm);
                        }
                    });
                    iv.setImageBitmap(bm); //비트맵 객체로 보여주기
                } catch(Exception e){

                }

            }
        });

        t.start();



        // 아이템 내 각 위젯에 데이터 반영
        nameTextView.setText(listViewItem.getName());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String name, String number, String email, String link) {
        ListViewItem item = new ListViewItem();

        item.setName(name);
        item.setNumber(number);
        item.setEmail(email);
        item.setLink(link);

        listViewItemList.add(item);
    }

    public void deleteItem(String name, String number, String email, String link){
        Log.i("before size",Integer.toString(listViewItemList.size()));
        for(int i=0; i<listViewItemList.size(); i++){
            ListViewItem c = listViewItemList.get(i);
            String cname = c.getName();
            String cnumber = c.getNumber();
            String cemail = c.getEmail();
            String clink = c.getLink();
            if(cname.compareTo(name)==0 && cnumber.compareTo(cnumber)==0 && cemail.compareTo(cemail)==0 && clink.compareTo(link)==0){
                listViewItemList.remove(i);
            }
        }
        Log.i("after size",Integer.toString(listViewItemList.size()));
    }
}


