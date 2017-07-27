package com.wolf.wifidebug;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView consoleTv;
    EditText inputEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openBtn = find(R.id.openBtn);
        Button closeBtn = find(R.id.closeBtn);

        consoleTv = find(R.id.consoleTv);
        inputEt = find(R.id.inputEt);

        consoleTv.setMovementMethod(new ScrollingMovementMethod());
        inputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    println(inputEt.getText().toString() + ": ");
                    exeCmd(inputEt.getText().toString());
                    return true;
                }
                return false;

            }

        });

        openBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }

    <T> T find(int resId) {
        return (T) findViewById(resId);
    }

    @Override
    public void onClick(View v) {
        if (R.id.openBtn == v.getId()) {
            openAdbd();
        } else if (R.id.closeBtn == v.getId()) {
            closeAdbd();
        }
    }

    void openAdbd() {
        consoleTv.setText("");
        exeCmd("su");
        //exeCmd("stop adbd");
        //exeCmd("setprop service.adb.tcp.port 5555");
        exeCmd("start adbd");
    }

    void closeAdbd() {
        consoleTv.setText("");
        exeCmd("su");
        exeCmd("stop adbd");
        //exeCmd("setprop service.adb.tcp.port 0");
        //exeCmd("start adbd");
    }

    void exeCmd(String cmdStr) {
        ExeCommand cmd = new ExeCommand(false).run(cmdStr, 60000);
        while (cmd.isRunning()) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {

            }
        }
        String buf = cmd.getResult();
        if (TextUtils.isEmpty(buf)) {
            buf = "...";
        }
        println(buf);
    }

    void println(String str) {
        consoleTv.append(str + "\n");
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - this.exitTime > 2000) {
            this.exitTime = now;
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
