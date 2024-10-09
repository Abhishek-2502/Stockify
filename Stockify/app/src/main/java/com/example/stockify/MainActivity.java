package com.example.stockify;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import android.content.Intent;
import android.net.Uri;
public class MainActivity extends AppCompatActivity {

    ImageView search, companyLogoImageView, priceImageView;
    TextView stockPriceTextView, stockPriceLabel, exchangeText, companyText, loadingText;
    EditText symbol;
    FrameLayout loadingOverlay;
    CardView animatedCardView;
    CardView additionalcardview;
    LinearLayout colorLinearLayout;

    private StockViewModel stockViewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Handler priceUpdateHandler = new Handler();
    private boolean isToastVisible = false;
    private Runnable priceUpdateRunnable;

    private Double previousPrice = null;

    // Additional information TextViews
    TextView additionalExchangeTextView , highPriceTextView, openPriceTextView, lowPriceTextView, previousCloseTextView, urlTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        loadingOverlay = findViewById(R.id.loadingOverlay);
        stockPriceTextView = findViewById(R.id.stockPriceTextView);
        stockPriceLabel = findViewById(R.id.stockPriceLabel);
        exchangeText = findViewById(R.id.exchangeText);
        companyText = findViewById(R.id.companyTextView);
        loadingText = findViewById(R.id.LoadingText);
        symbol = findViewById(R.id.symbolName);
        search = findViewById(R.id.search);
        companyLogoImageView = findViewById(R.id.companyLogoImageView);
        animatedCardView = findViewById(R.id.animatedCardView);
        additionalcardview = findViewById(R.id.additionalcardview);
        colorLinearLayout = findViewById(R.id.colorLinearLayout);
        priceImageView = findViewById(R.id.priceImageView);

        // Initialize additional information TextViews
        TextView additionalExchangeTextView = findViewById(R.id.additionalExchangeTextView);

        highPriceTextView = findViewById(R.id.highprice);
        openPriceTextView = findViewById(R.id.openprice);
        lowPriceTextView = findViewById(R.id.lowprice);
        previousCloseTextView = findViewById(R.id.previousclose);
        urlTextView = findViewById(R.id.URL);

        loadingText.setText("");
        animatedCardView.setVisibility(View.GONE); // Hide the CardView initially
        additionalcardview.setVisibility(View.GONE); // Hide the CardView initially

        // Initialize ViewModel
        stockViewModel = new ViewModelProvider(this).get(StockViewModel.class);

        // Observe LiveData from ViewModel
        stockViewModel.getStockPrice().observe(this, stockPrice -> {
            if (stockPrice != null) {
                updateStockPrice(stockPrice);
            }
        });

        stockViewModel.getCompanyName().observe(this, companyName -> {
            if (companyName != null) {
                companyText.setText(companyName);
            }
        });

        stockViewModel.getLogoUrl().observe(this, logoUrl -> {
            if (logoUrl != null) {
                Glide.with(this)
                        .load(logoUrl)
                        .into(companyLogoImageView);
            }
        });

        // Observe additional information
        stockViewModel.getHighPrice().observe(this, highPrice -> {
            if (highPrice != null) {
                highPriceTextView.setText(String.format("%.2f", highPrice));
            }
        });

        stockViewModel.getOpenPrice().observe(this, openPrice -> {
            if (openPrice != null) {
                openPriceTextView.setText(String.format("%.2f", openPrice));
            }
        });

        stockViewModel.getLowPrice().observe(this, lowPrice -> {
            if (lowPrice != null) {
                lowPriceTextView.setText(String.format("%.2f", lowPrice));
            }
        });

        stockViewModel.getPreviousClosePrice().observe(this, previousClose -> {
            if (previousClose != null) {
                previousCloseTextView.setText(String.format("%.2f", previousClose));
            }
        });

        stockViewModel.getWebUrl().observe(this, url -> {
            if (url != null) {
                urlTextView.setText(url);
            }
        });

        // Observe error message for non-intrusive hints
        stockViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty() && !isToastVisible) {
                isToastVisible = true;
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        stockViewModel.getExchange().observe(this, exchange -> {
            if (exchange != null) {
                additionalExchangeTextView.setText("Exchange: "+ exchange); // Set the exchange value in the additional card view
            }
        });

        stockViewModel.getPercentageChange().observe(this, change -> {
            if (change != null) {
                exchangeText.setText(change);
            }
        });

        stockViewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoadingOverlay();
            } else {
                hideLoadingOverlay();
            }
        });

        stockViewModel.getStockSymbol().observe(this, stockSymbol -> {
            if (stockSymbol != null) {
                stockPriceLabel.setText(stockSymbol); // Update stock price label
            }
        });

        // Search button click
        search.setOnClickListener(view -> {
            String symbolString = symbol.getText().toString();
            if (!symbolString.isEmpty()) {
                isToastVisible = false;
                colorLinearLayout.setBackgroundColor(Color.WHITE); // Reset color to white on new search
                previousPrice = null;
                loadingText.setText("");
                stockViewModel.fetchStockPrice(symbolString); // Fetch stock price on search click
                startPeriodicPriceUpdate(symbolString); // Start periodic updates
                priceImageView.setImageResource(R.drawable.white);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a valid symbol", Toast.LENGTH_SHORT).show();
            }
        });


// In onCreate method, after initializing urlTextView
        urlTextView.setOnClickListener(view -> {
            String url = urlTextView.getText().toString();
            if (!url.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } else {
                Toast.makeText(MainActivity.this, "No URL available", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showLoadingOverlay() {
        loadingOverlay.setVisibility(View.VISIBLE); // Show loading overlay
        animatedCardView.setVisibility(View.GONE); // Hide the CardView when loading starts
        additionalcardview.setVisibility(View.GONE); // Hide the CardView when loading starts
        search.setEnabled(false); // Disable the search button while loading
    }

    private void hideLoadingOverlay() {
        loadingOverlay.setVisibility(View.GONE); // Hide loading overlay
        // Only show the CardView if stock data is fetched successfully
        if (stockViewModel.getStockPrice().getValue() != null) {
            animatedCardView.setVisibility(View.VISIBLE); // Show the CardView after loading finishes
            additionalcardview.setVisibility(View.VISIBLE); // Show the CardView after loading finishes
        }
        search.setEnabled(true); // Enable the search button after loading completes
    }

    private void updateStockPrice(double currentPrice) {
        handler.post(() -> {
            String formattedPrice = String.format("%.2f", currentPrice);
            stockPriceTextView.setText(formattedPrice);

            // Update the color based on price change and the up/down image
            if (previousPrice != null) {
                animatedCardView.setVisibility(View.VISIBLE); // Ensure the CardView is visible when price is updated
                double priceDifference = Math.abs(currentPrice - previousPrice);
                String priceDiffFormatted = String.format("%.2f", priceDifference);

                // Update the loadingText with the price difference
                loadingText.setText("(" + priceDiffFormatted + ")");

                if (currentPrice > previousPrice) {
                    colorLinearLayout.setBackgroundColor(Color.GREEN);
                    priceImageView.setImageResource(R.drawable.up_image); // Set "up" image
                } else if (currentPrice < previousPrice) {
                    colorLinearLayout.setBackgroundColor(Color.RED);
                    priceImageView.setImageResource(R.drawable.down_image); // Set "down" image
                }

            }
            previousPrice = currentPrice;
        });
    }

    private void startPeriodicPriceUpdate(String symbol) {
        if (priceUpdateRunnable != null) {
            priceUpdateHandler.removeCallbacks(priceUpdateRunnable);
        }

        priceUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                stockViewModel.fetchStockPrice(symbol); // Fetch updated stock price periodically
                priceUpdateHandler.postDelayed(this, 60000); // Update every minute
            }
        };

        priceUpdateHandler.post(priceUpdateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        priceUpdateHandler.removeCallbacks(priceUpdateRunnable); // Clean up the handler
    }
}
