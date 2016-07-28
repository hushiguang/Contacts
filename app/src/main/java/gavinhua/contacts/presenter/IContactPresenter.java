package gavinhua.contacts.presenter;

import android.content.Context;

import gavinhua.contacts.model.ContactEntity;

/**
 * Created by GavinHua on 2016/4/1.
 */
public interface IContactPresenter {

    void initDataByIndex(int index);

    void deleteContact(Context mContext, int index);

    void addContact(Context mContext, ContactEntity contactEntity);

    int updateContact(Context mContext, int index, ContactEntity contactEntity);

    ContactEntity getIndex(int index);


}
