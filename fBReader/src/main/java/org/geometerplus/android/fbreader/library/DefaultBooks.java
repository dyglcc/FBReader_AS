package org.geometerplus.android.fbreader.library;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dyg.android.reader.R;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.fbreader.book.Book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import dyg.activity.FbDefaultActivity;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static dyg.activity.FbDefaultActivity.PATH_BOOKS;
import static dyg.activity.FbDefaultActivity.PATH_ICONS;

public class DefaultBooks {
    private static final String TAG = "defaut_book";
    WeakReference<FbDefaultActivity> activityWeakReference = null;
    //    private List<DefaultBookInfo> list = null;
    String[] strings = null;
    String[] icons = null;
    ConorTransform conorTransform;

    public DefaultBooks(final FbDefaultActivity defaultBooksActivity) {
        activityWeakReference = new WeakReference(defaultBooksActivity);
        File dirBooks = new File(defaultBooksActivity.getFilesDir(), PATH_BOOKS);
        File dirIcons = new File(defaultBooksActivity.getFilesDir(), PATH_ICONS);
        conorTransform = new ConorTransform(defaultBooksActivity, 20);
        conorTransform.setExceptCorner(false, false, false, false);
        if (dirBooks.exists() && dirIcons.exists()) {
            strings = dirBooks.list();
            icons = dirIcons.list();
        } else {
            Log.e(TAG, "DefaultBooks default file is empty");
            return;
        }
        final DefaultBooksAdapter adapter = new DefaultBooksAdapter();
        defaultBooksActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HeadGridView gridView = defaultBooksActivity.findViewById(R.id.layout_default_books);
                View headerView = LayoutInflater.from(defaultBooksActivity).inflate(R.layout.grid_head, null, false);
                gridView.addHeaderView(headerView);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.e(TAG, "onItemClick: " + position);
                        try{
                            String name = (String) parent.getAdapter().getItem(position);
                            Book book = new Book(name.hashCode(),
                                    defaultBooksActivity.getFilesDir()
                                            + "/" + PATH_BOOKS + "/"
                                            + name, name, "utf8", "en");
                            FBReader.openBookActivity(defaultBooksActivity, book, null);
                        }catch (Exception t){
                            t.printStackTrace();
                        }

                    }
                });
            }
        });


    }


    private class DefaultBooksAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return strings == null ? 0 : strings.length;
        }

        @Override
        public Object getItem(int position) {
            return strings[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (activityWeakReference == null || activityWeakReference.get() == null) {
                return null;
            }
            if (convertView == null) {
                convertView = LayoutInflater.from(activityWeakReference.get()).inflate(R.layout.item_default_book, null, false);
                holder = new ViewHolder((TextView) convertView.findViewById(R.id.tv_book_name),
                        (ImageView) convertView.findViewById(R.id.iv_img));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = strings[position];
            String picPath = findPicPath(name);
            try {
                // set textView
                holder.view.setText(name.substring(0, name.indexOf(".")).intern());

                if (picPath != null) {
                    File file = new File(activityWeakReference.get().getFilesDir() + "/" + PATH_ICONS + "/" + picPath);
                    holder.imageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(file)));
                    Glide.with(activityWeakReference
                            .get())
                            .load(file)
                            .transition(withCrossFade())
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(25)))
                            .into(holder.imageView);

                } else {
                    holder.imageView.setImageDrawable(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    private String findPicPath(String name) {
        String[] picNames = name.split("\\.");
        String picName = picNames[0];
        for (int i = 0; i < icons.length; i++) {
            if (icons[i].startsWith(picName)) {
                return icons[i];
            }
        }
        return null;
    }

    static final class ViewHolder {
        TextView view;
        ImageView imageView;

        public ViewHolder(TextView viewById, ImageView imageView) {
            view = viewById;
            this.imageView = imageView;
        }
    }
}
