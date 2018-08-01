package android.support.v7.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.PorterDuff.Mode;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuItemWrapperICS;
import android.support.v7.widget.DrawableUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({Scope.LIBRARY_GROUP})
public class SupportMenuInflater extends MenuInflater {
    static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
    static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
    static final String LOG_TAG = "SupportMenuInflater";
    static final int NO_ID = 0;
    private static final String XML_GROUP = "group";
    private static final String XML_ITEM = "item";
    private static final String XML_MENU = "menu";
    final Object[] mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
    final Object[] mActionViewConstructorArguments;
    Context mContext;
    private Object mRealOwner;

    private static class InflatedOnMenuItemClickListener implements OnMenuItemClickListener {
        private static final Class<?>[] PARAM_TYPES;
        private Method mMethod;
        private Object mRealOwner;

        static {
            Class[] clsArr = new Class[1];
            clsArr[0] = MenuItem.class;
            PARAM_TYPES = clsArr;
        }

        public InflatedOnMenuItemClickListener(Object obj, String str) {
            this.mRealOwner = obj;
            Class cls = obj.getClass();
            try {
                this.mMethod = cls.getMethod(str, PARAM_TYPES);
            } catch (Throwable e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't resolve menu item onClick handler ");
                stringBuilder.append(str);
                stringBuilder.append(" in class ");
                stringBuilder.append(cls.getName());
                InflateException inflateException = new InflateException(stringBuilder.toString());
                inflateException.initCause(e);
                throw inflateException;
            }
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            try {
                int i = 0;
                boolean z = true;
                Method method;
                Object obj;
                if (this.mMethod.getReturnType() == Boolean.TYPE) {
                    method = this.mMethod;
                    obj = this.mRealOwner;
                    Object[] objArr = new Object[z];
                    objArr[i] = menuItem;
                    return ((Boolean) method.invoke(obj, objArr)).booleanValue();
                }
                method = this.mMethod;
                obj = this.mRealOwner;
                Object[] objArr2 = new Object[z];
                objArr2[i] = menuItem;
                method.invoke(obj, objArr2);
                return z;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class MenuState {
        private static final int defaultGroupId = 0;
        private static final int defaultItemCategory = 0;
        private static final int defaultItemCheckable = 0;
        private static final boolean defaultItemChecked = false;
        private static final boolean defaultItemEnabled = true;
        private static final int defaultItemId = 0;
        private static final int defaultItemOrder = 0;
        private static final boolean defaultItemVisible = true;
        private int groupCategory;
        private int groupCheckable;
        private boolean groupEnabled;
        private int groupId;
        private int groupOrder;
        private boolean groupVisible;
        ActionProvider itemActionProvider;
        private String itemActionProviderClassName;
        private String itemActionViewClassName;
        private int itemActionViewLayout;
        private boolean itemAdded;
        private int itemAlphabeticModifiers;
        private char itemAlphabeticShortcut;
        private int itemCategoryOrder;
        private int itemCheckable;
        private boolean itemChecked;
        private CharSequence itemContentDescription;
        private boolean itemEnabled;
        private int itemIconResId;
        private ColorStateList itemIconTintList;
        private Mode itemIconTintMode;
        private int itemId;
        private String itemListenerMethodName;
        private int itemNumericModifiers;
        private char itemNumericShortcut;
        private int itemShowAsAction;
        private CharSequence itemTitle;
        private CharSequence itemTitleCondensed;
        private CharSequence itemTooltipText;
        private boolean itemVisible;
        private Menu menu;

        public MenuState(Menu menu) {
            Object obj = null;
            this.itemIconTintList = obj;
            this.itemIconTintMode = obj;
            this.menu = menu;
            resetGroup();
        }

        public void resetGroup() {
            int i = 0;
            this.groupId = i;
            this.groupCategory = i;
            this.groupOrder = i;
            this.groupCheckable = i;
            boolean z = true;
            this.groupVisible = z;
            this.groupEnabled = z;
        }

        public void readGroup(AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = SupportMenuInflater.this.mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuGroup);
            int i = 0;
            this.groupId = obtainStyledAttributes.getResourceId(R.styleable.MenuGroup_android_id, i);
            this.groupCategory = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_menuCategory, i);
            this.groupOrder = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_orderInCategory, i);
            this.groupCheckable = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_checkableBehavior, i);
            boolean z = true;
            this.groupVisible = obtainStyledAttributes.getBoolean(R.styleable.MenuGroup_android_visible, z);
            this.groupEnabled = obtainStyledAttributes.getBoolean(R.styleable.MenuGroup_android_enabled, z);
            obtainStyledAttributes.recycle();
        }

        public void readItem(AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = SupportMenuInflater.this.mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuItem);
            boolean z = false;
            this.itemId = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_android_id, z);
            this.itemCategoryOrder = (obtainStyledAttributes.getInt(R.styleable.MenuItem_android_menuCategory, this.groupCategory) & -65536) | (obtainStyledAttributes.getInt(R.styleable.MenuItem_android_orderInCategory, this.groupOrder) & 65535);
            this.itemTitle = obtainStyledAttributes.getText(R.styleable.MenuItem_android_title);
            this.itemTitleCondensed = obtainStyledAttributes.getText(R.styleable.MenuItem_android_titleCondensed);
            this.itemIconResId = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_android_icon, z);
            this.itemAlphabeticShortcut = getShortcut(obtainStyledAttributes.getString(R.styleable.MenuItem_android_alphabeticShortcut));
            int i = 4096;
            this.itemAlphabeticModifiers = obtainStyledAttributes.getInt(R.styleable.MenuItem_alphabeticModifiers, i);
            this.itemNumericShortcut = getShortcut(obtainStyledAttributes.getString(R.styleable.MenuItem_android_numericShortcut));
            this.itemNumericModifiers = obtainStyledAttributes.getInt(R.styleable.MenuItem_numericModifiers, i);
            if (obtainStyledAttributes.hasValue(R.styleable.MenuItem_android_checkable)) {
                this.itemCheckable = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_checkable, z);
            } else {
                this.itemCheckable = this.groupCheckable;
            }
            this.itemChecked = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_checked, z);
            this.itemVisible = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_visible, this.groupVisible);
            this.itemEnabled = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_enabled, this.groupEnabled);
            i = -1;
            this.itemShowAsAction = obtainStyledAttributes.getInt(R.styleable.MenuItem_showAsAction, i);
            this.itemListenerMethodName = obtainStyledAttributes.getString(R.styleable.MenuItem_android_onClick);
            this.itemActionViewLayout = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_actionLayout, z);
            this.itemActionViewClassName = obtainStyledAttributes.getString(R.styleable.MenuItem_actionViewClass);
            this.itemActionProviderClassName = obtainStyledAttributes.getString(R.styleable.MenuItem_actionProviderClass);
            boolean z2 = this.itemActionProviderClassName != null ? true : z;
            ColorStateList colorStateList = null;
            if (z2 && this.itemActionViewLayout == 0 && this.itemActionViewClassName == null) {
                this.itemActionProvider = (ActionProvider) newInstance(this.itemActionProviderClassName, SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionProviderConstructorArguments);
            } else {
                if (z2) {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");
                }
                this.itemActionProvider = colorStateList;
            }
            this.itemContentDescription = obtainStyledAttributes.getText(R.styleable.MenuItem_contentDescription);
            this.itemTooltipText = obtainStyledAttributes.getText(R.styleable.MenuItem_tooltipText);
            if (obtainStyledAttributes.hasValue(R.styleable.MenuItem_iconTintMode)) {
                this.itemIconTintMode = DrawableUtils.parseTintMode(obtainStyledAttributes.getInt(R.styleable.MenuItem_iconTintMode, i), this.itemIconTintMode);
            } else {
                this.itemIconTintMode = colorStateList;
            }
            if (obtainStyledAttributes.hasValue(R.styleable.MenuItem_iconTint)) {
                this.itemIconTintList = obtainStyledAttributes.getColorStateList(R.styleable.MenuItem_iconTint);
            } else {
                this.itemIconTintList = colorStateList;
            }
            obtainStyledAttributes.recycle();
            this.itemAdded = z;
        }

        private char getShortcut(String str) {
            char c = 0;
            return str == null ? c : str.charAt(c);
        }

        private void setItem(MenuItem menuItem) {
            boolean z = false;
            boolean z2 = true;
            menuItem.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled).setCheckable(this.itemCheckable >= z2 ? z2 : z).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId);
            if (this.itemShowAsAction >= 0) {
                menuItem.setShowAsAction(this.itemShowAsAction);
            }
            if (this.itemListenerMethodName != null) {
                if (SupportMenuInflater.this.mContext.isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
                menuItem.setOnMenuItemClickListener(new InflatedOnMenuItemClickListener(SupportMenuInflater.this.getRealOwner(), this.itemListenerMethodName));
            }
            boolean z3 = menuItem instanceof MenuItemImpl;
            if (z3) {
                MenuItemImpl menuItemImpl = (MenuItemImpl) menuItem;
            }
            if (this.itemCheckable >= 2) {
                if (z3) {
                    ((MenuItemImpl) menuItem).setExclusiveCheckable(z2);
                } else if (menuItem instanceof MenuItemWrapperICS) {
                    ((MenuItemWrapperICS) menuItem).setExclusiveCheckable(z2);
                }
            }
            if (this.itemActionViewClassName != null) {
                menuItem.setActionView((View) newInstance(this.itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionViewConstructorArguments));
                z = z2;
            }
            if (this.itemActionViewLayout > 0) {
                if (z) {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                } else {
                    menuItem.setActionView(this.itemActionViewLayout);
                }
            }
            if (this.itemActionProvider != null) {
                MenuItemCompat.setActionProvider(menuItem, this.itemActionProvider);
            }
            MenuItemCompat.setContentDescription(menuItem, this.itemContentDescription);
            MenuItemCompat.setTooltipText(menuItem, this.itemTooltipText);
            MenuItemCompat.setAlphabeticShortcut(menuItem, this.itemAlphabeticShortcut, this.itemAlphabeticModifiers);
            MenuItemCompat.setNumericShortcut(menuItem, this.itemNumericShortcut, this.itemNumericModifiers);
            if (this.itemIconTintMode != null) {
                MenuItemCompat.setIconTintMode(menuItem, this.itemIconTintMode);
            }
            if (this.itemIconTintList != null) {
                MenuItemCompat.setIconTintList(menuItem, this.itemIconTintList);
            }
        }

        public void addItem() {
            this.itemAdded = true;
            setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
        }

        public SubMenu addSubMenuItem() {
            this.itemAdded = true;
            SubMenu addSubMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
            setItem(addSubMenu.getItem());
            return addSubMenu;
        }

        public boolean hasAddedItem() {
            return this.itemAdded;
        }

        private <T> T newInstance(String str, Class<?>[] clsArr, Object[] objArr) {
            try {
                Constructor constructor = SupportMenuInflater.this.mContext.getClassLoader().loadClass(str).getConstructor(clsArr);
                constructor.setAccessible(true);
                return constructor.newInstance(objArr);
            } catch (Throwable e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Cannot instantiate class: ");
                stringBuilder.append(str);
                Log.w("SupportMenuInflater", stringBuilder.toString(), e);
                return null;
            }
        }
    }

    static {
        Class[] clsArr = new Class[1];
        clsArr[0] = Context.class;
        ACTION_VIEW_CONSTRUCTOR_SIGNATURE = clsArr;
    }

    public SupportMenuInflater(Context context) {
        super(context);
        this.mContext = context;
        this.mActionViewConstructorArguments = new Object[]{context};
    }

    public void inflate(int i, Menu menu) {
        Throwable e;
        if (menu instanceof SupportMenu) {
            XmlResourceParser xmlResourceParser = null;
            try {
                XmlResourceParser layout = this.mContext.getResources().getLayout(i);
                try {
                    parseMenu(layout, Xml.asAttributeSet(layout), menu);
                    if (layout != null) {
                        layout.close();
                    }
                    return;
                } catch (XmlPullParserException e2) {
                    e = e2;
                    xmlResourceParser = layout;
                    throw new InflateException("Error inflating menu XML", e);
                } catch (IOException e3) {
                    e = e3;
                    xmlResourceParser = layout;
                    throw new InflateException("Error inflating menu XML", e);
                } catch (Throwable th) {
                    e = th;
                    xmlResourceParser = layout;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw e;
                }
            } catch (XmlPullParserException e4) {
                e = e4;
                throw new InflateException("Error inflating menu XML", e);
            } catch (IOException e5) {
                e = e5;
                throw new InflateException("Error inflating menu XML", e);
            } catch (Throwable th2) {
                e = th2;
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
                throw e;
            }
        }
        super.inflate(i, menu);
    }

    private void parseMenu(XmlPullParser xmlPullParser, AttributeSet attributeSet, Menu menu) throws XmlPullParserException, IOException {
        Object obj;
        int i;
        int i2;
        Object obj2;
        int i3;
        MenuState menuState = new MenuState(menu);
        int eventType = xmlPullParser.getEventType();
        int i4;
        do {
            i4 = 1;
            if (eventType == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("menu")) {
                    eventType = xmlPullParser.next();
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Expecting menu, got ");
                    stringBuilder.append(name);
                    throw new RuntimeException(stringBuilder.toString());
                }
            }
            eventType = xmlPullParser.next();
            obj = null;
            i = 0;
            i2 = eventType;
            obj2 = obj;
            eventType = i;
            i3 = eventType;
            while (eventType == 0) {
                String name2;
                switch (i2) {
                    case 1:
                        throw new RuntimeException("Unexpected end of document");
                    case 2:
                        if (i3 != 0) {
                            break;
                        }
                        name2 = xmlPullParser.getName();
                        if (!name2.equals("group")) {
                            if (!name2.equals("item")) {
                                if (name2.equals("menu")) {
                                    parseMenu(xmlPullParser, attributeSet, menuState.addSubMenuItem());
                                    break;
                                }
                                i3 = i4;
                                obj2 = name2;
                                break;
                            }
                            menuState.readItem(attributeSet);
                            break;
                        }
                        menuState.readGroup(attributeSet);
                        break;
                    case 3:
                        name2 = xmlPullParser.getName();
                        if (i3 == 0 || !name2.equals(obj2)) {
                            if (!name2.equals("group")) {
                                if (name2.equals("item")) {
                                    if (!menuState.hasAddedItem()) {
                                        if (menuState.itemActionProvider == null || !menuState.itemActionProvider.hasSubMenu()) {
                                            menuState.addItem();
                                            break;
                                        } else {
                                            menuState.addSubMenuItem();
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                } else if (name2.equals("menu")) {
                                    eventType = i4;
                                    break;
                                } else {
                                    break;
                                }
                            }
                            menuState.resetGroup();
                            break;
                        }
                        obj2 = obj;
                        i3 = i;
                        break;
                    default:
                        break;
                }
                i2 = xmlPullParser.next();
            }
        } while (eventType != i4);
        obj = null;
        i = 0;
        i2 = eventType;
        obj2 = obj;
        eventType = i;
        i3 = eventType;
        while (eventType == 0) {
            String name22;
            switch (i2) {
                case 1:
                    throw new RuntimeException("Unexpected end of document");
                case 2:
                    if (i3 != 0) {
                        break;
                    }
                    name22 = xmlPullParser.getName();
                    if (!name22.equals("group")) {
                        if (!name22.equals("item")) {
                            if (name22.equals("menu")) {
                                parseMenu(xmlPullParser, attributeSet, menuState.addSubMenuItem());
                                break;
                            }
                            i3 = i4;
                            obj2 = name22;
                            break;
                        }
                        menuState.readItem(attributeSet);
                        break;
                    }
                    menuState.readGroup(attributeSet);
                    break;
                case 3:
                    name22 = xmlPullParser.getName();
                    if (i3 == 0 || !name22.equals(obj2)) {
                        if (!name22.equals("group")) {
                            if (name22.equals("item")) {
                                if (!menuState.hasAddedItem()) {
                                    if (menuState.itemActionProvider == null || !menuState.itemActionProvider.hasSubMenu()) {
                                        menuState.addItem();
                                        break;
                                    } else {
                                        menuState.addSubMenuItem();
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            } else if (name22.equals("menu")) {
                                eventType = i4;
                                break;
                            } else {
                                break;
                            }
                        }
                        menuState.resetGroup();
                        break;
                    }
                    obj2 = obj;
                    i3 = i;
                    break;
                default:
                    break;
            }
            i2 = xmlPullParser.next();
        }
    }

    Object getRealOwner() {
        if (this.mRealOwner == null) {
            this.mRealOwner = findRealOwner(this.mContext);
        }
        return this.mRealOwner;
    }

    private Object findRealOwner(Object obj) {
        return (!(obj instanceof Activity) && (obj instanceof ContextWrapper)) ? findRealOwner(((ContextWrapper) obj).getBaseContext()) : obj;
    }
}
