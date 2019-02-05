package com.kimcy929.iconpakagereader.iconhelper

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.core.content.res.ResourcesCompat
import com.kimcy929.iconpakagereader.utils.IconPackageUtils
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.util.*

/**
 * Created by kimcy929 08/11/2016
 */
class IconPackHelper {

    var packageName: String? = null
    var name: String? = null

    private var iconPackres: Resources? = null

    private var mContext: Context? = null

    fun setContext(c: Context) {
        mContext = c
    }

    private fun getDrawableResourceId(drawableName: String): Int {
        return iconPackres!!.getIdentifier(drawableName, "drawable", packageName)
    }

    fun loadDrawable(drawableName: String): Drawable? {
        val id = iconPackres!!.getIdentifier(drawableName, "drawable", packageName)
        try {
            if (id > 0) {
                return ResourcesCompat.getDrawable(iconPackres!!, id, null)
            }
        } catch (ignored: Exception) {
        }

        return null
    }

    fun loadAppFilter(mPackagesDrawables: MutableMap<String, String>) {
        // load appfilter.xml from the icon pack package
        val pm = mContext!!.packageManager

        try {
            var xpp: XmlPullParser? = null
            iconPackres = pm.getResourcesForApplication(packageName)
            val appfilterId = iconPackres!!.getIdentifier("appfilter", "xml", packageName)
            if (appfilterId > 0) {
                xpp = iconPackres!!.getXml(appfilterId)
            } else {
                // no resource found, try to open it from assests folder
                try {
                    val appfilterstream = iconPackres!!.assets.open("appfilter.xml")
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = true
                    xpp = factory.newPullParser()
                    xpp!!.setInput(appfilterstream, "UTF-8")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            if (xpp != null) {
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.name == "item") {
                            var componentName: String? = null
                            var drawableName: String? = null

                            for (i in 0 until xpp.attributeCount) {
                                if (xpp.getAttributeName(i) == "component") {
                                    componentName = xpp.getAttributeValue(i)
                                    if (componentName!!.contains(":")) break
                                } else if (xpp.getAttributeName(i) == "drawable") {
                                    drawableName = xpp.getAttributeValue(i)
                                }
                            }

                            if (!TextUtils.isEmpty(drawableName) && !TextUtils.isEmpty(componentName)) {
                                if (getDrawableResourceId(drawableName!!) > 0) {
                                    mPackagesDrawables[componentName!!] = drawableName
                                }
                            }
                        }
                    }
                    eventType = xpp.next()
                }
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadDrawableFile(packagesDrawables: MutableList<String>) {
        // load drawable.xml from the icon pack package
        val pm = mContext!!.packageManager

        try {
            var xpp: XmlPullParser? = null
            iconPackres = pm.getResourcesForApplication(packageName)
            val drawableId = iconPackres!!.getIdentifier("drawable", "xml", packageName)
            if (drawableId > 0) {
                xpp = iconPackres!!.getXml(drawableId)
            } else {
                // no resource found, try to open it from assests folder
                try {
                    val appfilterstream = iconPackres!!.assets.open("drawable.xml")
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = true
                    xpp = factory.newPullParser()
                    xpp!!.setInput(appfilterstream, "utf-8")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            if (xpp != null) {
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.name == "item") {
                            var drawableName: String? = null

                            for (i in 0 until xpp.attributeCount) {

                                if (xpp.getAttributeName(i) == "drawable") {
                                    drawableName = xpp.getAttributeValue(i)
                                }
                            }

                            if (!drawableName.isNullOrEmpty()) {
                                if (!packagesDrawables.contains(drawableName) && getDrawableResourceId(drawableName) > 0) {
                                    packagesDrawables.add(drawableName)
                                }
                            }
                        }
                    }
                    eventType = xpp.next()
                }

                Collections.sort(packagesDrawables, IconPackageUtils.DRAWABLE_NAME_COMPARATOR)

            } else {
                packagesDrawables.clear()
                loadAppFilter(packagesDrawables)
            }

        } catch (e: PackageManager.NameNotFoundException) {
            packagesDrawables.clear()
            loadAppFilter(packagesDrawables)
        } catch (e: XmlPullParserException) {
            packagesDrawables.clear()
            loadAppFilter(packagesDrawables)
        } catch (e: IOException) {
            packagesDrawables.clear()
            loadAppFilter(packagesDrawables)
        }

        if (packagesDrawables.isEmpty()) {
            loadAppFilter(packagesDrawables)
        }
    }

    private fun loadAppFilter(mPackagesDrawables: MutableList<String>) {
        // load appfilter.xml from the icon pack package
        val pm = mContext!!.packageManager

        try {
            var xpp: XmlPullParser? = null
            iconPackres = pm.getResourcesForApplication(packageName)
            val appfilterId = iconPackres!!.getIdentifier("appfilter", "xml", packageName)
            if (appfilterId > 0) {
                xpp = iconPackres!!.getXml(appfilterId)
            } else {
                // no resource found, try to open it from assests folder
                try {
                    val appfilterstream = iconPackres!!.assets.open("appfilter.xml")
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = true
                    xpp = factory.newPullParser()
                    xpp!!.setInput(appfilterstream, "UTF-8")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            if (xpp != null) {
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.name == "item") {
                            var componentName: String? = null
                            var drawableName: String? = null

                            for (i in 0 until xpp.attributeCount) {

                                if (xpp.getAttributeName(i) == "component") {
                                    componentName = xpp.getAttributeValue(i)
                                    if (componentName!!.contains(":")) break
                                } else if (xpp.getAttributeName(i) == "drawable") {
                                    drawableName = xpp.getAttributeValue(i)
                                }
                            }

                            if (!drawableName.isNullOrEmpty() && !componentName.isNullOrEmpty()) {
                                if (!mPackagesDrawables.contains(drawableName) && getDrawableResourceId(drawableName) > 0) {
                                    mPackagesDrawables.add(drawableName)
                                }
                            }
                        }
                    }
                    eventType = xpp.next()
                }

                Collections.sort(mPackagesDrawables, IconPackageUtils.DRAWABLE_NAME_COMPARATOR)
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

