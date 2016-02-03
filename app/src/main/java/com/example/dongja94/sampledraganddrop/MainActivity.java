package com.example.dongja94.sampledraganddrop;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    ListView listView;
    ArrayAdapter<String> mAdapter;

    int oldPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView = (ListView)findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        TextView tv = (TextView)findViewById(R.id.text_item1);
        tv.setOnLongClickListener(this);
        tv = (TextView)findViewById(R.id.text_item2);
        tv.setOnLongClickListener(this);
        tv = (TextView)findViewById(R.id.text_item3);
        tv.setOnLongClickListener(this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String data = (String)listView.getItemAtPosition(position);
                ClipData.Item item = new ClipData.Item(data);
                ClipData clipData = new ClipData(data, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                View.DragShadowBuilder builder = new View.DragShadowBuilder(view);
                oldPosition = position;
                view.startDrag(clipData, builder, view, 0);
                return true;
            }
        });
        listView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED : {
                        if (oldPosition == -1) {
                            View localState = (View) event.getLocalState();
                            localState.setVisibility(View.INVISIBLE);
                        }
                        return true;
                    }
                    case DragEvent.ACTION_DRAG_LOCATION :
                        if (oldPosition != -1) {
                            int x = (int)event.getX();
                            int y = (int)event.getY();
                            int currentPosition = listView.pointToPosition(x, y);
                            if (oldPosition != currentPosition) {
                                String item = (String)listView.getItemAtPosition(oldPosition);
                                mAdapter.remove(item);
                                if (currentPosition == ListView.INVALID_POSITION) {
                                    mAdapter.add(item);
                                    oldPosition = mAdapter.getCount() - 1;
                                } else {
                                    if (oldPosition < currentPosition) {
                                        mAdapter.insert(item, currentPosition - 1);
                                    } else {
                                        mAdapter.insert(item, currentPosition);
                                    }
                                    oldPosition = currentPosition;
                                }
                            }
                            return true;
                        }
                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (oldPosition == -1) {
                            listView.setBackgroundColor(Color.GREEN);
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        if (oldPosition == -1) {
                            listView.setBackgroundColor(Color.TRANSPARENT);
                        }
                        return true;
                    case DragEvent.ACTION_DROP:
                        if (oldPosition == -1) {
                            ClipData clipData = event.getClipData();
                            String data = clipData.getItemAt(0).getText().toString();
                            int x = (int) event.getX();
                            int y = (int) event.getY();
                            int position = listView.pointToPosition(x, y);
                            if (position == ListView.INVALID_POSITION) {
                                mAdapter.add(data);
                            } else {
                                mAdapter.insert(data, position);
                            }
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED: {
                        if (oldPosition == -1) {
                            View localState = (View) event.getLocalState();
                            localState.setVisibility(View.VISIBLE);
                            boolean isDrop = event.getResult();
                            if (isDrop) {
                                listView.setBackgroundColor(Color.TRANSPARENT);
                                Toast.makeText(MainActivity.this, "DRAG_ENDED after DROP", Toast.LENGTH_SHORT).show();
                            }
                        }
                        oldPosition = -1;
                        return true;
                    }

                }
                return false;
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        TextView tv = (TextView)v;
        String data = tv.getText().toString();
        ClipData.Item item = new ClipData.Item(data);
        ClipData clipData = new ClipData(data, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
        View.DragShadowBuilder builder = new View.DragShadowBuilder(v);
        v.startDrag(clipData, builder, v, 0);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
