package io.github.textrecognisionsample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class Analyzer implements ImageAnalysis.Analyzer {

    private CameraX cameraX;

    public Analyzer(CameraX cameraX) {
        this.cameraX = cameraX;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();
        if (mediaImage != null) {
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            recognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            Intent intent = new Intent();
                            intent.putExtra("text", visionText.getText());
                            cameraX.setResult(Activity.RESULT_OK, intent);
                            cameraX.finish();
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                }
                            });
        }

    }
}
