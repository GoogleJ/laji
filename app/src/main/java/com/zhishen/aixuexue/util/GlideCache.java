package com.zhishen.aixuexue.util;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;

public class GlideCache implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 1000; //1G
        builder.setDiskCache(
                new DiskLruCacheFactory( context.getCacheDir().getAbsolutePath()+"/GlideDisk", diskCacheSizeBytes )
        );
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {

    }
}
