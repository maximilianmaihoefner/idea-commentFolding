package com.maximilianmaihoefner.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;

/**
 * Utility methods related to the {@link ApplicationManager}.
 *
 * @author Maximilian Maihöfner
 * @since 11/20/2016
 */
public abstract class ApplicationUtils
{
    /**
     * Calls the {@link com.intellij.openapi.editor.FoldingModel#runBatchFoldingOperation} of the Editor using
     * {@link com.intellij.openapi.application.Application#invokeLater(Runnable)} with the supplied Runnable.
     *
     * @param editor The active {@link Editor} instance.
     * @param runnable The {@link Runnable} which should be executed.
     */
    public static void invokeRunBatchFoldingOperationLater(final Editor editor, final Runnable runnable)
    {
        ApplicationManager.getApplication().invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                editor.getFoldingModel().runBatchFoldingOperation(runnable);
            }
        });
    }
}
