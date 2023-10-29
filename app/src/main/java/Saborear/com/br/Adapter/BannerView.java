package Saborear.com.br.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import Saborear.com.br.Chatbot;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class BannerView extends PagerAdapter {

    private final Activity context;
    private final ArrayList<Bitmap> banner;
    private final ArrayList<Class> target;
    public BannerView(Activity context, ArrayList<Bitmap> banner, ArrayList<Class> target) {
        this.context = context;
        this.banner = banner;
        this.target = target;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.subbanner, container, false);

        ImageView img = rowView.findViewById(R.id.subbanner_imagem);
        TextView posicao = rowView.findViewById(R.id.subbanner_posicao);

        img.setImageBitmap(banner.get(position));

        String pos = "";
        for(int i = 0; i<getCount(); i++) {
            if(i == position) pos += "◉ ";
            else pos += "○ ";
        }
        posicao.setText(pos);

        rowView.setOnClickListener(v -> {
            Intent myIntent = new Intent(context, target.get(position));
            context.startActivity(myIntent);
            Chatbot.cadastrando = false;
            context.overridePendingTransition(0, 0);
        });

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(img);
        Utils.blackMode((ViewGroup) rowView, ignore, inverted);

        container.addView(rowView);

        return rowView;
    }

    @Override
    public int getCount() {
        return banner.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 1f;
    }
}
