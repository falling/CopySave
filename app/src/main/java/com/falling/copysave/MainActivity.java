package com.falling.copysave;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.falling.copysave.application.MyApplication;
import com.falling.copysave.bean.NoteBean;
import com.falling.copysave.service.CopySaveService;
import com.falling.copysave.viewBinder.NoteViewBinder;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MultiTypeAdapter adapter;
    private List<NoteBean> mItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,CopySaveService.class);
        startService(intent);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MultiTypeAdapter();
        adapter.register(NoteBean.class, new NoteViewBinder());
        mItem = MyApplication.getNoteDao().loadAll();
        adapter.setItems(mItem);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
