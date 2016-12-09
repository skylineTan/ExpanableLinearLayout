package com.skylinetan.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skylinetan.expanblelinearlayout.ExpandableLinearLayout;

public class MainActivity extends AppCompatActivity {

    private ExpandableLinearLayout mExpandableLinearLayout;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExpandableLinearLayout = (ExpandableLinearLayout) findViewById(R.id.expandable_linear_layout);
        imageView = (ImageView) findViewById(R.id.main_more_iv);
        textView = (TextView) findViewById(R.id.main_more_tv);
        mExpandableLinearLayout.setOnECViewListener(new ExpandableLinearLayout.OnExpandCollapseViewClickListener() {
            @Override
            public void onClick(View view, boolean isCollapsed) {
                //如果是正在收缩
                if(isCollapsed) {
                    imageView.setImageResource(R.mipmap.ic_collapse);
                    textView.setText("更多");
                }else {
                    imageView.setImageResource(R.mipmap.ic_expand);
                    textView.setText("收起");
                }
            }
        });
    }
}
