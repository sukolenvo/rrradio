package com.dakare.radiorecord.app.download.service.message;

import android.os.Bundle;
import android.os.Message;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import lombok.Data;

@Data
public class UpdateFileMessage extends FileMessage
{
    private static final String ID_KEY = "id";
    private static final String SIZE_KEY = "size";
    private static final String TOTAL_KEY = "total";
    private static final String STATUS_KEY = "status";

    private final long id;
    private long size;
    private long total;
    private DownloadAudioTable.Status status;

    public UpdateFileMessage(final long id, final DownloadAudioTable.Status status)
    {
        super(FileMessageType.UPDATE_ITEM);
        this.id = id;
        this.status = status;
    }

    public UpdateFileMessage(final long id, final long size, final long total)
    {
        super(FileMessageType.UPDATE_ITEM);
        this.id = id;
        this.size = size;
        this.total = total;
    }

    public UpdateFileMessage(final Bundle bundle)
    {
        super(FileMessageType.UPDATE_ITEM);
        this.id = bundle.getLong(ID_KEY);
        this.size = bundle.getLong(SIZE_KEY, 0);
        this.total = bundle.getLong(TOTAL_KEY, 0);
        if (bundle.containsKey(STATUS_KEY))
        {
            status = DownloadAudioTable.Status.valueOf(bundle.getString(STATUS_KEY));
        }
    }

    @Override
    public Message toMessage()
    {
        Message message = super.toMessage();
        Bundle args = new Bundle();
        args.putLong(ID_KEY, id);
        args.putLong(SIZE_KEY, size);
        args.putLong(TOTAL_KEY, total);
        if (status != null)
        {
            args.putString(STATUS_KEY, status.name());
        }
        message.setData(args);
        return message;
    }
}
