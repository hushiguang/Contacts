package gavinhua.contacts.presenter;

import android.content.Context;

import gavinhua.contacts.model.ContactEntity;
import gavinhua.contacts.model.ContactModel;
import gavinhua.contacts.model.IContactModel;
import gavinhua.contacts.view.IContactView;

/**
 * Created by GavinHua on 2016/4/1.
 */
public class ContactPresenter implements IContactPresenter {
    IContactView contactView;
    IContactModel contactModel;

    public ContactPresenter(IContactView contactView) {
        this.contactView = contactView;
        contactModel = new ContactModel();
    }

    @Override
    public void initDataByIndex(int index) {



        contactView.initData(contactModel.getContactModel(index));
    }

    @Override
    public void deleteContact(Context mContext, int index) {
        contactModel.deleteContact(mContext, index);
    }

    @Override
    public void addContact(Context mContext, ContactEntity contactEntity) {
        contactModel.addContact(mContext, contactEntity);
    }

    @Override
    public int updateContact(Context mContext, int index, ContactEntity contactEntity) {
        return contactModel.updateContact(mContext, index, contactEntity);
    }

    @Override
    public ContactEntity getIndex(int index) {
        return contactModel.getContactModel(index);
    }
}
