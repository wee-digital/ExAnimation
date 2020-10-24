package wee.digital.fpa.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    private static void convert(byte[] raw, int[] pixels, Double exposureCompensation) {
        if (exposureCompensation != null) {
            double ec = exposureCompensation;

            for (int i = 0; i < raw.length; ++i) {
                int grey = Math.min((int) ((0xFF & raw[i]) * ec), 255);
                pixels[i] = 0xFF000000 | 0x010101 * grey;
            }
        } else {
            for (int i = 0; i < raw.length; ++i) {
                int grey = 0xFF & raw[i];
                pixels[i] = 0xFF000000 | 0x010101 * grey;
            }
        }
    }

    public static Bitmap toBitmap(Bitmap bitmap, int[] pixels, byte[] raw, Double exposureCompensation) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        convert(raw, pixels, exposureCompensation);

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    public static Bitmap toBitmap(int width, int height, byte[] raw, Double exposureCompensation) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[raw.length];

        return toBitmap(bitmap, pixels, raw, exposureCompensation);
    }

    public static Bitmap getBGR(YuvImage yuvImage) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, outStream); // make JPG

        return BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size());
    }

    public static Bitmap rotate(Bitmap bmpImage, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        matrix.postScale(-1f, 1f);

        return Bitmap.createBitmap(bmpImage, 0, 0, bmpImage.getWidth(), bmpImage.getHeight(), matrix, true);
    }

    public static Bitmap crop(Bitmap source, int aspectWidth, int aspectHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        int width = sourceWidth;
        int height = width * aspectHeight / aspectWidth;
        int x = 0;
        int y = (sourceHeight - height) / 2;

        if (height > sourceHeight) {
            height = sourceHeight;
            width = height * aspectWidth / aspectHeight;
            x = (sourceWidth - width) / 2;
            y = 0;
        }

        Bitmap bitmap;
        if (x != 0 || y != 0 || source.getWidth() != width || source.getHeight() != height) {
            bitmap = Bitmap.createBitmap(source, x, y, width, height);
            source.recycle();
        } else return source;

        return bitmap;
    }

    public static Bitmap crop(byte[] jpeg, int aspectWidth, int aspectHeight) {
        return crop(toBitmap(jpeg), aspectWidth, aspectHeight);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap toBitmap(byte[] jpeg, int width, int height) {
        if (jpeg != null) {
            BitmapFactory.Options options = null;

            if (width > 0 && height > 0) {
                options = new BitmapFactory.Options();

                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);

                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateInSampleSize(options, width, height);
            }

            Bitmap srcBitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);
            int orientation = getOrientation(jpeg);

            if (srcBitmap != null && orientation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
                srcBitmap.recycle();
                return bitmap;
            }

            return srcBitmap;
        }

        return null;
    }

    public static Bitmap toBitmap(byte[] jpeg) {
        return toBitmap(jpeg, 0, 0);
    }

    public static int getOrientation(byte[] jpeg) {
        if (jpeg == null) {
            return 0;
        }
        int offset = 0;
        int length = 0;
        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
            int marker = jpeg[offset] & 0xFF;
            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;
            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                break;
            }
            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false);
            if (length < 2 || offset + length > jpeg.length) {
                Log.e(TAG, "Invalid length");
                return 0;
            }
            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                break;
            }
            // Skip other markers.
            offset += length;
            length = 0;
        }
        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.
            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.e(TAG, "Invalid byte order");
                return 0;
            }
            boolean littleEndian = (tag == 0x49492A00);
            // Get the offset and check if it is reasonable.
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                Log.e(TAG, "Invalid offset");
                return 0;
            }
            offset += count;
            length -= count;
            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian);
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    // We do not really care about type and count, do we?
                    int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                            return 0;
                        case 3:
                            return 180;
                        case 6:
                            return 90;
                        case 8:
                            return 270;
                    }
                    Log.i(TAG, "Unsupported orientation");
                    return 0;
                }
                offset += 12;
                length -= 12;
            }
        }
        return 0;
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes[offset] & 0xFF);
            offset += step;
        }
        return value;
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            /**
             * if your are targeting only api level >= 5
             * ExifInterface exif = new ExifInterface(src);
             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class.forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass.getConstructor(String.class);
                Object exifInstance = exifConstructor.newInstance(src);
                Method getAttributeInt = exifClass.getMethod("getAttributeInt", String.class, int.class);
                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[]{tagOrientation, 1});
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return orientation;
    }

    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static byte[] toRawByteArray(byte[] byteArray) {
        Bitmap rawBitmap = toBitmap(byteArray);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        rawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] rawByteArray = stream.toByteArray();
        rawBitmap.recycle();
        return rawByteArray;
    }

    public static byte[] imageToByteArray(Image image) {
        byte[] data = null;
        try {
            if (image.getFormat() == ImageFormat.JPEG) {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                data = new byte[buffer.capacity()];
                buffer.get(data);
                return data;
            } else if (image.getFormat() == ImageFormat.YUV_420_888) {
                data = NV21toJPEG(
                        YUV420toNV21(image),
                        image.getWidth(), image.getHeight(), 100);
                //data = YUV420toNV21(image);
            }
        } catch (Exception ex) {
            Log.e("ImagetoByteArray", ex.getMessage());
        }

        return data;
    }

    public static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
        return out.toByteArray();
    }

    public static byte[] YUV420toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;
                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;
                    break;
            }

            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }

    public static Bitmap byteArraytoBitmap(byte[] byteArray) {
        Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        bmp.copyPixelsFromBuffer(buffer);
        return bmp;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();
            stream.close();
            return byteArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap rotateBitmapFlip(Bitmap bitmap, Float degree, Boolean isFlip) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        if (isFlip) matrix.postScale(-1f, 1f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        Bitmap flipBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return flipBitmap;
    }
}
