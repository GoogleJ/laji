package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.QRCodeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jerome on 2018/7/7
 */
public class UserImgCodeActivity extends BaseActivity {
    private String TAG = UserImgCodeActivity.class.getSimpleName();
    private Unbinder unbinder = null;
    @BindView(R.id.ivPhoto) ImageView ivPhoto;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvContent) TextView tvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_imgcode);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));
        CreateQrCode();
    }

    private void showScanCode(Bitmap bitmap) {
        JSONObject userJson = AiApp.getInstance().getUserJson();
        tvName.setText(AiApp.getInstance().getUserNick());
        if (userJson != null) {
            tvContent.setText(userJson.getString(Constant.JSON_KEY_CITY));
        } else {
            tvContent.setText("暂未设置位置");
        }
        BitmapUtil.loadCircleImg(ivAvatar, AiApp.getInstance().getUserAvatar(), R.drawable.default_avatar);
        ivPhoto.setImageBitmap(bitmap);
    }

    public void CreateQrCode() {
        JSONObject userJson = AiApp.getInstance().getUserJson();
        JSONObject allobj = new JSONObject();
        allobj.put("codeType", 2);
        JSONObject object = new JSONObject();
        object.put(Constant.JSON_KEY_HXID, AiApp.getInstance().getUsername());
        object.put(Constant.JSON_KEY_NICK, userJson.getString(Constant.JSON_KEY_NICK));
        object.put(Constant.JSON_KEY_TEL, userJson.getString(Constant.JSON_KEY_TEL));
        object.put(Constant.JSON_KEY_FXID, userJson.getString(Constant.JSON_KEY_FXID));
        object.put(Constant.JSON_KEY_SEX, userJson.getString(Constant.JSON_KEY_SEX));
        object.put(Constant.JSON_KEY_AVATAR, userJson.getString(Constant.JSON_KEY_AVATAR));
        object.put(Constant.JSON_KEY_PROVINCE, userJson.getString(Constant.JSON_KEY_PROVINCE));
        object.put(Constant.JSON_KEY_CITY, userJson.getString(Constant.JSON_KEY_CITY));
        object.put(Constant.JSON_KEY_SIGN, userJson.getString(Constant.JSON_KEY_SIGN));
        allobj.put("data", object);
        Log.d(TAG, "------------" + allobj.toString());
        Bitmap bitmap = QRCodeUtil.createQRImage(allobj.toJSONString());
        showScanCode(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
