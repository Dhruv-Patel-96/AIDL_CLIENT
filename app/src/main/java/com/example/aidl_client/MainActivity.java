package com.example.aidl_client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidl_service.aidlInterface;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView textViewDisplayResult;
    private EditText editTextFirstValue, editTextSecondValue;
    private Button buttonAdd, buttonSubtract, buttonMultiply, buttonDivision, buttonClearData;
    int firstValue, secondValue;
    private Context mContext;


    public  aidlInterface aidlInterfaceObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the UI
        mContext=this;
        textViewDisplayResult = findViewById(R.id.display_result);
        editTextFirstValue = findViewById(R.id.edt_enter_first_value);
        editTextSecondValue = findViewById(R.id.edt_enter_second_value);
        buttonAdd = findViewById(R.id.addition);
        buttonSubtract = findViewById(R.id.subtraction);
        buttonMultiply = findViewById(R.id.multiplication);
        buttonDivision = findViewById(R.id.division);
        buttonClearData = findViewById(R.id.clear_data);

        //clickListener
        buttonAdd.setOnClickListener(this);
        buttonSubtract.setOnClickListener(this);
        buttonMultiply.setOnClickListener(this);
        buttonDivision.setOnClickListener(this);
        buttonClearData.setOnClickListener(this);

        bindAIDLService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addition:
                verifyAndCalculate(1);
                break;

            case R.id.subtraction:
                verifyAndCalculate(2);
                break;
            case R.id.multiplication:
                verifyAndCalculate(3);
                break;

            case R.id.division:
                verifyAndCalculate(4);
                break;
            case R.id.clear_data:
                editTextFirstValue.setText(null);
                editTextSecondValue.setText(null);
                textViewDisplayResult.setText(null);
                break;
            default:
                Toast.makeText(this, "Default case ", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyAndCalculate(int operationToPerform) {
        if (isAnyValueMissing()) {
            Toast.makeText(this, "Please enter both the value", Toast.LENGTH_SHORT).show();
        } else {
            int result;
            firstValue = Integer.parseInt(this.editTextFirstValue.getText().toString());
            secondValue = Integer.parseInt(this.editTextSecondValue.getText().toString());
            try {
                result=aidlInterfaceObject.calculateData(firstValue, secondValue, operationToPerform);
                textViewDisplayResult.setText("" + result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    ServiceConnection serviceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            aidlInterfaceObject=aidlInterface.Stub.asInterface( (IBinder) iBinder );

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };





    private void bindAIDLService() {
        //Try catch block is not added during video implementation
        try{
            Intent settingsIntent = new Intent("com.example.aidl_service");
            bindService(convertImplicitIntentToExplicitIntent(settingsIntent, mContext), serviceConnection, BIND_AUTO_CREATE);
        }catch (Exception e)
        {
            Toast.makeText(mContext, "Service App may not be present", Toast.LENGTH_SHORT).show();
            Log.e("AIDL_ERROR","EXCEPTION CAUGHT: "+e.toString());
            finish();
        }
    }




    public Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices( implicitIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get( 0 );
        ComponentName component = new ComponentName( serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name );
        Intent explicitIntent = new Intent( implicitIntent );
        explicitIntent.setComponent( component );
        return explicitIntent;
    }








    private boolean isAnyValueMissing() {
        return this.editTextFirstValue.getText().toString().isEmpty() && this.editTextSecondValue.getText().toString().isEmpty();
    }

    private int performCalculation(int firstValue, int secondValue, int operation) {
        //We will move this logic in second application afterwards
        switch (operation) {
            case 1:
                return firstValue + secondValue;

            case 2:
                return firstValue - secondValue;

            case 3:
                return firstValue * secondValue;

            case 4:
                return firstValue / secondValue;

            default:
                Log.d("Calculator App: ","Invalid Operation");
                return 0;
        }
    }
}
