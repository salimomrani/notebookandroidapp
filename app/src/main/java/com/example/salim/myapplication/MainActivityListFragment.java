package com.example.salim.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityListFragment extends ListFragment {

    private ArrayList<Note> notes;
    private NoteAdapter noteAdapter;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        String[] values = new String[] {
//                "Adroid", "WebOS" , "Ubuntu" , "Iphone" , "Mac" ,"Linux"
//        };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,values);
//
//        setListAdapter(adapter);

//        notes = new ArrayList<Note>();
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.PERSONAL));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.FINANCE));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.QUOTE));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.TECHNICAL));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.PERSONAL));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.TECHNICAL));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.TECHNICAL));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.TECHNICAL));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.PERSONAL));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.QUOTE));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.FINANCE));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.FINANCE));
//        notes.add(new Note("this is a new note title!","this is the body of our note!",Note.Category.FINANCE));


        NotebookDbAdapter dbAdapter = new NotebookDbAdapter(getActivity().getBaseContext());

        dbAdapter.open();
        notes = dbAdapter.getAllNotes();
        dbAdapter.close();

        noteAdapter = new NoteAdapter(getActivity(), notes);

        setListAdapter(noteAdapter);


        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.long_press_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int rowPosition = info.position;

        Note note = (Note) getListAdapter().getItem(rowPosition);
        switch (item.getItemId()) {
            case R.id.edit:
                launchNoteDetailActivity(MainActivity.FragmentToLaunch.EDIT, rowPosition);
                Log.d("Menu Clicks", "We pressed edit");
                return true;
            case R.id.delete:
                NotebookDbAdapter dbAdapter = new NotebookDbAdapter(getActivity().getBaseContext());
                dbAdapter.open();
                dbAdapter.deleteNote(note.getNoteId());
                notes.clear();
                notes.addAll(dbAdapter.getAllNotes());
                noteAdapter.notifyDataSetChanged();
                dbAdapter.close();
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        launchNoteDetailActivity(MainActivity.FragmentToLaunch.VIEW, position);
    }


    private void launchNoteDetailActivity(MainActivity.FragmentToLaunch ftl, int position) {


        Note note = (Note) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);

        intent.putExtra(MainActivity.NOTE_TITLE_EXTRA, note.getTitle());
        intent.putExtra(MainActivity.NOTE_MESSAGE_EXTRA, note.getMessage());
        intent.putExtra(MainActivity.NOTE_CATEGORY_EXTRA, note.getCategory());
        intent.putExtra(MainActivity.NOTE_ID_EXTRA, note.getNoteId());

        switch (ftl) {
            case VIEW:
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD_EXTRA, MainActivity.FragmentToLaunch.VIEW);
                break;
            case EDIT:
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD_EXTRA, MainActivity.FragmentToLaunch.EDIT);
                break;
        }
        startActivity(intent);

    }

}

