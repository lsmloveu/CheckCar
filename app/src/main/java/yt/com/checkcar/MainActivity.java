package yt.com.checkcar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import yt.com.checkcar.utils.Constant;
import yt.com.checkcar.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.edt_deviceno)
    EditText edtDeviceno;
    @InjectView(R.id.tv_seach)
    TextView tvSeach;
    @InjectView(R.id.tv_timeval)
    TextView tvTimeval;
    @InjectView(R.id.im_gps)
    ImageView imGps;
    @InjectView(R.id.im_net)
    ImageView imNet;
    @InjectView(R.id.tv_socval)
    TextView tvSocval;
    @InjectView(R.id.tv_volval)
    TextView tvVolval;
    @InjectView(R.id.tv_eleval)
    TextView tvEleval;
    @InjectView(R.id.tv_maxtemval)
    TextView tvMaxtemval;
    @InjectView(R.id.tv_mintemval)
    TextView tvMintemval;
    @InjectView(R.id.tv_maxonevolval)
    TextView tvMaxonevolval;
    @InjectView(R.id.tv_minonevolval)
    TextView tvMinonevolval;
    @InjectView(R.id.tv_controltemval)
    TextView tvControltemval;
    @InjectView(R.id.tv_chargestatusval)
    TextView tvChargestatusval;
    @InjectView(R.id.tv_chargelinkval)
    TextView tvChargelinkval;
    @InjectView(R.id.tv_runmeterval)
    TextView tvRunmeterval;
    @InjectView(R.id.tv_faultcodeval)
    TextView tvFaultcodeval;
    @InjectView(R.id.tv_faultval)
    TextView tvFaultval;
    @InjectView(R.id.rel_showfault)
    RelativeLayout relShowfault;
    //
    ProgressDialog progressDialog;
    //电机
    String[] faulucode1;
    //电池
    String[] faulucode2;
    //充电机
    String[] faulucode3;
    //整车
    String[] faulucode4;
    @InjectView(R.id.im_scan)
    ImageView imScan;
    public static final int REQUSET = 1;
    @InjectView(R.id.im_fault)
    ImageView imFault;
    String[] PERMISSIONS_CONTACT;
    private static final int REQUEST_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        arrayInit();
        versionJuge();
    }

    //android 6.0判断
    public void versionJuge() {
        if (Build.VERSION.SDK_INT >= 23) {
            PERMISSIONS_CONTACT = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
            showContacts(tvSeach);
        }
    }

    //
    public void showContacts(View v) {

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            requestContactsPermissions(tvSeach);
        }
    }

    //
    private void requestContactsPermissions(View v) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)
                ) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(v, "permission_contacts_rationale",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_CONTACT,
                                            REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CONTACTS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
            } else {
//                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //故障数组初始化
    public void arrayInit() {
        faulucode1 = new String[]{
                "预充故障", "主接故障", "IGBT故障", "过流", "电机控制器一般过温", "电机控制器严重过温", "严重过压",
                "严重欠压", "超速", "can总线故障", "中点电压", "档位故障", "油门故障", "电机一般过温", "电机严重过温"
        };
        faulucode2 = new String[]{
                "通讯中断", "绝缘过低", "绝缘低", "保留", "放电过流", "温度过高", "单体过放", "单体过充",
                "短路保护", "温升过快", "压差过大", "温差过大", "温度过低", "充电过流", "总压过低", "总压过高"
        };
        faulucode3 = new String[]{
                "硬件故障", "充电机过温保护", "输入电压错误，充电机停止工作", "充电机处于关闭状态", "通讯接收超时"
        };
        faulucode4 = new String[]{""};
    }

    public void clearText() {
        relShowfault.setVisibility(View.GONE);
        //Textview清空
        TextView[] textViews = new TextView[]{
                tvTimeval, tvFaultcodeval, tvFaultval,
                tvSocval, tvVolval, tvEleval, tvMaxtemval, tvMintemval, tvMaxonevolval, tvMinonevolval, tvControltemval, tvChargestatusval, tvChargelinkval, tvRunmeterval
        };
        textViews[0].setText("yyyy/MM/dd HH:mm:ss");
        textViews[1].setTextColor(Color.rgb(0x33,0x33,0x33));
        textViews[1].setText("0000");
        textViews[2].setText("");
        textViews[2].setVisibility(View.GONE);
        for (int i = 3; i < textViews.length; i++) {
            textViews[i].setText("——");
        }
        //Imageview
        imGps.setImageDrawable(getResources().getDrawable(R.drawable.iconoff));
        imNet.setImageDrawable(getResources().getDrawable(R.drawable.iconoff));
        imFault.setImageDrawable(getResources().getDrawable(R.drawable.iconerror1));
    }

    @OnClick({R.id.tv_seach, R.id.im_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            //查询处理
            case R.id.tv_seach: {
                try {
                    String deviceNo = edtDeviceno.getText().toString().trim();
                    //显示清空
                    clearText();
                    if (!deviceNo.equals("")) {
                        if (NetOpen.isNetworkAvailable(MainActivity.this)) {
                            progressDialog = ProgressDialog.show(MainActivity.this, "", "信息查询中，请稍候...", true);
                            //
                            OkHttpUtils
                                    .post()
                                    .url(BaseUrl.getStateInfoUrl)
                                    .addParams("deviceNo", deviceNo)
                                    .build()
                                    .connTimeOut(10 * 1000)
                                    .execute(new Callback<CommonBean>() {
                                        @Override
                                        public CommonBean parseNetworkResponse(Response response) throws Exception {
                                            String body = response.body().string();
                                            Log.i("js", body);
                                            CommonBean bean = new Gson().fromJson(body, CommonBean.class);
                                            return bean;
                                        }

                                        @Override
                                        public void onError(Call call, Exception e) {
                                            progressDialog.dismiss();
                                            Utils.showToastInUI(MainActivity.this, "请求数据失败！");
                                            Log.i("error", "" + e);
                                        }

                                        @Override
                                        public void onResponse(CommonBean response) {
                                            progressDialog.dismiss();
                                            //隐藏故障，清空故障
                                            if (response.getIsSuccess().equals("true")) {
                                                //时间
                                                String time = response.getTime();
                                                tvTimeval.setText(time);
                                                //gps
                                                String gpsState = response.getGpsState();
                                                if (gpsState.equals("0")) {
                                                    imGps.setImageDrawable(getResources().getDrawable(R.drawable.iconon));
                                                } else {
                                                    imGps.setImageDrawable(getResources().getDrawable(R.drawable.iconoff));
                                                }
                                                //网络通讯
                                                String netState = response.getNetState();
                                                if (netState.equals("0")) {
                                                    imNet.setImageDrawable(getResources().getDrawable(R.drawable.iconon));
                                                } else {
                                                    imNet.setImageDrawable(getResources().getDrawable(R.drawable.iconoff));
                                                }
                                                //soc
                                                String soc = response.getSoc();
                                                tvSocval.setText(soc);
                                                //总电压
                                                String totalVol = response.getTotalVol();
                                                tvVolval.setText(totalVol);
                                                //总电流
                                                String totalEle = response.getTotalEle();
                                                tvEleval.setText(totalEle);
                                                //最高温度
                                                String maxTem = response.getMaxTem();
                                                tvMaxtemval.setText(maxTem);
                                                //最低温度
                                                String minTem = response.getMinTem();
                                                tvMintemval.setText(minTem);
                                                //最高单体电压
                                                String maxOneVol = response.getMaxOneVol();
                                                tvMaxonevolval.setText(maxOneVol);
                                                //最低单体电压
                                                String minOneVol = response.getMinOneVol();
                                                tvMinonevolval.setText(minOneVol);
                                                //控制器温度
                                                String controlTem = response.getControlTem();
                                                tvControltemval.setText(controlTem);
                                                //充电状态
                                                String chargeState = response.getChargeState();
                                                if (chargeState.equals("1")) {
                                                    tvChargestatusval.setText("充电中");
                                                } else {
                                                    tvChargestatusval.setText("未充电");
                                                }
                                                //充电连接状态
                                                String chargeLinkState = response.getChargeLinkState();
                                                if (chargeLinkState.equals("1")) {
                                                    tvChargelinkval.setText("连接");
                                                } else {
                                                    tvChargelinkval.setText("断开");
                                                }
                                                //行驶里程
                                                String meter = response.getRunMeter();
                                                tvRunmeterval.setText(meter);
                                                //故障码
                                                String code = "";
                                                //所有故障综合显示
                                                String allFaultCode = "";
                                                //电机故障
                                                String electricalFault = response.getElectricalFault();
                                                if (electricalFault.equals("0")) {
                                                    //
                                                    code += "0";
                                                } else {
                                                    //
                                                    code += "1";
                                                    allFaultCode += "电机故障：";
                                                    String[] fault = electricalFault.split("/");
                                                    for (int i = 0; i < fault.length; i++) {
                                                        if (fault[i].equals("1")) {
                                                            allFaultCode += faulucode1[i] + "\t\t\t\t";
                                                        } else {

                                                        }
                                                    }
                                                    allFaultCode += "\n\n";
                                                }
                                                //电池故障
                                                String batteryFault = response.getBatteryFault();
                                                if (batteryFault.equals("0")) {
                                                    code += "0";
                                                } else {
                                                    code += "1";
                                                    allFaultCode += "电池故障：";
                                                    String[] fault = new String[16];
                                                    for (int i = 0; i < batteryFault.length(); i++) {
                                                        fault[i] = batteryFault.substring(i, i + 1);
                                                        if (fault[i].equals("1") && i != 3) {
                                                            allFaultCode += faulucode2[i] + "\t\t\t\t";
                                                        } else {

                                                        }
                                                    }
                                                    allFaultCode += "\n\n";
                                                }
                                                //充电机故障
                                                String chargingUnitFault = response.getChargingUnitFault();
                                                if (chargingUnitFault.equals("0")) {
                                                    code += "0";
                                                } else {
                                                    code += "1";
                                                    allFaultCode += "充电机故障：";
                                                    String[] fault = chargingUnitFault.split("/");
                                                    for (int i = 0; i < fault.length; i++) {
                                                        if (fault[i].equals("1")) {
                                                            allFaultCode += faulucode3[i] + "\t\t\t\t";
                                                        } else {

                                                        }
                                                    }
                                                    allFaultCode += "\n\n";
                                                }
                                                //整车故障
                                                String carloadFault = response.getCarloadFault();
                                                if (carloadFault.equals("0")) {
                                                    code += "0";
                                                } else {
                                                    code += "1";
                                                    allFaultCode += "整车故障：";
                                                    allFaultCode += "\n\n";
                                                }
//                                        故障码显示
                                                if(!code.equals("0000"))    {
                                                    relShowfault.setVisibility(View.VISIBLE);
                                                    tvFaultval.setVisibility(View.VISIBLE);
                                                    tvFaultcodeval.setTextColor(Color.RED);
                                                    imFault.setImageDrawable(getResources().getDrawable(R.drawable.iconerror));
                                                    tvFaultcodeval.setText(code);
                                                    //所有故障显示
                                                    tvFaultval.setText(allFaultCode);
                                                }
                                            } else {
                                                Utils.showToastInUI(MainActivity.this, response.getMessage());
                                            }
                                        }
                                    });
                        } else {
                            //无网络连接
                            Utils.showToastInUI(MainActivity.this, "网络连接出错！");
                            return;
                        }

                    } else {
                        //输入错误
                        Utils.showToastInUI(MainActivity.this, "输入不能为空！");
                        return;
                    }
                } catch (Exception e) {
                    Utils.showToastInUI(MainActivity.this, "发生未知错误");
                }
                break;
            }
            case R.id.im_scan:
                try {
                    Intent intent = new Intent(MainActivity.this, CommonScanActivity.class);
                    intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
                    startActivityForResult(intent, REQUSET);
                } catch (Exception e) {
                    Log.i("error", "" + e);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUSET && resultCode == RESULT_OK) {
            edtDeviceno.setText(data.getStringExtra("code"));
        }

    }
}
