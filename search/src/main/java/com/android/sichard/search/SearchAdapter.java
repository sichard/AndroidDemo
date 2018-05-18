package com.android.sichard.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sichard.search.data.App.AppInfo;
import com.android.sichard.search.data.App.AppList;
import com.android.sichard.search.data.App.AppTitle;
import com.android.sichard.search.data.App.IApp;
import com.android.sichard.search.data.App.IAppIml;
import com.android.sichard.search.data.App.MoreAppItem;
import com.android.sichard.search.data.DivideInfo;
import com.android.sichard.search.data.ISearchItem;
import com.android.sichard.search.data.appcenter.AppCenterInfo;
import com.android.sichard.search.data.appcenter.IAppCenterIml;
import com.android.sichard.search.data.contact.ContactInfo;
import com.android.sichard.search.data.contact.ContactPhotoCache;
import com.android.sichard.search.data.contact.ContactTitle;
import com.android.sichard.search.data.contact.IContact;
import com.android.sichard.search.data.contact.IContactIml;
import com.android.sichard.search.data.contact.MoreContactItem;
import com.android.sichard.search.data.message.IMessage;
import com.android.sichard.search.data.message.IMessageIml;
import com.android.sichard.search.data.message.MessageInfo;
import com.android.sichard.search.data.message.MessageTitle;
import com.android.sichard.search.data.message.MoreMessageItem;
import com.android.sichard.search.data.searchInWeb.ISearhInWebIml;
import com.android.sichard.search.data.searchInWeb.SearchInWebInfo;
import com.android.sichard.search.view.SearchAppCenterItemView;
import com.android.sichard.search.view.SearchAppView;
import com.android.sichard.search.view.SearchContactItemView;
import com.android.sichard.search.view.SearchInWebItemView;
import com.android.sichard.search.view.SearchMessageItemView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <br>类描述:搜索列表的adapter
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-8</b>
 */
public class SearchAdapter extends BaseAdapter {
    private Context mContext;
    private List<Object> mList = new ArrayList<>();
    private List<AppInfo> mAppList = new ArrayList<>();
    private List<ContactInfo> mContactList = new ArrayList<>();
    private List<MessageInfo> mMessageList = new ArrayList<>();
    private LayoutInflater mInflater;
    private ContactPhotoCache mContactPhotos;

    public SearchAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mContactPhotos = new ContactPhotoCache(context, 24); // 最多缓存联系人头像数目
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = null;
        final Object item = mList.get(position);
        if (item instanceof IAppIml) {
            switch (((IAppIml) item).mType) {
                case ISearchItem.TYPE_TITLE:
                    itemView = mInflater.inflate(R.layout.search_title_and_more, parent, false);
                    final TextView title = (TextView) itemView.findViewById(R.id.title);
                    title.setText(R.string.search_title_apps);
                    final TextView more = (TextView) itemView.findViewById(R.id.more);
                    final TextView less = (TextView) itemView.findViewById(R.id.less);
                    final AppTitle appTitle = (AppTitle) item;
                    if (appTitle.mState == ISearchItem.STATE_MORE) {
                        more.setVisibility(View.VISIBLE);
                    } else if (appTitle.mState == ISearchItem.STATE_LESS) {
                        less.setVisibility(View.VISIBLE);
                    }
                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showMoreSearchAppView();
                        }
                    });
                    less.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLessSearchAppView(new ArrayList<AppInfo>(mAppList));
                        }
                    });
                    itemView.setEnabled(false);
                    break;
                case ISearchItem.TYPE_ITEM:
                    SearchAppView searchAppView = (SearchAppView) mInflater.inflate(R.layout.search_app_view, parent, false);
                    final AppList appList = (AppList) item;
                    searchAppView.setChildList(appList.mAppList,false);
                    itemView = searchAppView;
                    break;
                case ISearchItem.MORE:
                    itemView = mInflater.inflate(R.layout.search_more_result, parent, false);
                    itemView.setVisibility(View.GONE);
                    final TextView moreResult = (TextView) itemView.findViewById(R.id.search_more_result);
                    moreResult.setText(mContext.getString(R.string.search_more_result, ((MoreAppItem) item).mCount));
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showMoreSearchAppView();
                        }
                    });
                    break;
            }
        } else if (item instanceof IContactIml) {
            switch (((IContactIml) item).mType) {
                case ISearchItem.TYPE_TITLE:
                    itemView = mInflater.inflate(R.layout.search_title_and_more, parent, false);
                    final TextView title = (TextView) itemView.findViewById(R.id.title);
                    title.setText(R.string.search_title_contacts);

                    final TextView more = (TextView) itemView.findViewById(R.id.more);
                    final TextView less = (TextView) itemView.findViewById(R.id.less);
                    final ContactTitle appTitle = (ContactTitle) item;
                    if (appTitle.mState == ISearchItem.STATE_MORE) {
                        more.setVisibility(View.VISIBLE);
                    } else if (appTitle.mState == ISearchItem.STATE_LESS) {
                        less.setVisibility(View.VISIBLE);
                    }

                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateSearchContactView();
                        }
                    });
                    less.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLessContactView(new ArrayList<ContactInfo>(mContactList));
                        }
                    });
                    itemView.setEnabled(false);
                    break;
                case ISearchItem.TYPE_ITEM:
                    ContactInfo contactInfo = (ContactInfo) item;
                    SearchContactItemView contactItemView = null;
                    if (convertView instanceof SearchContactItemView) {
                        contactItemView = (SearchContactItemView) convertView;
                    } else {
                        contactItemView = (SearchContactItemView) mInflater.inflate(R.layout.search_contact_item, parent, false);
                    }
                    contactItemView.setTag(contactInfo);
                    contactItemView.setHeadIcon(mContactPhotos.getPhoto(contactInfo));
                    contactItemView.setName(contactInfo);
                    setLastBackground(position, item, contactItemView);
                    itemView = contactItemView;
                    break;
                case ISearchItem.MORE:
                    itemView = mInflater.inflate(R.layout.search_more_result, parent, false);
                    itemView.setVisibility(View.GONE);
                    final TextView moreResult = (TextView) itemView.findViewById(R.id.search_more_result);
                    moreResult.setText(mContext.getString(R.string.search_more_result, ((MoreContactItem) item).mCount));
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateSearchContactView();
                        }
                    });
                    break;
            }
        } else if (item instanceof IMessage) {
            switch (((IMessageIml) item).mType) {
                case ISearchItem.TYPE_TITLE:
                    itemView = mInflater.inflate(R.layout.search_title_and_more, parent, false);
                    final TextView title = (TextView) itemView.findViewById(R.id.title);
                    title.setText(R.string.search_title_message);

                    final TextView more = (TextView) itemView.findViewById(R.id.more);
                    final TextView less = (TextView) itemView.findViewById(R.id.less);
                    final MessageTitle appTitle = (MessageTitle) item;
                    if (appTitle.mState == ISearchItem.STATE_MORE) {
                        more.setVisibility(View.VISIBLE);
                    } else if (appTitle.mState == ISearchItem.STATE_LESS) {
                        less.setVisibility(View.VISIBLE);
                    }

                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateMessageList();
                        }
                    });
                    less.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLessMessageList(new ArrayList<MessageInfo>(mMessageList));
                        }
                    });
                    itemView.setEnabled(false);
                    break;
                case ISearchItem.TYPE_ITEM:
                    MessageInfo messageInfo = (MessageInfo) item;
                    SearchMessageItemView messageItemView = null;
                    if (convertView instanceof SearchMessageItemView) {
                        messageItemView = (SearchMessageItemView) convertView;
                    } else {
                        messageItemView = (SearchMessageItemView) mInflater.inflate(R.layout.search_message_item, parent, false);
                    }
                    messageItemView.setMessageInfo(messageInfo);
                    setLastBackground(position, item, messageItemView);
                    itemView = messageItemView;
                    break;
                case ISearchItem.MORE:
                    itemView = mInflater.inflate(R.layout.search_more_result, parent, false);
                    itemView.setVisibility(View.GONE);
                    final TextView moreResult = (TextView) itemView.findViewById(R.id.search_more_result);
                    moreResult.setText(mContext.getString(R.string.search_more_result, ((MoreMessageItem) item).mCount));
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateMessageList();
                        }
                    });
                    break;
            }
        } else if (item instanceof IAppCenterIml) {
            switch (((IAppCenterIml) item).mType) {
                case ISearchItem.TYPE_TITLE:
                    itemView = mInflater.inflate(R.layout.search_title, parent, false);
                    final TextView title = (TextView) itemView.findViewById(R.id.title);
                    title.setText(R.string.search_title_apps);
                    break;
                case ISearchItem.TYPE_ITEM:
                    SearchAppCenterItemView appCenterItemView = (SearchAppCenterItemView) mInflater.inflate(R.layout.search_appcenter_item, parent, false);
                    AppCenterInfo appCenterInfo = (AppCenterInfo) item;
                    appCenterItemView.setAppCenterInfo(appCenterInfo);
                    ImageView appCenterIcon = (ImageView) appCenterItemView.findViewById(R.id.search_appcenter_item_head);
                    appCenterIcon.setImageResource(R.drawable.ic_search_in_apps);
                    itemView = appCenterItemView;
                    break;
            }
        } else if (item instanceof ISearhInWebIml) {
            switch (((ISearhInWebIml) item).mType) {
                case ISearchItem.TYPE_ITEM:
                    SearchInWebItemView appCenterItemView = (SearchInWebItemView) mInflater.inflate(R.layout.search_in_web_item, parent, false);
                    SearchInWebInfo searchInWebInfo = (SearchInWebInfo) item;
                    appCenterItemView.setSearchInWebInfo(searchInWebInfo);
                    itemView = appCenterItemView;
                    break;
            }
        } else if (item instanceof DivideInfo) {
            itemView = mInflater.inflate(R.layout.search_divide, parent, false);
        }
        return itemView;
    }


    /**
     * UI适配，设置最后一项为圆角
     * @param position
     * @param itemView
     */
    private void setLastBackground(int position, Object item, View itemView) {
        if (position + 1 == mList.size()) {
            itemView.findViewById(R.id.line).setVisibility(View.GONE);
        } else {
            final Object o = mList.get(position + 1);
            if (item instanceof IContactIml && !(o instanceof IContactIml)) {
                itemView.findViewById(R.id.line).setVisibility(View.GONE);
            } else if (item instanceof IMessageIml && !(o instanceof IMessageIml)) {
                itemView.findViewById(R.id.line).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置搜索App的结果列表
     *
     * @param list
     */
    public void setAppList(List<AppInfo> list) {
        mList.clear();
        if (list != null && list.size() > 0) {
            // 添加title
            final AppTitle appTitle = new AppTitle();
            appTitle.mType = IApp.TYPE_TITLE;
            mList.add(0, appTitle);

            if (list.size() > 10) {
                mAppList.clear();
                mAppList.addAll(list);
                MoreAppItem moreAppItem = new MoreAppItem();
                moreAppItem.mCount = list.size() - 10;

                // 添加item
                final List<AppInfo> appInfos = list.subList(0, 10);
                AppList appList = new AppList();
                appList.mAppList = appInfos;
                appList.mType = IApp.TYPE_ITEM;
                appList.mIsMore = true;
                mList.add(appList);

                // 添加more
//                mList.add(moreAppItem);
                appTitle.mState = ISearchItem.STATE_MORE;
            } else {
                AppList appList = new AppList();
                appList.mAppList = list;
                mList.add(appList);
                appTitle.mState = ISearchItem.STATE_NONE;
            }

            // 添加分隔符
            mList.add(new DivideInfo());
        }
    }

    public void showLessSearchAppView(List<AppInfo> list) {
        final Iterator<Object> iterator = mList.iterator();
        // 先移除所有app相关项
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IApp) {
                iterator.remove(); // TODO: 2016-12-15 如果app相关项移除结束，提前结束循环
            }
        }

        if (list != null && list.size() > 0) {
            // 添加title
            final AppTitle appTitle = new AppTitle();
            appTitle.mType = IApp.TYPE_TITLE;
            mList.add(0, appTitle);

            if (list.size() > 10) {
                mAppList.clear();
                mAppList.addAll(list);
                MoreAppItem moreAppItem = new MoreAppItem();
                moreAppItem.mCount = list.size() - 10;

                // 添加item
                final List<AppInfo> appInfos = list.subList(0, 10);
                AppList appList = new AppList();
                appList.mAppList = appInfos;
                appList.mType = IApp.TYPE_ITEM;
                appList.mIsMore = true;
                mList.add(1, appList);

                // 添加more
//                mList.add(moreAppItem);
                appTitle.mState = ISearchItem.STATE_MORE;
            } else {
                AppList appList = new AppList();
                appList.mAppList = list;
                mList.add(1, appList);
                appTitle.mState = ISearchItem.STATE_NONE;
            }
        }
        notifyDataSetChanged();
    }

    private void showMoreSearchAppView() {
        final Iterator<Object> iterator = mList.iterator();
        // 先移除所有app相关项
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IApp) {
                iterator.remove(); // TODO: 2016-12-15 如果app相关项移除结束，提前结束循环
            }
        }

        // 添加title
        final AppTitle appTitle = new AppTitle();
        appTitle.mType = IApp.TYPE_TITLE;
        mList.add(0, appTitle);

        // 添加item
        AppList appList = new AppList();
        appList.mAppList = mAppList;
        appList.mIsMore = false;
        mList.add(1, appList);
//        mList.add(new LessAppItem());
        appTitle.mState = ISearchItem.STATE_LESS;

        mList.add(new DivideInfo());
        notifyDataSetChanged();
    }

    /**
     * 设置搜索联系人的结果列表
     * @param contacts
     */
    public void setContactList(ArrayList<ContactInfo> contacts) {
        if(contacts != null) {
            final int size = contacts.size();
            if (size > 0) {
                // 添加title
                final ContactTitle contactTitle = new ContactTitle();
                mList.add(contactTitle);

                if (size > 3) {
                    mContactList.clear();
                    mContactList.addAll(contacts);
                    MoreContactItem moreContactItem = new MoreContactItem();
                    moreContactItem.mCount = size - 3;

                    // 添加item
                    final List<ContactInfo> contactInfos = contacts.subList(0, 3);
                    mList.addAll(contactInfos);

                    // 添加more
//                    mList.add(moreContactItem);
                    contactTitle.mState = ISearchItem.STATE_MORE;
                } else {
                    // 添加item
                    mList.addAll(contacts);
                    contactTitle.mState = ISearchItem.STATE_NONE;
                }
                // 添加分隔符
                mList.add(new DivideInfo());
            }
        }
    }

    public void showLessContactView(ArrayList<ContactInfo> contacts) {
        final Iterator<Object> iterator = mList.iterator();
        int index = -1;
        boolean isContact = true;
        // 先移除所有Contact相关项
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IContact) {
                if (isContact) {
                    isContact = false;
                    index = mList.indexOf(next);
                }
                iterator.remove();
            }
        }

        if(contacts != null) {
            final int size = contacts.size();
            if (size > 0) {
                // 添加title
                final ContactTitle contactTitle = new ContactTitle();
                mList.add(index, contactTitle);

                if (size > 3) {
                    mContactList.clear();
                    mContactList.addAll(contacts);
                    MoreContactItem moreContactItem = new MoreContactItem();
                    moreContactItem.mCount = size - 3;

                    // 添加item
                    final List<ContactInfo> contactInfos = contacts.subList(0, 3);
                    mList.addAll(index + 1, contactInfos);

                    // 添加more
//                    mList.add(moreContactItem);
                    contactTitle.mState = ISearchItem.STATE_MORE;
                } else {
                    // 添加item
                    mList.addAll(index + 1, contacts);
                    contactTitle.mState = ISearchItem.STATE_NONE;
                }
            }
        }
        notifyDataSetChanged();
    }

    private void updateSearchContactView() {
        final Iterator<Object> iterator = mList.iterator();
        int index = -1;
        boolean isContact = true;
        // 先移除所有Contact相关项
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IContact) {
                if (isContact) {
                    isContact = false;
                    index = mList.indexOf(next);
                }
                iterator.remove();
            }
        }

        // 添加title
        final ContactTitle contactTitle = new ContactTitle();
        contactTitle.mType = IApp.TYPE_TITLE;
        mList.add(index, contactTitle);

        // 添加item
        mList.addAll(index + 1, mContactList);
//        mList.add(new LessContactItem());
        contactTitle.mState = ISearchItem.STATE_LESS;

        notifyDataSetChanged();
    }

    public void setMessageList(List<MessageInfo> messageList) {
        if (messageList != null && messageList.size() > 0) {
            mMessageList.clear();
            mMessageList.addAll(messageList);

            MessageTitle messageTitle = new MessageTitle();
            mList.add(messageTitle);

            mList.addAll(mMessageList);
        }
    }

    /**
     * 设置搜索短信的结果列表
     * @param messageList
     */
    public void setMessageListAndMore(List<MessageInfo> messageList) {
        if(messageList != null) {
            final int size = messageList.size();
            if (size > 0) {
                // 添加title
                MessageTitle messageTitle = new MessageTitle();
                mList.add(messageTitle);

                if (size > 3) {
                    mMessageList.clear();
                    mMessageList.addAll(messageList);
                    MoreMessageItem moreMessageItem = new MoreMessageItem();
                    moreMessageItem.mCount = size - 3;

                    // 添加item
                    final List<MessageInfo> messageInfos = messageList.subList(0, 3);
                    mList.addAll(messageInfos);

                    // 添加more
//                    mList.add(moreMessageItem);
                    messageTitle.mState = ISearchItem.STATE_MORE;
                } else {
                    // 添加item
                    mList.addAll(messageList);
                    messageTitle.mState = ISearchItem.STATE_NONE;
                }
                // 添加分隔符
                mList.add(new DivideInfo());
            }
        }
    }

    public void showLessMessageList(List<MessageInfo> messageList) {
        final Iterator<Object> iterator = mList.iterator();
        int index = -1;
        boolean isMessage = true;
        // 先移除所有Contact相关项
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IMessage) {
                if (isMessage) {
                    isMessage = false;
                    index = mList.indexOf(next);
                }
                iterator.remove();
            }
        }

        if(messageList != null) {
            final int size = messageList.size();
            if (size > 0) {
                // 添加title
                MessageTitle messageTitle = new MessageTitle();
                mList.add(index, messageTitle);

                if (size > 3) {
                    mMessageList.clear();
                    mMessageList.addAll(messageList);
                    MoreMessageItem moreMessageItem = new MoreMessageItem();
                    moreMessageItem.mCount = size - 3;

                    // 添加item
                    final List<MessageInfo> messageInfos = messageList.subList(0, 3);
                    mList.addAll(index + 1, messageInfos);

                    // 添加more
//                    mList.add(moreMessageItem);
                    messageTitle.mState = ISearchItem.STATE_MORE;
                } else {
                    // 添加item
                    mList.addAll(index + 1, messageList);
                    messageTitle.mState = ISearchItem.STATE_NONE;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateMessageList() {
        final Iterator<Object> iterator = mList.iterator();
        int index = -1;
        boolean isMessage = true;
        // 先移除所有Contact相关项
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IMessage) {
                if (isMessage) {
                    isMessage = false;
                    index = mList.indexOf(next);
                }
                iterator.remove();
            }
        }

        // 添加title
        final MessageTitle messageTitle = new MessageTitle();
        messageTitle.mType = IApp.TYPE_TITLE;
        mList.add(index, messageTitle);

        // 添加item
        mList.addAll(index + 1, mMessageList);
//        mList.add(new LessMessageItem());
        messageTitle.mState = ISearchItem.STATE_LESS;

        notifyDataSetChanged();
    }

    public void setAppCenterList(AppCenterInfo appCenterInfo) {

        // 添加标题
//        mList.add(new AppCenterTitle());
        // Appcenter项
        mList.add(appCenterInfo);
    }

    public void setSearchInWeb(SearchInWebInfo searchInWebInfo) {
        mList.add(searchInWebInfo);
    }

    public void setContactPhotoLoadListener(ContactPhotoCache.ContactPhotoLoadListener contactPhotoLoadListener) {
        mContactPhotos.setContactPhotoLoadListener(contactPhotoLoadListener);
    }

    public void updateConvertView(View view, ContactInfo contact) {
        ImageView image = (ImageView) view.findViewById(R.id.search_contact_item_head);
        image.setImageDrawable(mContactPhotos.getPhoto(contact));
    }
}
