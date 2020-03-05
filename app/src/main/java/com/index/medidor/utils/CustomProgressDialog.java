package com.index.medidor.utils;

import android.app.Dialog;
import android.content.Context;

import com.index.medidor.R;

public class CustomProgressDialog extends Dialog {
    MaterialProgressBar progress1;

    Context mContext;
    CustomProgressDialog dialog;
	public CustomProgressDialog(Context context) {
		super(context);
        this.mContext=context;
	}
	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomProgressDialog show(CharSequence message) {

	    dialog = new CustomProgressDialog (mContext, R.style.ProgressDialog);
		dialog.setContentView(R.layout.progress_dialog);

        progress1 =  dialog.findViewById (R.id.progress1);

        progress1.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary);
		dialog.setCancelable(true);
        if(dialog!= null) {
             dialog.show ();
        }
		return dialog;
	}
    public CustomProgressDialog dismiss(CharSequence message) {
        if(dialog!=null) {
            dialog.dismiss();
        }
            return dialog;
    }
}
