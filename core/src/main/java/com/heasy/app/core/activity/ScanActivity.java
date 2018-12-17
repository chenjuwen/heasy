package com.heasy.app.core.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.heasy.app.core.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by Administrator on 2018/1/24.
 */
public class ScanActivity extends CaptureActivity implements CompoundBarcodeView.TorchListener {
    private DecoratedBarcodeView barcodeView;
    private CaptureManager captureManager;
    private Button btnBack, btnSwitch;
    private boolean isLightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnSwitch = (Button) findViewById(R.id.btnSwitch);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcodeView);
        barcodeView.setTorchListener(this);

        // 如果没有闪光灯功能，就去掉相关按钮
        if (!hasFlash()) {
            btnSwitch.setVisibility(View.GONE);
        }

        captureManager = new CaptureManager(this, barcodeView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();

        //关闭窗口，取消扫描
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //选择闪关灯
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLightOn) {
                    barcodeView.setTorchOff();
                } else {
                    barcodeView.setTorchOn();
                }
            }
        });
    }

    @Override
    public void onTorchOn() {
        isLightOn = true;
        ((Button)findViewById(R.id.btnSwitch)).setText("关闭闪关灯");
    }

    @Override
    public void onTorchOff() {
        isLightOn = false;
        ((Button)findViewById(R.id.btnSwitch)).setText("打开闪关灯");
    }

    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

}
