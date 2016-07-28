package gavinhua.contacts.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gavinhua.contacts.utils.ContactsManager;

/**
 * Created by HuShiGuang on 2016/4/3.
 */
public class ContactModel implements IContactModel{

    private static List<ContactEntity> contactEntities = new ArrayList<>();


    @Override
    public List<ContactEntity> getContactsList(Context mContext) {
        contactEntities = ContactsManager.getContacts(mContext, ContactsManager.NORMAL_TYPE);
        return contactEntities;
    }

    @Override
    public List<ContactEntity> getFrequentlyList(Context mContext) {
        return ContactsManager.getContacts(mContext, ContactsManager.USUALLY_TYPE);
    }

    @Override
    public List<ContactEntity> getAllContactsList() {
        if (contactEntities != null && contactEntities.size() != 0)
            sortContactModel(contactEntities);
        return contactEntities;
    }

    @Override
    public void deleteContact(Context mContext, int index) {
        ContactsManager.delContact(mContext, getContactModel(index).getName());
        contactEntities.remove(index);
    }

    @Override
    public void addContact(Context mContext, ContactEntity contactEntity) {
        ContactsManager.insertContacts(mContext, contactEntity);
        contactEntities.add(contactEntity);

    }

    @Override
    public int updateContact(Context mContext, int index, ContactEntity contactEntity) {
        ContactsManager.updateContact(mContext, getContactModel(index).getName(), contactEntity);
        contactEntities.remove(index);
        contactEntities.add(contactEntity);
        return getIndex(contactEntity);
    }


    @Override
    public int getIndex(ContactEntity contactEntity) {
        return contactEntities.indexOf(contactEntity);
    }

    @Override
    public ContactEntity getContactModel(int index) {
        return contactEntities.get(index);
    }

    private void sortContactModel(List<ContactEntity> contactEntities) {
        Collections.sort(contactEntities, new Comparator<ContactEntity>() {
            @Override
            public int compare(ContactEntity lhs, ContactEntity rhs) {
                return lhs.getIndex().compareTo(rhs.getIndex());
            }
        });
    }
}
