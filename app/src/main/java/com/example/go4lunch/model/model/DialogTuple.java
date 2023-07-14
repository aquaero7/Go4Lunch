package com.example.go4lunch.model.model;

public class DialogTuple<AlertDialog, Boolean> {
    private AlertDialog dialog;
    private Boolean response;

    public DialogTuple(AlertDialog dialog, Boolean response) {
        this.dialog = dialog;
        this.response = response;
    }

    public AlertDialog getDialog() {
        return dialog;
    }
    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }
    public Boolean getResponse() {
        return response;
    }
    public void setResponse(Boolean response) {
        this.response = response;
    }

}
