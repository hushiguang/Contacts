package gavinhua.contacts;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import gavinhua.contacts.model.ContactEntity;
import gavinhua.contacts.presenter.ContactListPresenterCompl;
import gavinhua.contacts.presenter.IContactListPresenter;
import gavinhua.contacts.utils.ColorUtils;
import gavinhua.contacts.view.IContactListView;

/**
 * Created by GavinHua on 2016/3/30.
 * 联系人列表
 */
public class ContactsFragment extends Fragment implements IContactListView {

    @Bind(R.id.contacts_recycler)
    RecyclerView contactsRecycler;

    List<ContactEntity> contactsFromDB = new ArrayList<>();

    List<ContactEntity> contacts = new ArrayList<>();

    public static final int RESPONSE_ADD_CODE = 103;
    public static final int RESPONSE_UPDATE_CODE = 104;
    public static final int RESPONSE_CODE = 102;
    public static final int REQUEST_CODE = 102;

    IContactListPresenter contactsPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsPresenter = new ContactListPresenterCompl(this);
        contactsPresenter.initAllContacts(getContext());
        contactsRecycler.setAdapter(new ContactsAdapter());
    }


    @Override
    public void onContactClick(int index) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("index", index);
        getActivity().startActivityForResult(intent, REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.dialpad_slide_in_bottom, android.R.anim.fade_out);
    }

    @Override
    public void finishInitData(List<ContactEntity> contactEntities) {
        contactsFromDB = contactEntities;
        contacts.addAll(contactsFromDB);
        contactsRecycler.getAdapter().notifyDataSetChanged();
        ((MainActivity) getActivity()).setOnSearchViewQueryTextChangeListener(new MainActivity.onSearchViewQueryTextChangeListener() {
            @Override
            public void onQueryTextChange(String newText) {
                ((ContactsAdapter) contactsRecycler.getAdapter()).filter(newText);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        contacts.clear();
        contactsFromDB.clear();
        contactsPresenter.getAllContacts();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class ContactsAdapter extends RecyclerView.Adapter<Holder> {


        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false));
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final ContactEntity contactEntity = contacts.get(position);

            if (position == 0 || !contacts.get(position - 1).getIndex().equals(contactEntity.getIndex())) {
                holder.contactIndex.setVisibility(View.VISIBLE);
            } else {
                holder.contactIndex.setVisibility(View.INVISIBLE);
            }

            holder.contactIndex.setText(contactEntity.getIndex());
            holder.contactName.setText(contactEntity.getName());
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle);
            assert drawable != null;
            drawable.setTint(ColorUtils.getColor(getResources(), contactEntity.getName()));

            holder.contactImg.setBackground(drawable);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactsPresenter.onClickContact(contactEntity);
                }
            });

        }

        @Override
        public int getItemCount() {
            return contacts != null ? contacts.size() : 0;
        }

        public void filter(String newText) {
            contacts.clear();
            if (TextUtils.isEmpty(newText)) {
                contacts.addAll(contactsFromDB);
            } else if (isChinese(newText.charAt(0))) {
                for (ContactEntity contactEntity : contactsFromDB) {
                    if (contactEntity.getName().contains(newText)) {
                        contacts.add(contactEntity);
                    }
                }
            } else {
                for (ContactEntity contactEntity : contactsFromDB) {
                    char[] chars = newText.toCharArray();
                    String p = "";
                    for (char c : chars) {
                        p += c + "+.*";
                    }
                    Pattern pattern = Pattern.compile(p);
                    Matcher matcher = pattern.matcher(contactEntity.getPinYin());
                    if (matcher.find()) {
                        contacts.add(contactEntity);
                    }

                }
            }
            notifyDataSetChanged();
        }

        private boolean isChinese(char a) {
            int v = (int) a;
            return (v >= 19968 && v <= 171941);
        }

    }

    class Holder extends RecyclerView.ViewHolder {
        @Bind(R.id.contact_index)
        TextView contactIndex;
        @Bind(R.id.contact_img)
        ImageView contactImg;
        @Bind(R.id.contact_name)
        TextView contactName;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
