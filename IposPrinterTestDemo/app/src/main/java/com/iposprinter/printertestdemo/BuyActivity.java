package com.iposprinter.printertestdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.appindexing.AppIndex;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;
import com.iposprinter.iposprinterservice.IPosPrinterService;
import com.iposprinter.printertestdemo.Utils.BytesUtil;
import com.iposprinter.printertestdemo.Utils.HandlerUtils;

import java.util.Random;

import static com.iposprinter.printertestdemo.MemInfo.bitmapRecycle;

public class BuyActivity extends AppCompatActivity {

    private EditText name, cardNumber, cardDate, cardCVV, amountNaira, amountBTC, walletAddress;
    private Pinview pin;

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;

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

    @OnTextChanged(value = R.id.amount_naira, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onAmountNairaTextChanged(Editable s){
        try{
            double nairaAmount = Double.parseDouble(amountNaira.getText().toString());
            double btc = nairaAmount * 0.00021221;
            amountBTC.setText(Double.toString(btc));
        }catch (Exception exception){

        }

    }

//    @OnTextChanged(value = R.id.amount_btc, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
//    protected void onAmountBTCTextChanged(Editable s){
//        try{
//            double btcAmount = Double.parseDouble(amountBTC.getText().toString());
//            double nairaAmount = btcAmount / 0.00021221;
//            amountNaira.setText(Double.toString(nairaAmount));
//        }catch (Exception exception){
//
//        }
//    }

    @OnTextChanged(value = R.id.cardNumberEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.cardDateEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.cardCVCEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            walletAddress.setText(data.getStringExtra("qrCodeValue"));
        }
    }

    public void scanQRCode(View view){
        Intent intent = new Intent(this, ScanQrActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Buy");
        setContentView(R.layout.activity_buy);
        name = (EditText) findViewById(R.id.customerName);
        cardNumber = (EditText) findViewById(R.id.cardNumberEditText);
        cardDate = (EditText) findViewById(R.id.cardDateEditText);
        cardCVV = (EditText) findViewById(R.id.cardCVCEditText);
        amountNaira = (EditText) findViewById(R.id.amount_naira);
        amountBTC = (EditText) findViewById(R.id.amount_btc);
        walletAddress = (EditText) findViewById(R.id.wallet_address);
        pin = (Pinview) findViewById(R.id.pinview);
        ButterKnife.bind(this);
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


//    public boolean verifyInput(){
//        return false;
//    }
//
//    public void pay(){
//
//    }

    public void onClick(View v){
        if (getPrinterStatus() == PRINTER_NORMAL)
            printReceipt();
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
                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
                try {
                    mIPosPrinterService.printSpecifiedTypeText("Bitcoin POS\n", "ST", 48, callback);
                    mIPosPrinterService.printSpecifiedTypeText("Peace John Eyibio\n", "ST", 32, callback);
                    mIPosPrinterService.printSpecifiedTypeText("\n", "ST", 22, callback);
                    mIPosPrinterService.printBlankLines(1, 16, callback);
                    mIPosPrinterService.printSpecifiedTypeText("ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n0123456789\n", "ST", 16, callback);
                    mIPosPrinterService.setPrinterPrintAlignment(0, callback);
                    mIPosPrinterService.printQRCode("Bitcoin POS\n", 10, 1, callback);
                    mIPosPrinterService.printSpecifiedTypeText("**********END***********\n\n", "ST", 32, callback);
                    bitmapRecycle(mBitmap);
                    mIPosPrinterService.printerPerformPrint(160, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeKeyboard();
    }

    public void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}