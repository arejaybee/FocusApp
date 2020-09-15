package net.arejaybee.focus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class MessageDialogFragent extends DialogFragment {

    EditText mTitle;

    // Use this instance of the interface to deliver action events
    MessageDialogFragent.NoticeDialogListener mListener;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(MessageDialogFragent dialog);
        void onDialogNegativeClick(MessageDialogFragent dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = (inflater.inflate(R.layout.fragment_message, null));
        init(view);
        builder.setView(view);
        return builder.create();
    }

    /**
     * Called when the main page attaches this fragment, after create
     * @param context
     **/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (MessageDialogFragent.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /**
     * Get the message that the user wishes to add
     * @return - the value of the editText
     **/
    public String getText(){
        return mTitle.getText().toString();
    }

    /**
     * Initialize variables. Grabs the objects from the layout
     **/
    private void init(View view){

        view.findViewById(R.id.fmbtn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogPositiveClick(MessageDialogFragent.this);
            }
        });
        view.findViewById(R.id.fmbtn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogNegativeClick(MessageDialogFragent.this);
            }
        });

        mTitle = view.findViewById(R.id.fmet_title);
        mTitle.requestFocus();
    }
}

