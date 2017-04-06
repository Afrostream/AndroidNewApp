package tv.afrostream.app.utils;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by bahri on 06/04/2017.
 */

public final class SystemUIUtil {

    public static void hideDefaultControls(@NonNull final Activity activity) {
        final Window window = activity.getWindow();

        if (window == null) {
            return;
        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final View decorView = window.getDecorView();

        if (decorView != null) {
            int uiOptions = decorView.getSystemUiVisibility();

            if (Build.VERSION.SDK_INT >= 14) {
                uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }

            if (Build.VERSION.SDK_INT >= 16) {
                uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 19) {
                uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void showDefaultControls(@NonNull final Activity activity) {
        final Window window = activity.getWindow();

        if (window == null) {
            return;
        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        final View decorView = window.getDecorView();

        if (decorView != null) {
            int uiOptions = decorView.getSystemUiVisibility();

            if (Build.VERSION.SDK_INT >= 14) {
                uiOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }

            if (Build.VERSION.SDK_INT >= 16) {
                uiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 19) {
                uiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}