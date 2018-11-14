/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.util.CommonUtils;


public class User implements Parcelable {

    /**
     * initial letter for nickname
     */
    protected String initialLetter;

    protected User(Parcel in) {
        initialLetter = in.readString();
        avatar = in.readString();
        username = in.readString();
        userInfo = in.readString();
        nick = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * avatar of the user
     */
    protected String avatar;
    private String username;
    protected String userInfo;
    private String nick;
    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String avatar, String nick) {
        this.username = username;
        this.nick = nick;
        this.avatar = avatar;
    }

    public String getInitialLetter() {
        if (initialLetter == null) {
            CommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }


    public String getAvatar() {
        if (!TextUtils.isEmpty(avatar)) {
            if (!avatar.contains("http")) {
                avatar = Constant.NEW_API_HOST + "upload/" + avatar;
            }
        }
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserInfo() {

        return userInfo;

    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof User)) {
            return false;
        }
        return getUsername().equals(((User) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(initialLetter);
        parcel.writeString(avatar);
        parcel.writeString(username);
        parcel.writeString(userInfo);
        parcel.writeString(nick);
    }
}
