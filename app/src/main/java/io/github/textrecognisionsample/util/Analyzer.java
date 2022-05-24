package io.github.textrecognisionsample.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;

import io.github.textrecognisionsample.activity.CameraX;

public class Analyzer implements ImageAnalysis.Analyzer {

    private final CameraX cameraX;

    public Analyzer(CameraX cameraX) {
        this.cameraX = cameraX;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();
        if (mediaImage != null) {
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            BarcodeScanner scanner = BarcodeScanning.getClient();

            recognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            Intent intent = new Intent();
                            intent.putExtra("text", visionText.getText());

                            Task<List<Barcode>> result1 = scanner.process(inputImage)
                                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onSuccess(List<Barcode> barcodes) {
                                            if (barcodes.size() > 0) {
                                                intent.putExtra("barcode", barcodes.get(0).getRawValue());
                                            } else {
                                                intent.putExtra("barcode", "No data");
                                            }

                                            DataAnalysis dataAnalysis = new DataAnalysis(visionText.getText());
                                            dataAnalysis.filterData(intent);

                                            cameraX.setResult(Activity.RESULT_OK, intent);
                                            cameraX.finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            cameraX.setResult(Activity.RESULT_OK, intent);
                                            cameraX.finish();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    cameraX.finish();
                                }
                            });
        }

    }
}
