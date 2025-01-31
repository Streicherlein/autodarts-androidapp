package com.example.autodarts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

public class WebPagerAdapter extends RecyclerView.Adapter<WebPagerAdapter.WebViewHolder> {

    private final List<String> urls;
    private final Context context;

    public WebPagerAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @NonNull
    @Override
    public WebViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.webview_page, parent, false);
        return new WebViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WebViewHolder holder, int position) {
        String url = urls.get(position);

        // Setze WebViewClient für die WebView
        holder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true; // Verhindert das Öffnen in einem externen Browser
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Entferne das "Pull-to-Refresh"-Lade-Symbol, wenn die Seite fertig geladen ist
                holder.swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Lade die URL der Seite
        holder.webView.loadUrl(url);

        // Setze die Swipe-to-Refresh Logik
        holder.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Wenn der Benutzer nach unten zieht, lade die Seite neu
            holder.webView.reload();
        });

        // Überwache die Scroll-Position mit OnTouchListener
        holder.webView.setOnTouchListener((v, event) -> {
            // Prüfe, ob die WebView ganz oben ist
            if (holder.webView.getScrollY() == 0) {
                holder.swipeRefreshLayout.setEnabled(true); // Swipe-Refresh aktivieren, wenn am oberen Rand
            } else {
                holder.swipeRefreshLayout.setEnabled(false); // Swipe-Refresh deaktivieren, wenn nicht am oberen Rand
            }
            return false; // Damit die Touch-Events weiterhin an die WebView weitergegeben werden
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class WebViewHolder extends RecyclerView.ViewHolder {
        WebView webView;
        SwipeRefreshLayout swipeRefreshLayout;

        WebViewHolder(View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.webview);
            swipeRefreshLayout = itemView.findViewById(R.id.swipeRefreshLayout);

            // WebView Einstellungen
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);

            // Cookies aktivieren
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
    }
}



