package com.maximilianmaihoefner.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.project.Project;
import com.maximilianmaihoefner.Constants;
import com.maximilianmaihoefner.utils.ApplicationUtils;

/**
 * Collapses lines starting with "//" e.g. comments and checks the next lines and combines them into
 * one Folding if it is also a comment or checks the following line if it is empty.
 *
 * @author Maximilian Maihöfner
 * @since 11/20/2016
 */
public class CollapseCommentsAction extends AnAction
{
    private static final Logger logger = Logger.getInstance(CollapseCommentsAction.class);

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
     * Creates {@link FoldRegion FoldRegions} for comments or comment blocks and collapses them.
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
                final Document document = editor.getDocument();
                final String text = document.getText();
                final String[] lines = text.split(Constants.LINE_BREAK);

                logger.debug("Line count: " + lines.length);

                for(int i = 0; i < lines.length; i++)
                {
                    String line = lines[i];
                    logger.debug(line);

                    if(line.trim().startsWith(Constants.COMMENT))
                    {
                        final int startIndex = text.indexOf(line);
                        final int endIndex = getEndIndex(i, text, lines);

                        logger.debug("startIndex: " + startIndex + ", endIndex: " + endIndex);

                        FoldRegion foldRegion = editor.getFoldingModel().getFoldRegion(startIndex, endIndex);

                        if(foldRegion == null)
                        {
                            foldRegion = editor.getFoldingModel().addFoldRegion(startIndex, endIndex, "_");
                        }

                        if(foldRegion != null)
                        {
                            foldRegion.setExpanded(false);
                        }
                    }
                }
            }
        };

        ApplicationUtils.invokeRunBatchFoldingOperationLater(editor, runnable);
    }

    /**
     * Checks the line at the specified index and continues to check for comments in the following lines.
     *
     * @param index The starting line which should be checked.
     * @param text The entire text of the Document which should be checked, needed to get the index of the comment
     * within the Document.
     * @param lines The text of the Document split into lines.
     *
     * @return The index within the Document where the comment or comment block ends.
     */
    private int getEndIndex(int index, String text, String[] lines)
    {
        int endIndex = Constants.INVALID_END_INDEX;
        //Check if the current line is a comment
        if(index < lines.length && lines[index].trim().startsWith(Constants.COMMENT))
        {
            endIndex = text.indexOf(lines[index]) + lines[index].length();
        }

        //Check the next line if it is also a comment or empty.
        if(index + 1 < lines.length &&
                (lines[index + 1].trim().startsWith(Constants.COMMENT) || lines[index + 1].trim().isEmpty()))
        {
            final int tmpIndex = getEndIndex(index + 1, text, lines);
            if(tmpIndex != Constants.INVALID_END_INDEX)
            {
                endIndex = tmpIndex;
            }
        }

        return endIndex;
    }
}
