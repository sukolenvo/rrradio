package com.dakare.radiorecord.app.message_studio;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.utils.AbstractDialog;

public class MessageStudioDialog extends AbstractDialog {

    public MessageStudioDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_message_studio);
        final TextView phoneView = (TextView) findViewById(R.id.phone);
        String phoneNumber = PreferenceManager.getInstance(context).getPhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            phoneView.setText(phoneNumber);
        }
        final TextView messageView = (TextView) findViewById(R.id.message);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageView.getText() == null ? "" : messageView.getText().toString();
                message = message.trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(context, R.string.warning_empty_message, Toast.LENGTH_LONG).show();
                } else {
                    String phone = phoneView.getText() == null ? "" : phoneView.getText().toString();
                    phone = phone.trim();
                    if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(context, R.string.warning_empty_phone, Toast.LENGTH_LONG).show();
                    } else {
                        PreferenceManager.getInstance(context).setPhoneNumber(phone);
                        new MessageSender().sendMessage(message, phone);
                        dismiss();
                    }
                }
            }
        });
    }

    public static void show(Context context) {
        new MessageStudioDialog(context).show();
    }
}
