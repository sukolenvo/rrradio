package com.dakare.radiorecord.app.download.service.message;

import android.os.Bundle;
import android.os.Message;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
public class RemoveFileResponse extends FileMessage
{
    private static final String IDS_KEY = "id";

    private final ArrayList<Integer> ids = new ArrayList<>();

    public RemoveFileResponse()
    {
        super(FileMessageType.REMOVE_RESPONSE);
    }

    public RemoveFileResponse(final int id)
    {
        super(FileMessageType.REMOVE_RESPONSE);
        addId(id);
    }

    public RemoveFileResponse(final Bundle bundle)
    {
        super(FileMessageType.REMOVE_RESPONSE);
        ids.addAll(bundle.getIntegerArrayList(IDS_KEY));
    }

    public void addId(final int id)
    {
        ids.add(id);
    }

    @Override
    public Message toMessage()
    {
        Message message = super.toMessage();
        Bundle args = new Bundle();
        args.putIntegerArrayList(IDS_KEY, ids);
        message.setData(args);
        return message;
    }
}
