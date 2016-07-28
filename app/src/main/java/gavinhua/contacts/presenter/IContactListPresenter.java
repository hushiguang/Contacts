package gavinhua.contacts.presenter;

import android.content.Context;

import gavinhua.contacts.model.ContactEntity;

/**
 * Created by GavinHua on 2016/4/1.
 */
public interface IContactListPresenter {

    void initAllContacts(Context mContext);

    void initFrequentlyContacts(Context mContext);

    void getAllContacts();

    void onClickContact(ContactEntity contactEntity);

}
