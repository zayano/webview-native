package com.example.webviewodoo;

import java.util.Observable;

public class HtmlJSInterface extends Observable {
    private String html;

    /**
     * @return The most recent HTML received by the interface
     */
    public String getHtml() {
        return this.html;
    }

    /**
     * Sets most recent HTML and notifies observers.
     *
     * @param html
     *          The full HTML of a page
     */
    public void setHtml(String html) {
        this.html = html;
        setChanged();
        notifyObservers(html);
    }
}
