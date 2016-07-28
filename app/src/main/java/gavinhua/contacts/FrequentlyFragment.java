package gavinhua.contacts;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import gavinhua.contacts.model.ContactEntity;
import gavinhua.contacts.presenter.ContactListPresenterCompl;
import gavinhua.contacts.presenter.IContactListPresenter;
import gavinhua.contacts.utils.ColorUtils;
import gavinhua.contacts.view.IContactListView;

/**
 * Created by GavinHua on 2016/3/30.
 * 显示收藏的联系人
 */
public class FrequentlyFragment extends Fragment implements IContactListView {

    @Bind(R.id.frequently_recycler)
    RecyclerView frequentlyRecycler;

    /* 常用的联系人列表 **/
    List<ContactEntity> contactEntities = new ArrayList<>();
    /* 当前上下文对象 **/
    Context mContext;
    /* 当前的适配器对象 **/
    FrequentlyAdapter mAdapter;

    IContactListPresenter contactsPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frequently, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsPresenter = new ContactListPresenterCompl(this);
    }


    @Override
    public void onContactClick(int index) {
    }

    @Override
    public void finishInitData(List<ContactEntity> contactEntities) {
        this.contactEntities = contactEntities;
        mAdapter = new FrequentlyAdapter();
        frequentlyRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        contactsPresenter.initFrequentlyContacts(getContext());
    }

    class FrequentlyAdapter extends RecyclerView.Adapter<Holder> {
        public int HEAD_CODE = 0, BODY_CODE = 1;

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEAD_CODE) {
                TextView mText = new TextView(mContext);
                mText.setTextSize(24);
                mText.setText(R.string.frequently_hint);
                mText.setTextColor(getResources().getColor(R.color.colorPrimary));
                mText.setTypeface(Typeface.DEFAULT_BOLD);
                return new Holder(mText, HEAD_CODE);
            } else {
                return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.item_favorites, parent, false), BODY_CODE);
            }

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            //如果是头部信息
            if (getItemViewType(position) == HEAD_CODE) {
                //WTF
                holder.itemView.setPadding(65, 65, 0, 0);
                holder.itemView.invalidate();

            } else { //否则是recycleView信息
                final ContactEntity contactEntity = contactEntities.get(position - 1);
                holder.itemView.setTag(contactEntity);
                holder.mContactName.setText(contactEntity.getName());
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle);
                assert drawable != null;
                drawable.setTint(ColorUtils.getColor(getResources(), contactEntity.getName()));

                holder.mContactImage.setBackground(drawable);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + contactEntity.getPhone()));
                        startActivity(intent);
                    }
                });

            }

        }

        @Override
        public int getItemCount() {
            return contactEntities.size() + 1;
        }


        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return HEAD_CODE;
            else
                return BODY_CODE;
        }
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView mContactName;
        ImageView mContactImage;

        public Holder(View itemView, int type) {
            super(itemView);
            if (type == 1) {
                mContactName = (TextView) itemView.findViewById(R.id.item_contact_name);
                mContactImage = (ImageView) itemView.findViewById(R.id.item_contact_img);
            }
        }


    }
}
