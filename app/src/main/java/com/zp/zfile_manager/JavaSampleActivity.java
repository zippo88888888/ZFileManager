package com.zp.zfile_manager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zp.z_file.common.ZFileManageHelp;
import com.zp.z_file.content.ZFileBean;
import com.zp.z_file.content.ZFileConfiguration;
import com.zp.z_file.listener.ZFileSelectListener;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JavaSampleActivity extends AppCompatActivity implements ZFileSelectListener {

    private TextView resultTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_sample);
        // 图片显示自定义配置
        ZFileConfiguration.ZFileResources resources = new ZFileConfiguration.ZFileResources(R.drawable.ic_diy_yp);
        // 操作自定义配置
        final ZFileConfiguration configuration = new ZFileConfiguration.Build()
                .resources(resources)
                .boxStyle(ZFileConfiguration.STYLE1)
                .maxLength(3)
                .maxLengthStr("亲，最多选3个！")
                .build();
        resultTxt = findViewById(R.id.main_resultTxt);

        findViewById(R.id.java_startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZFileManageHelp.getInstance()
                        .setConfiguration(configuration)
                        .setFileResultListener(JavaSampleActivity.this)
                        .start(JavaSampleActivity.this);
            }
        });
    }

    @Override
    public void onSelected(@Nullable List<ZFileBean> fileList) {
        if (fileList == null || fileList.size() <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (ZFileBean bean : fileList) {
            sb.append(bean.toString()).append("\n\n");
        }
        resultTxt.setText(sb.toString());
    }
}
