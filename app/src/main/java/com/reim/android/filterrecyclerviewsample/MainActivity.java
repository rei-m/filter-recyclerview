package com.reim.android.filterrecyclerviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.reim.android.filterrecyclerview.FilterRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    FilterRecyclerView filterRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filterRecyclerView = (FilterRecyclerView) findViewById(R.id.activity_main_filter_recycler_view);
        filterRecyclerView.setAdapter(new MainFilterRecyclerAdapter(this));

        List<ListItem> itemList = new ArrayList<>();
        itemList.add(new ListItem("Sam", "Dog"));
        itemList.add(new ListItem("Spot", "Cat"));
        itemList.add(new ListItem("Pip", "Cat"));
        itemList.add(new ListItem("Piper", "Dog"));
        itemList.add(new ListItem("Duke", "Cat"));
        itemList.add(new ListItem("Max", "Dog"));
        itemList.add(new ListItem("Charlie", "Cat"));
        itemList.add(new ListItem("Rocky", "Dog"));
        itemList.add(new ListItem("Zack", "Cat"));
        itemList.add(new ListItem("Tiny", "Dog"));

        filterRecyclerView.initializeItemList(itemList);
    }

    @Override
    public void onBackPressed() {
        if (filterRecyclerView.isOpened()) {
            filterRecyclerView.close();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_search:
                filterRecyclerView.open(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

