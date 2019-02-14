package com.google.android.gms.samples.vision.ocrreader;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

public class GalleryFragment extends Fragment {

    private static final String TAG = "Tab2Fragment";

    private String value;

    private ImageView imageView;
    private TextView detectedTextView;

    // A TextToSpeech engine for speaking a String value.
    private TextToSpeech tts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        imageView = (ImageView) view.findViewById(R.id.image);
        detectedTextView = (TextView) view.findViewById(R.id.detected_text);

        view.findViewById(R.id.detected_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(value, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = android.content.ClipData.newPlainText("Label", value);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Text was copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGallery();
            }
        });

        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("TTS", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("TTS", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getContext(), listener);

        return view;
    }

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {

                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;

                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TextRecognizer textRecognizer = new TextRecognizer.Builder(this.getContext()).build();
                if (!textRecognizer.isOperational()) {
                    new AlertDialog.Builder(this.getContext()).setMessage("Text recognizer could not be set up on your device :(").show();
                }

                Frame frame = new Frame.Builder().setBitmap(bitmapImage).build();
                SparseArray<TextBlock> text = textRecognizer.detect(frame);

                value = "";

                for (int i = 0; i < text.size(); i++) {
                    TextBlock textBlock = text.valueAt(i);
                    if (textBlock != null && textBlock.getValue() != null) {
                        value += textBlock.getValue();
                        value += System.lineSeparator();
                    }
                }

                imageView.setImageBitmap(bitmapImage);
                detectedTextView.setText(value);

                textRecognizer.release();
            }
        }
    }
}
