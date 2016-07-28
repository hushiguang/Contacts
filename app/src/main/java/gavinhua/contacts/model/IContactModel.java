package gavinhua.contacts.model;

import android.content.Context;

import java.util.List;

/**
 * Created by GavinHua on 2016/4/1.
 */
public interface IContactModel {

    List<ContactEntity> getContactsList(Context mContext);

    List<ContactEntity> getFrequentlyList(Context mContext);

    List<ContactEntity> getAllContactsList();

    void deleteContact(Context mContext, int index);

    void addContact(Context mContext, ContactEntity contactEntity);

    int updateContact(Context mContext, int index, ContactEntity contactEntity);

    int getIndex(ContactEntity contactEntity);

    ContactEntity getContactModel(int index);

}
