package com.utt.zipapp.Model;

public class CustomItemsSpinner {
    private String spinnerTitle;
    private int spinnerImage;

    public CustomItemsSpinner(String spinnerTitle, int spinnerImage) {
        this.spinnerTitle = spinnerTitle;
        this.spinnerImage = spinnerImage;
    }

    public String getSpinnerTitle() {
        return spinnerTitle;
    }

    public void setSpinnerTitle(String spinnerTitle) {
        this.spinnerTitle = spinnerTitle;
    }

    public int getSpinnerImage() {
        return spinnerImage;
    }

    public void setSpinnerImage(int spinnerImage) {
        this.spinnerImage = spinnerImage;
    }
}
