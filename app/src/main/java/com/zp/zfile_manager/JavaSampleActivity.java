package com.zp.zfile_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zp.z_file.content.ZFileConfiguration;

public class JavaSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_sample);

        ZFileConfiguration.ZFileResources zFileResources = new ZFileConfiguration.ZFileResources();
        ZFileConfiguration configuration = new ZFileConfiguration.Build()
                .resources(zFileResources)
                .authority("")
                .build();

    }

}
