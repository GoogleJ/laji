package com.zhishen.aixuexue.activity.newfriend;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendsFinalActivity extends BaseActivity {

    @BindView(R.id.et_reason)
    EditText etReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_final);
        ButterKnife.bind(this);
        setTitle(getString(R.string.add_friend));
        String userInfo = getIntent().getStringExtra(Constant.KEY_USER_INFO);

        JSONObject finalJsonObject = null;

        if (TextUtils.isEmpty(userInfo)) {

        } else {
            JSONObject jsonObject = null;
            try {
                jsonObject = JSONObject.parseObject(userInfo);
            } catch (JSONException e) {
            }
            if (jsonObject == null) {
                finish();
                return;
            }
            finalJsonObject = jsonObject;
        }

        initView();

        JSONObject finalJsonObject1 = finalJsonObject;
        showRightTextView(getString(R.string.button_send), view -> addContact(finalJsonObject1 == null ? getIntent().getStringExtra("userId") : finalJsonObject1.getString(Constant.JSON_KEY_HXID), etReason.getText().toString().trim()));
    }

    private void initView() {
        etReason.setText(getString(R.string.i_am) + AiApp.getInstance().getUserJson().getString(Constant.JSON_KEY_NICK));
        if (etReason.getText() instanceof Spannable) {
            Spannable spanText = etReason.getText();
            Selection.setSelection(spanText, etReason.getText().length());
        }
    }

    /**
     * 添加contact
     *
     * @param
     */
    public void addContact(final String hxid, String reason) {
        CommonUtils.showDialog(mActivity, getString(R.string.Is_sending_a_request));
        JSONObject userJson = AiApp.getInstance().getUserJson();
        JSONObject data = new JSONObject();
        data.put("ADD_REASON", reason);
        data.put("userId", userJson.getString("userId"));
        data.put("nick", userJson.getString("nick"));
        data.put("avatar", userJson.getString("avatar"));
        data.put("role", userJson.getString(Constant.JSON_KEY_ROLE));
        data.put("teamId", userJson.getString("teamId"));
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("action", 1000);
        bodyJson.put("data", data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setBody(bodyJson.toJSONString());
        customMessage.setFrom(AiApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(hxid);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(AddFriendsFinalActivity.this, R.string.send_successful);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(AddFriendsFinalActivity.this, R.string.Request_add_buddy_failure);
                        finish();
                    }
                });
            }
        });
    }
}
