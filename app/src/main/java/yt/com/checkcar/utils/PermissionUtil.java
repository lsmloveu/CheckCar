package yt.com.checkcar.utils;

import android.content.pm.PackageManager;

/**
 * Created by LSM on 2016/9/29.
 */
public abstract class PermissionUtil {


    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
