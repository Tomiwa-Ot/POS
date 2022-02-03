package com.iposprinter.kefa;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;
import com.iposprinter.iposprinterservice.IPosPrinterService;
import com.iposprinter.kefa.Utils.HandlerUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.iposprinter.kefa.MemInfo.bitmapRecycle;

public class BuyActivity extends AppCompatActivity {

    private EditText amountNaira, walletAddress;
    private Pinview pin;
    private ProgressBar progressBar;
    private ResponseListener listener;

    private static final String VERIFY_PIN_URL = "https://tomiwa.com.ng/kefa/verify_pin";
    private static final String VERIFY_AMOUNT_URL = "https://kefa-communication.com/api/fetch_rates";
    private static final String BUY_URL = "https://kefa-communication.com/api/buy";
    private static final String FETCH_TRXN_URL = "https://kefa-communication.com/api/fetch_txn";
    private String token, email;

    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private Random random = new Random();

    private static final String TAG = "IPosPrinterTestDemo";

    private final int MSG_TEST = 1;
    private final int MSG_IS_NORMAL = 2;
    private final int MSG_IS_BUSY = 3;
    private final int MSG_PAPER_LESS = 4;
    private final int MSG_PAPER_EXISTS = 5;
    private final int MSG_THP_HIGH_TEMP = 6;
    private final int MSG_THP_TEMP_NORMAL = 7;
    private final int MSG_MOTOR_HIGH_TEMP = 8;
    private final int MSG_MOTOR_HIGH_TEMP_INIT_PRINTER = 9;
    private final int MSG_CURRENT_TASK_PRINT_COMPLETE = 10;

    private final int PRINTER_NORMAL = 0;
    private final int PRINTER_PAPERLESS = 1;
    private final int PRINTER_THP_HIGH_TEMPERATURE = 2;
    private final int PRINTER_MOTOR_HIGH_TEMPERATURE = 3;
    private final int PRINTER_IS_BUSY = 4;
    private final int PRINTER_ERROR_UNKNOWN = 5;

    private final String PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservic e.NORMAL_ACTION";
    private final String PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION";
    private final String PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION";
    private final String PRINTER_THP_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION";
    private final String PRINTER_THP_NORMALTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION";
    private final String PRINTER_MOTOR_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION";
    private final String PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION";
    private final String PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION = "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION";
    private final String GET_CUST_PRINTAPP_PACKAGENAME_ACTION = "android.print.action.CUST_PRINTAPP_PACKAGENAME";



    private int printerStatus = 0;

    /*循环打印类型*/
    private final int MULTI_THREAD_LOOP_PRINT = 1;
    private final int INPUT_CONTENT_LOOP_PRINT = 2;
    private final int DEMO_LOOP_PRINT = 3;
    private final int PRINT_DRIVER_ERROR_TEST = 4;
    private final int DEFAULT_LOOP_PRINT = 0;

    //循环打印标志位
    private int loopPrintFlag = DEFAULT_LOOP_PRINT;
    private byte loopContent = 0x00;
    private int printDriverTestCount = 0;

    private IPosPrinterService mIPosPrinterService;
    private IPosPrinterCallback callback = null;
    private HandlerUtils.MyHandler handler;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if (resultCode == RESULT_OK) {
                HttpsTrustManager.allowAllSSL();
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest request = new StringRequest(Request.Method.POST, FETCH_TRXN_URL,
                        response -> {
                            try{
                                JSONObject obj = new JSONObject(response);
                                if(obj.getString("status").equals("valid")){
                                    listener.printReceipt(obj);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(BuyActivity.this, "Oops something went wrong", Toast.LENGTH_LONG).show();
                                }
                            } catch(JSONException e){
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }, error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(BuyActivity.this, "Oops something went wrong", Toast.LENGTH_SHORT).show();
                }
                ){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("merchant_transaction_id", data.getStringExtra("merchant_transaction_id"));
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("User-Agent", "KEFA POS");
                        params.put("Authorization", token);
                        return params;
                    }
                };
                requestQueue.add(request);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void scanQRCode(View view){
        if(ActivityCompat.checkSelfPermission(BuyActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(BuyActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, REQUEST_CAMERA_PERMISSION
            );
        }else{
            Intent intent = new Intent(this, ScanQrActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Buy");
        setContentView(R.layout.activity_buy);
        amountNaira = (EditText) findViewById(R.id.amount_naira);
        walletAddress = (EditText) findViewById(R.id.wallet_address);
        pin = (Pinview) findViewById(R.id.pinview);
        progressBar = (ProgressBar) findViewById(R.id.buy_progress);
        token = getIntent().getStringExtra("token");
        email = getIntent().getStringExtra("email");
        closeKeyboard();
        listener = new ResponseListener() {
            @Override
            public void gotResponse(JSONObject object) {
                try {
                    progressBar.setVisibility(View.GONE);
                    String secret = object.getString("secret");
                    String signature = generateSignature(walletAddress.getText().toString(), secret);
                    String url = BUY_URL + "?address=" + walletAddress.getText().toString() + "&fiat_amount=" + amountNaira.getText().toString() + "&signature=" + signature;
                    CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
                    openCustomTab(BuyActivity.this, customIntent.build(), Uri.parse(url));
                } catch (JSONException | NoSuchAlgorithmException jsonException) {
                    jsonException.printStackTrace();
                }
            }

            @Override
            public void historyResponse(JSONArray obj) {

            }

            @Override
            public void printReceipt(JSONObject object) {
//                try {
//                    progressBar.setVisibility(View.GONE);
//                } catch (JSONException jsonException) {
//                    jsonException.printStackTrace();
//                }
            }
        };
        handler = new HandlerUtils.MyHandler(iHandlerIntent);
        callback = new IPosPrinterCallback.Stub() {

            @Override
            public void onRunResult(final boolean isSuccess) throws RemoteException {
                Log.i(TAG, "result:" + isSuccess + "\n");
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG, "result:" + value + "\n");
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private ServiceConnection connectService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIPosPrinterService = IPosPrinterService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIPosPrinterService = null;
        }
    };

    private HandlerUtils.IHandlerIntent iHandlerIntent = new HandlerUtils.IHandlerIntent() {
        @Override
        public void handlerIntent(Message msg) {
            switch (msg.what) {
                case MSG_TEST:
                    break;
                case MSG_IS_NORMAL:
                    if (getPrinterStatus() == PRINTER_NORMAL) {
                        loopPrint(loopPrintFlag);
                    }
                    break;
                case MSG_IS_BUSY:
                    Toast.makeText(BuyActivity.this, R.string.printer_is_working, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(BuyActivity.this, R.string.out_of_paper, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:
                    Toast.makeText(BuyActivity.this, R.string.exists_paper, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                    Toast.makeText(BuyActivity.this, R.string.printer_high_temp_alarm, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_MOTOR_HIGH_TEMP:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(BuyActivity.this, R.string.motor_high_temp_alarm, Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP_INIT_PRINTER, 180000);  //马达高温报警，等待3分钟后复位打印机
                    break;
                case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                    printerInit();
                    break;
                case MSG_CURRENT_TASK_PRINT_COMPLETE:
                    Toast.makeText(BuyActivity.this, R.string.printer_current_task_print_complete, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public void multiThreadLoopPrint() {
        Log.e(TAG, "发起打印任务 --> ");
        switch (random.nextInt(12)) {
            case 0:
                printReceipt();
                break;
            default:
                break;
        }
    }

    public void demoLoopPrint() {
        Log.e(TAG, "发起演示模式 --> ");
        switch (random.nextInt(7)) {
            case 0:
                printReceipt();
                break;
            default:
                break;
        }
    }


    public void loopPrint(int flag) {
        switch (flag) {
            case MULTI_THREAD_LOOP_PRINT:
                multiThreadLoopPrint();
                break;
            case DEMO_LOOP_PRINT:
                demoLoopPrint();
                break;
            case INPUT_CONTENT_LOOP_PRINT:
                bigDataPrintTest(127, loopContent);
                break;
            case PRINT_DRIVER_ERROR_TEST:
                printDriverTest();
                break;
            default:
                break;
        }
    }

    public void bigDataPrintTest(final int numK, final byte data) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                int num4K = 1024 * 4;
                int length = numK > 127 ? num4K * 127 : num4K * numK;
                byte[] dataBytes = new byte[length];
                for (int i = 0; i < length; i++) {
                    dataBytes[i] = data;
                }
                try {
                    mIPosPrinterService.printRawData(dataBytes, callback);
                    mIPosPrinterService.printerPerformPrint(160, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void printDriverTest() {
        if (printDriverTestCount >= 8) {
            loopPrintFlag = DEFAULT_LOOP_PRINT;
            printDriverTestCount = 0;
        } else {
            printDriverTestCount++;
            bigDataPrintTest(printDriverTestCount * 16, (byte) 0x11);
        }
    }


    public int getPrinterStatus() {

        Log.i(TAG, "***** printerStatus" + printerStatus);
        try {
            printerStatus = mIPosPrinterService.getPrinterStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "#### printerStatus" + printerStatus);
        return printerStatus;
    }

    public void printerInit() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    mIPosPrinterService.printerInit(callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private BroadcastReceiver IPosPrinterStatusListener = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.DONUT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Log.d(TAG, "IPosPrinterStatusListener onReceive action = null");
                return;
            }
            Log.d(TAG, "IPosPrinterStatusListener action = " + action);
            if (action.equals(PRINTER_NORMAL_ACTION)) {
                handler.sendEmptyMessageDelayed(MSG_IS_NORMAL, 0);
            } else if (action.equals(PRINTER_PAPERLESS_ACTION)) {
                handler.sendEmptyMessageDelayed(MSG_PAPER_LESS, 0);
            } else if (action.equals(PRINTER_BUSY_ACTION)) {
                handler.sendEmptyMessageDelayed(MSG_IS_BUSY, 0);
            } else if (action.equals(PRINTER_PAPEREXISTS_ACTION)) {
                handler.sendEmptyMessageDelayed(MSG_PAPER_EXISTS, 0);
            } else if (action.equals(PRINTER_THP_HIGHTEMP_ACTION)) {
                handler.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_THP_NORMALTEMP_ACTION)) {
                handler.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL, 0);
            } else if (action.equals(PRINTER_MOTOR_HIGHTEMP_ACTION))  //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
                handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION)) {
                handler.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE, 0);
            } else if (action.equals(GET_CUST_PRINTAPP_PACKAGENAME_ACTION)) {
                String mPackageName = intent.getPackage();
                Log.d(TAG, "*******GET_CUST_PRINTAPP_PACKAGENAME_ACTION：" + action + "*****mPackageName:" + mPackageName);

            } else {
                handler.sendEmptyMessageDelayed(MSG_TEST, 0);
            }
        }
    };



    public void onClick(View v) throws NoSuchAlgorithmException {
        if(verifyDetails()) {
            verifyPin();
        }
    }

    public void openCustomTab(Activity activity, CustomTabsIntent intent, Uri uri) {
        String chromePackage = "com.android.chrome";
        intent.intent.setPackage(chromePackage);
        intent.launchUrl(activity, uri);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected void onResume() {
        Log.d(TAG, "activity onResume");
        super.onResume();
        //绑定服务
        Intent intent = new Intent();
        intent.setPackage("com.iposprinter.iposprinterservice");
        intent.setAction("com.iposprinter.iposprinterservice.IPosPrintService");
        //startService(intent);
        bindService(intent, connectService, Context.BIND_AUTO_CREATE);
        //注册打印机状态接收器
        IntentFilter printerStatusFilter = new IntentFilter();
        printerStatusFilter.addAction(PRINTER_NORMAL_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_BUSY_ACTION);
        printerStatusFilter.addAction(GET_CUST_PRINTAPP_PACKAGENAME_ACTION);

        registerReceiver(IPosPrinterStatusListener, printerStatusFilter);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "activity onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "activity onStop");
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        loopPrintFlag = DEFAULT_LOOP_PRINT;
        unregisterReceiver(IPosPrinterStatusListener);
        unbindService(connectService);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "activity onDestroy");
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public void printReceipt(){
        ThreadPoolManager.getInstance().executeTask(new Runnable() {

            @Override
            public void run() {
                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.kefa_logo);
                try {
                    mIPosPrinterService.printSpecifiedTypeText("      Kefa Communication\n\n", "ST", 24, callback);
                    mIPosPrinterService.printSpecifiedTypeText("------------------------\n\n*********预订单*********\n", "ST", 32, callback);
                    mIPosPrinterService.printSpecifiedTypeText("  期望送达时间:[18:00]\n\n", "ST", 32, callback);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n下单时间: " + "01-01 12:00", "ST", 24, callback);
                    mIPosPrinterService.printSpecifiedTypeText("备注: 别太辣\n", "ST", 32, callback);
                    mIPosPrinterService.printSpecifiedTypeText("菜品          数量   小计" + "金额\n--------------------------------\n\n", "ST", 24, callback);
                    mIPosPrinterService.printSpecifiedTypeText("红烧肉          X1    12\n红烧肉1         X1   " + " 12\n红烧肉2         X1    12\n\n", "ST", 32, callback);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callback);
                    mIPosPrinterService.printSpecifiedTypeText("配送费                         5\n餐盒费        " +
                            " " +
                            " " +
                            "               1\n[超时赔付] - 详见订单\n可口可乐: x1", "ST", 24, callback);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callback);
                    mIPosPrinterService.printSpecifiedTypeText("合计                18元\n\n", "ST", 32, callback);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callback);
                    mIPosPrinterService.printSpecifiedTypeText("张* 18312345678\n地址信息\n", "ST", 48, callback);
                    mIPosPrinterService.printSpecifiedTypeText("--------------------------------\n", "ST", 24, callback);

                    mIPosPrinterService.printerPerformPrint(160, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
//        ThreadPoolManager.getInstance().executeTask(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
//                try {
//                    mIPosPrinterService.printSpecifiedTypeText("Bitcoin POS\n", "ST", 48, callback);
//                    mIPosPrinterService.printSpecifiedTypeText("Peace John Eyibio\n", "ST", 32, callback);
//                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 22, callback);
//                    mIPosPrinterService.printBlankLines(1, 16, callback);
//                    mIPosPrinterService.printSpecifiedTypeText("ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n0123456789\n", "ST", 16, callback);
//                    mIPosPrinterService.setPrinterPrintAlignment(0, callback);
//                    mIPosPrinterService.printQRCode("Bitcoin POS\n", 10, 1, callback);
//                    mIPosPrinterService.printSpecifiedTypeText("**********END***********\n\n", "ST", 32, callback);
//                    bitmapRecycle(mBitmap);
//                    mIPosPrinterService.printerPerformPrint(160, callback);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeKeyboard();
    }

    private void verifyAmount() {
        HttpsTrustManager.allowAllSSL();
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, VERIFY_AMOUNT_URL,
                response -> {
                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("status").equals("ok")){
                            verifyPin();
                        }else if(obj.getString("status").equals("less")){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(BuyActivity.this, "Incorrect pin", Toast.LENGTH_SHORT).show();
                        } else if(obj.getString("status").equals("more")){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(BuyActivity.this, "Oops something went wrong", Toast.LENGTH_LONG).show();
                        }
                    } catch(JSONException e){
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(BuyActivity.this, "Oops something went wrong", Toast.LENGTH_SHORT).show();
        }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("pin", pin.getValue());
                params.put("fiatAmount", amountNaira.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("User-Agent", "KEFA POS");
                params.put("Authorization", token);
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void verifyPin(){
        HttpsTrustManager.allowAllSSL();
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, VERIFY_PIN_URL,
                response -> {
                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("status").equals("valid")){
                            listener.gotResponse(obj);
                        }else if(obj.getString("status").equals("invalid")){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(BuyActivity.this, "Incorrect pin", Toast.LENGTH_SHORT).show();
                        } else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(BuyActivity.this, "Oops something went wrong", Toast.LENGTH_LONG).show();
                        }
                    } catch(JSONException e){
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }, error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(BuyActivity.this, "Check internet connection", Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("pin", pin.getValue());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("User-Agent", "KEFA POS");
                params.put("Authorization", token);
                return params;
            }
        };
        requestQueue.add(request);
    }

    public void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String generateSignature(String address, String secret) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] digest = messageDigest.digest((address + secret).getBytes());
        StringBuilder builder = new StringBuilder();
        for(byte d : digest) {
            builder.append(Integer.toString((d & 0xff) + 0x100, 16).substring(1));
        }
        return builder.toString();
    }

    public boolean verifyDetails() {
        boolean amountValidate, addressValidate;
        TextInputLayout t_amount = (TextInputLayout) findViewById(R.id.amount_input_layout);
        TextInputLayout t_address = (TextInputLayout) findViewById(R.id.wallet_input_layout);

        if(!amountNaira.getText().toString().isEmpty()) {
            amountValidate = true;
            t_amount.setError(null);
        } else {
            amountValidate = false;
            t_amount.setError("*Enter a valid amount");
        }

        if(!walletAddress.getText().toString().isEmpty()) {
            addressValidate = true;
            t_address.setError(null);
        } else {
            addressValidate = false;
            t_address.setError("*Enter a valid address");
        }
        return amountValidate && addressValidate;
    }


}