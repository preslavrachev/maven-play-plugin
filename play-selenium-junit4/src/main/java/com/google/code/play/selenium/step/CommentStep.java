package com.google.code.play.selenium.step;

import com.google.code.play.selenium.Step;

public class CommentStep
    implements Step
{

    private String comment;

    public CommentStep( String comment )
    {
        this.comment = comment;
    }

    public void execute()
        throws Exception
    {
    }

    public String toString()
    {
        return "//" + comment;
    }
}
