package com.example.smuocr;

import android.Manifest;
import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class Permission {

    /*
        Description : in the android system,
                      permission is required to use an external device.
                      So, it is the class that gets that permission.
     */
    Context p_context;

    public Permission(Context context) {
        p_context = context;
    }

    /*
        Description : TedPermission lib for setting permissions.
        Argument : None
        Return : None
     */

    public void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Constant.isPermission = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Constant.isPermission = false;
            }
        };

        TedPermission.with(p_context)

                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                .setDeniedMessage("권한을 허용해야 앱을 사용할 수 있습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }
}
