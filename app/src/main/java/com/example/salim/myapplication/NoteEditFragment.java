package com.example.salim.myapplication;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteEditFragment extends Fragment {


    private static final String MODIFIERD_CATEGORY = "Modified Category";
    private ImageButton noteCatButton;
    private EditText title, message;
    private Note.Category saveButtonCategory;
    private AlertDialog categoryDialogObject, confirmDialogObject;

    private boolean newNote = false;

    public NoteEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();

        if (bundle != null) {
            newNote = bundle.getBoolean(NoteDetailActivity.NEW_NOTE_EXTRA, false);
        }
        if (savedInstanceState != null) {
            saveButtonCategory = (Note.Category) savedInstanceState.get(MODIFIERD_CATEGORY);
        }
        // Inflate the layout for this fragment

        View fragmentLayout = inflater.inflate(R.layout.fragment_note_edit, container, false);


        title = (EditText) fragmentLayout.findViewById(R.id.editNoteTitle);
        message = (EditText) fragmentLayout.findViewById(R.id.editnotemessage);
        noteCatButton = (ImageButton) fragmentLayout.findViewById(R.id.editNoteButton);
        Button saveButton = (Button) fragmentLayout.findViewById(R.id.saveNote);

        Intent intent = getActivity().getIntent();


        title.setText(intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA, ""));
        message.setText(intent.getExtras().getString(MainActivity.NOTE_MESSAGE_EXTRA, ""));

        if (saveButtonCategory != null) {
            noteCatButton.setImageResource(Note.categoryToDrawable(saveButtonCategory));
        } else if (!newNote) {


            Note.Category noteCat = (Note.Category) intent.getSerializableExtra(MainActivity.NOTE_CATEGORY_EXTRA);
            saveButtonCategory = noteCat;
            noteCatButton.setImageResource(Note.categoryToDrawable(noteCat));

        }
        buildCategoryDialog();
        buildConfirmDialog();


        noteCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialogObject.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialogObject.show();
            }
        });

        return fragmentLayout;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(MODIFIERD_CATEGORY, saveButtonCategory);

    }

    private void buildCategoryDialog() {
        final String[] categories = new String[]{"Personal", "Technical", "Quote", "Finance"};

        AlertDialog.Builder categoriesBuilder = new AlertDialog.Builder(getActivity());
        categoriesBuilder.setTitle("Choose Note Type");

        categoriesBuilder.setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                categoryDialogObject.cancel();
                switch (item) {
                    case 0://personal
                        saveButtonCategory = Note.Category.PERSONAL;
                        noteCatButton.setImageResource(R.drawable.p);
                        break;
                    case 1://technical
                        saveButtonCategory = Note.Category.TECHNICAL;
                        noteCatButton.setImageResource(R.drawable.t);
                        break;
                    case 2://quote
                        saveButtonCategory = Note.Category.QUOTE;
                        noteCatButton.setImageResource(R.drawable.q);
                        break;
                    case 3://finance
                        saveButtonCategory = Note.Category.FINANCE;
                        noteCatButton.setImageResource(R.drawable.f);
                        break;

                }
            }
        });

        categoryDialogObject = categoriesBuilder.create();
    }

    public void buildConfirmDialog() {

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());

        confirmBuilder.setTitle("Are you sure?");
        confirmBuilder.setMessage("Are you sure you want to save the note?");
        confirmBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("Save Note", "Note title:" + title.getText() + "Note message:"
                        + message.getText() + "Note category" + saveButtonCategory);
                NotebookDbAdapter dbAdapter = new NotebookDbAdapter(getActivity().getBaseContext());
                dbAdapter.open();
                if (newNote) {
                    dbAdapter.createNote(title.getText() + "", message.getText() + "", (saveButtonCategory == null) ? Note.Category.PERSONAL : saveButtonCategory);
                } else {

                }
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });

        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing here
            }
        });
        confirmDialogObject = confirmBuilder.create();
    }

}
