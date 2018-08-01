package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.XmlResourceParser;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({Scope.LIBRARY_GROUP})
public class AnimationUtilsCompat {
    public static Interpolator loadInterpolator(Context context, int i) throws NotFoundException {
        Throwable e;
        StringBuilder stringBuilder;
        NotFoundException notFoundException;
        if (VERSION.SDK_INT >= 21) {
            return AnimationUtils.loadInterpolator(context, i);
        }
        XmlResourceParser xmlResourceParser = null;
        if (i == 17563663) {
            try {
                return new FastOutLinearInInterpolator();
            } catch (XmlPullParserException e2) {
                e = e2;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Can't load animation resource ID #0x");
                stringBuilder.append(Integer.toHexString(i));
                notFoundException = new NotFoundException(stringBuilder.toString());
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (IOException e3) {
                e = e3;
                try {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Can't load animation resource ID #0x");
                    stringBuilder.append(Integer.toHexString(i));
                    notFoundException = new NotFoundException(stringBuilder.toString());
                    notFoundException.initCause(e);
                    throw notFoundException;
                } catch (Throwable th) {
                    e = th;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw e;
                }
            }
        } else if (i == 17563661) {
            return new FastOutSlowInInterpolator();
        } else {
            if (i == 17563662) {
                return new LinearOutSlowInInterpolator();
            }
            XmlResourceParser animation = context.getResources().getAnimation(i);
            try {
                Interpolator createInterpolatorFromXml = createInterpolatorFromXml(context, context.getResources(), context.getTheme(), animation);
                if (animation != null) {
                    animation.close();
                }
                return createInterpolatorFromXml;
            } catch (XmlPullParserException e4) {
                e = e4;
                xmlResourceParser = animation;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Can't load animation resource ID #0x");
                stringBuilder.append(Integer.toHexString(i));
                notFoundException = new NotFoundException(stringBuilder.toString());
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (IOException e5) {
                e = e5;
                xmlResourceParser = animation;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Can't load animation resource ID #0x");
                stringBuilder.append(Integer.toHexString(i));
                notFoundException = new NotFoundException(stringBuilder.toString());
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (Throwable th2) {
                e = th2;
                xmlResourceParser = animation;
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
                throw e;
            }
        }
    }

    private static Interpolator createInterpolatorFromXml(Context context, Resources resources, Theme theme, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        Interpolator interpolator = null;
        while (true) {
            int next = xmlPullParser.next();
            if ((next != 3 || xmlPullParser.getDepth() > depth) && next != 1) {
                if (next == 2) {
                    AttributeSet asAttributeSet = Xml.asAttributeSet(xmlPullParser);
                    String name = xmlPullParser.getName();
                    if (name.equals("linearInterpolator")) {
                        interpolator = new LinearInterpolator();
                    } else {
                        AccelerateInterpolator accelerateInterpolator;
                        if (name.equals("accelerateInterpolator")) {
                            accelerateInterpolator = new AccelerateInterpolator(context, asAttributeSet);
                        } else if (name.equals("decelerateInterpolator")) {
                            accelerateInterpolator = new DecelerateInterpolator(context, asAttributeSet);
                        } else if (name.equals("accelerateDecelerateInterpolator")) {
                            interpolator = new AccelerateDecelerateInterpolator();
                        } else if (name.equals("cycleInterpolator")) {
                            accelerateInterpolator = new CycleInterpolator(context, asAttributeSet);
                        } else if (name.equals("anticipateInterpolator")) {
                            accelerateInterpolator = new AnticipateInterpolator(context, asAttributeSet);
                        } else if (name.equals("overshootInterpolator")) {
                            accelerateInterpolator = new OvershootInterpolator(context, asAttributeSet);
                        } else if (name.equals("anticipateOvershootInterpolator")) {
                            accelerateInterpolator = new AnticipateOvershootInterpolator(context, asAttributeSet);
                        } else if (name.equals("bounceInterpolator")) {
                            interpolator = new BounceInterpolator();
                        } else if (name.equals("pathInterpolator")) {
                            accelerateInterpolator = new PathInterpolatorCompat(context, asAttributeSet, xmlPullParser);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Unknown interpolator name: ");
                            stringBuilder.append(xmlPullParser.getName());
                            throw new RuntimeException(stringBuilder.toString());
                        }
                        interpolator = accelerateInterpolator;
                    }
                }
            }
        }
        return interpolator;
    }
}
