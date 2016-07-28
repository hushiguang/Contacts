package gavinhua.contacts.view;

import java.util.List;

import gavinhua.contacts.model.ContactEntity;

/**
 * Created by GavinHua on 2016/4/1.
 */
public interface IContactListView {

    void onContactClick(int index);

    void finishInitData(List<ContactEntity> contactEntities);
}
