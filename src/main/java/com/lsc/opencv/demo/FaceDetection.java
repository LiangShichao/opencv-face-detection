package com.lsc.opencv.demo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static com.lsc.opencv.demo.util.ResourcesUtil.getResourcePath;

public class FaceDetection {
    public static void main(String[] args) {
        // 1. Load the native library.
        loadNativeLibrary();

        // 2. Load a original imagegit
        Mat image = loadImage("lena.png");

        // 3. Create a face detector from the cascade file in the resources directory.
        Mat faceImage = getFaceDetectionImage(image, "n95.png");

        // 4. Save the visualized detection.
        writeToFile(faceImage, getResourcePath("") + "faceDetection-lena.png");
    }

    public static void loadNativeLibrary() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Mat loadImage(String filename, int... flags) {
        if (flags.length > 0) {
            return Imgcodecs.imread(getResourcePath(filename), flags[0]);
        } else {
            return Imgcodecs.imread(getResourcePath(filename));
        }
    }

    public static Mat getFaceDetectionImage(Mat image, String maskPath) {
        // 3.1 Create a cascade classifier
        CascadeClassifier faceDetector = new CascadeClassifier(getResourcePath("lbpcascade_frontalface.xml"));
        // 3.2 MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        // 3.3 Face detection
        faceDetector.detectMultiScale(image, faceDetections);
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        // 3.4 Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            //【1】读入两幅图像并检查图像是否读取成功
            Mat srcImage = image;
            Mat signal = loadImage(getFilenameByRect(maskPath, rect));
            //【2】定义一个Mat类型并给其设定ROI区域
            System.out.println(rect.width);
            System.out.println(signal.width());
            Mat imageROI = srcImage.submat(new Rect(rect.x + (rect.width - signal.width()) / 2, rect.y + (rect.height + signal.height()) * 2 /5, signal.width(), signal.height()));
            //【3】加载掩模（必须是灰度图）
            Mat mask = loadImage(getFilenameByRect(maskPath, rect), 0);    //参数0显示为灰度图
            //【4】将掩模复制到ROI
            signal.copyTo(imageROI, mask);
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
        return image;
    }

    public static void writeToFile(Mat image, String filename) {
        System.out.println(String.format("Writing %s", filename));
        Imgcodecs.imwrite(filename, image);
    }

    public static String getFilenameByRect(String fileName, Rect rect) {
        int width = 0;
        if (rect.width >= 192) {
            width = 128;
        } else if (rect.width >= 128) {
            width = 96;
        } else if (rect.width >= 96) {
            width = 64;
        } else if (rect.width >= 64) {
            width = 56;
        } else if (rect.width >= 48) {
            width = 48;
        } else {
            width = 32;
        }
        String name = fileName.replace(".png", "_" + width + ".png");
        System.out.println(name);
        return name;
    }
}