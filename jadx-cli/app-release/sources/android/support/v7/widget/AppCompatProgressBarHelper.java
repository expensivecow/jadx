package android.support.v7.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.widget.ProgressBar;

class AppCompatProgressBarHelper {
    private static final int[] TINT_ATTRS = new int[]{16843067, 16843068};
    private Bitmap mSampleTile;
    private final ProgressBar mView;

    static {
        int i = 2;
    }

    AppCompatProgressBarHelper(ProgressBar progressBar) {
        this.mView = progressBar;
    }

    void loadFromAttributes(AttributeSet attributeSet, int i) {
        boolean z = false;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attributeSet, TINT_ATTRS, i, z);
        Drawable drawableIfKnown = obtainStyledAttributes.getDrawableIfKnown(z);
        if (drawableIfKnown != null) {
            this.mView.setIndeterminateDrawable(tileifyIndeterminate(drawableIfKnown));
        }
        drawableIfKnown = obtainStyledAttributes.getDrawableIfKnown(1);
        if (drawableIfKnown != null) {
            this.mView.setProgressDrawable(tileify(drawableIfKnown, z));
        }
        obtainStyledAttributes.recycle();
    }

    private Drawable tileify(Drawable drawable, boolean z) {
        Drawable wrappedDrawable;
        if (drawable instanceof DrawableWrapper) {
            DrawableWrapper drawableWrapper = (DrawableWrapper) drawable;
            wrappedDrawable = drawableWrapper.getWrappedDrawable();
            if (wrappedDrawable != null) {
                drawableWrapper.setWrappedDrawable(tileify(wrappedDrawable, z));
            }
        } else {
            boolean z2 = true;
            if (drawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                int numberOfLayers = layerDrawable.getNumberOfLayers();
                Drawable[] drawableArr = new Drawable[numberOfLayers];
                int i = 0;
                for (int i2 = i; i2 < numberOfLayers; i2++) {
                    int id = layerDrawable.getId(i2);
                    Drawable drawable2 = layerDrawable.getDrawable(i2);
                    boolean z3 = (id == 16908301 || id == 16908303) ? z2 : i;
                    drawableArr[i2] = tileify(drawable2, z3);
                }
                wrappedDrawable = new LayerDrawable(drawableArr);
                while (i < numberOfLayers) {
                    wrappedDrawable.setId(i, layerDrawable.getId(i));
                    i++;
                }
                return wrappedDrawable;
            } else if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (this.mSampleTile == null) {
                    this.mSampleTile = bitmap;
                }
                Drawable shapeDrawable = new ShapeDrawable(getDrawableShape());
                shapeDrawable.getPaint().setShader(new BitmapShader(bitmap, TileMode.REPEAT, TileMode.CLAMP));
                shapeDrawable.getPaint().setColorFilter(bitmapDrawable.getPaint().getColorFilter());
                return z ? new ClipDrawable(shapeDrawable, 3, z2) : shapeDrawable;
            }
        }
        return drawable;
    }

    private Drawable tileifyIndeterminate(Drawable drawable) {
        if (!(drawable instanceof AnimationDrawable)) {
            return drawable;
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
        int numberOfFrames = animationDrawable.getNumberOfFrames();
        Drawable animationDrawable2 = new AnimationDrawable();
        animationDrawable2.setOneShot(animationDrawable.isOneShot());
        int i = 0;
        while (true) {
            int i2 = 10000;
            if (i < numberOfFrames) {
                Drawable tileify = tileify(animationDrawable.getFrame(i), true);
                tileify.setLevel(i2);
                animationDrawable2.addFrame(tileify, animationDrawable.getDuration(i));
                i++;
            } else {
                animationDrawable2.setLevel(i2);
                return animationDrawable2;
            }
        }
    }

    private Shape getDrawableShape() {
        int i = 8;
        RectF rectF = null;
        return new RoundRectShape(new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f}, rectF, rectF);
    }

    Bitmap getSampleTime() {
        return this.mSampleTile;
    }
}
