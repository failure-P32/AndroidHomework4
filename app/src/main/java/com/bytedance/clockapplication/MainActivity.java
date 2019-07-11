package com.bytedance.clockapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.bytedance.clockapplication.fragment.ClockFragment;
import com.bytedance.clockapplication.widget.Clock;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private List<ClockFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments.add(new ClockFragment());
        fragments.get(0).setTimeZone("GMT+8"); // Beijing Time

        mViewPager = findViewById(R.id.vp); // Initialize ViewPager
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return fragments.get(position).getTimeZone();
            }

            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }
        });
        TabLayout mTabLayout = findViewById(R.id.tl);
        mTabLayout.setupWithViewPager(mViewPager);

        Button mButton = findViewById(R.id.b_new);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialogView = View.inflate(MainActivity.this, R.layout.dialog_timezone, null);
                // Show a dialog to select time zone
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("选择时区")
                        .setView(dialogView)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText editText = dialogView.findViewById(R.id.et_off);
                                int time = Integer.parseInt(editText.getText().toString());
                                if (time > 12 || time < -12) { // Invalid input
                                    Toast.makeText(MainActivity.this, "时差不能大于24", Toast.LENGTH_SHORT).show();
                                    dialogInterface.cancel();
                                } else {
                                    int aTime = Math.abs(time);
                                    String timeZone = "GMT" + (time >= 0 ? "+" : "-") + aTime;
                                    ClockFragment clockFragment = new ClockFragment();
                                    clockFragment.setTimeZone(timeZone);
                                    fragments.add(clockFragment); // Add a new clock
                                    mViewPager.getAdapter().notifyDataSetChanged();
                                    mViewPager.setCurrentItem(fragments.size() - 1);
                                    dialogInterface.dismiss();
                                }
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

}

