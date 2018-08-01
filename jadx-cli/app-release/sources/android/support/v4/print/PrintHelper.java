package android.support.v4.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument.Page;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PrintHelper {
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    private final PrintHelperVersionImpl mImpl;

    @Retention(RetentionPolicy.SOURCE)
    private @interface ColorMode {
    }

    public interface OnPrintFinishCallback {
        void onFinish();
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    interface PrintHelperVersionImpl {
        int getColorMode();

        int getOrientation();

        int getScaleMode();

        void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback);

        void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException;

        void setColorMode(int i);

        void setOrientation(int i);

        void setScaleMode(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ScaleMode {
    }

    @RequiresApi(19)
    private static class PrintHelperApi19 implements PrintHelperVersionImpl {
        private static final String LOG_TAG = "PrintHelperApi19";
        private static final int MAX_PRINT_SIZE = 3500;
        int mColorMode;
        final Context mContext;
        Options mDecodeOptions = null;
        protected boolean mIsMinMarginsHandlingCorrect;
        private final Object mLock = new Object();
        int mOrientation;
        protected boolean mPrintActivityRespectsOrientation;
        int mScaleMode;

        PrintHelperApi19(Context context) {
            int i = 2;
            this.mScaleMode = i;
            this.mColorMode = i;
            boolean z = true;
            this.mPrintActivityRespectsOrientation = z;
            this.mIsMinMarginsHandlingCorrect = z;
            this.mContext = context;
        }

        public void setScaleMode(int i) {
            this.mScaleMode = i;
        }

        public int getScaleMode() {
            return this.mScaleMode;
        }

        public void setColorMode(int i) {
            this.mColorMode = i;
        }

        public void setOrientation(int i) {
            this.mOrientation = i;
        }

        public int getOrientation() {
            if (this.mOrientation == 0) {
                return 1;
            }
            return this.mOrientation;
        }

        public int getColorMode() {
            return this.mColorMode;
        }

        private static boolean isPortrait(Bitmap bitmap) {
            return bitmap.getWidth() <= bitmap.getHeight();
        }

        protected Builder copyAttributes(PrintAttributes printAttributes) {
            Builder minMargins = new Builder().setMediaSize(printAttributes.getMediaSize()).setResolution(printAttributes.getResolution()).setMinMargins(printAttributes.getMinMargins());
            if (printAttributes.getColorMode() != 0) {
                minMargins.setColorMode(printAttributes.getColorMode());
            }
            return minMargins;
        }

        public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
            if (bitmap != null) {
                MediaSize mediaSize;
                final int i = this.mScaleMode;
                PrintManager printManager = (PrintManager) this.mContext.getSystemService("print");
                if (isPortrait(bitmap)) {
                    mediaSize = MediaSize.UNKNOWN_PORTRAIT;
                } else {
                    mediaSize = MediaSize.UNKNOWN_LANDSCAPE;
                }
                final String str2 = str;
                final Bitmap bitmap2 = bitmap;
                final OnPrintFinishCallback onPrintFinishCallback2 = onPrintFinishCallback;
                printManager.print(str, new PrintDocumentAdapter() {
                    private PrintAttributes mAttributes;

                    public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes2, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
                        this.mAttributes = printAttributes2;
                        int i = 1;
                        layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(str2).setContentType(i).setPageCount(i).build(), printAttributes2.equals(printAttributes) ^ i);
                    }

                    public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
                        PrintHelperApi19.this.writeBitmap(this.mAttributes, i, bitmap2, parcelFileDescriptor, cancellationSignal, writeResultCallback);
                    }

                    public void onFinish() {
                        if (onPrintFinishCallback2 != null) {
                            onPrintFinishCallback2.onFinish();
                        }
                    }
                }, new Builder().setMediaSize(mediaSize).setColorMode(this.mColorMode).build());
            }
        }

        private Matrix getMatrix(int i, int i2, RectF rectF, int i3) {
            float max;
            Matrix matrix = new Matrix();
            float f = (float) i;
            float width = rectF.width() / f;
            if (i3 == 2) {
                max = Math.max(width, rectF.height() / ((float) i2));
            } else {
                max = Math.min(width, rectF.height() / ((float) i2));
            }
            matrix.postScale(max, max);
            width = rectF.width() - (f * max);
            f = 2.0f;
            matrix.postTranslate(width / f, (rectF.height() - (((float) i2) * max)) / f);
            return matrix;
        }

        private void writeBitmap(PrintAttributes printAttributes, int i, Bitmap bitmap, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
            PrintAttributes printAttributes2;
            int i2 = 0;
            if (this.mIsMinMarginsHandlingCorrect) {
                printAttributes2 = printAttributes;
            } else {
                printAttributes2 = copyAttributes(printAttributes).setMinMargins(new Margins(i2, i2, i2, i2)).build();
            }
            final CancellationSignal cancellationSignal2 = cancellationSignal;
            final Bitmap bitmap2 = bitmap;
            final PrintAttributes printAttributes3 = printAttributes;
            final int i3 = i;
            final ParcelFileDescriptor parcelFileDescriptor2 = parcelFileDescriptor;
            final WriteResultCallback writeResultCallback2 = writeResultCallback;
            new AsyncTask<Void, Void, Throwable>() {
                protected Throwable doInBackground(Void... voidArr) {
                    Throwable th;
                    Bitmap access$100;
                    try {
                        th = null;
                        if (cancellationSignal2.isCanceled()) {
                            return th;
                        }
                        PrintedPdfDocument printedPdfDocument = new PrintedPdfDocument(PrintHelperApi19.this.mContext, printAttributes2);
                        access$100 = PrintHelperApi19.this.convertBitmapForColorMode(bitmap2, printAttributes2.getColorMode());
                        if (cancellationSignal2.isCanceled()) {
                            return th;
                        }
                        int i = 1;
                        try {
                            RectF rectF;
                            Page startPage = printedPdfDocument.startPage(i);
                            if (PrintHelperApi19.this.mIsMinMarginsHandlingCorrect) {
                                rectF = new RectF(startPage.getInfo().getContentRect());
                            } else {
                                PrintedPdfDocument printedPdfDocument2 = new PrintedPdfDocument(PrintHelperApi19.this.mContext, printAttributes3);
                                Page startPage2 = printedPdfDocument2.startPage(i);
                                RectF rectF2 = new RectF(startPage2.getInfo().getContentRect());
                                printedPdfDocument2.finishPage(startPage2);
                                printedPdfDocument2.close();
                                rectF = rectF2;
                            }
                            Matrix access$200 = PrintHelperApi19.this.getMatrix(access$100.getWidth(), access$100.getHeight(), rectF, i3);
                            if (!PrintHelperApi19.this.mIsMinMarginsHandlingCorrect) {
                                access$200.postTranslate(rectF.left, rectF.top);
                                startPage.getCanvas().clipRect(rectF);
                            }
                            startPage.getCanvas().drawBitmap(access$100, access$200, th);
                            printedPdfDocument.finishPage(startPage);
                            if (cancellationSignal2.isCanceled()) {
                                printedPdfDocument.close();
                                if (parcelFileDescriptor2 != null) {
                                    parcelFileDescriptor2.close();
                                }
                            } else {
                                printedPdfDocument.writeTo(new FileOutputStream(parcelFileDescriptor2.getFileDescriptor()));
                                printedPdfDocument.close();
                                if (parcelFileDescriptor2 != null) {
                                    parcelFileDescriptor2.close();
                                }
                                try {
                                } catch (IOException unused) {
                                    if (access$100 != bitmap2) {
                                        access$100.recycle();
                                    }
                                    return th;
                                }
                            }
                        } catch (Throwable th2) {
                            printedPdfDocument.close();
                            if (parcelFileDescriptor2 != null) {
                                parcelFileDescriptor2.close();
                            }
                        }
                    } catch (IOException unused2) {
                        if (access$100 != bitmap2) {
                            access$100.recycle();
                        }
                        throw th2;
                    } catch (IOException unused3) {
                        if (access$100 != bitmap2) {
                            access$100.recycle();
                        }
                        return th2;
                    } catch (Throwable th3) {
                        return th3;
                    }
                }

                protected void onPostExecute(Throwable th) {
                    if (cancellationSignal2.isCanceled()) {
                        writeResultCallback2.onWriteCancelled();
                    } else if (th == null) {
                        writeResultCallback2.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } else {
                        Log.e("PrintHelperApi19", "Error writing printed content", th);
                        writeResultCallback2.onWriteFailed(null);
                    }
                }
            }.execute(new Void[i2]);
        }

        public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException {
            final int i = this.mScaleMode;
            final String str2 = str;
            final Uri uri2 = uri;
            final OnPrintFinishCallback onPrintFinishCallback2 = onPrintFinishCallback;
            PrintDocumentAdapter anonymousClass3 = new PrintDocumentAdapter() {
                private PrintAttributes mAttributes;
                Bitmap mBitmap = null;
                AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;

                public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes2, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
                    synchronized (this) {
                        this.mAttributes = printAttributes2;
                    }
                    if (cancellationSignal.isCanceled()) {
                        layoutResultCallback.onLayoutCancelled();
                    } else if (this.mBitmap != null) {
                        int i = 1;
                        layoutResultCallback.onLayoutFinished(new PrintDocumentInfo.Builder(str2).setContentType(i).setPageCount(i).build(), printAttributes2.equals(printAttributes) ^ i);
                    } else {
                        final CancellationSignal cancellationSignal2 = cancellationSignal;
                        final PrintAttributes printAttributes3 = printAttributes2;
                        final PrintAttributes printAttributes4 = printAttributes;
                        final LayoutResultCallback layoutResultCallback2 = layoutResultCallback;
                        this.mLoadBitmap = new AsyncTask<Uri, Boolean, Bitmap>() {
                            protected void onPreExecute() {
                                cancellationSignal2.setOnCancelListener(new OnCancelListener() {
                                    public void onCancel() {
                                        AnonymousClass3.this.cancelLoad();
                                        AnonymousClass1.this.cancel(false);
                                    }
                                });
                            }

                            protected Bitmap doInBackground(Uri... uriArr) {
                                try {
                                    return PrintHelperApi19.this.loadConstrainedBitmap(uri2);
                                } catch (FileNotFoundException unused) {
                                    return null;
                                }
                            }

                            protected void onPostExecute(Bitmap bitmap) {
                                super.onPostExecute(bitmap);
                                if (bitmap != null && (!PrintHelperApi19.this.mPrintActivityRespectsOrientation || PrintHelperApi19.this.mOrientation == 0)) {
                                    MediaSize mediaSize;
                                    synchronized (this) {
                                        mediaSize = AnonymousClass3.this.mAttributes.getMediaSize();
                                    }
                                    if (!(mediaSize == null || mediaSize.isPortrait() == PrintHelperApi19.isPortrait(bitmap))) {
                                        Matrix matrix = new Matrix();
                                        matrix.postRotate(90.0f);
                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    }
                                }
                                AnonymousClass3.this.mBitmap = bitmap;
                                Object obj = null;
                                if (bitmap != null) {
                                    int i = 1;
                                    layoutResultCallback2.onLayoutFinished(new PrintDocumentInfo.Builder(str2).setContentType(i).setPageCount(i).build(), i ^ printAttributes3.equals(printAttributes4));
                                } else {
                                    layoutResultCallback2.onLayoutFailed(obj);
                                }
                                AnonymousClass3.this.mLoadBitmap = obj;
                            }

                            protected void onCancelled(Bitmap bitmap) {
                                layoutResultCallback2.onLayoutCancelled();
                                AnonymousClass3.this.mLoadBitmap = null;
                            }
                        }.execute(new Uri[0]);
                    }
                }

                private void cancelLoad() {
                    synchronized (PrintHelperApi19.this.mLock) {
                        if (PrintHelperApi19.this.mDecodeOptions != null) {
                            PrintHelperApi19.this.mDecodeOptions.requestCancelDecode();
                            PrintHelperApi19.this.mDecodeOptions = null;
                        }
                    }
                }

                public void onFinish() {
                    super.onFinish();
                    cancelLoad();
                    if (this.mLoadBitmap != null) {
                        this.mLoadBitmap.cancel(true);
                    }
                    if (onPrintFinishCallback2 != null) {
                        onPrintFinishCallback2.onFinish();
                    }
                    if (this.mBitmap != null) {
                        this.mBitmap.recycle();
                        this.mBitmap = null;
                    }
                }

                public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
                    PrintHelperApi19.this.writeBitmap(this.mAttributes, i, this.mBitmap, parcelFileDescriptor, cancellationSignal, writeResultCallback);
                }
            };
            PrintManager printManager = (PrintManager) this.mContext.getSystemService("print");
            Builder builder = new Builder();
            builder.setColorMode(this.mColorMode);
            if (this.mOrientation == 1 || this.mOrientation == 0) {
                builder.setMediaSize(MediaSize.UNKNOWN_LANDSCAPE);
            } else if (this.mOrientation == 2) {
                builder.setMediaSize(MediaSize.UNKNOWN_PORTRAIT);
            }
            printManager.print(str, anonymousClass3, builder.build());
        }

        private Bitmap loadConstrainedBitmap(Uri uri) throws FileNotFoundException {
            if (uri == null || this.mContext == null) {
                throw new IllegalArgumentException("bad argument to getScaledBitmap");
            }
            Options options = new Options();
            boolean z = true;
            options.inJustDecodeBounds = z;
            loadBitmap(uri, options);
            int i = options.outWidth;
            int i2 = options.outHeight;
            Bitmap bitmap = null;
            if (i <= 0 || i2 <= 0) {
                return bitmap;
            }
            int max = Math.max(i, i2);
            int i3 = z;
            while (max > 3500) {
                max >>>= 1;
                i3 <<= 1;
            }
            if (i3 <= 0 || Math.min(i, i2) / i3 <= 0) {
                return bitmap;
            }
            Options options2;
            synchronized (this.mLock) {
                this.mDecodeOptions = new Options();
                this.mDecodeOptions.inMutable = z;
                this.mDecodeOptions.inSampleSize = i3;
                options2 = this.mDecodeOptions;
            }
            try {
                Bitmap loadBitmap = loadBitmap(uri, options2);
                synchronized (this.mLock) {
                    this.mDecodeOptions = bitmap;
                }
                return loadBitmap;
            } catch (Throwable th) {
                synchronized (this.mLock) {
                    this.mDecodeOptions = bitmap;
                }
            }
        }

        private Bitmap loadBitmap(Uri uri, Options options) throws FileNotFoundException {
            Throwable th;
            if (uri == null || this.mContext == null) {
                throw new IllegalArgumentException("bad argument to loadBitmap");
            }
            InputStream inputStream = null;
            try {
                InputStream openInputStream = this.mContext.getContentResolver().openInputStream(uri);
                try {
                    Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream, inputStream, options);
                    if (openInputStream != null) {
                        try {
                            openInputStream.close();
                        } catch (Throwable e) {
                            Log.w("PrintHelperApi19", "close fail ", e);
                        }
                    }
                    return decodeStream;
                } catch (Throwable th2) {
                    th = th2;
                    inputStream = openInputStream;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e2) {
                            Log.w("PrintHelperApi19", "close fail ", e2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e22) {
                        Log.w("PrintHelperApi19", "close fail ", e22);
                    }
                }
                throw th;
            }
        }

        private Bitmap convertBitmapForColorMode(Bitmap bitmap, int i) {
            if (i != 1) {
                return bitmap;
            }
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            float f = 0.0f;
            colorMatrix.setSaturation(f);
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(bitmap, f, f, paint);
            canvas.setBitmap(null);
            return createBitmap;
        }
    }

    private static final class PrintHelperStub implements PrintHelperVersionImpl {
        int mColorMode;
        int mOrientation;
        int mScaleMode;

        public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
        }

        public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) {
        }

        private PrintHelperStub() {
            int i = 2;
            this.mScaleMode = i;
            this.mColorMode = i;
            this.mOrientation = 1;
        }

        public void setScaleMode(int i) {
            this.mScaleMode = i;
        }

        public int getScaleMode() {
            return this.mScaleMode;
        }

        public int getColorMode() {
            return this.mColorMode;
        }

        public void setColorMode(int i) {
            this.mColorMode = i;
        }

        public void setOrientation(int i) {
            this.mOrientation = i;
        }

        public int getOrientation() {
            return this.mOrientation;
        }
    }

    @RequiresApi(20)
    private static class PrintHelperApi20 extends PrintHelperApi19 {
        PrintHelperApi20(Context context) {
            super(context);
            this.mPrintActivityRespectsOrientation = false;
        }
    }

    @RequiresApi(23)
    private static class PrintHelperApi23 extends PrintHelperApi20 {
        protected Builder copyAttributes(PrintAttributes printAttributes) {
            Builder copyAttributes = super.copyAttributes(printAttributes);
            if (printAttributes.getDuplexMode() != 0) {
                copyAttributes.setDuplexMode(printAttributes.getDuplexMode());
            }
            return copyAttributes;
        }

        PrintHelperApi23(Context context) {
            super(context);
            this.mIsMinMarginsHandlingCorrect = false;
        }
    }

    @RequiresApi(24)
    private static class PrintHelperApi24 extends PrintHelperApi23 {
        PrintHelperApi24(Context context) {
            super(context);
            boolean z = true;
            this.mIsMinMarginsHandlingCorrect = z;
            this.mPrintActivityRespectsOrientation = z;
        }
    }

    public static boolean systemSupportsPrint() {
        return VERSION.SDK_INT >= 19;
    }

    public PrintHelper(Context context) {
        if (VERSION.SDK_INT >= 24) {
            this.mImpl = new PrintHelperApi24(context);
        } else if (VERSION.SDK_INT >= 23) {
            this.mImpl = new PrintHelperApi23(context);
        } else if (VERSION.SDK_INT >= 20) {
            this.mImpl = new PrintHelperApi20(context);
        } else if (VERSION.SDK_INT >= 19) {
            this.mImpl = new PrintHelperApi19(context);
        } else {
            this.mImpl = new PrintHelperStub();
        }
    }

    public void setScaleMode(int i) {
        this.mImpl.setScaleMode(i);
    }

    public int getScaleMode() {
        return this.mImpl.getScaleMode();
    }

    public void setColorMode(int i) {
        this.mImpl.setColorMode(i);
    }

    public int getColorMode() {
        return this.mImpl.getColorMode();
    }

    public void setOrientation(int i) {
        this.mImpl.setOrientation(i);
    }

    public int getOrientation() {
        return this.mImpl.getOrientation();
    }

    public void printBitmap(String str, Bitmap bitmap) {
        this.mImpl.printBitmap(str, bitmap, null);
    }

    public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
        this.mImpl.printBitmap(str, bitmap, onPrintFinishCallback);
    }

    public void printBitmap(String str, Uri uri) throws FileNotFoundException {
        this.mImpl.printBitmap(str, uri, null);
    }

    public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) throws FileNotFoundException {
        this.mImpl.printBitmap(str, uri, onPrintFinishCallback);
    }
}
