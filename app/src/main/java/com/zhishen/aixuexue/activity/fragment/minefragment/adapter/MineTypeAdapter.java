package com.zhishen.aixuexue.activity.fragment.minefragment.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.htmessage.sdk.client.HTClient;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.weight.NoScrollGridView;

import java.util.List;

/**
 * Created by Jerome on 2018/6/28
 */
public class MineTypeAdapter extends BaseMultiItemQuickAdapter<MultiTypeItem, BaseViewHolder> {
    private MenuAdapter menuAdapter;
    private MenuCourseAdapter courseAdapter;
    private MenuInfoAdapter infoAdapter;
    private MenuToolsAdapter toolsAdapter;
    private UserInfoItemOnClickListener infoItemOnClickListener;
    private UserCourseItemOnClickListener courseItemOnClickListener;
    private UserMenuItemOnClickListener userMenuItemOnClickListener;
    private UserToolsItemOnClickListener userToolsItemOnClickListener;

    public MineTypeAdapter() {
        super(null);
        addItemType(MultiTypeItem.MINE_USER_INFO, R.layout.view_mine_usr_info);
        addItemType(MultiTypeItem.MINE_USER_MENU, R.layout.view_mine_usr_menu);
        addItemType(MultiTypeItem.MINE_USER_COURSE, R.layout.view_mine_usr_tools);
        addItemType(MultiTypeItem.MINE_USER_TOOLS, R.layout.view_mine_usr_tools);
    }

    public void setUserMenuItemOnClickListener(UserMenuItemOnClickListener userMenuItemOnClickListener) {
        if (userMenuItemOnClickListener == null) {
            throw new NullPointerException("info listener is null");
        }
        this.userMenuItemOnClickListener = userMenuItemOnClickListener;
    }

    public void setInfoItemOnClickListener(UserInfoItemOnClickListener infoItemOnClickListener) {
        if (infoItemOnClickListener == null) {
            throw new NullPointerException("info listener is null");
        }
        this.infoItemOnClickListener = infoItemOnClickListener;
    }

    public void setCourseItemOnClickListener(UserCourseItemOnClickListener courseItemOnClickListener) {
        if (courseItemOnClickListener == null) {
            throw new NullPointerException("course listener is null");
        }
        this.courseItemOnClickListener = courseItemOnClickListener;
    }

    public void setUserToolsItemOnClickListener(UserToolsItemOnClickListener userToolsItemOnClickListener) {
        if (userToolsItemOnClickListener == null) {
            this.userToolsItemOnClickListener = userToolsItemOnClickListener;
        }
        this.userToolsItemOnClickListener = userToolsItemOnClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiTypeItem item) {
        switch (helper.getItemViewType()) {
            case MultiTypeItem.MINE_USER_INFO: {
                HomeBean.UserMenuBean usrInfoBean = (HomeBean.UserMenuBean) item.getData();
                if (usrInfoBean == null)
                    return;
                ImageView avatar = helper.getView(R.id.iv_mine_avatar);
                NoScrollGridView gvInfoView = helper.getView(R.id.gvInfoView);

                boolean isLogin = HTClient.getInstance().isLogined();
                BitmapUtil.loadCircleImg(avatar, AiApp.getInstance().getUserAvatar(), R.drawable.default_avatar);
                helper.setGone(R.id.tv_mine_id, isLogin)
                        .setText(R.id.tv_mine_id, isLogin ? "ID:" + AiApp.getInstance().getUsername() : "")
                        .setText(R.id.tvName, isLogin ? AiApp.getInstance().getUserNick() : "登录/注册")
                        .addOnClickListener(R.id.tvSign).addOnClickListener(R.id.ll_mine_user);

                gvInfoView.setAdapter(infoAdapter = new MenuInfoAdapter(mContext));
                gvInfoView.setOnItemClickListener((parent, view, position, id) -> {
                    if (infoItemOnClickListener != null) {
                        infoItemOnClickListener.getFollowInfo(infoAdapter.getItem(position).getId());
                    }
                });
                infoAdapter.addData(usrInfoBean.getList());
            }
            break;
            case MultiTypeItem.MINE_USER_MENU: {
                HomeBean.UserMenuBean menu = (HomeBean.UserMenuBean) item.getData();
                if (null == menu) return;
                NoScrollGridView menuView = helper.getView(R.id.toolGridView);
                menuView.setAdapter(menuAdapter = new MenuAdapter(mContext));
                menuAdapter.addData(menu.getList());
                menuView.setOnItemClickListener((parent, view, position, id) -> {
                    if (userMenuItemOnClickListener != null) {
                        String itemId = menuAdapter.getItem(position).getId();
                        userMenuItemOnClickListener.getMenuInfo(itemId, menuAdapter.getItem(position));
                    }
                });
            }
            break;
            case MultiTypeItem.MINE_USER_COURSE: {
                HomeBean.UserMenuBean course = (HomeBean.UserMenuBean) item.getData();
                if (course == null) return;
                helper.setText(R.id.tvTitle, course.getGroupTitle());
                NoScrollGridView courseView = helper.getView(R.id.toolGridView);
                courseView.setAdapter(courseAdapter = new MenuCourseAdapter(mContext));
                courseAdapter.addData(course.getList());
                courseView.setOnItemClickListener((parent, view, position, id) -> {
                    if (courseItemOnClickListener != null) {
                        String itemId = courseAdapter.getItem(position).getId();
                        courseItemOnClickListener.getCourseInfo(itemId, courseAdapter.getItem(position));
                    }
                });
            }
            break;
            case MultiTypeItem.MINE_USER_TOOLS: {
                HomeBean.UserMenuBean toolBean = (HomeBean.UserMenuBean) item.getData();
                if (toolBean == null) return;
                helper.setText(R.id.tvTitle, toolBean.getGroupTitle());
                NoScrollGridView toolGridView = helper.getView(R.id.toolGridView);
                toolGridView.setAdapter(toolsAdapter = new MenuToolsAdapter(mContext));
                toolsAdapter.addData(toolBean.getList());
                toolGridView.setOnItemClickListener((parent, view, position, id) -> {
                    if (userToolsItemOnClickListener != null) {
                        String itemId = toolsAdapter.getItem(position).getId();
                        String title = toolsAdapter.getItem(position).getTitle();
                        userToolsItemOnClickListener.getToolsInfo(itemId, toolsAdapter.getItem(position), title);
                    }
                });
            }
            break;
            default:
                break;
        }
    }

    public void updateCount(List<HomeBean.UserMenuBean.UserMenu> usrMenu) {
        infoAdapter.update(usrMenu);
        notifyDataSetChanged();
    }

    public interface UserInfoItemOnClickListener {

        void getFollowInfo(String id);
    }

    public interface UserCourseItemOnClickListener {

        void getCourseInfo(String id, HomeBean.UserMenuBean.UserMenu item);
    }

    public interface UserMenuItemOnClickListener {
        void getMenuInfo(String id, HomeBean.UserMenuBean.UserMenu item);
    }

    public interface UserToolsItemOnClickListener {

        void getToolsInfo(String id, HomeBean.UserMenuBean.UserMenu item, String title);
    }
}
