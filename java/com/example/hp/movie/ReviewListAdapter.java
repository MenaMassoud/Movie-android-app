package com.example.hp.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ReviewListAdapter extends ArrayAdapter<review> {

    private ArrayList<review> revObjects;

    private Context c;

    public ReviewListAdapter(Context context, int textViewResourceId, ArrayList<review> objects) {
        super(context, textViewResourceId, objects);
        this.revObjects = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.review_listview_item, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        review i = revObjects.get(position);
        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView authorName = (TextView) v.findViewById(R.id.auther_textview);
            TextView contentText = (TextView) v.findViewById(R.id.content_textview);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (authorName != null) {
                authorName.setText(i.getAuther());

            }
            if (contentText != null) {
                contentText.setText(i.getContent());
            }
        }
        return v;
    }
}



