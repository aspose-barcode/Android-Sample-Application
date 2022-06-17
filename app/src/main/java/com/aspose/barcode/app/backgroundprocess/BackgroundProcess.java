package com.aspose.barcode.app.backgroundprocess;

public abstract class BackgroundProcess implements Runnable
{
    private ProcessFinishedListener listener;

    protected abstract void runProcess();

    public abstract void processBackgroundResults();

    @Override
    public final void run()
    {
        runProcess();
        if(listener != null)
            listener.invoke();
    }

    public void setProcessFinishedListener(ProcessFinishedListener listener)
    {
        this.listener = listener;
    }
}