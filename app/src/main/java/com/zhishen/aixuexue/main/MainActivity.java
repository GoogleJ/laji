package com.zhishen.aixuexue.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.LoginActivity;
import com.zhishen.aixuexue.activity.fragment.findfragment.FindFragment;
import com.zhishen.aixuexue.activity.fragment.homefragment.HomeFragment;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.InterActionFragment;
import com.zhishen.aixuexue.activity.fragment.minefragment.MineFragment;
import com.zhishen.aixuexue.activity.fragment.worldfragment.WorldFakeActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.WorldFragment;
import com.zhishen.aixuexue.manager.IMAction;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.StatusBarHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends FragmentActivity implements InterActionFragment.OnClickBottomListener {
    @BindView(R.id.tv_main_find)
    TextView tvMainFind;
    @BindView(R.id.iv_main_find)
    CircleImageView ivMainFind;
    @BindView(R.id.rl_main_find)
    RelativeLayout rlMainFind;
    @BindView(R.id.tv_main_world)
    TextView tvMainWorld;
    @BindView(R.id.iv_main_world)
    ImageView ivMainWorld;
    @BindView(R.id.rl_main_world)
    RelativeLayout rlMainWorld;
    @BindView(R.id.iv_main_interaction)
    ImageView ivMainInteraction;
    @BindView(R.id.tv_main_interaction)
    TextView tvMainInteraction;
    @BindView(R.id.rl_main_interaction)
    RelativeLayout rlMainInteraction;
    @BindView(R.id.tv_main_home)
    TextView tvMainHome;
    @BindView(R.id.iv_main_home)
    ImageView ivMainHome;
    @BindView(R.id.rl_main_home)
    RelativeLayout rlMainHome;
    @BindView(R.id.tv_main_me)
    TextView tvMainMe;
    @BindView(R.id.iv_main_me)
    ImageView ivMainMe;
    @BindView(R.id.rl_main_me)
    RelativeLayout rlMainMe;
    @BindView(R.id.unread_msg_number)
    TextView unread_msg_number;
    private String TAG = getClass().getName();
    private FindFragment findFragment;
    private WorldFragment worldFragment;
    private InterActionFragment interactionFragment;
    private HomeFragment HomeFragment;
    private MineFragment meFragment;
    private boolean isConflictDialogShow;
    private AlertDialog.Builder exceptionBuilder;
    public boolean isConflict = false;
    private int position = 2;
    public static final String ACTION_TYPE_POSITION = "action_type";
    private static UnReadMessageClick click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (hasKitKat() && !hasLollipop()) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        StatusBarHelper.setStatusBarLightMode(this);
        if (getIntent().getBooleanExtra(IMAction.ACTION_CONFLICT, false) && !isConflictDialogShow) {
            showConflicDialog();
        }
        ButterKnife.bind(this);
        showFragment(position);
        registReceiver();
    }

    private void initUnReadMessageNumber() {
        unReadNumber = 0;
        List<HTConversation> list = HTClient.getInstance().conversationManager().getAllConversations();
        hasUnReadInviateNubmer = LocalUserManager.getInstance().getHasUnReadInviateNubmer();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HTConversation conversation = list.get(i);
                unReadNumber += conversation.getUnReadCount();
            }
        }
        showUnreadNumber(unReadNumber);
        Log.d("1212", "mainactivity 第一次初始化值  " + unReadNumber + "     " + hasUnReadInviateNubmer);
    }

    private void showConflicDialog() {
        isConflictDialogShow = true;
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (exceptionBuilder == null)
                    exceptionBuilder = new AlertDialog.Builder(MainActivity.this);
                exceptionBuilder.setTitle(st);
                exceptionBuilder.setMessage(R.string.connect_conflict);
                exceptionBuilder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    exceptionBuilder = null;
                    isConflictDialogShow = false;
                    AiApp.getInstance().setUserJson(null);
                    finish();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                });
                exceptionBuilder.setCancelable(false);
                exceptionBuilder.show();
                isConflict = true;
            } catch (Exception e) {
                Log.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("position");
    }

    public void showFragment(int position) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        resetTab();
        hideFragment(transaction);
        switch (position) {
            case 0://世界界面
                if (interactionFragment == null) {
                    interactionFragment = new InterActionFragment();
                    interactionFragment.setOnClickBottomListener(this);
                    transaction.add(R.id.fl_content, interactionFragment);
                } else {
                    transaction.show(interactionFragment);
                }
                ivMainHome.setImageResource(R.drawable.tab_home_select);
                tvMainHome.setTextColor(getResources().getColor(R.color.light_blue));
                break;
            case 1://互动界面

                if (findFragment == null) {
                    findFragment = new FindFragment();
                    transaction.add(R.id.fl_content, findFragment);
                } else {
                    transaction.show(findFragment);
                }
                ivMainInteraction.setImageResource(R.drawable.tab_interaction_select);
                tvMainInteraction.setTextColor(getResources().getColor(R.color.light_blue));

                break;


            case 2://爱学学界面

                if (HomeFragment == null) {
                    HomeFragment = new HomeFragment();
                    transaction.add(R.id.fl_content, HomeFragment);
                } else {
                    transaction.show(HomeFragment);
                }
                ivMainFind.setImageResource(R.drawable.tab_find_select);
                tvMainFind.setTextColor(getResources().getColor(R.color.light_blue));
                break;

            case 3://发现界面
                Bundle bundle = new Bundle();
                if (worldFragment == null) {
                    worldFragment = new WorldFragment();
                    transaction.add(R.id.fl_content, worldFragment);
                } else {
                    transaction.show(worldFragment);
                }
                bundle.putInt(WorldFakeActivity.CIRCLE_TYPE, 0);
                worldFragment.setArguments(bundle);
                ivMainWorld.setImageResource(R.drawable.tab_friends_select);
                tvMainWorld.setTextColor(getResources().getColor(R.color.light_blue));
                break;
            case 4://我的界面
                if (meFragment == null) {
                    meFragment = new MineFragment();
                    transaction.add(R.id.fl_content, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                ivMainMe.setImageResource(R.drawable.tab_me_select);
                tvMainMe.setTextColor(getResources().getColor(R.color.light_blue));

                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (findFragment != null) {
            transaction.hide(findFragment);
        }
        if (worldFragment != null) {
            transaction.hide(worldFragment);
        }
        if (interactionFragment != null) {
            transaction.hide(interactionFragment);
        }
        if (HomeFragment != null) {
            transaction.hide(HomeFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
    }

    @OnClick({R.id.rl_main_find, R.id.rl_main_world, R.id.rl_main_interaction, R.id.rl_main_home, R.id.rl_main_me})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_main_home:
                showFragment(0);

                break;
            case R.id.rl_main_interaction:
                showFragment(1);

                break;
            case R.id.rl_main_find:
                showFragment(2);
                break;


            case R.id.rl_main_world:
                showFragment(3);

                break;
            case R.id.rl_main_me:
                showFragment(4);

                break;
        }
    }

    private void resetTab() {
        ivMainFind.setImageResource(R.drawable.tab_find_select);
        ivMainWorld.setImageResource(R.drawable.tab_friends_normal);
        ivMainInteraction.setImageResource(R.drawable.tab_interaction_normal);
        ivMainHome.setImageResource(R.drawable.tab_home_normal);
        ivMainMe.setImageResource(R.drawable.tab_me_normal);

        tvMainFind.setTextColor(getResources().getColor(R.color.dark));
        tvMainWorld.setTextColor(getResources().getColor(R.color.dark));
        tvMainInteraction.setTextColor(getResources().getColor(R.color.dark));
        tvMainHome.setTextColor(getResources().getColor(R.color.dark));
        tvMainMe.setTextColor(getResources().getColor(R.color.dark));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exceptionBuilder != null) {
            exceptionBuilder.create().dismiss();
            exceptionBuilder = null;
            isConflictDialogShow = false;
        }
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showFragment(position = intent.getIntExtra(ACTION_TYPE_POSITION, position));
        if (intent.getBooleanExtra(IMAction.ACTION_CONFLICT, false) && !isConflictDialogShow) {
            showConflicDialog();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unReadNumber = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUnReadMessageNumber();
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public void onClick() {
        showFragment(1);
    }

    private int unReadNumber;
    boolean hasUnReadInviateNubmer;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unReadNumber = intent.getIntExtra("unReadNumber", 0);
            hasUnReadInviateNubmer = LocalUserManager.getInstance().getHasUnReadInviateNubmer();
            Log.d("1212", "  mainactivity 广播接收到的消息数   unreadnumber  ===  " + unReadNumber + "   hasUnReadInviateNubmer  ----   " + hasUnReadInviateNubmer);
            String action = intent.getAction();
            if (action.equals(IMAction.ACTION_NEW_MESSAGE)) {//新消息未读数
                showUnreadNumber(unReadNumber);
                if (click != null) {
                    click.setUnReadMessageNumber(unReadNumber);
                }
            } else if (IMAction.ACTION_INVITE_MESSAGE.equals(action)) {//新邀请未读数
                if (unread_msg_number.getText().toString().equals("")) {
                    if (hasUnReadInviateNubmer) {
                        unread_msg_number.setVisibility(View.VISIBLE);
                    } else {
                        unread_msg_number.setVisibility(View.GONE);
                    }
                } else {
                    if (TextUtils.isEmpty(unread_msg_number.getText().toString())) {
                        showUnreadNumber(unReadNumber);
                    } else {
                        showUnreadNumber(Integer.parseInt(unread_msg_number.getText().toString()) + unReadNumber);
                    }
                }
                if (click != null) {
                    click.hasInviteMessage(hasUnReadInviateNubmer);
                }
            } else if (action.equals("ConversationUnReadNumber")) {//已读消息剩余数
                showUnreadNumber(unReadNumber);
                if (click != null) {
                    click.setUnReadMessageNumber(unReadNumber);
                }
            } else if (action.equals("InviteMessageUnreadNumber")) {//已读邀请剩余数
                if (click != null) {
                    click.hasInviteMessage(hasUnReadInviateNubmer);
                }
                if (TextUtils.isEmpty(unread_msg_number.getText().toString())) {
                    showUnreadNumber(unReadNumber);
                } else {
                    showUnreadNumber(Integer.parseInt(unread_msg_number.getText().toString()) + unReadNumber);
                }
            }
        }
    };

    private void registReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_INVITE_MESSAGE);
        intentFilter.addAction("ConversationUnReadNumber");
        intentFilter.addAction("InviteMessageUnreadNumber");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void showUnreadNumber(int count) {
        if (count <= 0) {
            if (hasUnReadInviateNubmer) {
                unread_msg_number.setVisibility(View.VISIBLE);
                unread_msg_number.setText("");
            } else {
                unread_msg_number.setVisibility(View.GONE);
            }
        } else {
            unread_msg_number.setVisibility(View.VISIBLE);
            unread_msg_number.setText(count + "");
        }
    }

    public interface UnReadMessageClick {
        void setUnReadMessageNumber(int count);

        void hasInviteMessage(boolean hasMessage);
    }

    public void setClick(UnReadMessageClick click) {
        MainActivity.click = click;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long startTime;

    /**
     * 退出APP
     */
    private void exitApp() {
        if (System.currentTimeMillis() - startTime >= 2000) {
            CommonUtils.showToastShort(this, "再按一次退出");
            startTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
