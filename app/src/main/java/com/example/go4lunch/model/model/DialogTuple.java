package com.example.go4lunch.model.model;

/**
 * Allows to move the build of a Dialog from an activity to its ViewModel
 * and to retrieve both Dialog and user's response into the activity,
 * where the response will be handled.
 * @param <AlertDialog>
 * @param <Boolean>
 */
public class DialogTuple<AlertDialog, Boolean> {
    private AlertDialog dialog;
    private Boolean response;

    // Constructor
    public DialogTuple(AlertDialog dialog, Boolean response) {
        this.dialog = dialog;
        this.response = response;
    }


    // GETTERS

    public AlertDialog getDialog() {
        return dialog;
    }
    public Boolean getResponse() {
        return response;
    }


    // SETTERS

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }
    public void setResponse(Boolean response) {
        this.response = response;
    }

}
