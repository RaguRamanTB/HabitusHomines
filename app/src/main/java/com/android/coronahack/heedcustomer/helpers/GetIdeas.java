package com.android.coronahack.heedcustomer.helpers;

public class GetIdeas {

    private String titleIdea, contentIdea, ideaBy;

    public GetIdeas() {
    }

    public GetIdeas(String titleIdea, String contentIdea, String ideaBy) {
        this.titleIdea = titleIdea;
        this.contentIdea = contentIdea;
        this.ideaBy = ideaBy;
    }

    public String getIdeaBy() {
        return ideaBy;
    }

    public void setIdeaBy(String ideaBy) {
        this.ideaBy = ideaBy;
    }

    public String getContentIdea() {
        return contentIdea;
    }

    public void setContentIdea(String contentIdea) {
        this.contentIdea = contentIdea;
    }

    public String getTitleIdea() {
        return titleIdea;
    }

    public void setTitleIdea(String titleIdea) {
        this.titleIdea = titleIdea;
    }
}
