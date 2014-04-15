package com.famo.twentyonedays.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.famo.twentyonedays.R;

public class MyDialog {

	public static AlertDialog Alert(Context context, int resId) {
		return Alert(context, context.getResources().getString(resId));
	}
	public static AlertDialog Alert(Context context, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.tip)
				.setMessage(message)
				.setNegativeButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		return builder.create();
	}
	public static AlertDialog Alert(Context context, int resId,DialogInterface.OnClickListener event) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.tip)
				.setMessage(context.getResources().getString(resId))
				.setNegativeButton(R.string.yes,event);
		return builder.create();
	}
	public static AlertDialog Alert(Context context, CharSequence message,DialogInterface.OnClickListener event) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.tip)
				.setMessage(message)
				.setNegativeButton(R.string.yes,event);
		return builder.create();
	}

	public static AlertDialog Confirm(Context context, int resId,
			DialogInterface.OnClickListener okClickListener,
			DialogInterface.OnClickListener cancelClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.tip)
				.setMessage(context.getResources().getString(resId))
				.setPositiveButton(R.string.yes, okClickListener)
				.setNegativeButton(R.string.no,
						cancelClickListener);
		return builder.create();
	}
	public static AlertDialog Confirm(Context context, CharSequence message,
			DialogInterface.OnClickListener okClickListener,
			DialogInterface.OnClickListener cancelClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.tip)
				.setMessage(message)
				.setPositiveButton(R.string.yes, okClickListener)
				.setNegativeButton(R.string.no,
						cancelClickListener);
		return builder.create();
	}
}
