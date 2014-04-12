package com.talktostrangers.chatui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.talktostrangers.R;
import com.talktostrangers.core.Backend;
import com.talktostrangers.core.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ca.weixiao.widget.InfiniteScrollListAdapter;
import ca.weixiao.widget.InfiniteScrollListView;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ChatListFragment extends Fragment implements Backend.ChatListener {

    private OnFragmentInteractionListener mListener;
    private InfiniteScrollListView mListView;
    private EditText mChatInput;
    private String mChatType = Backend.AREA_CHAT;

    private ChatAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ChatListFragment newInstance() {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_chat, container, false);
        mListView = (InfiniteScrollListView)layout.findViewById(R.id.chatList);
        mChatInput = (EditText) layout.findViewById(R.id.chatInputText);

        mAdapter = new ChatAdapter();
        mListView.setAdapter(mAdapter);

        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Backend.getInstance().setChatListener(this);
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated

    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public void onReceivedMessages(Message[] messages) {
        mAdapter.addEntriesToBottom(Arrays.asList(messages));
    }

    private class ChatAdapter extends InfiniteScrollListAdapter {
        private List<Message> entries = new ArrayList<Message>();

        public void addEntriesToTop(List<Message> entries) {
            // Add entries in reversed order to achieve a sequence used in most of messaging/chat apps
            if (entries != null) {
                Collections.reverse(entries);
            }
            // Add entries to the top of the list
            this.entries.addAll(0, entries);
            notifyDataSetChanged();
        }

        public void addEntriesToBottom(List<Message> entries) {
            // Add entries to the bottom of the list
            this.entries.addAll(entries);
            notifyDataSetChanged();
        }

        @Override
        protected void onScrollNext() {

        }

        @Override
        public View getInfiniteScrollListView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.row_chat_list, parent, false);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.message_text);

            Message m = entries.get(position);
            textView.setText(m.text);

            return convertView;
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int i) {
            return entries.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }

}
