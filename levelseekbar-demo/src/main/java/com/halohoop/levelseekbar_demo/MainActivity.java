package com.halohoop.levelseekbar_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.halohoop.levelseekbar.LevelSeekbar;

public class MainActivity extends AppCompatActivity implements LevelSeekbar.LevelChangedListener {

    private LevelSeekbar levelSeeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        levelSeeker = (LevelSeekbar) findViewById(R.id.level_seek_bar);
        levelSeeker.setLevelChangeListener(this);
    }

    @Override
    public void onLevelChanged(int levelIndex, int levelVal, String levelDesc) {
        Toast.makeText(this,levelVal + levelDesc, Toast.LENGTH_SHORT).show();
    }
}
