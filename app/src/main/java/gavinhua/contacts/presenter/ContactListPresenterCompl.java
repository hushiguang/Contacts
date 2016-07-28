package gavinhua.contacts.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import gavinhua.contacts.model.ContactEntity;
import gavinhua.contacts.model.ContactModel;
import gavinhua.contacts.model.IContactModel;
import gavinhua.contacts.view.IContactListView;

/**
 * Created by GavinHua on 2016/4/1.
 */
public class ContactListPresenterCompl implements IContactListPresenter {
    IContactListView contactsView;
    IContactModel contactModel;

    Handler handler = new Handler(Looper.getMainLooper());

    public ContactListPresenterCompl(IContactListView contactsView) {
        this.contactsView = contactsView;
        contactModel = new ContactModel();
    }

    @Override
    public void initAllContacts(final Context mContext) {
        final List<ContactEntity> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {

                list.addAll(contactModel.getContactsList(mContext));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactsView.finishInitData(list);
                    }
                });
            }
        }).start();
    }

    @Override
    public void initFrequentlyContacts(final Context mContext) {
        final List<ContactEntity> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.addAll(contactModel.getFrequentlyList(mContext));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactsView.finishInitData(list);
                    }
                });
            }
        }).start();

    }

    @Override
    public void getAllContacts() {
        List<ContactEntity> list = new ArrayList<>();
        list.addAll(contactModel.getAllContactsList());
        contactsView.finishInitData(list);

    }


    @Override
    public void onClickContact(ContactEntity contactEntity) {
        contactsView.onContactClick(contactModel.getIndex(contactEntity));
    }


}
