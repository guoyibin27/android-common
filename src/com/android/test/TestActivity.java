package com.android.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.common.R;
import com.android.common.UIBinder;
import com.android.common.bind.annotation.Component;
import com.android.common.bind.annotation.Event;
import com.android.common.bind.annotation.Resource;
import com.android.common.bind.constant.EventName;
import com.android.common.bind.constant.ResultType;
import com.android.common.http.*;
import com.android.common.utils.Logger;
import org.apache.http.Header;

/**
 * 测试类
 * Component控件注解，根据注解的id自动实例化控件，并且支持事件注册
 * <p/>
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午5:30
 */
public class TestActivity extends Activity {

    Logger logger = Logger.getLogger(TestActivity.class);


    @Component(id = R.id.button1, onClick = "button1Click")
    Button button1;

    @Component(id = R.id.button2)
    private Button button2;

    @Component(id = R.id.button3)
    Button button3;

    @Resource(id = R.string.app_name, type = Resource.ResourceType.STRING)
    String ss;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UIBinder.bind(this, R.layout.main);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.debug(ss);
    }


    public void button1Click(View v) {
        Toast.makeText(this, "Button 1 Clicked", Toast.LENGTH_SHORT).show();
    }

    //Event注解中的source指的是事件源，也就是某一个控件的实例名字，不能写错，否则会找不到事件源，无法注册事件
    @Event(name = EventName.onClick, source = "button2")
    public void button2Click(View view) {

        AsyncHTTPClient client = new AsyncHTTPClient();
        client.get("http://www.baidu.com", new DefaultHttpResponseCallback() {
            @Override
            public void onFailure(Throwable tr) {
                logger.error("onFailure", tr);
                Toast.makeText(TestActivity.this, "Button 2 Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                logger.error("onStart");
            }

            @Override
            public void onFinish() {
                logger.error("onFinish");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String content) {
                logger.error("onSuccess + " + content);
            }
        });


        SyncHTTPClient syncHTTPClient = new SyncHTTPClient();
        syncHTTPClient.get("http://www.baidu.com", ResultType.TEXT.TEXT);
        int a = syncHTTPClient.getResponseCode();
        logger.error(a + "");
        logger.error(syncHTTPClient.getResponseBody().toString());
        logger.error("TaTatds");

    }
}