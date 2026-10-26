package com.comparelab.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.view.WindowManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MainActivity extends Activity {
    private WebView web;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_REQ = 7331;
    private File projectDir;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(0xFF10141E);
        getWindow().setNavigationBarColor(0xFF10141E);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        projectDir = new File(getFilesDir(), "projects");
        projectDir.mkdirs();
        web = new WebView(this);
        web.setBackgroundColor(0xFF10141E);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setDefaultTextEncodingName("UTF-8");
        web.addJavascriptInterface(new StorageBridge(), "NativeStorage");
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> cb, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = cb;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Seleziona immagini o GIF"), FILE_REQ);
                return true;
            }
        });
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != FILE_REQ || fileCallback == null) return;
        Uri[] resultUris = null;
        if (result == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int len = data.getClipData().getItemCount();
                resultUris = new Uri[len];
                for (int i=0; i<len; i++) resultUris[i] = data.getClipData().getItemAt(i).getUri();
            } else if (data.getData() != null) resultUris = new Uri[]{data.getData()};
        }
        fileCallback.onReceiveValue(resultUris);
        fileCallback = null;
    }
    private String cleanId(String id) { return id != null && id.matches("[a-zA-Z0-9_-]{1,90}") ? id : null; }
    class StorageBridge {
        @JavascriptInterface public String list() {
            JSONArray list = new JSONArray();
            File[] files = projectDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null) return "[]";
            for (File f : files) {
                try {
                    JSONObject obj = new JSONObject(Files.readString(f.toPath(), StandardCharsets.UTF_8));
                    JSONObject meta = new JSONObject();
                    meta.put("id", obj.optString("id"));
                    meta.put("name", obj.optString("name", "Progetto"));
                    meta.put("updated", obj.optLong("updated", 0));
                    JSONArray pics = obj.optJSONArray("pics");
                    for (int i = 0; i < 2; i++) {
                        JSONObject pic = pics == null ? null : pics.optJSONObject(i);
                        meta.put("pic"+i, pic == null ? "" : pic.optString("name", ""));
                    }
                    list.put(meta);
                } catch (Exception ignored) { }
            }
            return list.toString();
        }
        @JavascriptInterface public String load(String id) {
            String safe = cleanId(id); if (safe == null) return "";
            try { return Files.readString(new File(projectDir, safe + ".json").toPath(), StandardCharsets.UTF_8); }
            catch(Exception e) { return ""; }
        }
        @JavascriptInterface public boolean save(String id, String json) {
            String safe = cleanId(id);
            if (safe == null || json == null) return false;
            try {
                File tmp = new File(projectDir, safe + ".tmp");
                Files.writeString(tmp.toPath(), json, StandardCharsets.UTF_8);
                File dest = new File(projectDir, safe + ".json");
                if (dest.exists() && !dest.delete()) return false;
                return tmp.renameTo(dest);
            } catch(Exception e) { return false; }
        }
        @JavascriptInterface public void delete(String id) {
            String safe = cleanId(id);
            if (safe != null) new File(projectDir, safe + ".json").delete();
        }
    }
    @Override public void onBackPressed() {
        web.evaluateJavascript("window.goBack && window.goBack()", null);
    }
}
