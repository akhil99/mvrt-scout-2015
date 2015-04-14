package com.mvrt.superscouter;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.mvrt.superscouter.adapters.MatchRecordListAdapter;
import com.mvrt.superscouter.view.TabFragment;
import com.zxing.Contents;
import com.zxing.QRCodeEncoder;

import org.json.JSONObject;

public class MatchQRFragment extends TabFragment{

    ImageView imageView;
    String url;

    public static MatchQRFragment createInstance(String url){
        MatchQRFragment frag = new MatchQRFragment();
        frag.url = url;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_qr, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        imageView = (ImageView)v.findViewById(R.id.match_qrCode);

        int qrCodeDimention = 1000;

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(url, null,
            Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);

        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getTitle() {
        return "QR Code";
    }

}
