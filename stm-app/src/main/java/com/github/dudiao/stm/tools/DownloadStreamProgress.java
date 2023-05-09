package com.github.dudiao.stm.tools;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.io.unit.DataSizeUtil;

/**
 * @author songyinyin
 * @since 2023/5/9 17:27
 */
public class DownloadStreamProgress implements StreamProgress {

    private double percent = 10;

    private boolean isPrint = false;

    @Override
    public void start() {
        System.out.print("开始下载");
    }

    @Override
    public void progress(long total, long progressSize) {
        if (total > 0) {
            if (!isPrint) {
                String format = DataSizeUtil.format(total);
                System.out.printf("，总大小：%s. ->", format);
                isPrint = true;
            }
            double progressPercentage = Math.floor(((float) progressSize / total) * 100);
            if (progressPercentage > percent) {
                System.out.print("->");
                percent += 10;
            }
        }

    }

    @Override
    public void finish() {
        System.out.println("\n下载完成");
    }
}