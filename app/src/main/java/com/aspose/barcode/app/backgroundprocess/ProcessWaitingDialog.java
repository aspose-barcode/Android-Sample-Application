package com.aspose.barcode.app.backgroundprocess;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.aspose.barcode.app.R;
import com.aspose.barcode.app.backgroundprocess.BackgroundProcess;
import com.aspose.barcode.app.backgroundprocess.ProcessFinishedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessWaitingDialog<T extends BackgroundProcess>  extends AlertDialog.Builder
{
    protected final T process;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private List<DialogInterface.OnDismissListener> dismissListeners = new ArrayList<>();

    public ProcessWaitingDialog(@NonNull Context context, T process, String message)
    {
        super(context);

        this.setView(R.layout.progress);
        this.setMessage(message);
        setCancelable(false);
        setNegativeButton("CANCEL", (dialog, which) -> finishProcess());
        this.process = process;
    }

    @Override
    public AlertDialog show()
    {
        AlertDialog alertDialog = super.show();
        process.setProcessFinishedListener(alertDialog::cancel);
        alertDialog.setOnCancelListener(dialog -> finishProcess());
        executor.submit(process);
        return alertDialog;
    }

    private void finishProcess()
    {
        executor.shutdownNow();
        process.processBackgroundResults();
    }
}