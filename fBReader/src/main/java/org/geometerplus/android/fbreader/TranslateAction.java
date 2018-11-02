/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.ui.android.R;

import java.util.HashMap;
import java.util.List;

import dyg.beans.CiBaWordBeanJson;
import dyg.beans.GsonBuildList;
import dyg.net.LoveFamousBookNet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class TranslateAction extends FBAndroidAction {
    private final static String TAG = "TranslateAction";
    private Activity activity;
    private Dialog dialog;
    private TextView trans_phonetic, trans_line0, trans_line1, trans_line2, trans_more;

    TranslateAction(FBReader baseActivity, FBReaderApp fbreader) {
        super(baseActivity, fbreader);
        this.activity = baseActivity;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_translate, null);
        dialog.setContentView(view);
        trans_phonetic = (TextView) view.findViewById(R.id.trans_phonetic);
        trans_line0 = (TextView) view.findViewById(R.id.trans_line0);
        trans_line1 = (TextView) view.findViewById(R.id.trans_line1);
        trans_line2 = (TextView) view.findViewById(R.id.trans_line2);
        trans_more = (TextView) view.findViewById(R.id.trans_more);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog1) {
                trans_line0.setVisibility(View.GONE);
                trans_line1.setVisibility(View.GONE);
                trans_line2.setVisibility(View.GONE);
                trans_more.setVisibility(View.GONE);
//                Window window = dialog.getWindow();
//                window.setAttributes(null);
            }
        });


    }

    @Override
    protected void run(final Object... params) {

        if (params[0] == null) {
            Log.i(TAG, "run: TranslateAction param is empty return");
            return;
        }

        String key = (String) params[0];
        if (TextUtils.isEmpty(key)) {
            Log.i(TAG, "run: TranslateAction translate key  is empty return");
            return;
        }
        key = key.trim().toLowerCase();
        if (TextUtils.isEmpty(key)) {
            Log.i(TAG, "run: TranslateAction translate key  is empty return");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://dict-co.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuildList.buildGson()))
                .build();
        LoveFamousBookNet lbn = retrofit.create(LoveFamousBookNet.class);
        HashMap map = new HashMap();
        map.put("w", key);
        map.put("type", "json");
        map.put("key", "297EB35CDF5FEEEFD6A13200E46FA720");
        Call<CiBaWordBeanJson> res = lbn.getWords(map);
        res.enqueue(new Callback<CiBaWordBeanJson>() {
            @Override
            public void onResponse(Call<CiBaWordBeanJson> call, Response<CiBaWordBeanJson> response) {
                CiBaWordBeanJson ciBaWordBeanJson = response.body();
                if (ciBaWordBeanJson != null) {
                    CiBaWordBeanJson.ExchangeBean exchangeBean = ciBaWordBeanJson.getExchange();
                    int str = ciBaWordBeanJson.getIs_CRI();
                    List<CiBaWordBeanJson.SymbolsBean> list = ciBaWordBeanJson.getSymbols();
                    String wordname = ciBaWordBeanJson.getWord_name();
                    trans_line0.setText(wordname);
                    trans_line0.setVisibility(View.VISIBLE);
                    int x = (int) params[1];
                    int y = (int) params[2];
                    if ((x + y) > 0) {
                        Window window = dialog.getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();
                        if (wlp != null) {
                            wlp.x = x;
                            wlp.y = y;
                            todo 位置显示有问题
                        }
                        window.setAttributes(wlp);
                    }
                    dialog.show();
                }

            }

            @Override
            public void onFailure(Call<CiBaWordBeanJson> call, Throwable t) {
                System.out.println("请求失败");
                System.out.println(t.getMessage());
                new AlertDialog.Builder(activity).setTitle("enen").setMessage(t.getMessage()).create().show();
            }
        });


    }
}
