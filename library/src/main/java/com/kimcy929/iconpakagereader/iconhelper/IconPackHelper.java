package com.kimcy929.iconpakagereader.iconhelper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.kimcy929.iconpakagereader.utils.IconPackageUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by kimcy929 08/11/2016
 */
public class IconPackHelper {

    public String packageName;
    public String name;

    private Resources iconPackres = null;

    private Context mContext;

    public void setContext(Context c) {
        mContext = c;
    }

    private int getDrawableResourceId(String drawableName) {
        return iconPackres.getIdentifier(drawableName, "drawable", packageName);
    }

    public Drawable loadDrawable(String drawableName) {
        int id = iconPackres.getIdentifier(drawableName, "drawable", packageName);
        try {
            if (id > 0) {
                return iconPackres.getDrawable(id);
            }
        } catch (Resources.NotFoundException ignored) {}

        return null;
    }

    public void loadAppFilter(Map<String, String> mPackagesDrawables) {
        // load appfilter.xml from the icon pack package
        PackageManager pm = mContext.getPackageManager();

        try {
            XmlPullParser xpp = null;
            iconPackres = pm.getResourcesForApplication(packageName);
            int appfilterId = iconPackres.getIdentifier("appfilter", "xml", packageName);
            if (appfilterId > 0) {
                xpp = iconPackres.getXml(appfilterId);
            } else {
                // no resource found, try to open it from assests folder
                try {
                    InputStream appfilterstream = iconPackres.getAssets().open("appfilter.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    xpp = factory.newPullParser();
                    xpp.setInput(appfilterstream, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (xpp != null) {
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("item")) {
                            String componentName = null;
                            String drawableName = null;

                            for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                if (xpp.getAttributeName(i).equals("component")) {
                                    componentName = xpp.getAttributeValue(i);
                                    if (componentName.contains(":")) break;
                                } else if (xpp.getAttributeName(i).equals("drawable")) {
                                    drawableName = xpp.getAttributeValue(i);
                                }
                            }

                            if (!TextUtils.isEmpty(drawableName) && !TextUtils.isEmpty(componentName)) {
                                if (getDrawableResourceId(drawableName) > 0) {
                                    mPackagesDrawables.put(componentName, drawableName);
                                }
                            }
                        }
                    }
                    eventType = xpp.next();
                }
            }

        } catch (PackageManager.NameNotFoundException | XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDrawableFile(List<String> packagesDrawables) {
        // load drawable.xml from the icon pack package
        PackageManager pm = mContext.getPackageManager();

        try {
            XmlPullParser xpp = null;
            iconPackres = pm.getResourcesForApplication(packageName);
            int drawableId = iconPackres.getIdentifier("drawable", "xml", packageName);
            if (drawableId > 0) {
                xpp = iconPackres.getXml(drawableId);
            } else {
                // no resource found, try to open it from assests folder
                try {
                    InputStream appfilterstream = iconPackres.getAssets().open("drawable.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    xpp = factory.newPullParser();
                    xpp.setInput(appfilterstream, "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (xpp != null) {
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("item")) {
                            String drawableName = null;

                            for (int i = 0; i < xpp.getAttributeCount(); i++) {

                                if (xpp.getAttributeName(i).equals("drawable")) {
                                    drawableName = xpp.getAttributeValue(i);
                                }
                            }

                            if (!TextUtils.isEmpty(drawableName)) {
                                if (!packagesDrawables.contains(drawableName)
                                        && getDrawableResourceId(drawableName) > 0) {
                                    packagesDrawables.add(drawableName);
                                }
                            }
                        }
                    }
                    eventType = xpp.next();
                }

                Collections.sort(packagesDrawables, IconPackageUtils.DRAWABLE_NAME_COMPARATOR);

            } else {
                packagesDrawables.clear();
                loadAppFilter(packagesDrawables);
            }

        } catch (PackageManager.NameNotFoundException | XmlPullParserException | IOException e) {
            packagesDrawables.clear();
            loadAppFilter(packagesDrawables);
        }

        if (packagesDrawables.isEmpty()) {
            loadAppFilter(packagesDrawables);
        }
    }

    private void loadAppFilter(List<String> mPackagesDrawables) {
        // load appfilter.xml from the icon pack package
        PackageManager pm = mContext.getPackageManager();

        try {
            XmlPullParser xpp = null;
            iconPackres = pm.getResourcesForApplication(packageName);
            int appfilterId = iconPackres.getIdentifier("appfilter", "xml", packageName);
            if (appfilterId > 0) {
                xpp = iconPackres.getXml(appfilterId);
            } else {
                // no resource found, try to open it from assests folder
                try {
                    InputStream appfilterstream = iconPackres.getAssets().open("appfilter.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    xpp = factory.newPullParser();
                    xpp.setInput(appfilterstream, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (xpp != null) {
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("item")) {
                            String componentName = null;
                            String drawableName = null;

                            for (int i = 0; i < xpp.getAttributeCount(); i++) {

                                if (xpp.getAttributeName(i).equals("component")) {
                                    componentName = xpp.getAttributeValue(i);
                                    if (componentName.contains(":")) break;
                                } else if (xpp.getAttributeName(i).equals("drawable")) {
                                    drawableName = xpp.getAttributeValue(i);
                                }
                            }

                            if (!TextUtils.isEmpty(drawableName) && !TextUtils.isEmpty(componentName)) {
                                if (!mPackagesDrawables.contains(drawableName)
                                        && getDrawableResourceId(drawableName) > 0) {
                                    mPackagesDrawables.add(drawableName);
                                }
                            }
                        }
                    }
                    eventType = xpp.next();
                }

                Collections.sort(mPackagesDrawables, IconPackageUtils.DRAWABLE_NAME_COMPARATOR);
            }

        } catch (PackageManager.NameNotFoundException | XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
}

