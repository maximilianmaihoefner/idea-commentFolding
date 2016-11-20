package com.maximilianmaihoefner.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.maximilianmaihoefner.Constants;
import com.maximilianmaihoefner.utils.ApplicationUtils;

/**
 * Expands all collapsed FoldRegions containing comments and removes the FoldRegion.
 *
 * @author Maximilian Maihöfner
 * @since 11/20/2016
 */
public class ExpandCommentsAction extends AnAction
{
    /**
     * Checks if a Project is loaded and the Editor Window is open and hides/shows the action accordingly.
     *
     * @param e The triggered {@link AnActionEvent ActionEvent}.
     */
    @Override
    public void update(AnActionEvent e)
    {
        Project project = e.getData(CommonDataKeys.PROJECT);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        e.getPresentation().setVisible(project != null && editor != null);
    }

    /**
     * Looks for {@link FoldRegion FoldRegions} which contain comments or comment blocks
     * and expands them and removes them.
     *
     * @param e The triggered {@link AnActionEvent ActionEvent}.
     */
    @Override
    public void actionPerformed(AnActionEvent e)
    {
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        final Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                final FoldRegion[] foldRegions = editor.getFoldingModel().getAllFoldRegions();
                for(FoldRegion foldRegion : foldRegions)
                {
                    String text = foldRegion.getDocument().getText(new TextRange(foldRegion.getStartOffset(),
                                                                                 foldRegion.getEndOffset()));

                    if(text.trim().startsWith(Constants.COMMENT))
                    {
                        foldRegion.setExpanded(true);
                        editor.getFoldingModel().removeFoldRegion(foldRegion);
                    }
                }
            }
        };

        ApplicationUtils.invokeRunBatchFoldingOperationLater(editor, runnable);
    }
}
