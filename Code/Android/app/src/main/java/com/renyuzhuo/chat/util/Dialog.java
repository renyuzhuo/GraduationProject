package com.renyuzhuo.chat.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.Message;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.model.TeamMessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dialog {
    static Context context;
    public static final int PHOTOHRAPH = 0;
    public static final int PHOTOZOOM = 1;
    public static final int PHOTORESOULT = 2;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static String tempPath;

    public static void setPhotoDialog(Context context) {
        Dialog.context = context;
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_photo, null);
        dialog.setView(view);
        dialog.show();

        Window win = dialog.getWindow();
        win.setContentView(R.layout.dialog_choose_photo);
        Button layout_local = (Button) win.findViewById(R.id.dialog_select_photo_local_sdcard);
        Button layout_camera = (Button) win.findViewById(R.id.dialog_select_photo_open_camera);

        layout_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                ((Activity) (Dialog.context)).startActivityForResult(intent, PHOTOZOOM);
            }
        });

        layout_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                tempPath = sdf.format(new Date()) + ".jpg";
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Global.TEMP_PATH, tempPath)));
                ((Activity) (Dialog.context)).startActivityForResult(intent2, PHOTOHRAPH);
            }
        });
        dialog.show();
    }

    public static void startPhotoZoom(Context context, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        ((Activity) context).startActivityForResult(intent, PHOTORESOULT);
    }

    public static void setReSendDialog(final Context context, final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.resend));
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                message.setToken(ChatApplication.getToken());
                ChatService.sendMessage(message);
                return;
            }
        }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });
        builder.create().show();
    }

    public static void setReSendDialog(final Context context, final TeamMessage teamMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.resend));
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                teamMessage.setToken(ChatApplication.getToken());
                ChatService.sendMessage(teamMessage);
                return;
            }
        }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });
        builder.create().show();
    }
}
