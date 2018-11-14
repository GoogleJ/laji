package com.zhishen.aixuexue.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.promeg.pinyinhelper.Pinyin;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.manager.IMAction;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yangfaming on 2018/6/20.
 */

public class CommonUtils {
    private static Toast toast;
    private static long lastClickTime = 0;
    private static long DIFF = 1000;
    private static int lastButtonId = -1;
    private static Dialog dialog;
    private static Dialog aleartDialog;

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showToastShort(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showDialog(Context context, Object loadText) {
        if (context == null || loadText == null) {
            return;
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tv_loading_text = dialog.findViewById(R.id.tv_dialog_content);
        if (loadText instanceof Integer) {
            tv_loading_text.setText(context.getString(((int) loadText)));
        } else if (loadText instanceof String) {
            tv_loading_text.setText((String) loadText);
        }
        dialog.show();

    }

    /**
     * 取消弹窗
     */
    public static void cencelDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * check if network avalable
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 弹窗没有取消按钮
     *
     * @param context
     * @param title
     * @param content
     */
    public static void showAlertDialogNoCancle(Activity context, String title, String content, String cancle, String ok, boolean showTitle, View.OnClickListener onClickListener) {
        if (content == null) {
            return;
        }
        aleartDialog = new Dialog(context, R.style.dialog);
        View dialogView = View.inflate(context, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = dialogView.findViewById(R.id.tv_delete_people);
        TextView tv_delete_title = dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = dialogView.findViewById(R.id.tv_ok);
        tv_cancle.setText(cancle);
        tv_ok.setText(ok);
        tv_delete_title.setText(title);
        tv_delete_people.setText(content);
        if (showTitle) {
            tv_delete_title.setVisibility(View.VISIBLE);
        } else {
            tv_delete_title.setVisibility(View.GONE);
        }
        aleartDialog.setContentView(dialogView);
        aleartDialog.setCanceledOnTouchOutside(false);
        Window window = aleartDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        params.width = (int) (d.widthPixels * 0.7);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        aleartDialog.show();
        tv_ok.setOnClickListener(onClickListener);
        tv_cancle.setOnClickListener(v -> aleartDialog.dismiss());
    }

    public static void canAlertDialog() {
        if (aleartDialog != null) {
            aleartDialog.dismiss();
        }
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            Log.v("isFastDoubleClick", "短时间内按钮多次触发");
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }

    /**
     * 发送广播提示管理员开启全员禁言
     *
     * @param context
     * @param groupId
     * @param object
     */
    public static void sendNoTalkBrocast(Context context, String groupId, JSONObject object) {
        if (context == null) {
            return;
        }
        String adminId = object.getString("adminId");
        String adminNick = object.getString("adminNick");
        String groupName = object.getString("groupName");
        if (TextUtils.isEmpty(adminNick)) {
            adminNick = adminId;
        }
        if (TextUtils.isEmpty(groupName)) {
            groupName = groupId;
        }
        String content = String.format(context.getString(R.string.manager_is_open_no_talk), adminNick, groupName);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.ACTION_HAS_NO_TALK).putExtra(Constant.JSON_KEY_FXID, groupId).putExtra("content", content));
    }

    /**
     * 发送广播提示管理员解除全员禁言
     *
     * @param context
     * @param groupId
     * @param object
     */
    public static void sendCancleNoTalkBrocast(Context context, String groupId, JSONObject object) {
        if (context == null) {
            return;
        }
        String adminId = object.getString("adminId");
        String adminNick = object.getString("adminNick");
        String groupName = object.getString("groupName");
        if (TextUtils.isEmpty(adminNick)) {
            adminNick = adminId;
        }
        if (TextUtils.isEmpty(groupName)) {
            groupName = groupId;
        }
        String content = String.format(context.getString(R.string.manager_is_open_no_talk), adminNick, groupName);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.ACTION_HAS_CANCLED_NO_TALK).putExtra(Constant.JSON_KEY_FXID, groupId).putExtra("content", content));
    }

    public static void setUserInitialLetter(User user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;
        if (!TextUtils.isEmpty(user.getNick())) {
            letter = Pinyin.toPinyin(user.getNick().toCharArray()[0]);
            user.setInitialLetter(letter.toUpperCase().substring(0, 1));
            if (isNumeric(user.getInitialLetter()) || !check(user.getInitialLetter())) {
                user.setInitialLetter("#");
            }
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.getUsername())) {
            letter = Pinyin.toPinyin(user.getUsername().toCharArray()[0]);
        }
        user.setInitialLetter(letter.substring(0, 1));
        if (isNumeric(user.getInitialLetter()) || !check(user.getInitialLetter())) {
            user.setInitialLetter("#");
        }
    }

    public static boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static User Json2User(JSONObject userJson) {
        User user = new User(userJson.getString(Constant.JSON_KEY_HXID));
        user.setNick(userJson.getString(Constant.JSON_KEY_NICK));
        user.setAvatar(userJson.getString(Constant.JSON_KEY_AVATAR));
        user.setUserInfo(userJson.toJSONString());
        CommonUtils.setUserInitialLetter(user);
        return user;
    }

    public static void loadUserAvatar(Context context, Object object, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asBitmap().load(object)
                .apply(RequestOptions.bitmapTransform(new CircleCrop())
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }


    /**
     * 加载群组默认头像
     *
     * @param context
     * @param object
     * @param imageView
     */
    public static void loadGroupAvatar(Context context, Object object, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asBitmap().load(object).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.user_icon_chat_default).error(R.drawable.user_icon_chat_default)).into(imageView);
    }

    public static int getSupportSoftInputHeight(Activity activity) {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight(activity);
            Log.d("observeSoftKeyboard---9", String.valueOf(getSoftButtonsBarHeight(activity)));

        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }

        return softInputHeight;
    }

    private static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        }
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    public static boolean isSdcardExist() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static void customizeMarkerIcon(final Context context, final Object url, final OnMarkerIconLoadListener listener) {
        if (context == null) {
            return;
        }
        final View markerView = LayoutInflater.from(context).inflate(R.layout.layout_marker, null);
        final CircleImageView icon = markerView.findViewById(R.id.marker_item_icon);
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .centerCrop()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                //待图片加载完毕后再设置bitmapDes
                if (bitmap == null) {
                    icon.setImageResource(R.drawable.default_avatar);
                } else {
                    icon.setImageBitmap(bitmap);
                }
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(markerView);
                listener.markerIconLoadingFinished(markerView, descriptor);
            }
        });
    }

    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            Log.d("CommonUtils", "-----context is null");
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            if (bMute) {
                int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            } else {
                int result = am.abandonAudioFocus(null);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
        }
        return bool;
    }

    public static void showAlertDialog(Activity context, String title, String content, final OnDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = (TextView) dialogView.findViewById(R.id.tv_delete_people);
        View view_line_dialog = dialogView.findViewById(R.id.view_line_dialog);
        TextView tv_delete_title = (TextView) dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        tv_delete_title.setText(TextUtils.isEmpty(title) ? context.getString(R.string.prompt) : title);
        tv_delete_people.setText(content);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onPriformClock();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onCancleClock();
            }
        });
    }

    //键盘显示监听
    public static void observeSoftKeyboard(final Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - rect.bottom;

                if (Build.VERSION.SDK_INT >= 20) {
                    // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
                    keyboardHeight = keyboardHeight - getSoftButtonsBarHeight(activity);

                }

                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(keyboardHeight, !hide, this);
                }

                previousKeyboardHeight = height;

            }
        });
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener);
    }

    public interface OnDialogClickListener {
        void onPriformClock();

        void onCancleClock();
    }

    public interface OnMarkerIconLoadListener {
        void markerIconLoadingFinished(View view, BitmapDescriptor descriptor);
    }
}
