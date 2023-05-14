package io.github.apprunner.tools;

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
        System.out.print("start download ->");
    }

    @Override
    public void progress(long total, long progressSize) {
        if (total > 0) {
            if (!isPrint) {
                String format = DataSizeUtil.format(total);
                System.out.printf(", total size: %s. ->", format);
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
        System.out.println("\nfinish download.");
    }
}